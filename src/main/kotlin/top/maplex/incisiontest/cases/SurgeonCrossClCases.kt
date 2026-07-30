package top.maplex.incisiontest.cases


import taboolib.module.incision.annotation.SelectorKind

import taboolib.module.incision.annotation.Selector

import taboolib.module.incision.annotation.Pointcut

import org.bukkit.Bukkit
import taboolib.module.incision.annotation.Lead
import taboolib.module.incision.annotation.Splice
import taboolib.module.incision.annotation.Surgeon
import taboolib.module.incision.api.Theatre

/**
 * 跨 ClassLoader 切口 Demo —— 证明 Incision 能穿越 IsolatedClassLoader 边界。
 *
 * NMS 目标（通过 CraftServer.getServer() 拿到 MinecraftServer 实例）：
 * - isHardcore()boolean   —— @Lead 监听
 * - getMotd()String       —— @Splice 替换返回值
 * - getPlayerCount()int   —— @Lead 监听
 *
 * Bukkit 目标（静态 API，server ClassLoader）：
 * - Bukkit#getMaxPlayers()int  —— @Splice 替换返回值
 */
@Surgeon
object SurgeonCrossClCases {

    @Volatile var isHardcoreLeadHits = 0
    @Volatile var getPlayerCountLeadHits = 0

    // ---- NMS ----

    // 不同版本把实现放在 MinecraftServer 或 DedicatedServer；两条逻辑坐标都交给
    // 自动 remap，实际虚调用只会经过当前版本真正声明的方法一次。
    @Lead(pointcut = Pointcut(anyOf = [
        Selector(kind = SelectorKind.METHOD, owner = "net/minecraft/server/MinecraftServer", name = "isHardcore", descriptor = "()Z"),
        Selector(kind = SelectorKind.METHOD, owner = "net/minecraft/server/dedicated/DedicatedServer", name = "isHardcore", descriptor = "()Z"),
    ], maxMatches = 2))
    fun onIsHardcore(theatre: Theatre) {
        isHardcoreLeadHits++
    }

    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "net/minecraft/server/MinecraftServer", name = "getPlayerCount", descriptor = "()I")]))
    fun onGetPlayerCount(theatre: Theatre) {
        getPlayerCountLeadHits++
    }

    // ---- Bukkit ----
    // getMaxPlayers 由 DSL 测试用例动态切入（见 AllCases.testBukkitMaxPlayers），
    // 此处用 @Lead 做静态注解版本，验证注解路径同样能穿越 CL 边界。

    @Volatile var getMaxPlayersLeadHits = 0

    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "org/bukkit/Bukkit", name = "getMaxPlayers", descriptor = "()I")]))
    fun onGetMaxPlayers(theatre: Theatre) {
        getMaxPlayersLeadHits++
        // 仅集成用例打开标记时记录跨插件顺序，避免普通回归调用污染共享观察值。
        if (System.getProperty("incision.bridge.sequence.active", "false").toBoolean()) {
            val sequence = System.getProperty("incision.bridge.sequence", "")
            System.setProperty("incision.bridge.sequence", listOf(sequence, "main").filter { it.isNotEmpty() }.joinToString(","))
        }
    }

    fun reset() {
        isHardcoreLeadHits = 0
        getPlayerCountLeadHits = 0
        getMaxPlayersLeadHits = 0
    }
}
