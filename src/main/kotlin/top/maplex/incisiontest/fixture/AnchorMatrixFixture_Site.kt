package top.maplex.incisiontest.fixture

/**
 * Task #13 第二批 —— @Site 字段全变种专属 fixture。
 *
 * 设计：每个目标方法专属一个锚点变种，互不耦合，便于断言计数：
 * - [siteHeadMethod] / [siteTailMethod] / [siteReturnMethod] / [siteThrowMethod]
 * - [siteInvokeChain] —— 连续四次 helper 调用（覆盖 ordinal 0/1/2/-1）
 * - [siteFieldGet] / [siteFieldPut] / [siteNewObject]
 * - [siteOffsetBaseline] —— 简单 helper 调用，用于 offset=0 基线
 * - [siteTargetEmpty] —— 描述符 target 留空 / 填值的对照
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class AnchorMatrixFixture_Site {

    @Volatile var counter: Int = 0
    @Volatile var lastWritten: Int = 0

    fun helperSite(x: Int): Int = x + 1

    fun siteHeadMethod(): String = "head"
    fun siteTailMethod(): String = "tail"
    fun siteReturnMethod(): Int = 7
    fun siteThrowMethod(): String {
        try {
            throw IllegalArgumentException("site-throw")
        } catch (e: IllegalArgumentException) {
            return "caught:${e.message}"
        }
    }

    /** 连续四次 helperSite 调用 —— 用于 @Site.ordinal 全变种 */
    fun siteInvokeChain(seed: Int): Int {
        val a = helperSite(seed)
        val b = helperSite(seed + 10)
        val c = helperSite(seed + 100)
        val d = helperSite(seed + 1000)
        return a + b + c + d
    }

    fun siteFieldGet(): Int = counter
    fun siteFieldPut(v: Int) { lastWritten = v }
    fun siteNewObject(): StringBuilder = StringBuilder("site-new")

    /** offset=0 基线 —— 简单 helper 调用 */
    fun siteOffsetBaseline(seed: Int): Int = helperSite(seed)

    /** 描述符 target 留空 vs 填值对照（NEW 锚点 target=空 时匹配任意 NEW） */
    fun siteTargetEmpty(): Any {
        val a = StringBuilder("a")
        return a
    }

    fun reset() {
        counter = 0
        lastWritten = 0
    }
}
