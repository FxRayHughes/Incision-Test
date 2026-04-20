package top.maplex.incisiontest.fixture

/**
 * 被织入的目标类。
 * 注意：本类不能是 @SurgeryDesk object，且必须有可被 retransform 的非 abstract / native 方法。
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class TargetFixture {

    var fieldHits: Int = 0
    var lastArg: Any? = null

    fun greet(name: String): String {
        fieldHits++
        return "hello, $name"
    }

    fun add(a: Int, b: Int): Int = a + b

    fun longCalc(a: Long, b: Long): Long = a * b

    fun voidNoArg() {
        fieldHits++
    }

    fun returnsBoolean(flag: Boolean): Boolean = !flag

    fun mayThrow(throwIt: Boolean): String {
        if (throwIt) error("boom")
        return "ok"
    }

    fun multiArg(a: Int, b: String, c: Double): String = "$a-$b-$c"

    fun callsHelper(x: Int): Int {
        return helper(x) + 1
    }

    fun helper(x: Int): Int = x * 10

    fun touchesField(): Int {
        fieldHits++
        return fieldHits
    }

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
