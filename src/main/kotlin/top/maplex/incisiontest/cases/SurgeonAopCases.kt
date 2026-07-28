package top.maplex.incisiontest.cases

import taboolib.module.incision.annotation.MatchMode

import taboolib.module.incision.annotation.SelectorKind

import taboolib.module.incision.annotation.Selector

import taboolib.module.incision.annotation.Pointcut

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

    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "top/maplex/incisiontest/fixture/SurgeonAopTargetFixture", name = "touchesField", descriptor = "()I")]))
    fun beforeTouchField(theatre: Theatre) {
        beforeHits++
    }

    @Trail(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "top/maplex/incisiontest/fixture/SurgeonAopTargetFixture", name = "multiArg", descriptor = "(ILjava/lang/String;D)Ljava/lang/String;")]))
    fun afterMultiArg(theatre: Theatre) {
        afterHits++
        lastAuditArgs = theatre.args.toList()
    }

    @Splice(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "top/maplex/incisiontest/fixture/SurgeonAopTargetFixture", name = "callsHelper", descriptor = "(I)I")]))
    fun aroundCallsHelper(theatre: Theatre): Any? {
        aroundHits++
        val t0 = System.nanoTime()
        val result = theatre.resume.proceed()
        lastDurationNanos = System.nanoTime() - t0
        return result
    }

    @Splice(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "top/maplex/incisiontest/fixture/SurgeonAopTargetFixture", name = "greet", descriptor = "(Ljava/lang/String;)Ljava/lang/String;")]))
    fun aroundModifyArgs(theatre: Theatre): Any? {
        aroundArgsHits++
        val newArgs = theatre.args.copyOf()
        newArgs[0] = "patched-${newArgs[0]}"
        return theatre.resume.proceed(*newArgs)
    }

    @Splice(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "top/maplex/incisiontest/fixture/SurgeonAopTargetFixture", name = "rewriteTarget", descriptor = "(I)I")]))
    fun aroundRewriteResult(theatre: Theatre): Any? {
        aroundResultHits++
        val result = theatre.resume.proceed()
        return theatre.resume.proceedResult((result as Int) + 100)
    }

    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "top/maplex/incisiontest/fixture/SurgeonAopTargetFixture", name = "staticEcho", descriptor = "(Ljava/lang/String;)Ljava/lang/String;")]))
    @KotlinTarget(jvmStaticBridge = true)
    fun onStaticEcho(theatre: Theatre) {
        staticLeadHits++
    }

    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "top/maplex/incisiontest/fixture/SurgeonAopTargetFixture", name = "companionEcho", descriptor = "(Ljava/lang/String;)Ljava/lang/String;")]))
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
