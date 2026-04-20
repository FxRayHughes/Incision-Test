package top.maplex.incisiontest.cases

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
    private const val FAKE = "top.maplex.incisiontest.api.FakeVersionMatcher"
    private const val NOOP = "top.maplex.incisiontest.api.PluginNoopVersionMatcher"

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

    @Lead(scope = "method:$FIX#bothEmpty()java.lang.String")
    @Version(matcher = FAKE)
    fun onBothEmpty(theatre: Theatre) { bothEmptyHits++ }

    @Lead(scope = "method:$FIX#onlyEndGiven()java.lang.String")
    @Version(end = "3.0", matcher = FAKE)
    fun onOnlyEndGiven(theatre: Theatre) { onlyEndGivenHits++ }

    @Lead(scope = "method:$FIX#onlyStartGiven()java.lang.String")
    @Version(start = "1.0", matcher = FAKE)
    fun onOnlyStartGiven(theatre: Theatre) { onlyStartGivenHits++ }

    @Lead(scope = "method:$FIX#singlePoint()java.lang.String")
    @Version(start = "2.5", end = "2.5", matcher = FAKE)
    fun onSinglePoint(theatre: Theatre) { singlePointHits++ }

    /** start>end 非法：matches 应判定为 false，advice 被剔除 */
    @Lead(scope = "method:$FIX#invertedRange()java.lang.String")
    @Version(start = "9.0", end = "1.0", matcher = FAKE)
    fun onInvertedRange(theatre: Theatre) { invertedRangeHits++ }

    // ---- 每种 advice 注解的 @Version 可用性 ----

    @Lead(scope = "method:$FIX#leadTarget()java.lang.String")
    @Version(start = "2.0", end = "3.0", matcher = FAKE)
    fun onLeadVersioned(theatre: Theatre) { leadHits++ }

    @Trail(scope = "method:$FIX#trailTarget()java.lang.String")
    @Version(start = "2.0", end = "3.0", matcher = FAKE)
    fun onTrailVersioned(theatre: Theatre) { trailHits++ }

    @Splice(scope = "method:$FIX#spliceTarget()java.lang.String")
    @Version(start = "2.0", end = "3.0", matcher = FAKE)
    fun onSpliceVersioned(theatre: Theatre): Any? {
        spliceHits++
        theatre.resume.proceed()
        return null
    }

    @Excise(scope = "method:$FIX#exciseTarget()java.lang.String")
    @Version(start = "2.0", end = "3.0", matcher = FAKE)
    fun onExciseVersioned(theatre: Theatre) { exciseHits++ }

    @Bypass(
        method = "$FIX#bypassCallerTarget()int",
        site = Site(anchor = Anchor.INVOKE, target = "$FIX#helperForBypass()int"),
    )
    @Version(start = "2.0", end = "3.0", matcher = FAKE)
    fun onBypassVersioned(theatre: Theatre): Any? {
        bypassHits++
        return 1000
    }

    @Graft(
        method = "$FIX#graftCallerTarget()int",
        site = Site(anchor = Anchor.NEW, target = "java.lang.StringBuilder"),
    )
    @Version(start = "2.0", end = "3.0", matcher = FAKE)
    fun onGraftVersioned(theatre: Theatre) { graftHits++ }

    @Trim(method = "$FIX#trimReturnTarget()int", kind = Trim.Kind.RETURN)
    @Version(start = "2.0", end = "3.0", matcher = FAKE)
    fun onTrimReturnVersioned(theatre: Theatre): Any? {
        trimReturnHits++
        return 777
    }

    // ---- matcher fqcn 来源矩阵 ----

    /** 空 fqcn —— 非 NMS 环境 MC matcher current() 多半为 null，matches 应判定 false */
    @Lead(scope = "method:$FIX#defaultMatcherTarget()java.lang.String")
    @Version(start = "2.0", end = "3.0")
    fun onDefaultMatcher(theatre: Theatre) { defaultMatcherHits++ }

    @Lead(scope = "method:$FIX#explicitNoopMatcherTarget()java.lang.String")
    @Version(start = "1.0", end = "1.5", matcher = NOOP)
    fun onExplicitNoop(theatre: Theatre) { explicitNoopHits++ }

    /** Fake matcher 但不给区间 → matches 双空端点 → 命中 */
    @Lead(scope = "method:$FIX#fakeMatcherNoBoundsTarget()java.lang.String")
    @Version(matcher = FAKE)
    fun onFakeNoBounds(theatre: Theatre) { fakeMatcherNoBoundsHits++ }

    // ---- 段比较矩阵 ----

    @Lead(scope = "method:$FIX#segmentLongerStart()java.lang.String")
    @Version(start = "2.5.0", end = "3.0", matcher = FAKE)
    fun onSegLongerStart(theatre: Theatre) { segLongerStartHits++ }

    @Lead(scope = "method:$FIX#segmentLongerEnd()java.lang.String")
    @Version(start = "2.0", end = "2.5.9", matcher = FAKE)
    fun onSegLongerEnd(theatre: Theatre) { segLongerEndHits++ }

    @Lead(scope = "method:$FIX#microStepBelow()java.lang.String")
    @Version(start = "2.6", end = "3.0", matcher = FAKE)
    fun onMicroStepBelow(theatre: Theatre) { microStepBelowHits++ }

    @Lead(scope = "method:$FIX#microStepAbove()java.lang.String")
    @Version(start = "2.0", end = "2.4", matcher = FAKE)
    fun onMicroStepAbove(theatre: Theatre) { microStepAboveHits++ }
}
