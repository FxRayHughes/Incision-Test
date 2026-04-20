package top.maplex.incisiontest.cases

import taboolib.module.incision.annotation.Excise
import taboolib.module.incision.annotation.Graft
import taboolib.module.incision.annotation.Lead
import taboolib.module.incision.annotation.Site
import taboolib.module.incision.annotation.Splice
import taboolib.module.incision.annotation.Surgeon
import taboolib.module.incision.annotation.Trim
import taboolib.module.incision.api.Anchor
import taboolib.module.incision.api.Shift
import taboolib.module.incision.api.Theatre

/**
 * Task #13 追加扩展 cases —— 对应 [top.maplex.incisiontest.fixture.AnchorMatrixFixture_Extras]。
 *
 * 覆盖：
 *  - FIELD_GET / FIELD_PUT / NEW 锚点 × shift BEFORE/AFTER 全网格
 *  - @Trim 同一 3 参方法上挂 ARG index=0/1/2 三条独立 advice
 *  - @Splice skip (短路，不 proceed) —— 验证原方法体未执行
 *  - @Excise —— 验证原方法体未执行、返回值被替换
 *  - 描述符变种：完整 JVM 形式 `Ljava/lang/String;` 风格 & 无括号方法写法
 */
@Surgeon
object SurgeonAnchorMatrixExtrasCases {

    private const val FIX = "top.maplex.incisiontest.fixture.AnchorMatrixFixture_Extras"

    @Volatile var fieldGetBeforeHits = 0
    @Volatile var fieldGetAfterHits = 0
    @Volatile var fieldPutBeforeHits = 0
    @Volatile var fieldPutAfterHits = 0
    @Volatile var newBeforeHits = 0
    @Volatile var newAfterHits = 0

    @Volatile var tripleArg0Hits = 0
    @Volatile var tripleArg1Hits = 0
    @Volatile var tripleArg2Hits = 0

    @Volatile var spliceSkipAdviceHits = 0
    @Volatile var exciseRawAdviceHits = 0
    @Volatile var noParenLeadHits = 0

    // ---- FIELD_GET × BEFORE / AFTER ----

    @Graft(
        method = "$FIX#readCounterShift()int",
        site = Site(
            anchor = Anchor.FIELD_GET,
            target = "$FIX#counterShift:int",
            shift = Shift.BEFORE,
        ),
    )
    fun fieldGetBefore(theatre: Theatre) { fieldGetBeforeHits++ }

    @Graft(
        method = "$FIX#readCounterShift()int",
        site = Site(
            anchor = Anchor.FIELD_GET,
            target = "$FIX#counterShift:int",
            shift = Shift.AFTER,
        ),
    )
    fun fieldGetAfter(theatre: Theatre) { fieldGetAfterHits++ }

    // ---- FIELD_PUT × BEFORE / AFTER ----

    @Graft(
        method = "$FIX#writeCounterShift(int)V",
        site = Site(
            anchor = Anchor.FIELD_PUT,
            target = "$FIX#counterShift:int",
            shift = Shift.BEFORE,
        ),
    )
    fun fieldPutBefore(theatre: Theatre) { fieldPutBeforeHits++ }

    @Graft(
        method = "$FIX#writeCounterShift(int)V",
        site = Site(
            anchor = Anchor.FIELD_PUT,
            target = "$FIX#counterShift:int",
            shift = Shift.AFTER,
        ),
    )
    fun fieldPutAfter(theatre: Theatre) { fieldPutAfterHits++ }

    // ---- NEW × BEFORE / AFTER ----

    @Graft(
        method = "$FIX#allocateBuilder()java.lang.StringBuilder",
        site = Site(
            anchor = Anchor.NEW,
            target = "java.lang.StringBuilder",
            shift = Shift.BEFORE,
        ),
    )
    fun newBefore(theatre: Theatre) { newBeforeHits++ }

    @Graft(
        method = "$FIX#allocateBuilder()java.lang.StringBuilder",
        site = Site(
            anchor = Anchor.NEW,
            target = "java.lang.StringBuilder",
            shift = Shift.AFTER,
        ),
    )
    fun newAfter(theatre: Theatre) { newAfterHits++ }

    // ---- @Trim 同 3 参方法 × ARG index=0/1/2 ----

    @Trim(
        method = "$FIX#tripleArgEcho(java.lang.String,java.lang.String,java.lang.String)java.lang.String",
        kind = Trim.Kind.ARG,
        index = 0,
    )
    fun tripleArg0(theatre: Theatre): Any? {
        tripleArg0Hits++
        return "T0"
    }

    @Trim(
        method = "$FIX#tripleArgEcho(java.lang.String,java.lang.String,java.lang.String)java.lang.String",
        kind = Trim.Kind.ARG,
        index = 1,
    )
    fun tripleArg1(theatre: Theatre): Any? {
        tripleArg1Hits++
        return "T1"
    }

    @Trim(
        method = "$FIX#tripleArgEcho(java.lang.String,java.lang.String,java.lang.String)java.lang.String",
        kind = Trim.Kind.ARG,
        index = 2,
    )
    fun tripleArg2(theatre: Theatre): Any? {
        tripleArg2Hits++
        return "T2"
    }

    // ---- @Splice skip —— 不调用 proceed() ----

    @Splice(scope = "method:$FIX#spliceSkipTarget(int)int")
    fun spliceShortCircuit(theatre: Theatre): Any? {
        spliceSkipAdviceHits++
        return -999 // 不 proceed，直接返回
    }

    // ---- @Excise 方法级切除 ----

    @Excise(scope = "method:$FIX#exciseRawTarget()java.lang.String")
    fun exciseFullBody(theatre: Theatre): Any? {
        exciseRawAdviceHits++
        return "excised-value"
    }

    // ---- 描述符变种：无括号方法名 ----

    @Lead(scope = "method:$FIX#noParenMethod()java.lang.String")
    fun noParenLead(theatre: Theatre) { noParenLeadHits++ }

    fun reset() {
        fieldGetBeforeHits = 0
        fieldGetAfterHits = 0
        fieldPutBeforeHits = 0
        fieldPutAfterHits = 0
        newBeforeHits = 0
        newAfterHits = 0
        tripleArg0Hits = 0
        tripleArg1Hits = 0
        tripleArg2Hits = 0
        spliceSkipAdviceHits = 0
        exciseRawAdviceHits = 0
        noParenLeadHits = 0
    }
}
