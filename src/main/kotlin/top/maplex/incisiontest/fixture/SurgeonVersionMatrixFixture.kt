package top.maplex.incisiontest.fixture

/**
 * 第二基座 fixture —— 给 #16 扩展矩阵提供独立目标方法。
 *
 * 每个方法只承载一种 @Version 参数变种，避免 advice 间命中污染。
 * 本 fixture 全部使用 FakeVersionMatcher("2.5") 作为判据。
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class SurgeonVersionMatrixFixture {

    // ------- 端点空缺矩阵 -------
    /** 双空端点：无过滤，始终命中 */
    fun bothEmpty(): String = "raw"
    /** 仅 start 空：end=3.0，current=2.5 命中 */
    fun onlyEndGiven(): String = "raw"
    /** 仅 end 空：start=1.0，current=2.5 命中 */
    fun onlyStartGiven(): String = "raw"
    /** start=end 单点命中（=2.5） */
    fun singlePoint(): String = "raw"
    /** start>end 非法区间：matches 实现应判定不命中 */
    fun invertedRange(): String = "raw"

    // ------- advice 类型矩阵（每种 advice 至少一例 @Version） -------
    fun leadTarget(): String = "raw"
    fun trailTarget(): String = "raw"
    fun spliceTarget(): String = "raw"
    fun exciseTarget(): String = "raw"
    fun bypassCallerTarget(): Int = helperForBypass() + 1
    fun helperForBypass(): Int = 10
    fun graftCallerTarget(): Int { val sb = StringBuilder(); sb.append("x"); return sb.length }
    fun trimReturnTarget(): Int = 7

    // ------- matcher fqcn 来源矩阵 -------
    /** 空 fqcn → 默认 MinecraftVersionMatcher（非 NMS 环境 current() 可能为 null） */
    fun defaultMatcherTarget(): String = "raw"
    /** 显式指定 NoopVersionMatcher fqcn */
    fun explicitNoopMatcherTarget(): String = "raw"
    /** 指定 Fake 但 start/end 完全空 */
    fun fakeMatcherNoBoundsTarget(): String = "raw"

    // ------- 版本段比较矩阵 -------
    /** 不同段长：current=2.5，start=2.5.0 → 语义相等，应命中 */
    fun segmentLongerStart(): String = "raw"
    /** 不同段长：current=2.5，end=2.5.9 → 2.5 < 2.5.9，命中 */
    fun segmentLongerEnd(): String = "raw"
    /** current=2.5 刚好 < start=2.6，不命中 */
    fun microStepBelow(): String = "raw"
    /** current=2.5 刚好 > end=2.4，不命中 */
    fun microStepAbove(): String = "raw"
}
