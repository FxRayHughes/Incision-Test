package top.maplex.incisiontest.fixture

/**
 * @Operation(enabled × priority) 8 格矩阵基座。
 *
 * 行：enabled = true / false
 * 列：priority = -10 / 0 / 5 / 100
 *
 * 共 8 个目标方法，分别承载唯一一条 advice。
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class OperationEnabledPriorityFixture {

    fun cellTPriNeg10(): Int = 1
    fun cellTPri0(): Int = 2
    fun cellTPri5(): Int = 3
    fun cellTPri100(): Int = 4
    fun cellFPriNeg10(): Int = 5
    fun cellFPri0(): Int = 6
    fun cellFPri5(): Int = 7
    fun cellFPri100(): Int = 8

    /** 同 priority（=42）多 advice 稳定顺序断言专用目标。 */
    fun samePriorityTarget(): MutableList<String> = mutableListOf()

    /** disable→resume→suspend 循环专用目标。 */
    fun cycleTarget(): Int = 99
}
