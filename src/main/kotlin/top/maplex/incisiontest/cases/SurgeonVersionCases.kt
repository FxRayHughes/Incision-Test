package top.maplex.incisiontest.cases

import taboolib.module.incision.annotation.Lead
import taboolib.module.incision.annotation.Splice
import taboolib.module.incision.annotation.Surgeon
import taboolib.module.incision.annotation.Version
import taboolib.module.incision.api.Theatre

/**
 * Remap A+B+C 综合演示 + #16 扩展覆盖。
 *
 * A/B/C 基础：
 * - A. NMS 方法名 remap —— [onNmsGetPlayerCount] 切入 MinecraftServer#getPlayerCount
 * - B. @Version 区间过滤 —— [onLegacy]/[onModern]/[onFuture] 三段真实版本
 * - C. NoopVersionMatcher 显式 fqcn —— [onNoop]
 *
 * #16 扩展覆盖（全部依赖 FakeVersionMatcher("2.5")，不需要 NMS 环境）：
 * - [onFakeInRange]            : 区间 [2.0, 3.0] 内
 * - [onFakeBelowStart]         : current=2.5 < start=3.0，不命中
 * - [onFakeAboveEnd]           : current=2.5 > end=1.0，不命中
 * - [onFakeBoundaryEnd]        : start="" end="2.5"，含端点
 * - [onFakeBoundaryStart]      : start="2.5" end=""，含端点
 * - [onMissingMatcherFallback] : matcher fqcn 写错，回退 Noop，始终命中
 * - [onMultiRange0..2]         : 同名三段区间，仅中段命中
 */
@Surgeon
object SurgeonVersionCases {

    @Volatile var legacyHits = 0
    @Volatile var modernHits = 0
    @Volatile var futureHits = 0
    @Volatile var noopHits = 0
    @Volatile var nmsRemapHits = 0

    @Volatile var fakeInRangeHits = 0
    @Volatile var fakeBelowStartHits = 0
    @Volatile var fakeAboveEndHits = 0
    @Volatile var fakeBoundaryEndHits = 0
    @Volatile var fakeBoundaryStartHits = 0
    @Volatile var missingMatcherHits = 0
    @Volatile var multiRangeIdx0Hits = 0
    @Volatile var multiRangeIdx1Hits = 0
    @Volatile var multiRangeIdx2Hits = 0

    fun reset() {
        legacyHits = 0; modernHits = 0; futureHits = 0; noopHits = 0; nmsRemapHits = 0
        fakeInRangeHits = 0; fakeBelowStartHits = 0; fakeAboveEndHits = 0
        fakeBoundaryEndHits = 0; fakeBoundaryStartHits = 0; missingMatcherHits = 0
        multiRangeIdx0Hits = 0; multiRangeIdx1Hits = 0; multiRangeIdx2Hits = 0
    }

    // ---- 基础 A+B+C（保留） ----

    @Lead(scope = "method:top.maplex.incisiontest.fixture.SurgeonVersionTargetFixture#versionedTarget()java.lang.String")
    @Version(start = "1.8", end = "1.16")
    fun onLegacy(theatre: Theatre) { legacyHits++ }

    @Lead(scope = "method:top.maplex.incisiontest.fixture.SurgeonVersionTargetFixture#versionedTarget()java.lang.String")
    @Version(start = "1.17", end = "1.21")
    fun onModern(theatre: Theatre) { modernHits++ }

    @Lead(scope = "method:top.maplex.incisiontest.fixture.SurgeonVersionTargetFixture#versionedTarget()java.lang.String")
    @Version(start = "26.1")
    fun onFuture(theatre: Theatre) { futureHits++ }

    @Splice(scope = "method:top.maplex.incisiontest.fixture.SurgeonVersionTargetFixture#noopMatcherTarget()java.lang.String")
    @Version(matcher = "top.maplex.incisiontest.api.PluginNoopVersionMatcher")
    fun onNoop(theatre: Theatre): String? {
        noopHits++
        return "noop-replaced"
    }

    @Lead(scope = "method:net.minecraft.server.MinecraftServer#getPlayerCount()int")
    fun onNmsGetPlayerCount(theatre: Theatre) { nmsRemapHits++ }

    // ---- #16 扩展：FakeVersionMatcher("2.5") 驱动的边界覆盖 ----

    @Lead(scope = "method:top.maplex.incisiontest.fixture.SurgeonVersionTargetFixture#fakeMatcherInRange()java.lang.String")
    @Version(start = "2.0", end = "3.0", matcher = "top.maplex.incisiontest.api.FakeVersionMatcher")
    fun onFakeInRange(theatre: Theatre) { fakeInRangeHits++ }

    @Lead(scope = "method:top.maplex.incisiontest.fixture.SurgeonVersionTargetFixture#fakeMatcherBelowStart()java.lang.String")
    @Version(start = "3.0", end = "4.0", matcher = "top.maplex.incisiontest.api.FakeVersionMatcher")
    fun onFakeBelowStart(theatre: Theatre) { fakeBelowStartHits++ }

    @Lead(scope = "method:top.maplex.incisiontest.fixture.SurgeonVersionTargetFixture#fakeMatcherAboveEnd()java.lang.String")
    @Version(start = "0.1", end = "1.0", matcher = "top.maplex.incisiontest.api.FakeVersionMatcher")
    fun onFakeAboveEnd(theatre: Theatre) { fakeAboveEndHits++ }

    @Lead(scope = "method:top.maplex.incisiontest.fixture.SurgeonVersionTargetFixture#fakeMatcherBoundaryEnd()java.lang.String")
    @Version(end = "2.5", matcher = "top.maplex.incisiontest.api.FakeVersionMatcher")
    fun onFakeBoundaryEnd(theatre: Theatre) { fakeBoundaryEndHits++ }

    @Lead(scope = "method:top.maplex.incisiontest.fixture.SurgeonVersionTargetFixture#fakeMatcherBoundaryStart()java.lang.String")
    @Version(start = "2.5", matcher = "top.maplex.incisiontest.api.FakeVersionMatcher")
    fun onFakeBoundaryStart(theatre: Theatre) { fakeBoundaryStartHits++ }

    @Lead(scope = "method:top.maplex.incisiontest.fixture.SurgeonVersionTargetFixture#missingMatcherFallback()java.lang.String")
    @Version(matcher = "top.maplex.incisiontest.api.ThisMatcherDoesNotExist")
    fun onMissingMatcherFallback(theatre: Theatre) { missingMatcherHits++ }

    // 同名多版本：三段区间挂同一目标，仅 current="2.5" 命中中段
    @Lead(scope = "method:top.maplex.incisiontest.fixture.SurgeonVersionTargetFixture#multiRangeByIndex()java.lang.String")
    @Version(start = "0", end = "1", matcher = "top.maplex.incisiontest.api.FakeVersionMatcher")
    fun onMultiRange0(theatre: Theatre) { multiRangeIdx0Hits++ }

    @Lead(scope = "method:top.maplex.incisiontest.fixture.SurgeonVersionTargetFixture#multiRangeByIndex()java.lang.String")
    @Version(start = "2", end = "3", matcher = "top.maplex.incisiontest.api.FakeVersionMatcher")
    fun onMultiRange1(theatre: Theatre) { multiRangeIdx1Hits++ }

    @Lead(scope = "method:top.maplex.incisiontest.fixture.SurgeonVersionTargetFixture#multiRangeByIndex()java.lang.String")
    @Version(start = "4", end = "5", matcher = "top.maplex.incisiontest.api.FakeVersionMatcher")
    fun onMultiRange2(theatre: Theatre) { multiRangeIdx2Hits++ }
}
