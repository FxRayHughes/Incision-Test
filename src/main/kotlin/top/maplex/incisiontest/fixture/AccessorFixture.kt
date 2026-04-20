package top.maplex.incisiontest.fixture

/**
 * [top.maplex.incisiontest.cases.AccessorCases] 专属 fixture。
 *
 * 包含各种访问级别和修饰符的字段/方法，用于测试 IncisionAccessor / Lambda 工厂 / Theatre DSL。
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class AccessorFixture {

    // ---- 实例字段 ----
    private val privateFinalName: String = "incision"
    private var privateMutableCount: Int = 0
    protected val protectedValue: Double = 3.14
    internal val internalTag: String = "internal-tag"
    val publicLabel: String = "public-label"

    // ---- 方法 ----
    private fun privateAdd(a: Int, b: Int): Int = a + b

    private fun privateGreet(name: String): String = "hello, $name"

    fun publicDouble(x: Int): Int = x * 2

    // 重载方法 — 用于测试 descriptor 消歧
    private fun overloaded(x: Int): String = "int:$x"
    private fun overloaded(x: String): String = "str:$x"

    // 修改 privateMutableCount 的辅助（验证写入后读取一致）
    fun getCount(): Int = privateMutableCount

    // 用于 callMethod 测试
    private fun formatInfo(prefix: String, num: Int): String = "$prefix#$num"

    companion object {
        @JvmStatic
        private val STATIC_SECRET: String = "top-secret"

        @JvmStatic
        private var staticCounter: Int = 0

        @JvmStatic
        private fun staticHelper(msg: String): String = "[$msg]"

        @JvmStatic
        fun getStaticCounter(): Int = staticCounter

        @JvmStatic
        fun resetStaticCounter() { staticCounter = 0 }
    }
}

/**
 * 用于 arg / cast 工具方法测试的 fixture。
 * 模拟一个有多参数方法的目标类。
 */
@Suppress("unused")
class UtilFixture {
    fun process(name: String, count: Int, tag: Any?): String = "$name:$count:$tag"
}

/**
 * 用于 cast 测试的简单类型层次。
 */
@Suppress("unused")
interface Greetable {
    fun greet(): String
}

@Suppress("unused")
class GreetableImpl(private val msg: String) : Greetable {
    override fun greet(): String = msg
    private val secret: String = "greetable-secret"
}

/**
 * 继承场景 — 字段声明在父类。
 */
@Suppress("unused")
open class AccessorParentFixture {
    private val parentSecret: String = "from-parent"
    protected var parentCounter: Int = 0
}

@Suppress("unused")
class AccessorChildFixture : AccessorParentFixture() {
    val childLabel: String = "child"

    fun describe(): String = "child:$childLabel"
}
