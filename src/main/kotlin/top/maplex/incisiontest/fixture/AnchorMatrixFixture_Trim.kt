package top.maplex.incisiontest.fixture

/**
 * Task #13 第二批 —— @Trim 全字段网格 fixture。
 *
 * 提供单参 / 双参 / 三参 / 四参方法，配合不同返回类型，
 * 让 @Trim 的 kind=ARG/RETURN、index=0/1/2/3 在每个组合下都有目标。
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class AnchorMatrixFixture_Trim {

    @Volatile var lastSingle: String = "none"
    @Volatile var lastDouble: String = "none"
    @Volatile var lastTriple: String = "none"
    @Volatile var lastQuad: String = "none"

    fun trimSingleArg(name: String): String {
        lastSingle = name
        return "single:$name"
    }

    fun trimDoubleArg(left: String, right: String): String {
        lastDouble = "$left|$right"
        return "double:$left:$right"
    }

    fun trimTripleArg(a: String, b: String, c: String): String {
        lastTriple = "$a|$b|$c"
        return "triple:$a:$b:$c"
    }

    fun trimQuadArg(a: String, b: String, c: String, d: String): String {
        lastQuad = "$a|$b|$c|$d"
        return "quad:$a:$b:$c:$d"
    }

    fun trimReturnInt(): Int = 10
    fun trimReturnLong(): Long = 100L
    fun trimReturnString(): String = "raw"
    fun trimReturnBoolean(): Boolean = false

    fun reset() {
        lastSingle = "none"
        lastDouble = "none"
        lastTriple = "none"
        lastQuad = "none"
    }
}
