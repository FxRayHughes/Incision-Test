package top.maplex.incisiontest.cases

import taboolib.module.incision.annotation.MatchMode

import taboolib.module.incision.annotation.SelectorKind

import taboolib.module.incision.annotation.Selector

import taboolib.module.incision.annotation.Pointcut

import taboolib.module.incision.annotation.Bypass
import taboolib.module.incision.annotation.Excise
import taboolib.module.incision.annotation.Graft
import taboolib.module.incision.annotation.Lead
import taboolib.module.incision.annotation.Site
import taboolib.module.incision.annotation.Splice
import taboolib.module.incision.annotation.Surgeon
import taboolib.module.incision.annotation.Trail
import taboolib.module.incision.annotation.Trim
import taboolib.module.incision.api.Anchor
import taboolib.module.incision.api.Shift
import taboolib.module.incision.api.Theatre

/**
 * Task #13 锚点矩阵 —— 锚点 × advice × shift × ordinal 全覆盖。
 *
 * 所有目标都落在 [top.maplex.incisiontest.fixture.AnchorMatrixFixture]，
 * 与其它 cases 物理隔离。每个计数器对应一个独立 advice，
 * 由 [top.maplex.incisiontest.AllCases] 中对应的 test* 方法做硬断言。
 */
@Surgeon
object SurgeonAnchorMatrixCases {

    private const val FIX = "top.maplex.incisiontest.fixture.AnchorMatrixFixture"

    // ---- 计数器 ----
    @Volatile var headLeadHits = 0
    @Volatile var tailTrailNormalHits = 0
    @Volatile var trailReturnFlagTrueHits = 0
    @Volatile var trailReturnFlagFalseHits = 0
    @Volatile var trailThrowHits = 0
    @Volatile var trailThrowOk = 0

    @Volatile var graftInvokeBeforeOrd0 = 0
    @Volatile var graftInvokeBeforeOrd1 = 0
    @Volatile var graftInvokeBeforeOrdAll = 0
    @Volatile var graftInvokeAfterOrd0 = 0

    @Volatile var graftFieldGetHits = 0
    @Volatile var graftFieldPutHits = 0
    @Volatile var graftNewHits = 0
    @Volatile var graftThrowHits = 0

    @Volatile var bypassReplaceHits = 0
    @Volatile var trimReturnHits = 0
    @Volatile var trimArg0Hits = 0
    @Volatile var trimArg1Hits = 0

    @Volatile var exciseHits = 0
    @Volatile var spliceProceedHits = 0
    @Volatile var spliceSkipHits = 0

    // ====================================================================
    // 语义锚点：HEAD / TAIL / RETURN / THROW —— 用 @Lead / @Trail 表达
    // ====================================================================

