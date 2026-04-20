package top.maplex.incisiontest.cases

import taboolib.module.incision.annotation.Lead
import taboolib.module.incision.annotation.Splice
import taboolib.module.incision.annotation.Surgeon
import taboolib.module.incision.annotation.Trail
import taboolib.module.incision.api.Theatre

/**
 * 基础注解测试 — @Surgeon object，包含 @Lead / @Trail / @Splice 三种核心 advice。
 *
 * 由 incision 模块的 SurgeonScanner 在 ENABLE 阶段自动扫描注册。
 *
 * 所有目标都落在 [top.maplex.incisiontest.fixture.SurgeonBasicTargetFixture]，
 * 与瞬态测试使用的 TargetFixture 完全解耦。
 */
@Surgeon
object SurgeonBasicCases {

    @Volatile var leadHits = 0
    @Volatile var trailHits = 0
    @Volatile var spliceHits = 0
    @Volatile var lastArg: Any? = null

    @Lead(scope = "method:top.maplex.incisiontest.fixture.SurgeonBasicTargetFixture#greet(java.lang.String)java.lang.String")
    fun onGreetLead(theatre: Theatre) {
        leadHits++
        lastArg = theatre.args.firstOrNull()
    }

    @Trail(scope = "method:top.maplex.incisiontest.fixture.SurgeonBasicTargetFixture#voidNoArg()V")
    fun onVoidTrail(theatre: Theatre) {
        trailHits++
    }

    @Splice(scope = "method:top.maplex.incisiontest.fixture.SurgeonBasicTargetFixture#add(int,int)int")
    fun onAddSplice(theatre: Theatre): Any? {
        spliceHits++
        // 返回非 null → 替换原方法返回值
        return ((theatre.args[0] as Int) + (theatre.args[1] as Int)) * 1000
    }

    fun reset() {
        leadHits = 0; trailHits = 0; spliceHits = 0; lastArg = null
    }
}
