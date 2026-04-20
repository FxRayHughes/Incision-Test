package top.maplex.incisiontest.fixture

/**
 * [top.maplex.incisiontest.cases.SurgeonInsnPatternCases] 专属 fixture。
 *
 * [accumulate] 中含特征指令序列：`ICONST_5; ISTORE; ILOAD; IADD`，
 * 对应 InsnPattern 子任务（Op.ICONST_5 → ISTORE → ILOAD → IADD）的最小可识别样本。
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class InsnPatternFixture {

    var hits: Int = 0

    fun accumulate(input: Int): Int {
        hits++
        val five = 5            // ICONST_5; ISTORE
        return input + five     // ILOAD; ILOAD; IADD
    }
}
