package top.maplex.incisiontest.fixture

/**
 * Incision 运行时开销基准目标。
 *
 * 每个公开方法都执行相同的 `value + 1` 业务逻辑，差异只来自对应织入类型。基线方法不得添加
 * advice，否则相对开销失去意义；普通 helper 则专门保留 INVOKE 指令供 Site 基准定位。
 */
class PerformanceFixture {

    /** 独立安装探针，不参与任何计时样本。 */
    fun probe(value: Int): Int = value + 1

    fun baseline(value: Int): Int = value + 1

    fun lead(value: Int): Int = value + 1

    fun leadTrail(value: Int): Int = value + 1

    fun predicate(value: Int): Int = value + 1

    fun splice(value: Int): Int = value + 1

    fun siteInvoke(value: Int): Int = helper(value)

    private fun helper(value: Int): Int = value + 1
}
