package top.maplex.incisiontest.fixture

/**
 * [top.maplex.incisiontest.cases.SurgeonPredicateCases] 专属 fixture。
 *
 * 提供多种签名的方法以便 where 谓词覆盖所有语法算子：
 * - 字符串/整数/浮点/布尔/null/对象/List 等多样化参数类型
 * - 属性访问/方法调用目标（Named、Box、Holder 等简单 POJO）
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class PredicateFixture {

    @Volatile var hits: Int = 0
    @Volatile var lastName: String? = null
    @Volatile var lastLevel: Int = 0

    /** 名称 + 等级：覆盖 == != < > <= >= 与布尔组合、matches 正则。 */
    fun spawn(name: String, level: Int): String {
        hits++; lastName = name; lastLevel = level
        return "spawn:$name:$level"
    }

    /** 纯字符串：覆盖字符串比较、matches、in。 */
    fun log(msg: String): String {
        hits++
        return "log:$msg"
    }

    /** 属性访问目标：args[0] 是 Named 实例。 */
    fun register(named: Named): String {
        hits++
        return "registered:${named.name}"
    }

    /** 方法调用目标：args[0] 是 String，直接调其方法。 */
    fun trigger(tag: String): String {
        hits++
        return "trig:$tag"
    }

    /** 下标/in 目标：参数是 List。 */
    fun handle(items: List<Any?>): String {
        hits++
        return "handle:${items.size}"
    }

    /** 可空目标：args[0] 可能为 null，用于 ?. 短路。 */
    fun touch(named: Named?): String {
        hits++
        return "touch:${named?.name}"
    }

    /** 双数值：浮点/整数字面量比较。 */
    fun measure(distance: Double): String {
        hits++
        return "measure:$distance"
    }

    /** 类型算子目标：参数是 Object，可以是任何类型。 */
    fun accept(obj: Any?): String {
        hits++
        return "accept:${obj?.javaClass?.simpleName}"
    }

    /** 布尔字面量 + null 字面量目标。 */
    fun flag(value: Any?): String {
        hits++
        return "flag:$value"
    }

    /** where 基线：where="" 应等价于不过滤，所有调用均命中。 */
    fun whereEmpty(value: Int): Int { hits++; return value }

    /** @Trail 上的 where：args[0] > 0 才计数尾部。返回值用于断言原方法仍执行。 */
    fun whereTrail(value: Int): Int { hits++; return value * 2 }

    /** @Splice 上的 where：仅 args[0] >= 100 时由 advice 接管，否则放行原方法。 */
    fun whereSplice(value: Int): Int { hits++; return value }

    /** @Excise 上的 where：仅当 args[0] == "cut" 时整段替换为常量，否则原样返回。 */
    fun whereExcise(tag: String): String { hits++; return "raw:$tag" }

    /** @Bypass 上的 where：替换内部对 helper 的调用，仅当原参数 > 50 时启用。 */
    fun whereBypass(value: Int): Int {
        hits++
        return whereBypassHelper(value)
    }
    fun whereBypassHelper(value: Int): Int = value + 1

    /** @Graft 上的 where：在 invoke helper 前植入，where 过滤入参。 */
    fun whereGraft(value: Int): Int {
        hits++
        return whereGraftHelper(value)
    }
    fun whereGraftHelper(value: Int): Int = value * 10

    /** @Trim ARG 上的 where：仅当原参数 > 0 时改写 index=0 的实参。 */
    fun whereTrim(value: Int): Int { hits++; return value }

    /** 简单 POJO —— 供 property/method 访问。 */
    class Named(val name: String, val size: Int = 0) {
        fun startsWith(prefix: String): Boolean = name.startsWith(prefix)
        fun length(): Int = name.length
    }
}
