package top.maplex.incisiontest.cases

import taboolib.module.incision.annotation.Graft
import taboolib.module.incision.annotation.Site
import taboolib.module.incision.annotation.Surgeon
import taboolib.module.incision.api.Anchor
import taboolib.module.incision.api.Shift
import taboolib.module.incision.api.Theatre

/**
 * Site offset / ordinal / shift 锚点测试 — Task #14。
 *
 * 目标方法 [top.maplex.incisiontest.fixture.OffsetFixture.chain] / [chainWithGap] 内多次连续
 * helper 调用 + 字段读写填充。
 *
 * 覆盖：
 * - ordinal=0/1/2 — 第 N 次 helper 命中
 * - shift=BEFORE / AFTER 配合 offset=0/+1/+2/-1
 * - ordinal × offset 联合
 */
@Suppress("unused")
@Surgeon
object SurgeonOffsetCases {

    @Volatile var ordinal0Hits = 0
    @Volatile var ordinal1Hits = 0
    @Volatile var ordinal2Hits = 0
    @Volatile var afterShiftHits = 0

    // 新增 — Task #14 扩展
    @Volatile var offsetZeroHits = 0
    @Volatile var afterOffset1Hits = 0
    @Volatile var afterOffset2Hits = 0
    @Volatile var beforeNegOffsetHits = 0
    @Volatile var ordinal0AfterOffset1Hits = 0
    @Volatile var ordinal2AfterOffset2Hits = 0
    @Volatile var anyOrdinalHits = 0

    // 追加覆盖
    @Volatile var offsetPlus5Hits = 0
    @Volatile var offsetMinus2Hits = 0
    @Volatile var beforeOffset2Hits = 0
    @Volatile var afterOffset0Hits = 0
    @Volatile var ordinal1AfterOffset1Hits = 0
    @Volatile var fieldGetOffsetHits = 0
    @Volatile var fieldPutOffsetHits = 0

    fun reset() {
        ordinal0Hits = 0; ordinal1Hits = 0; ordinal2Hits = 0; afterShiftHits = 0
        offsetZeroHits = 0
        afterOffset1Hits = 0
        afterOffset2Hits = 0
        beforeNegOffsetHits = 0
        ordinal0AfterOffset1Hits = 0
        ordinal2AfterOffset2Hits = 0
        anyOrdinalHits = 0
        offsetPlus5Hits = 0
        offsetMinus2Hits = 0
        beforeOffset2Hits = 0
        afterOffset0Hits = 0
        ordinal1AfterOffset1Hits = 0
        fieldGetOffsetHits = 0
        fieldPutOffsetHits = 0
    }

    @Graft(
        method = "top.maplex.incisiontest.fixture.OffsetFixture#chain()int",
        site = Site(
            anchor = Anchor.INVOKE,
            target = "top.maplex.incisiontest.fixture.OffsetFixture#helper(int)int",
            shift = Shift.BEFORE,
            ordinal = 0
        )
    )
    fun onFirstHelperBefore(theatre: Theatre) {
        ordinal0Hits++
    }

    @Graft(
        method = "top.maplex.incisiontest.fixture.OffsetFixture#chain()int",
        site = Site(
            anchor = Anchor.INVOKE,
            target = "top.maplex.incisiontest.fixture.OffsetFixture#helper(int)int",
            shift = Shift.BEFORE,
            ordinal = 1
        )
    )
    fun onSecondHelperBefore(theatre: Theatre) {
        ordinal1Hits++
    }

    @Graft(
        method = "top.maplex.incisiontest.fixture.OffsetFixture#chain()int",
        site = Site(
            anchor = Anchor.INVOKE,
            target = "top.maplex.incisiontest.fixture.OffsetFixture#helper(int)int",
            shift = Shift.BEFORE,
            ordinal = 2
        )
    )
    fun onThirdHelperBefore(theatre: Theatre) {
        ordinal2Hits++
    }

