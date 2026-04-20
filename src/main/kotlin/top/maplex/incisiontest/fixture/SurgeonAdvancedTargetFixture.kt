package top.maplex.incisiontest.fixture

/**
 * [top.maplex.incisiontest.cases.SurgeonAdvancedCases] 专属 fixture。
 * 覆盖 @Excise / @Bypass / @Operation 测试场景。
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class SurgeonAdvancedTargetFixture {

    fun returnsBoolean(flag: Boolean): Boolean = !flag

    fun longCalc(a: Long, b: Long): Long = a * b
}
