package top.maplex.incisiontest.fixture

/**
 * Task #13 追加扩展 fixture —— 覆盖 team-lead 第二轮矩阵要求：
 *
 * 1. FIELD_GET / FIELD_PUT / NEW 锚点的 BEFORE/AFTER 两种 shift
 * 2. @Trim 三参数方法的 ARG index=0/1/2 三条独立 advice
 * 3. @Splice skip (短路) —— [spliceSkipTarget] 带副作用计数器，便于验证目标未执行
 * 4. @Excise 方法级切除 —— [exciseRawTarget] 带副作用计数器
 * 5. 描述符变种：无括号方法 [noParenMethod]
 *
 * 所有字段加 @Volatile，跨线程可见；`reset()` 清零计数器。
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class AnchorMatrixFixture_Extras {

    // ---- FIELD_GET / FIELD_PUT BEFORE/AFTER 目标字段 ----
    @Volatile var counterShift: Int = 0
    @Volatile var lastWriteShift: Int = 0

    @Volatile var lastTriple012: String = "none"

    @Volatile var spliceSkipBodyExecuted = 0
    @Volatile var exciseRawBodyExecuted = 0
    @Volatile var noParenBodyExecuted = 0

    /** FIELD_GET 锚点目标 —— BEFORE/AFTER 两条独立 advice 打同一方法 */
    fun readCounterShift(): Int {
        return counterShift
    }

    /** FIELD_PUT 锚点目标 */
    fun writeCounterShift(v: Int) {
        counterShift = v
    }

    /** NEW 锚点目标 —— BEFORE 应在 NEW 指令之前，AFTER 在 NEW/<init> 之后 */
    fun allocateBuilder(): StringBuilder {
        val sb = StringBuilder()
        sb.append("seed")
        return sb
    }

    /** 3 参方法 —— 同时挂 ARG index=0/1/2 三条 advice */
    fun tripleArgEcho(a: String, b: String, c: String): String {
        lastTriple012 = "$a|$b|$c"
        return "triple:$a:$b:$c"
    }

    /** @Splice 短路目标 —— 如果 advice 不 proceed，这里的计数不应增加 */
    fun spliceSkipTarget(seed: Int): Int {
        spliceSkipBodyExecuted++
        return seed * 100
    }

    /** @Excise 目标 —— 如果 @Excise 生效，这里的计数不应增加 */
    fun exciseRawTarget(): String {
        exciseRawBodyExecuted++
        return "raw-excise-body"
    }

    /** 无括号方法名 —— 用于验证描述符变种 `voidMethod` 写法（@Lead/@Trail 允许无括号） */
    fun noParenMethod(): String {
        noParenBodyExecuted++
        return "no-paren"
    }

    fun reset() {
        counterShift = 0
        lastWriteShift = 0
        lastTriple012 = "none"
        spliceSkipBodyExecuted = 0
        exciseRawBodyExecuted = 0
        noParenBodyExecuted = 0
    }
}
