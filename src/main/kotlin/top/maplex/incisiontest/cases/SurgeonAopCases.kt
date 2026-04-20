package top.maplex.incisiontest.cases

import taboolib.module.incision.annotation.KotlinTarget
import taboolib.module.incision.annotation.Lead
import taboolib.module.incision.annotation.Splice
import taboolib.module.incision.annotation.Surgeon
import taboolib.module.incision.annotation.Trail
import taboolib.module.incision.api.Theatre

/**
 * AOP 风格 Demo —— 类比 Spring @Aspect。
 *
 * 所有目标都落在 [top.maplex.incisiontest.fixture.SurgeonAopTargetFixture]。
 * 内部方法已做物理隔离，避免 advice 之间互相串味。
 */
@Surgeon
object SurgeonAopCases {

    @Volatile var beforeHits = 0
    @Volatile var afterHits = 0
    @Volatile var aroundHits = 0
    @Volatile var aroundArgsHits = 0
    @Volatile var aroundResultHits = 0
    @Volatile var staticLeadHits = 0
    @Volatile var companionLeadHits = 0
    @Volatile var lastDurationNanos: Long = -1L
    @Volatile var lastAuditArgs: List<Any?> = emptyList()

    @Lead(scope = "method:top.maplex.incisiontest.fixture.SurgeonAopTargetFixture#touchesField()int")
    fun beforeTouchField(theatre: Theatre) {
        beforeHits++
    }

    @Trail(scope = "method:top.maplex.incisiontest.fixture.SurgeonAopTargetFixture#multiArg(int,java.lang.String,double)java.lang.String")
    fun afterMultiArg(theatre: Theatre) {
        afterHits++
        lastAuditArgs = theatre.args.toList()
    }

    @Splice(scope = "method:top.maplex.incisiontest.fixture.SurgeonAopTargetFixture#callsHelper(int)int")
    fun aroundCallsHelper(theatre: Theatre): Any? {
        aroundHits++
        val t0 = System.nanoTime()
        val result = theatre.resume.proceed()
        lastDurationNanos = System.nanoTime() - t0
        return result
    }

    @Splice(scope = "method:top.maplex.incisiontest.fixture.SurgeonAopTargetFixture#greet(java.lang.String)java.lang.String")
    fun aroundModifyArgs(theatre: Theatre): Any? {
        aroundArgsHits++
        val newArgs = theatre.args.copyOf()
        newArgs[0] = "patched-${newArgs[0]}"
        return theatre.resume.proceed(*newArgs)
    }

    @Splice(scope = "method:top.maplex.incisiontest.fixture.SurgeonAopTargetFixture#rewriteTarget(int)int")
    fun aroundRewriteResult(theatre: Theatre): Any? {
        aroundResultHits++
        val result = theatre.resume.proceed()
        return theatre.resume.proceedResult((result as Int) + 100)
    }

    @Lead(scope = "method:top.maplex.incisiontest.fixture.SurgeonAopTargetFixture#staticEcho(java.lang.String)java.lang.String")
    @KotlinTarget(jvmStaticBridge = true)
    fun onStaticEcho(theatre: Theatre) {
        staticLeadHits++
    }

    @Lead(scope = "method:top.maplex.incisiontest.fixture.SurgeonAopTargetFixture#companionEcho(java.lang.String)java.lang.String")
    @KotlinTarget(companionInstance = true)
    fun onCompanionEcho(theatre: Theatre) {
        companionLeadHits++
    }

    fun reset() {
        beforeHits = 0
        afterHits = 0
        aroundHits = 0
        aroundArgsHits = 0
        aroundResultHits = 0
        staticLeadHits = 0
        companionLeadHits = 0
        lastDurationNanos = -1L
        lastAuditArgs = emptyList()
    }
}
