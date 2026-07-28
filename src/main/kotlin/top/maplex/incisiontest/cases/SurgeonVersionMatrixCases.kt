package top.maplex.incisiontest.cases

import taboolib.module.incision.annotation.MatchMode

import taboolib.module.incision.annotation.SelectorKind

import taboolib.module.incision.annotation.Selector

import taboolib.module.incision.annotation.Pointcut

import taboolib.module.incision.annotation.Bypass
import taboolib.module.incision.annotation.Excise
import taboolib.module.incision.annotation.Graft
import taboolib.module.incision.annotation.Lead
import taboolib.module.incision.annotation.Site
import taboolib.module.incision.annotation.Splice
import taboolib.module.incision.annotation.Surgeon
import taboolib.module.incision.annotation.Trail
import taboolib.module.incision.annotation.Trim
import taboolib.module.incision.annotation.Version
import taboolib.module.incision.api.Anchor
import taboolib.module.incision.api.Theatre

/**
 * #16 扩展矩阵 —— 全 advice 类型 × @Version 参数边界覆盖。
 *
 * 目标：
 * 1. 每个 advice 注解（@Lead/@Trail/@Splice/@Excise/@Bypass/@Graft/@Trim）至少 1 个 @Version 用例。
 * 2. @Version 三字段 (start/end/matcher) 的所有典型变种。
 * 3. matcher fqcn 来源：空（MC 默认）/ Noop 显式 / 自定义 Fake / 不存在的 fqcn。
 * 4. 段比较：不同段数、单点、反向区间、微步。
 *
 * 全部用例依赖 FakeVersionMatcher("2.5")，不依赖真实 NMS 环境。
 */
@Surgeon
object SurgeonVersionMatrixCases {

    private const val FIX = "top.maplex.incisiontest.fixture.SurgeonVersionMatrixFixture"
    // ---- 命中计数 ----
    @Volatile var bothEmptyHits = 0
    @Volatile var onlyEndGivenHits = 0
    @Volatile var onlyStartGivenHits = 0
    @Volatile var singlePointHits = 0
    @Volatile var invertedRangeHits = 0

    @Volatile var leadHits = 0
    @Volatile var trailHits = 0
    @Volatile var spliceHits = 0
    @Volatile var exciseHits = 0
    @Volatile var bypassHits = 0
    @Volatile var graftHits = 0
    @Volatile var trimReturnHits = 0

    @Volatile var defaultMatcherHits = 0
    @Volatile var explicitNoopHits = 0
    @Volatile var fakeMatcherNoBoundsHits = 0

    @Volatile var segLongerStartHits = 0
    @Volatile var segLongerEndHits = 0
    @Volatile var microStepBelowHits = 0
    @Volatile var microStepAboveHits = 0

    fun reset() {
        bothEmptyHits = 0; onlyEndGivenHits = 0; onlyStartGivenHits = 0
        singlePointHits = 0; invertedRangeHits = 0
        leadHits = 0; trailHits = 0; spliceHits = 0; exciseHits = 0
        bypassHits = 0; graftHits = 0; trimReturnHits = 0
        defaultMatcherHits = 0; explicitNoopHits = 0; fakeMatcherNoBoundsHits = 0
        segLongerStartHits = 0; segLongerEndHits = 0
        microStepBelowHits = 0; microStepAboveHits = 0
    }

