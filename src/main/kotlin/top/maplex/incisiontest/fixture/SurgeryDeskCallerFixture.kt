package top.maplex.incisiontest.fixture

/**
 * @SurgeryDesk 多层调用基座 —— 验证标记在不同调用层都能通过 Scalpel.transient 栈帧检查。
 *
 * 这里只是普通可被织入的目标方法；@SurgeryDesk 注解作用于调用方 object，本 fixture
 * 由 [top.maplex.incisiontest.cases.SurgeryDeskMatrixCases] 在 entry / 嵌套层调用 transient。
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class SurgeryDeskCallerFixture {

    @Volatile var hits: Int = 0

    fun observable(input: Int): Int {
        hits++
        return input * 2
    }
}
