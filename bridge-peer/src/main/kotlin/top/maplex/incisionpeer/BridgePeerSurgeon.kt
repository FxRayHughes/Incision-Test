package top.maplex.incisionpeer

import taboolib.module.incision.annotation.Lead
import taboolib.module.incision.annotation.Pointcut
import taboolib.module.incision.annotation.Selector
import taboolib.module.incision.annotation.SelectorKind
import taboolib.module.incision.annotation.Surgeon
import taboolib.module.incision.api.Theatre

/**
 * 与主测试插件声明同一个服务端 ClassLoader 目标。
 *
 * 两个独立 ClassLoader 同时侵入 getMaxPlayers，要求 Bridge 把两个 advice 串成执行链；
 * 任一 dispatcher 被覆盖、重复织入或旧 loader 残留都会表现为标记缺失或重复。
 */
@Surgeon
object BridgePeerSurgeon {

    @Lead(
        pointcut = Pointcut(
            anyOf = [Selector(
                kind = SelectorKind.METHOD,
                owner = "org/bukkit/Bukkit",
                name = "getMaxPlayers",
                descriptor = "()I",
            )],
        ),
    )
    fun onGetMaxPlayers(theatre: Theatre) {
        BridgePeerState.hit()
    }
}