    // ---- 端点空缺矩阵 ----

    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "bothEmpty", descriptor = "()Ljava/lang/String;")]))
    @Version(matcher = top.maplex.incisiontest.api.FakeVersionMatcher::class)
    fun onBothEmpty(theatre: Theatre) { bothEmptyHits++ }

    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "onlyEndGiven", descriptor = "()Ljava/lang/String;")]))
    @Version(end = "3.0", matcher = top.maplex.incisiontest.api.FakeVersionMatcher::class)
    fun onOnlyEndGiven(theatre: Theatre) { onlyEndGivenHits++ }

    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "onlyStartGiven", descriptor = "()Ljava/lang/String;")]))
    @Version(start = "1.0", matcher = top.maplex.incisiontest.api.FakeVersionMatcher::class)
    fun onOnlyStartGiven(theatre: Theatre) { onlyStartGivenHits++ }

    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "singlePoint", descriptor = "()Ljava/lang/String;")]))
    @Version(start = "2.5", end = "2.5", matcher = top.maplex.incisiontest.api.FakeVersionMatcher::class)
    fun onSinglePoint(theatre: Theatre) { singlePointHits++ }

    /** start>end 非法：matches 应判定为 false，advice 被剔除 */
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "invertedRange", descriptor = "()Ljava/lang/String;")]))
    @Version(start = "9.0", end = "1.0", matcher = top.maplex.incisiontest.api.FakeVersionMatcher::class)
    fun onInvertedRange(theatre: Theatre) { invertedRangeHits++ }

    // ---- 每种 advice 注解的 @Version 可用性 ----

    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "leadTarget", descriptor = "()Ljava/lang/String;")]))
    @Version(start = "2.0", end = "3.0", matcher = top.maplex.incisiontest.api.FakeVersionMatcher::class)
    fun onLeadVersioned(theatre: Theatre) { leadHits++ }

    @Trail(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "trailTarget", descriptor = "()Ljava/lang/String;")]))
    @Version(start = "2.0", end = "3.0", matcher = top.maplex.incisiontest.api.FakeVersionMatcher::class)
    fun onTrailVersioned(theatre: Theatre) { trailHits++ }

    @Splice(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "spliceTarget", descriptor = "()Ljava/lang/String;")]))
    @Version(start = "2.0", end = "3.0", matcher = top.maplex.incisiontest.api.FakeVersionMatcher::class)
    fun onSpliceVersioned(theatre: Theatre): Any? {
        spliceHits++
        theatre.resume.proceed()
        return null
    }

    @Excise(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "exciseTarget", descriptor = "()Ljava/lang/String;")]))
    @Version(start = "2.0", end = "3.0", matcher = top.maplex.incisiontest.api.FakeVersionMatcher::class)
    fun onExciseVersioned(theatre: Theatre) { exciseHits++ }

    @Bypass(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "bypassCallerTarget", descriptor = "()I")]),
        site = Site(anchor = Anchor.INVOKE, target = Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "helperForBypass", descriptor = "()I")),
    )
    @Version(start = "2.0", end = "3.0", matcher = top.maplex.incisiontest.api.FakeVersionMatcher::class)
    fun onBypassVersioned(theatre: Theatre): Any? {
        bypassHits++
        return 1000
    }

    @Graft(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "graftCallerTarget", descriptor = "()I")]),
        site = Site(anchor = Anchor.NEW, target = Selector(kind = SelectorKind.CLASS, owner = "java/lang/StringBuilder")),
    )
    @Version(start = "2.0", end = "3.0", matcher = top.maplex.incisiontest.api.FakeVersionMatcher::class)
    fun onGraftVersioned(theatre: Theatre) { graftHits++ }

    @Trim(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "trimReturnTarget", descriptor = "()I")]), kind = Trim.Kind.RETURN)
    @Version(start = "2.0", end = "3.0", matcher = top.maplex.incisiontest.api.FakeVersionMatcher::class)
    fun onTrimReturnVersioned(theatre: Theatre): Any? {
        trimReturnHits++
        return 777
    }

    // ---- matcher fqcn 来源矩阵 ----

    /** 默认 matcher 使用真实 Minecraft 版本；未知版本与区间外版本都必须 fail-closed。 */
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "defaultMatcherTarget", descriptor = "()Ljava/lang/String;")]))
    @Version(start = "2.0", end = "3.0")
    fun onDefaultMatcher(theatre: Theatre) { defaultMatcherHits++ }

    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "explicitNoopMatcherTarget", descriptor = "()Ljava/lang/String;")]))
    @Version(start = "1.0", end = "1.5", matcher = top.maplex.incisiontest.api.PluginNoopVersionMatcher::class)
    fun onExplicitNoop(theatre: Theatre) { explicitNoopHits++ }

    /** Fake matcher 但不给区间 → matches 双空端点 → 命中 */
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "fakeMatcherNoBoundsTarget", descriptor = "()Ljava/lang/String;")]))
    @Version(matcher = top.maplex.incisiontest.api.FakeVersionMatcher::class)
    fun onFakeNoBounds(theatre: Theatre) { fakeMatcherNoBoundsHits++ }

    // ---- 段比较矩阵 ----

    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "segmentLongerStart", descriptor = "()Ljava/lang/String;")]))
    @Version(start = "2.5.0", end = "3.0", matcher = top.maplex.incisiontest.api.FakeVersionMatcher::class)
    fun onSegLongerStart(theatre: Theatre) { segLongerStartHits++ }

    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "segmentLongerEnd", descriptor = "()Ljava/lang/String;")]))
    @Version(start = "2.0", end = "2.5.9", matcher = top.maplex.incisiontest.api.FakeVersionMatcher::class)
    fun onSegLongerEnd(theatre: Theatre) { segLongerEndHits++ }

    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "microStepBelow", descriptor = "()Ljava/lang/String;")]))
    @Version(start = "2.6", end = "3.0", matcher = top.maplex.incisiontest.api.FakeVersionMatcher::class)
    fun onMicroStepBelow(theatre: Theatre) { microStepBelowHits++ }

    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "microStepAbove", descriptor = "()Ljava/lang/String;")]))
    @Version(start = "2.0", end = "2.4", matcher = top.maplex.incisiontest.api.FakeVersionMatcher::class)
    fun onMicroStepAbove(theatre: Theatre) { microStepAboveHits++ }
}
