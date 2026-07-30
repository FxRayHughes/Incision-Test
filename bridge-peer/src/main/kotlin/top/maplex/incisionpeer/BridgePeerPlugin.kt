package top.maplex.incisionpeer

import taboolib.common.platform.Plugin
import taboolib.common.platform.function.console

/**
 * 第二个 Incision 插件入口。
 *
 * 本插件不能与主测试插件合并打包，否则两套 advice 会落入同一个 ClassLoader，
 * 无法复现 Leaf、AuraSkills 场景中 Bridge 需要按声明插件路由 dispatcher 的问题。
 */
object BridgePeerPlugin : Plugin() {

    override fun onActive() {
        BridgePeerState.reset()
        System.setProperty(BridgePeerState.ENABLED_PROPERTY, "true")
        console().sendMessage("§a[Incision-Bridge-Peer] independent Bridge lease ready")
    }

    override fun onDisable() {
        // 保留命中计数供主插件确认 disable 后没有幽灵 dispatcher，只有启用状态需要即时撤销。
        System.setProperty(BridgePeerState.ENABLED_PROPERTY, "false")
    }
}
