package top.maplex.incisiontest.cases

import taboolib.module.incision.annotation.Bypass
import taboolib.module.incision.annotation.Excise
import taboolib.module.incision.annotation.Operation
import taboolib.module.incision.annotation.Site
import taboolib.module.incision.annotation.Surgeon
import taboolib.module.incision.api.Anchor
import taboolib.module.incision.api.Theatre

/**
 * Mixin 风格 Demo —— 类比 SpongeMixin @Overwrite / @Redirect。
 *
 * 所有目标都落在 [top.maplex.incisiontest.fixture.SurgeonMixinTargetFixture]。
 *
 * - @Excise  = @Overwrite：整段替换 mayThrow，永不抛异常
 * - @Bypass  = @Redirect：重定向 helper(int) 的调用，返回固定值
 * - @Operation(priority) 控制多 advice 执行顺序
 */
@Surgeon(priority = 10)
object SurgeonMixinCases {

    @Volatile var exciseHits = 0
    @Volatile var bypassHits = 0

    /** @Overwrite：整段替换 mayThrow，始终返回 "mixin-safe"，不再抛异常。 */
    @Excise(scope = "method:top.maplex.incisiontest.fixture.SurgeonMixinTargetFixture#mayThrow(boolean)java.lang.String")
    fun overwriteMayThrow(theatre: Theatre): Any? {
        exciseHits++
        return "mixin-safe"
    }

    /** @Redirect：重定向 helper(int) 调用，返回固定值 888。 */
    @Operation(priority = 50)
    @Bypass(
        method = "top.maplex.incisiontest.fixture.SurgeonMixinTargetFixture#helper(int)int",
        site = Site(anchor = Anchor.HEAD)
    )
    fun redirectHelper(theatre: Theatre): Any? {
        bypassHits++
        return 888
    }

    fun reset() {
        exciseHits = 0; bypassHits = 0
    }
}
