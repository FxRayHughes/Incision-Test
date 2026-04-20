package top.maplex.incisiontest.fixture

/**
 * [top.maplex.incisiontest.cases.SurgeonSiteCases] 专属 fixture。
 *
 * 专为 @Site / @Graft / @Bypass / @Trim 等锚点场景设计：
 * 每个方法内部都有可被锚定的子结构（INVOKE / FIELD_GET / FIELD_PUT / NEW / THROW）。
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class SurgeonSiteTargetFixture {

    @Volatile var counter: Int = 0
    @Volatile var lastMessage: String = "none"

    /** 内部辅助方法 —— 将被 @Graft / @Bypass 作为 INVOKE 锚点 */
    fun helper(x: Int): Int = x + 1

    /** 内部调用 helper 两次 —— 用于验证 INVOKE 锚点命中次数 */
    fun invokeHelper(x: Int): Int {
        val a = helper(x)
        val b = helper(x + 10)
        return a + b
    }

    /** 读字段 —— 用于 FIELD_GET 锚点 */
    fun readCounter(): Int {
        return counter
    }

    /** 写字段 —— 用于 FIELD_PUT 锚点 */
    fun writeCounter(v: Int) {
        counter = v
    }

    /** 构造对象 —— 用于 NEW 锚点 */
    fun allocate(): StringBuilder {
        return StringBuilder("allocated")
    }

    /** 抛异常 —— 用于 THROW 锚点（外层捕获） */
    fun throwIt(): String {
        try {
            throw RuntimeException("expected-throw")
        } catch (e: RuntimeException) {
            return "caught:${e.message}"
        }
    }

    /** 正常 return —— 用于 RETURN 锚点 / @Trim RETURN */
    fun plainReturn(): Int {
        return 10
    }

    /** 单参方法 —— 用于 @Trim ARG */
    fun echo(msg: String): String {
        lastMessage = msg
        return "echo:$msg"
    }

    fun reset() {
        counter = 0
        lastMessage = "none"
    }
}