    @Graft(
        method = "top.maplex.incisiontest.fixture.OffsetFixture#chain()int",
        site = Site(
            anchor = Anchor.INVOKE,
            target = "top.maplex.incisiontest.fixture.OffsetFixture#helper(int)int",
            shift = Shift.AFTER,
            ordinal = 0,
            offset = 1
        )
    )
    fun onFirstHelperAfterOffset1(theatre: Theatre) {
        afterShiftHits++
    }

    // ---- 新增 ----

    /** offset=0：等价于直接锚定，作对照用。 */
    @Graft(
        method = "top.maplex.incisiontest.fixture.OffsetFixture#chain()int",
        site = Site(
            anchor = Anchor.INVOKE,
            target = "top.maplex.incisiontest.fixture.OffsetFixture#helper(int)int",
            shift = Shift.BEFORE,
            ordinal = 0,
            offset = 0
        )
    )
    fun onFirstHelperBeforeOffsetZero(theatre: Theatre) {
        offsetZeroHits++
    }

    /** AFTER 方向 + offset=+1 在带 gap 的目标方法中对所有 ordinal 命中。 */
    @Graft(
        method = "top.maplex.incisiontest.fixture.OffsetFixture#chainWithGap()int",
        site = Site(
            anchor = Anchor.INVOKE,
            target = "top.maplex.incisiontest.fixture.OffsetFixture#helper(int)int",
            shift = Shift.AFTER,
            ordinal = -1,
            offset = 1
        )
    )
    fun onAfterOffset1All(theatre: Theatre) {
        afterOffset1Hits++
    }

    /** AFTER 方向 + offset=+2。 */
    @Graft(
        method = "top.maplex.incisiontest.fixture.OffsetFixture#chainWithGap()int",
        site = Site(
            anchor = Anchor.INVOKE,
            target = "top.maplex.incisiontest.fixture.OffsetFixture#helper(int)int",
            shift = Shift.AFTER,
            ordinal = -1,
            offset = 2
        )
    )
    fun onAfterOffset2All(theatre: Theatre) {
        afterOffset2Hits++
    }

    /** BEFORE 方向 + offset=-1 (向前回退 1 条指令)。 */
    @Graft(
        method = "top.maplex.incisiontest.fixture.OffsetFixture#chainWithGap()int",
        site = Site(
            anchor = Anchor.INVOKE,
            target = "top.maplex.incisiontest.fixture.OffsetFixture#helper(int)int",
            shift = Shift.BEFORE,
            ordinal = -1,
            offset = -1
        )
    )
    fun onBeforeNegOffset(theatre: Theatre) {
        beforeNegOffsetHits++
    }

    /** ordinal=0 + AFTER + offset=+1 联合。 */
    @Graft(
        method = "top.maplex.incisiontest.fixture.OffsetFixture#chainWithGap()int",
        site = Site(
            anchor = Anchor.INVOKE,
            target = "top.maplex.incisiontest.fixture.OffsetFixture#helper(int)int",
            shift = Shift.AFTER,
            ordinal = 0,
            offset = 1
        )
    )
    fun onOrdinal0AfterOffset1(theatre: Theatre) {
        ordinal0AfterOffset1Hits++
    }

    /** ordinal=2 + AFTER + offset=+2 联合。 */
    @Graft(
        method = "top.maplex.incisiontest.fixture.OffsetFixture#chainWithGap()int",
        site = Site(
            anchor = Anchor.INVOKE,
            target = "top.maplex.incisiontest.fixture.OffsetFixture#helper(int)int",
            shift = Shift.AFTER,
            ordinal = 2,
            offset = 2
        )
    )
    fun onOrdinal2AfterOffset2(theatre: Theatre) {
        ordinal2AfterOffset2Hits++
    }

    /** ordinal=-1：所有 helper 调用都命中。 */
    @Graft(
        method = "top.maplex.incisiontest.fixture.OffsetFixture#chain()int",
        site = Site(
            anchor = Anchor.INVOKE,
            target = "top.maplex.incisiontest.fixture.OffsetFixture#helper(int)int",
            shift = Shift.BEFORE,
            ordinal = -1
        )
    )
    fun onAnyOrdinal(theatre: Theatre) {
        anyOrdinalHits++
    }

