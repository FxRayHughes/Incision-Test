package top.maplex.incisiontest.cases

import taboolib.module.incision.annotation.MatchMode

import taboolib.module.incision.annotation.SelectorKind

import taboolib.module.incision.annotation.Selector

import taboolib.module.incision.annotation.Pointcut

import taboolib.module.incision.annotation.Bypass
import taboolib.module.incision.annotation.Graft
import taboolib.module.incision.annotation.Lead
import taboolib.module.incision.annotation.Site
import taboolib.module.incision.annotation.Splice
import taboolib.module.incision.annotation.Surgeon
import taboolib.module.incision.annotation.Trail
import taboolib.module.incision.annotation.Trim
import taboolib.module.incision.api.Anchor
import taboolib.module.incision.api.Theatre

/**
 * Task #13 第二批 —— scope DSL 与描述符变种网格。
 *
 * 覆盖：
 * - @Lead / @Trail / @Splice 的 scope: `method:` 完整描述符 / `method:` 带 `(*)` 通配 / `class:` 前缀
 * - @Bypass / @Graft / @Trim 的 method 字段：完整描述符 / `(*)` 通配
 *
 * 「无前缀」与畸形 scope 由 trauma 用例覆盖（见 testTraumaBadScope）。
 */
@Surgeon
object SurgeonAnchorMatrixScopeCases {

    private const val FIX = "top.maplex.incisiontest.fixture.AnchorMatrixFixture_Scope"

    @Volatile var leadFullHits = 0
    @Volatile var leadWildHits = 0
    @Volatile var leadStarTargetHits = 0
    @Volatile var trailClassPrefixA = 0
    @Volatile var trailClassPrefixB = 0
    @Volatile var spliceFullHits = 0
    @Volatile var bypassFullDescHits = 0
    @Volatile var bypassWildDescHits = 0
    @Volatile var graftFullDescHits = 0
    @Volatile var trimFullDescHits = 0

    /** scope: 完整 method 描述符 */
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "scopeMethodFull", descriptor = "(Ljava/lang/String;)Ljava/lang/String;")]))
    fun leadFullDescriptor(theatre: Theatre) { leadFullHits++ }

    /** scope: 带 `(*)` 通配实参 */
    @Lead(scope = "method:$FIX#scopeMethodWild(*)")
    fun leadWildArgs(theatre: Theatre) { leadWildHits++ }

    /** scope: `*` 通配方法名 */
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "scopeStarTarget", descriptor = "()Ljava/lang/String;")]))
    fun leadExactTarget(theatre: Theatre) { leadStarTargetHits++ }

    /** scope: `class:` 前缀 + `method:` 联合（And） —— scopeClassA */
    @Trail(pointcut = Pointcut(allOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "scopeClassA", descriptor = "()I")]),
        onThrow = false,
    )
    fun trailClassAndMethodA(theatre: Theatre) { trailClassPrefixA++ }

    /** scope: `class:` 前缀 + `method:` 联合 —— scopeClassB */
    @Trail(pointcut = Pointcut(allOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "scopeClassB", descriptor = "()I")]),
        onThrow = false,
    )
    fun trailClassAndMethodB(theatre: Theatre) { trailClassPrefixB++ }

    /** @Splice scope: 完整 method 描述符 */
    @Splice(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "scopeShorthandArgs", descriptor = "(Ljava/lang/String;I)Ljava/lang/String;")]))
    fun spliceFull(theatre: Theatre): Any? {
        spliceFullHits++
        return theatre.resume.proceed()
    }

    /** @Bypass.method: 完整描述符 —— 替换 inner1 调用 */
    @Bypass(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "scopeInvokeHelper", descriptor = "()I")]),
        site = Site(
            anchor = Anchor.INVOKE,
            target = Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "inner1", descriptor = "()I"),
        ),
    )
    fun bypassFullDescriptor(theatre: Theatre): Any? {
        bypassFullDescHits++
        return 500
    }

    /** @Graft.method: 完整描述符 —— 在 inner2 调用前触发 */
    @Graft(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "scopeInvokeHelper", descriptor = "()I")]),
        site = Site(
            anchor = Anchor.INVOKE,
            target = Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "inner2", descriptor = "()I"),
        ),
    )
    fun graftFullDescriptor(theatre: Theatre) { graftFullDescHits++ }

    /** @Trim.method: 完整描述符 + ARG index=0。与 @Splice 拆到独立目标，避免同方法长期互相污染。 */
    @Trim(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "scopeTrimArgs", descriptor = "(Ljava/lang/String;I)Ljava/lang/String;")]),
        kind = Trim.Kind.ARG,
        index = 0,
    )
    fun trimFullDescriptor(theatre: Theatre): Any? {
        trimFullDescHits++
        return "scoped-arg"
    }

    fun reset() {
        leadFullHits = 0
        leadWildHits = 0
        leadStarTargetHits = 0
        trailClassPrefixA = 0
        trailClassPrefixB = 0
        spliceFullHits = 0
        bypassFullDescHits = 0
        bypassWildDescHits = 0
        graftFullDescHits = 0
        trimFullDescHits = 0
    }
}
