package top.maplex.incisiontest.cases

import org.bukkit.Bukkit
import top.maplex.incisiontest.CaseResult
import top.maplex.incisiontest.runCase

/**
 * 两个独立 Incision 插件共享 canonical Bridge 的运行时集成测试。
 *
 * 测试只观察公开调用结果、命中次数和 canonical lease 数；不得直接修改 Bridge 私有表，
 * 否则无法发现注册到了错误同名 Bridge 副本的故障。
 */
object BridgePeerIntegrationCases {

    private const val PEER_NAME = "Incision-Bridge-Peer"
    private const val ENABLED_PROPERTY = "incision.bridge.peer.enabled"
    private const val HITS_PROPERTY = "incision.bridge.peer.hits"
    private const val SEQUENCE_ACTIVE_PROPERTY = "incision.bridge.sequence.active"
    private const val SEQUENCE_PROPERTY = "incision.bridge.sequence"

    fun testDualDispatch(): CaseResult = runCase("bridge-peer-dual-dispatch") { asserts, _ ->
        val peer = enabledPeer()
        asserts.notNull(peer, "$PEER_NAME 必须作为独立插件启用")
        if (peer == null) return@runCase

        SurgeonCrossClCases.reset()
        System.setProperty(HITS_PROPERTY, "0")
        System.setProperty(SEQUENCE_PROPERTY, "")
        val leases = canonicalLeaseCount()
        asserts.check(leases >= 2, "canonical Bridge 至少应持有两个插件 lease，actual=$leases")

        System.setProperty(SEQUENCE_ACTIVE_PROPERTY, "true")
        try {
            Bukkit.getMaxPlayers()
        } finally {
            System.setProperty(SEQUENCE_ACTIVE_PROPERTY, "false")
        }

        asserts.equal(1, SurgeonCrossClCases.getMaxPlayersLeadHits, "主插件 getMaxPlayers dispatcher 恰好命中一次")
        asserts.equal(1, peerHits(), "peer getMaxPlayers dispatcher 恰好命中一次")
        val sequence = System.getProperty(SEQUENCE_PROPERTY, "").split(',').filter { it.isNotEmpty() }
        asserts.equal(2, sequence.size, "同目标只形成两段 advice 执行序列")
        asserts.equal(1, sequence.count { it == "main" }, "执行序列包含一次主插件 advice")
        asserts.equal(1, sequence.count { it == "peer" }, "执行序列包含一次 peer advice")
        asserts.note("跨插件实际执行顺序=${sequence.joinToString(" -> ")}")
    }

    fun testDisableIsolation(): CaseResult = runCase("bridge-peer-disable-isolation") { asserts, _ ->
        val peer = enabledPeer()
        asserts.notNull(peer, "$PEER_NAME 必须在本用例开始时启用")
        if (peer == null) return@runCase

        val leasesBefore = canonicalLeaseCount()
        disablePeer(peer)
        val leasesAfter = canonicalLeaseCount()
        val peerHitsAfterDisable = peerHits()

        asserts.equal(leasesBefore - 1, leasesAfter, "禁用 peer 只释放自己的 Bridge lease")
        asserts.equal("false", System.getProperty(ENABLED_PROPERTY), "peer 生命周期已进入 disable")

        SurgeonCrossClCases.reset()
        Bukkit.getMaxPlayers()

        asserts.equal(1, SurgeonCrossClCases.getMaxPlayersLeadHits, "peer 卸载后主 dispatcher 仍恰好命中一次")
        asserts.equal(peerHitsAfterDisable, peerHits(), "peer 卸载后其已织入目标不得再收到 Bridge dispatch")
    }

    /** 反射穿过 Bukkit Plugin 边界，避免旧版本测试 classpath 缺失现代 Namespaced 父接口。 */
    private fun enabledPeer(): Any? {
        val manager = Bukkit.getPluginManager()
        val peer = manager.javaClass.getMethod("getPlugin", String::class.java).invoke(manager, PEER_NAME) ?: return null
        val enabled = peer.javaClass.getMethod("isEnabled").invoke(peer) as? Boolean ?: false
        return peer.takeIf { enabled }
    }

    private fun disablePeer(peer: Any) {
        val manager = Bukkit.getPluginManager()
        val disable = manager.javaClass.methods.first {
            it.name == "disablePlugin" && it.parameterCount == 1
        }
        disable.invoke(manager, peer)
    }

    private fun peerHits(): Int = System.getProperty(HITS_PROPERTY, "-1").toIntOrNull() ?: -1

    /** 始终从 system ClassLoader 取得 canonical 类，禁止链接到主插件 jar 内的同名 Bridge。 */
    private fun canonicalLeaseCount(): Int {
        val bridge = Class.forName(
            "io.izzel.incision.bridge.IncisionBridge",
            true,
            ClassLoader.getSystemClassLoader(),
        )
        return (bridge.getMethod("localLeaseCount").invoke(null) as Number).toInt()
    }
}