    // ---- 追加：更多 offset / shift 组合 ----

    /** offset=+5 大跳（可能越界或命中远处指令）。 */
    @Graft(
        method = "top.maplex.incisiontest.fixture.OffsetFixture#chainWithGap()int",
        site = Site(
            anchor = Anchor.INVOKE,
            target = "top.maplex.incisiontest.fixture.OffsetFixture#helper(int)int",
            shift = Shift.AFTER,
            ordinal = 0,
            offset = 5
        )
    )
    fun onOffsetPlus5(theatre: Theatre) {
        offsetPlus5Hits++
    }

    /** offset=-2 回退 2 条。 */
    @Graft(
        method = "top.maplex.incisiontest.fixture.OffsetFixture#chainWithGap()int",
        site = Site(
            anchor = Anchor.INVOKE,
            target = "top.maplex.incisiontest.fixture.OffsetFixture#helper(int)int",
            shift = Shift.BEFORE,
            ordinal = 1,
            offset = -2
        )
    )
    fun onOffsetMinus2(theatre: Theatre) {
        offsetMinus2Hits++
    }

    /** shift=BEFORE + offset=+2（四组合之一）。 */
    @Graft(
        method = "top.maplex.incisiontest.fixture.OffsetFixture#chainWithGap()int",
        site = Site(
            anchor = Anchor.INVOKE,
            target = "top.maplex.incisiontest.fixture.OffsetFixture#helper(int)int",
            shift = Shift.BEFORE,
            ordinal = -1,
            offset = 2
        )
    )
    fun onBeforeOffset2(theatre: Theatre) {
        beforeOffset2Hits++
    }

    /** shift=AFTER + offset=0（四组合之二）。 */
    @Graft(
        method = "top.maplex.incisiontest.fixture.OffsetFixture#chain()int",
        site = Site(
            anchor = Anchor.INVOKE,
            target = "top.maplex.incisiontest.fixture.OffsetFixture#helper(int)int",
            shift = Shift.AFTER,
            ordinal = -1,
            offset = 0
        )
    )
    fun onAfterOffset0(theatre: Theatre) {
        afterOffset0Hits++
    }

    /** ordinal=1 + AFTER + offset=+1 交叉组合。 */
    @Graft(
        method = "top.maplex.incisiontest.fixture.OffsetFixture#chainWithGap()int",
        site = Site(
            anchor = Anchor.INVOKE,
            target = "top.maplex.incisiontest.fixture.OffsetFixture#helper(int)int",
            shift = Shift.AFTER,
            ordinal = 1,
            offset = 1
        )
    )
    fun onOrdinal1AfterOffset1(theatre: Theatre) {
        ordinal1AfterOffset1Hits++
    }

    /** FIELD_GET 锚点 + offset=+1：在读 counter 之后偏移 1 条指令。 */
    @Graft(
        method = "top.maplex.incisiontest.fixture.OffsetFixture#chainWithGap()int",
        site = Site(
            anchor = Anchor.FIELD_GET,
            target = "top.maplex.incisiontest.fixture.OffsetFixture#counter:int",
            shift = Shift.AFTER,
            ordinal = 0,
            offset = 1
        )
    )
    fun onFieldGetOffset(theatre: Theatre) {
        fieldGetOffsetHits++
    }

    /** FIELD_PUT 锚点 + BEFORE + offset=0 基线。 */
    @Graft(
        method = "top.maplex.incisiontest.fixture.OffsetFixture#chainWithGap()int",
        site = Site(
            anchor = Anchor.FIELD_PUT,
            target = "top.maplex.incisiontest.fixture.OffsetFixture#counter:int",
            shift = Shift.BEFORE,
            ordinal = 0,
            offset = 0
        )
    )
    fun onFieldPutOffset(theatre: Theatre) {
        fieldPutOffsetHits++
    }
}
