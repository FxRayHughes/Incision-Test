package top.maplex.incisiontest.fixture

/**
 * Task #13 第二批 —— scope DSL 与描述符变种 fixture。
 *
 * 提供多种特征方法，让 @Lead/@Trail/@Splice/@Excise 的 scope 字段、
 * @Bypass/@Graft/@Trim 的 method 描述符字段在不同写法下都有匹配目标。
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class AnchorMatrixFixture_Scope {

    @Volatile var leadFullCount = 0
    @Volatile var leadWildCount = 0
    @Volatile var leadStarTargetCount = 0

    fun scopeMethodFull(msg: String): String = "full:$msg"
    fun scopeMethodWild(a: Int, b: Int): Int = a + b
    fun scopeStarTarget(): String = "star"
    fun scopeClassA(): Int = 100
    fun scopeClassB(): Int = 200
    fun scopeShorthandArgs(name: String, age: Int): String = "$name@$age"
    fun scopeTrimArgs(name: String, age: Int): String = "$name@$age"

    /** 给 @Bypass / @Graft 用的多 INVOKE，用于描述符变种命中 */
    fun scopeInvokeHelper(): Int {
        val a = inner1()
        val b = inner2()
        return a + b
    }

    fun inner1(): Int = 1
    fun inner2(): Int = 2

    fun reset() {
        leadFullCount = 0
        leadWildCount = 0
        leadStarTargetCount = 0
    }
}