    /** @Lead = HEAD 语义锚点（方法入口） */
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "matrixSimpleReturn", descriptor = "()Ljava/lang/String;")]))
    fun headLead(theatre: Theatre) {
        headLeadHits++
    }

    /** @Trail = TAIL 语义锚点（正常出口） */
    @Trail(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "matrixSimpleReturn", descriptor = "()Ljava/lang/String;")]), onThrow = false)
    fun tailTrailNormal(theatre: Theatre) {
        tailTrailNormalHits++
    }

    /** @Trail RETURN —— 多 return 出口都应触发（flag=true 与 flag=false 各一次） */
    @Trail(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "matrixMultiReturn", descriptor = "(Z)I")]), onThrow = false)
    fun trailMultiReturn(theatre: Theatre) {
        val flag = theatre.args[0] as Boolean
        if (flag) trailReturnFlagTrueHits++ else trailReturnFlagFalseHits++
    }

    /** @Trail THROW —— onThrow=true 时异常出口也触发 */
    @Trail(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "matrixThrowUncaught", descriptor = "(Z)Ljava/lang/String;")]), onThrow = true)
    fun trailOnThrow(theatre: Theatre) {
        if (theatre.throwable != null) trailThrowHits++
        else trailThrowOk++
    }

    // ====================================================================
    // INVOKE 锚点 × ordinal 0/1/-1 × shift BEFORE/AFTER
    // ====================================================================

    /** @Graft INVOKE BEFORE ordinal=0 —— 仅命中第一次 helperA 调用 */
    @Graft(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "matrixInvokeChain", descriptor = "(I)I")]),
        site = Site(
            anchor = Anchor.INVOKE,
            target = Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "helperA", descriptor = "(I)I"),
            shift = Shift.BEFORE,
            ordinal = 0,
        ),
    )
    fun graftInvokeBefore0(theatre: Theatre) {
        graftInvokeBeforeOrd0++
    }

    /** @Graft INVOKE BEFORE ordinal=1 —— 仅命中第二次 helperA 调用 */
    @Graft(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "matrixInvokeChain", descriptor = "(I)I")]),
        site = Site(
            anchor = Anchor.INVOKE,
            target = Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "helperA", descriptor = "(I)I"),
            shift = Shift.BEFORE,
            ordinal = 1,
        ),
    )
    fun graftInvokeBefore1(theatre: Theatre) {
        graftInvokeBeforeOrd1++
    }

    /** @Graft INVOKE BEFORE ordinal=-1 —— 命中全部三次 helperA 调用 */
    @Graft(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "matrixInvokeChain", descriptor = "(I)I")]),
        site = Site(
            anchor = Anchor.INVOKE,
            target = Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "helperA", descriptor = "(I)I"),
            shift = Shift.BEFORE,
            ordinal = -1,
            maxMatches = -1,
        ),
    )
    fun graftInvokeBeforeAll(theatre: Theatre) {
        graftInvokeBeforeOrdAll++
    }

    /** @Graft INVOKE AFTER ordinal=0 —— 仅命中第一次 helperA 调用之后 */
    @Graft(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "matrixInvokeChain", descriptor = "(I)I")]),
        site = Site(
            anchor = Anchor.INVOKE,
            target = Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "helperA", descriptor = "(I)I"),
            shift = Shift.AFTER,
            ordinal = 0,
        ),
    )
    fun graftInvokeAfter0(theatre: Theatre) {
        graftInvokeAfterOrd0++
    }

    // ====================================================================
    // FIELD_GET / FIELD_PUT / NEW / THROW
    // ====================================================================

    @Graft(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "matrixFieldRead", descriptor = "()I")]),
        site = Site(
            anchor = Anchor.FIELD_GET,
            target = Selector(kind = SelectorKind.FIELD, owner = "$FIX", name = "counter", descriptor = "I"),
        ),
    )
    fun graftFieldGet(theatre: Theatre) {
        graftFieldGetHits++
    }

    @Graft(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "matrixFieldWrite", descriptor = "(I)V")]),
        site = Site(
            anchor = Anchor.FIELD_PUT,
            target = Selector(kind = SelectorKind.FIELD, owner = "$FIX", name = "lastWritten", descriptor = "I"),
        ),
    )
    fun graftFieldPut(theatre: Theatre) {
        graftFieldPutHits++
    }

    @Graft(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "matrixAllocate", descriptor = "()Ljava/util/ArrayList;")]),
        site = Site(
            anchor = Anchor.NEW,
            target = Selector(kind = SelectorKind.CLASS, owner = "java/util/ArrayList"),
        ),
    )
    fun graftNew(theatre: Theatre) {
        graftNewHits++
    }

    /** @Graft THROW —— 在 throw 指令前触发 */
    @Graft(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "matrixThrowUncaught", descriptor = "(Z)Ljava/lang/String;")]),
        site = Site(anchor = Anchor.THROW),
    )
    fun graftThrow(theatre: Theatre) {
        graftThrowHits++
    }

    // ====================================================================
    // @Bypass INVOKE —— 替换调用返回值
    // ====================================================================

    @Bypass(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "matrixInvokeMixed", descriptor = "(I)I")]),
        site = Site(
            anchor = Anchor.INVOKE,
            target = Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "helperA", descriptor = "(I)I"),
        ),
    )
    fun bypassReplace(theatre: Theatre): Any? {
        bypassReplaceHits++
        return 9999
    }

    // ====================================================================
    // @Trim RETURN / ARG (index=0) / ARG (index=1)
    // ====================================================================

    @Trim(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "matrixPlainReturn", descriptor = "()I")]),
        kind = Trim.Kind.RETURN,
    )
    fun trimReturnPlain(theatre: Theatre): Any? {
        trimReturnHits++
        return -1
    }

    @Trim(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "matrixEcho", descriptor = "(Ljava/lang/String;)Ljava/lang/String;")]),
        kind = Trim.Kind.ARG,
        index = 0,
    )
    fun trimArg0(theatre: Theatre): Any? {
        trimArg0Hits++
        return "trimmed-0"
    }

    @Trim(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "matrixCombine", descriptor = "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;")]),
        kind = Trim.Kind.ARG,
        index = 1,
    )
    fun trimArg1(theatre: Theatre): Any? {
        trimArg1Hits++
        return "trimmed-1"
    }

    // ====================================================================
    // @Excise 方法级整段替换
    // ====================================================================

    @Excise(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "matrixExciseTarget", descriptor = "()Ljava/lang/String;")]))
    fun exciseFullReplace(theatre: Theatre): Any? {
        exciseHits++
        return "excised-payload"
    }

    // ====================================================================
    // @Splice proceed (放行) / skip (短路)
    // ====================================================================

    /** @Splice proceed —— 放行原方法、不改返回 */
    @Splice(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "matrixSpliceTarget", descriptor = "(I)I")]))
    fun spliceProceedAndKeep(theatre: Theatre): Any? {
        spliceProceedHits++
        return theatre.resume.proceed()
    }

    fun reset() {
        headLeadHits = 0
        tailTrailNormalHits = 0
        trailReturnFlagTrueHits = 0
        trailReturnFlagFalseHits = 0
        trailThrowHits = 0
        trailThrowOk = 0
        graftInvokeBeforeOrd0 = 0
        graftInvokeBeforeOrd1 = 0
        graftInvokeBeforeOrdAll = 0
        graftInvokeAfterOrd0 = 0
        graftFieldGetHits = 0
        graftFieldPutHits = 0
        graftNewHits = 0
        graftThrowHits = 0
        bypassReplaceHits = 0
        trimReturnHits = 0
        trimArg0Hits = 0
        trimArg1Hits = 0
        exciseHits = 0
        spliceProceedHits = 0
        spliceSkipHits = 0
    }
}
