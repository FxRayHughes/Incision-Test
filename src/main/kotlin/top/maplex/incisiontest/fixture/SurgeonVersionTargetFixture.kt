package top.maplex.incisiontest.fixture

/**
 * [top.maplex.incisiontest.cases.SurgeonVersionCases] 专属 fixture。
 *
 * 为每种 @Version 边界场景提供独立目标方法，避免彼此命中计数污染。
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class SurgeonVersionTargetFixture {

    // 原 A+B+C 基础用例
    fun versionedTarget(): String = "raw"
    fun noopMatcherTarget(): String = "raw"

    // #16 扩展边界覆盖（均使用 FakeVersionMatcher("2.5") 作为判据，避免依赖 NMS）
    /** 区间 [2.0, 3.0] → 必命中 */
    fun fakeMatcherInRange(): String = "raw"
    /** 区间 [3.0, 4.0] → 早于 start，不命中 */
    fun fakeMatcherBelowStart(): String = "raw"
    /** 区间 [0.1, 1.0] → 晚于 end，不命中 */
    fun fakeMatcherAboveEnd(): String = "raw"
    /** start="" end="2.5" → 边界同点（闭区间）命中 */
    fun fakeMatcherBoundaryEnd(): String = "raw"
    /** start="2.5" end="" → 边界同点（闭区间）命中 */
    fun fakeMatcherBoundaryStart(): String = "raw"
    /** matcher fqcn 写错，应回落 NoopVersionMatcher，始终命中 */
    fun missingMatcherFallback(): String = "raw"
    /** 同名多版本 advice：三个 @Lead 在 [0,1]/[2,3]/[4,5]，FakeVersionMatcher("2.5") → 只有中段命中 */
    fun multiRangeByIndex(): String = "raw"
}
