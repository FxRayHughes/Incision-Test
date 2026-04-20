package top.maplex.incisiontest.cases

import taboolib.module.incision.annotation.Bypass
import taboolib.module.incision.annotation.Excise
import taboolib.module.incision.annotation.Graft
import taboolib.module.incision.annotation.Lead
import taboolib.module.incision.annotation.Site
import taboolib.module.incision.annotation.Surgeon
import taboolib.module.incision.annotation.Trail
import taboolib.module.incision.api.Anchor
import taboolib.module.incision.api.Shift
import taboolib.module.incision.api.Theatre

/**
 * Task #13 第二批 —— @Site 字段全变种网格。
 *
 * 覆盖维度：
 * - anchor × 8（HEAD / TAIL / RETURN / THROW / INVOKE / FIELD_GET / FIELD_PUT / NEW）
 * - shift × 2（BEFORE / AFTER；仅 INVOKE 锚点显式列出）
 * - ordinal × 4（0 / 1 / 2 / -1，配合 [siteInvokeChain] 四次调用）
 * - target 填 / 空 对照
 * - offset = 0 基线
 *
 * @Lead/@Trail 隐含 anchor=HEAD/TAIL；@Graft 显式声明 @Site；@Excise/@Splice 整段。
 */
@Surgeon
object SurgeonAnchorMatrixSiteCases {

    private const val FIX = "top.maplex.incisiontest.fixture.AnchorMatrixFixture_Site"

    @Volatile var headHits = 0
    @Volatile var tailHits = 0
    @Volatile var returnHits = 0
    @Volatile var throwHits = 0
    @Volatile var invokeOrd0Hits = 0
    @Volatile var invokeOrd1Hits = 0
    @Volatile var invokeOrd2Hits = 0
    @Volatile var invokeOrdAllHits = 0
    @Volatile var invokeBeforeHits = 0
    @Volatile var invokeAfterHits = 0
    @Volatile var fieldGetHits = 0
    @Volatile var fieldPutHits = 0
    @Volatile var newWithTargetHits = 0
    @Volatile var newEmptyTargetHits = 0
    @Volatile var offsetZeroHits = 0

    // ---- anchor × 8 ----

    @Lead(scope = "method:$FIX#siteHeadMethod()java.lang.String")
    fun anchorHead(theatre: Theatre) { headHits++ }

    @Trail(scope = "method:$FIX#siteTailMethod()java.lang.String", onThrow = false)
    fun anchorTail(theatre: Theatre) { tailHits++ }

    @Graft(
        method = "$FIX#siteReturnMethod()int",
        site = Site(anchor = Anchor.RETURN),
    )
    fun anchorReturn(theatre: Theatre) { returnHits++ }

    @Graft(
        method = "$FIX#siteThrowMethod()java.lang.String",
        site = Site(anchor = Anchor.THROW),
    )
    fun anchorThrow(theatre: Theatre) { throwHits++ }

    // ---- INVOKE × ordinal ----

    @Graft(
        method = "$FIX#siteInvokeChain(int)int",
        site = Site(
            anchor = Anchor.INVOKE,
            target = "$FIX#helperSite(int)int",
            shift = Shift.BEFORE,
            ordinal = 0,
        ),
    )
    fun siteInvokeOrd0(theatre: Theatre) { invokeOrd0Hits++ }

    @Graft(
        method = "$FIX#siteInvokeChain(int)int",
        site = Site(
            anchor = Anchor.INVOKE,
            target = "$FIX#helperSite(int)int",
            shift = Shift.BEFORE,
            ordinal = 1,
        ),
    )
    fun siteInvokeOrd1(theatre: Theatre) { invokeOrd1Hits++ }

    @Graft(
        method = "$FIX#siteInvokeChain(int)int",
        site = Site(
            anchor = Anchor.INVOKE,
            target = "$FIX#helperSite(int)int",
            shift = Shift.BEFORE,
            ordinal = 2,
        ),
    )
    fun siteInvokeOrd2(theatre: Theatre) { invokeOrd2Hits++ }

    @Graft(
        method = "$FIX#siteInvokeChain(int)int",
        site = Site(
            anchor = Anchor.INVOKE,
            target = "$FIX#helperSite(int)int",
            shift = Shift.BEFORE,
            ordinal = -1,
        ),
    )
    fun siteInvokeOrdAll(theatre: Theatre) { invokeOrdAllHits++ }

    // ---- INVOKE × shift BEFORE/AFTER on offset baseline ----

    @Graft(
        method = "$FIX#siteOffsetBaseline(int)int",
        site = Site(
            anchor = Anchor.INVOKE,
            target = "$FIX#helperSite(int)int",
            shift = Shift.BEFORE,
            offset = 0,
        ),
    )
    fun siteShiftBefore(theatre: Theatre) { invokeBeforeHits++ }

    @Graft(
        method = "$FIX#siteOffsetBaseline(int)int",
        site = Site(
            anchor = Anchor.INVOKE,
            target = "$FIX#helperSite(int)int",
            shift = Shift.AFTER,
            offset = 0,
        ),
    )
    fun siteShiftAfter(theatre: Theatre) { invokeAfterHits++ }

    // ---- FIELD_GET / FIELD_PUT ----

    @Graft(
        method = "$FIX#siteFieldGet()int",
        site = Site(
            anchor = Anchor.FIELD_GET,
            target = "$FIX#counter:int",
        ),
    )
    fun siteFieldGetGraft(theatre: Theatre) { fieldGetHits++ }

    @Graft(
        method = "$FIX#siteFieldPut(int)V",
        site = Site(
            anchor = Anchor.FIELD_PUT,
            target = "$FIX#lastWritten:int",
        ),
    )
    fun siteFieldPutGraft(theatre: Theatre) { fieldPutHits++ }

    // ---- NEW × target 填 / 空 ----

    @Graft(
        method = "$FIX#siteNewObject()java.lang.StringBuilder",
        site = Site(
            anchor = Anchor.NEW,
            target = "java.lang.StringBuilder",
        ),
    )
    fun siteNewWithTarget(theatre: Theatre) { newWithTargetHits++ }

    /** target 留空 —— 期望匹配方法内任意 NEW 指令 */
    @Graft(
        method = "$FIX#siteTargetEmpty()java.lang.Object",
        site = Site(
            anchor = Anchor.NEW,
            target = "",
        ),
    )
    fun siteNewEmptyTarget(theatre: Theatre) { newEmptyTargetHits++ }

    // ---- offset=0 基线 ----

    @Graft(
        method = "$FIX#siteOffsetBaseline(int)int",
        site = Site(
            anchor = Anchor.INVOKE,
            target = "$FIX#helperSite(int)int",
            shift = Shift.BEFORE,
            ordinal = 0,
            offset = 0,
        ),
    )
    fun siteOffsetZero(theatre: Theatre) { offsetZeroHits++ }

    fun reset() {
        headHits = 0; tailHits = 0; returnHits = 0; throwHits = 0
        invokeOrd0Hits = 0; invokeOrd1Hits = 0; invokeOrd2Hits = 0; invokeOrdAllHits = 0
        invokeBeforeHits = 0; invokeAfterHits = 0
        fieldGetHits = 0; fieldPutHits = 0
        newWithTargetHits = 0; newEmptyTargetHits = 0
        offsetZeroHits = 0
    }
}
