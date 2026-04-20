package top.maplex.incisiontest.fixture

/**
 * [top.maplex.incisiontest.cases.SurgeonAopCases] 专属 fixture。
 *
 * 注意内部拆分：
 * - `callsHelper` / `internalHelper` 是 aroundCallsHelper 路径，`internalHelper`
 *   不被任何 advice 织入，确保 proceed() 后的原逻辑不会被其他 splice 再次改写。
 * - `rewriteTarget` 专门给 aroundRewriteResult 使用，避免与 aroundCallsHelper
 *   的内部依赖交叉。
 * - `greet` 专门给 aroundModifyArgs 使用，与 TargetFixture.greet 的瞬态测试解耦。
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class SurgeonAopTargetFixture {

    var fieldHits: Int = 0

    fun touchesField(): Int {
        fieldHits++
        return fieldHits
    }

    fun multiArg(a: Int, b: String, c: Double): String = "$a-$b-$c"

    /** aroundCallsHelper 目标。内部调用 internalHelper（不被 advice 织入）。 */
    fun callsHelper(x: Int): Int = internalHelper(x) + 1

    /** 不被任何 advice 织入，用于隔离 aroundCallsHelper 的 proceed 路径。 */
    fun internalHelper(x: Int): Int = x * 10

    /** aroundModifyArgs 目标。 */
    fun greet(name: String): String {
        fieldHits++
        return "hello, $name"
    }

    /** aroundRewriteResult 目标。 */
    fun rewriteTarget(x: Int): Int = x * 10

    companion object {
        @JvmStatic
        var staticCounter: Int = 0

        var companionHits: Int = 0

        @JvmStatic
        fun staticEcho(v: String): String {
            staticCounter++
            return "static:$v"
        }

        fun companionEcho(v: String): String {
            companionHits++
            return "companion:$v"
        }
    }
}
