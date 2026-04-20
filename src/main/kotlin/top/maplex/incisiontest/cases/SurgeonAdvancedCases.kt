package top.maplex.incisiontest.cases

import taboolib.module.incision.annotation.Excise
import taboolib.module.incision.annotation.Bypass
import taboolib.module.incision.annotation.Operation
import taboolib.module.incision.annotation.Site
import taboolib.module.incision.api.Anchor
import taboolib.module.incision.annotation.Surgeon
import taboolib.module.incision.api.Theatre

/**
 * @Bypass / @Excise / @Operation 优先级注解的覆盖测试。
 *
 * 所有目标都落在 [top.maplex.incisiontest.fixture.SurgeonAdvancedTargetFixture]。
 *
 * - onReturnsBoolExcise: 整段替换 returnsBoolean，始终返回 true
 * - onLongCalcBypass: 替换 longCalc 的返回（借用 Bypass#method 字段作为目标描述符）
 * - Operation(priority=100) 覆盖默认优先级
 */
@Surgeon(priority = 5)
object SurgeonAdvancedCases {

    @Volatile var exciseHits = 0
    @Volatile var bypassHits = 0

    @Excise(scope = "method:top.maplex.incisiontest.fixture.SurgeonAdvancedTargetFixture#returnsBoolean(boolean)boolean")
    fun onReturnsBoolExcise(theatre: Theatre): Any? {
        exciseHits++
        return true
    }

    @Operation(priority = 100)
    @Bypass(
        method = "top.maplex.incisiontest.fixture.SurgeonAdvancedTargetFixture#longCalc(long,long)long",
        site = Site(anchor = Anchor.HEAD)
    )
    fun onLongCalcBypass(theatre: Theatre): Any? {
        bypassHits++
        return 777L
    }

    fun reset() {
        exciseHits = 0; bypassHits = 0
    }
}
