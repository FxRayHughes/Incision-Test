package top.maplex.incisiontest.fixture

/**
 * 极值 @Surgeon(priority) + 三 surgeon 全局排序基座。
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class SurgeonPriorityExtremeFixture {

    /** 极大 priority 单 advice 命中目标 */
    fun extremeMaxTarget(): Int = 1

    /** 极小 priority 单 advice 命中目标 */
    fun extremeMinTarget(): Int = 2

    /** 三 Surgeon 跨 object 排序共享目标 */
    fun threeSurgeonTarget(): MutableList<String> = mutableListOf()
}
