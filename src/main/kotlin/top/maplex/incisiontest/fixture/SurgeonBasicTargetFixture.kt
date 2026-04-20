package top.maplex.incisiontest.fixture

/**
 * [top.maplex.incisiontest.cases.SurgeonBasicCases] 专属 fixture。
 *
 * 每个 @Surgeon case 类拥有自己的 fixture，避免永久 advice 与瞬态测试
 * 争抢同一目标方法导致链路抢断。
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class SurgeonBasicTargetFixture {

    var fieldHits: Int = 0
    var lastArg: Any? = null

    fun greet(name: String): String {
        fieldHits++
        lastArg = name
        return "hello, $name"
    }

    fun voidNoArg() {
        fieldHits++
    }

    fun add(a: Int, b: Int): Int = a + b
}
