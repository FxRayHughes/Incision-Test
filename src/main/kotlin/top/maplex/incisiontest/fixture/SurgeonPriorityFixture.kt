package top.maplex.incisiontest.fixture

/**
 * @Surgeon(priority) 默认/0/正/负 四档协作目标基座。
 *
 * 四个 surgeon object（默认值、显式 0、正数 30、负数 -10）各自挂一条 advice，
 * 期望按 priority 全局排序：30 → 0(默认/显式) → -10。
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class SurgeonPriorityFixture {

    /** 多个 surgeon object 共同织入 —— 用于跨 priority 排序断言。 */
    fun pSharedTarget(): MutableList<String> = mutableListOf()
}
