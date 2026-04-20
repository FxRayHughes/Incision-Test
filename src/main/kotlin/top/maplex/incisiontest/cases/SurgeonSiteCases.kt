package top.maplex.incisiontest.cases

import taboolib.module.incision.annotation.Bypass
import taboolib.module.incision.annotation.Graft
import taboolib.module.incision.annotation.Site
import taboolib.module.incision.annotation.Surgeon
import taboolib.module.incision.annotation.Trim
import taboolib.module.incision.api.Anchor
import taboolib.module.incision.api.Shift
import taboolib.module.incision.api.Theatre

/**
 * Site / Graft / Bypass / Trim 覆盖用例。
 *
 * 验证锚点定位能力：INVOKE / FIELD_GET / FIELD_PUT / NEW / THROW / RETURN，
 * 配合 @Graft（在锚点插入）、@Bypass（替换调用）、@Trim（修整值）三种 advice。
 */
@Surgeon
object SurgeonSiteCases {

    private const val FIX = "top.maplex.incisiontest.fixture.SurgeonSiteTargetFixture"

    @Volatile var graftInvokeBeforeHits = 0
    @Volatile var graftInvokeAfterHits = 0
    @Volatile var graftFieldGetHits = 0
    @Volatile var graftFieldPutHits = 0
    @Volatile var graftNewHits = 0
    @Volatile var graftThrowHits = 0
    @Volatile var bypassHelperHits = 0
    @Volatile var trimReturnHits = 0
    @Volatile var trimArgHits = 0

    /** @Graft —— INVOKE 锚点 BEFORE：在调用 helper 之前触发 */
    @Graft(
        method = "$FIX#invokeHelper(int)int",
        site = Site(
            anchor = Anchor.INVOKE,
            target = "$FIX#helper(int)int",
            shift = Shift.BEFORE,
        ),
    )
    fun graftBeforeHelperInvoke(theatre: Theatre) {
        graftInvokeBeforeHits++
    }

    /** @Graft —— INVOKE 锚点 AFTER：在调用 helper 之后触发 */
    @Graft(
        method = "$FIX#invokeHelper(int)int",
        site = Site(
            anchor = Anchor.INVOKE,
            target = "$FIX#helper(int)int",
            shift = Shift.AFTER,
        ),
    )
    fun graftAfterHelperInvoke(theatre: Theatre) {
        graftInvokeAfterHits++
    }

    /** @Graft —— FIELD_GET：读取 counter 时触发 */
    @Graft(
        method = "$FIX#readCounter()int",
        site = Site(
            anchor = Anchor.FIELD_GET,
            target = "$FIX#counter:int",
        ),
    )
    fun graftOnFieldGet(theatre: Theatre) {
        graftFieldGetHits++
    }

    /** @Graft —— FIELD_PUT：写 counter 时触发 */
    @Graft(
        method = "$FIX#writeCounter(int)V",
        site = Site(
            anchor = Anchor.FIELD_PUT,
            target = "$FIX#counter:int",
        ),
    )
    fun graftOnFieldPut(theatre: Theatre) {
        graftFieldPutHits++
    }

    /** @Graft —— NEW：构造 StringBuilder 时触发 */
    @Graft(
        method = "$FIX#allocate()java.lang.StringBuilder",
        site = Site(
            anchor = Anchor.NEW,
            target = "java.lang.StringBuilder",
        ),
    )
    fun graftOnNew(theatre: Theatre) {
        graftNewHits++
    }

    /** @Graft —— THROW：抛异常时触发 */
    @Graft(
        method = "$FIX#throwIt()java.lang.String",
        site = Site(anchor = Anchor.THROW),
    )
    fun graftOnThrow(theatre: Theatre) {
        graftThrowHits++
    }

    /** @Bypass —— 替换 invokeHelper 内对 helper 的调用 */
    @Bypass(
        method = "$FIX#invokeHelper(int)int",
        site = Site(
            anchor = Anchor.INVOKE,
            target = "$FIX#helper(int)int",
        ),
    )
    fun bypassHelperInvoke(theatre: Theatre): Any? {
        bypassHelperHits++
        // 原 helper(x) = x+1，全部替换为固定 1000
        return 1000
    }

    /** @Trim RETURN —— 把 plainReturn 的返回值改写 */
    @Trim(
        method = "$FIX#plainReturn()int",
        kind = Trim.Kind.RETURN,
    )
    fun trimReturnPlain(theatre: Theatre): Any? {
        trimReturnHits++
        return 999
    }

    /** @Trim ARG —— 把 echo 的参数 msg 改写 */
    @Trim(
        method = "$FIX#echo(java.lang.String)java.lang.String",
        kind = Trim.Kind.ARG,
        index = 0,
    )
    fun trimEchoArg(theatre: Theatre): Any? {
        trimArgHits++
        return "trimmed"
    }

    fun reset() {
        graftInvokeBeforeHits = 0
        graftInvokeAfterHits = 0
        graftFieldGetHits = 0
        graftFieldPutHits = 0
        graftNewHits = 0
        graftThrowHits = 0
        bypassHelperHits = 0
        trimReturnHits = 0
        trimArgHits = 0
    }
}
