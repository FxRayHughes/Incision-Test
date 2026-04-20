package top.maplex.incisiontest.fixture

/**
 * Task #13 锚点矩阵测试专属 fixture。
 *
 * 为每种锚点 / advice 组合提供独立目标方法，避免互相干扰：
 * - HEAD / TAIL / RETURN / THROW —— 通过不同入口 / 出口形态的方法暴露
 * - INVOKE × ordinal —— [matrixInvokeChain] 内连续调用 helper 三次，分别覆盖 ordinal 0/1/-1
 * - FIELD_GET / FIELD_PUT —— [matrixFieldRead] / [matrixFieldWrite] 各自单点
 * - NEW —— [matrixAllocate] 构造 ArrayList
 * - 异常路径 —— [matrixThrowUncaught] 不在内部捕获，用于覆盖外层 advice 异常出口
 * - ARG —— [matrixEcho] / [matrixCombine] 暴露 index=0 / index=1
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class AnchorMatrixFixture {

    @Volatile var counter: Int = 0
    @Volatile var lastWritten: Int = 0
    @Volatile var lastEcho: String = "none"
    @Volatile var lastCombine: String = "none"

    /** INVOKE 锚点目标 —— 入口处 + 多调用，便于 ordinal 区分 */
    fun helperA(x: Int): Int = x + 1
    fun helperB(x: Int): Int = x + 2

    /**
     * INVOKE 锚点测试入口。
     * 调用 helperA 三次，分别对应 ordinal=0 / 1 / 2（-1 命中全部）。
     */
    fun matrixInvokeChain(seed: Int): Int {
        val a = helperA(seed)
        val b = helperA(seed + 10)
        val c = helperA(seed + 100)
        return a + b + c
    }

    /** INVOKE 锚点 — 不同被调方法，验证 target 区分能力 */
    fun matrixInvokeMixed(seed: Int): Int {
        val a = helperA(seed)
        val b = helperB(seed)
        return a + b
    }

    /** HEAD / TAIL —— 简单方法，直接返回 */
    fun matrixSimpleReturn(): String {
        return "raw-return"
    }

    /** RETURN —— 多 return 出口（条件分支） */
    fun matrixMultiReturn(flag: Boolean): Int {
        if (flag) return 1
        return 2
    }

    /** THROW —— 内部不捕获异常，由调用方接住 */
    fun matrixThrowUncaught(shouldThrow: Boolean): String {
        if (shouldThrow) throw IllegalStateException("matrix-throw")
        return "ok"
    }

    /** FIELD_GET 锚点 */
    fun matrixFieldRead(): Int {
        return counter
    }

    /** FIELD_PUT 锚点 */
    fun matrixFieldWrite(v: Int) {
        lastWritten = v
    }

    /** NEW 锚点 —— 构造 ArrayList 实例 */
    fun matrixAllocate(): ArrayList<String> {
        val list = ArrayList<String>()
        list.add("seed")
        return list
    }

    /** ARG (index=0) —— @Trim 入参修整 */
    fun matrixEcho(msg: String): String {
        lastEcho = msg
        return "echo:$msg"
    }

    /** ARG (index=0/1) —— 双参数，验证 @Trim index 选择能力 */
    fun matrixCombine(left: String, right: String): String {
        lastCombine = "$left|$right"
        return "$left+$right"
    }

    /** @Excise 整段替换目标 */
    fun matrixExciseTarget(): String {
        return "original-implementation"
    }

    /** @Splice proceed / skip 目标 */
    fun matrixSpliceTarget(seed: Int): Int {
        return seed * 10
    }

    /** @Trim RETURN 专属目标 */
    fun matrixPlainReturn(): Int {
        return 5
    }

    fun reset() {
        counter = 0
        lastWritten = 0
        lastEcho = "none"
        lastCombine = "none"
    }
}
