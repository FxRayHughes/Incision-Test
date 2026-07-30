package top.maplex.incisionpeer

/**
 * 跨插件测试只通过 JDK System properties 交换基础值，避免测试代码本身依赖任一插件 ClassLoader。
 * 所有读写发生在服务端主线程；这里刻意不引入共享 API jar，以免掩盖 Bridge 的隔离边界。
 */
object BridgePeerState {

    const val ENABLED_PROPERTY = "incision.bridge.peer.enabled"
    const val HITS_PROPERTY = "incision.bridge.peer.hits"
    const val SEQUENCE_ACTIVE_PROPERTY = "incision.bridge.sequence.active"
    const val SEQUENCE_PROPERTY = "incision.bridge.sequence"

    fun reset() {
        System.setProperty(HITS_PROPERTY, "0")
    }

    fun hit() {
        val current = System.getProperty(HITS_PROPERTY, "0").toIntOrNull() ?: 0
        System.setProperty(HITS_PROPERTY, (current + 1).toString())
        if (System.getProperty(SEQUENCE_ACTIVE_PROPERTY, "false").toBoolean()) {
            val sequence = System.getProperty(SEQUENCE_PROPERTY, "")
            System.setProperty(SEQUENCE_PROPERTY, listOf(sequence, "peer").filter { it.isNotEmpty() }.joinToString(","))
        }
    }
}
