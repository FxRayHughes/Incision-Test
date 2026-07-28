package top.maplex.incisiontest.cases

import taboolib.module.incision.annotation.MatchMode

import taboolib.module.incision.annotation.SelectorKind

import taboolib.module.incision.annotation.Selector

import taboolib.module.incision.annotation.Pointcut

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
    @Graft(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "invokeHelper", descriptor = "(I)I")]),
        site = Site(
            anchor = Anchor.INVOKE,
            target = Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "helper", descriptor = "(I)I"),
            shift = Shift.BEFORE,
            ordinal = -1,
            maxMatches = -1,
        ),
    )
    fun graftBeforeHelperInvoke(theatre: Theatre) {
        graftInvokeBeforeHits++
    }

    /** @Graft —— INVOKE 锚点 AFTER：在调用 helper 之后触发 */
    @Graft(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "invokeHelper", descriptor = "(I)I")]),
        site = Site(
            anchor = Anchor.INVOKE,
            target = Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "helper", descriptor = "(I)I"),
            shift = Shift.AFTER,
            ordinal = -1,
            maxMatches = -1,
        ),
    )
    fun graftAfterHelperInvoke(theatre: Theatre) {
        graftInvokeAfterHits++
    }

    /** @Graft —— FIELD_GET：读取 counter 时触发 */
    @Graft(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "readCounter", descriptor = "()I")]),
        site = Site(
            anchor = Anchor.FIELD_GET,
            target = Selector(kind = SelectorKind.FIELD, owner = "$FIX", name = "counter", descriptor = "I"),
        ),
    )
    fun graftOnFieldGet(theatre: Theatre) {
        graftFieldGetHits++
    }

    /** @Graft —— FIELD_PUT：写 counter 时触发 */
    @Graft(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "writeCounter", descriptor = "(I)V")]),
        site = Site(
            anchor = Anchor.FIELD_PUT,
            target = Selector(kind = SelectorKind.FIELD, owner = "$FIX", name = "counter", descriptor = "I"),
        ),
    )
    fun graftOnFieldPut(theatre: Theatre) {
        graftFieldPutHits++
    }

    /** @Graft —— NEW：构造 StringBuilder 时触发 */
    @Graft(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "allocate", descriptor = "()Ljava/lang/StringBuilder;")]),
        site = Site(
            anchor = Anchor.NEW,
            target = Selector(kind = SelectorKind.CLASS, owner = "java/lang/StringBuilder"),
        ),
    )
    fun graftOnNew(theatre: Theatre) {
        graftNewHits++
    }

    /** @Graft —— THROW：抛异常时触发 */
    @Graft(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "throwIt", descriptor = "()Ljava/lang/String;")]),
        site = Site(anchor = Anchor.THROW),
    )
    fun graftOnThrow(theatre: Theatre) {
        graftThrowHits++
    }

    /** @Bypass —— 替换 invokeHelper 内对 helper 的调用 */
    @Bypass(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "invokeHelper", descriptor = "(I)I")]),
        site = Site(
            anchor = Anchor.INVOKE,
            target = Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "helper", descriptor = "(I)I"),
            ordinal = -1,
            maxMatches = -1,
        ),
    )
    fun bypassHelperInvoke(theatre: Theatre): Any? {
        bypassHelperHits++
        // 原 helper(x) = x+1，全部替换为固定 1000
        return 1000
    }

    /** @Trim RETURN —— 把 plainReturn 的返回值改写 */
    @Trim(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "plainReturn", descriptor = "()I")]),
        kind = Trim.Kind.RETURN,
    )
    fun trimReturnPlain(theatre: Theatre): Any? {
        trimReturnHits++
        return 999
    }

    /** @Trim ARG —— 把 echo 的参数 msg 改写 */
    @Trim(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "echo", descriptor = "(Ljava/lang/String;)Ljava/lang/String;")]),
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
