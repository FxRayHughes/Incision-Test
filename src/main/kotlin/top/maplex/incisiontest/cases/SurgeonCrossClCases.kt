package top.maplex.incisiontest.cases

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

    @Lead(scope = "method:net.minecraft.server.MinecraftServer#isHardcore()boolean")
    fun onIsHardcore(theatre: Theatre) {
        isHardcoreLeadHits++
    }

    @Lead(scope = "method:net.minecraft.server.MinecraftServer#getPlayerCount()int")
    fun onGetPlayerCount(theatre: Theatre) {
        getPlayerCountLeadHits++
    }

    // ---- Bukkit ----
    // getMaxPlayers 由 DSL 测试用例动态切入（见 AllCases.testBukkitMaxPlayers），
    // 此处用 @Lead 做静态注解版本，验证注解路径同样能穿越 CL 边界。

    @Volatile var getMaxPlayersLeadHits = 0

    @Lead(scope = "method:org.bukkit.Bukkit#getMaxPlayers()int")
    fun onGetMaxPlayers(theatre: Theatre) {
        getMaxPlayersLeadHits++
    }

    fun reset() {
        isHardcoreLeadHits = 0
        getPlayerCountLeadHits = 0
        getMaxPlayersLeadHits = 0
    }
}
