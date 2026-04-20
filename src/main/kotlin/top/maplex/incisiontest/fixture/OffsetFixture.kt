package top.maplex.incisiontest.fixture

/**
 * [top.maplex.incisiontest.cases.SurgeonOffsetCases] 专属 fixture。
 *
 * [chain] 顺序调用 [helper] 三次，便于按 ordinal / shift / offset 锚点切割不同位置。
 * [chainWithGap] 在每次调用之间塞入若干无关指令，方便测 offset=+1/+2/-1 对锚点的偏移。
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class OffsetFixture {

    var helperHits: Int = 0
    var counter: Int = 0

    fun helper(v: Int): Int {
        helperHits++
        return v + 1
    }

    /** 三次连续 helper 调用 — 测 ordinal=0/1/2 与 BEFORE/AFTER shift。 */
    fun chain(): Int {
        val a = helper(1)
        val b = helper(2)
        val c = helper(3)
        return a + b + c
    }

    /**
     * 每次 helper 调用后插入 NOP 等无关指令（PUTFIELD / GETFIELD / IADD），
     * 用于检验 offset=+1/+2 沿 AFTER 方向跨越的指令数是否生效。
     */
    fun chainWithGap(): Int {
        val a = helper(1)
        counter = counter + 1            // PUTFIELD; GETFIELD; ICONST_1; IADD; PUTFIELD
        val b = helper(2)
        counter = counter + 2            // 同上
        val c = helper(3)
        counter = counter + 3
        return a + b + c
    }
}
