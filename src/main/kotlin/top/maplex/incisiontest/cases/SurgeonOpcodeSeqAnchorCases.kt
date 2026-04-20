package top.maplex.incisiontest.cases

import taboolib.module.incision.annotation.Graft
import taboolib.module.incision.annotation.InsnPattern
import taboolib.module.incision.annotation.Lead
import taboolib.module.incision.annotation.Op
import taboolib.module.incision.annotation.Site
import taboolib.module.incision.annotation.Step
import taboolib.module.incision.annotation.Surgeon
import taboolib.module.incision.api.Anchor
import taboolib.module.incision.api.Shift
import taboolib.module.incision.api.Theatre

/**
 * OpcodeSeq + 锚点组合测试 — Task #14。
 *
 * - HEAD anchor 不需要 target，只用 InsnPattern 进一步定位
 * - INVOKE anchor 配 target，可与 InsnPattern 叠加做更精准的过滤
 *
 * 这里既验证扫描器能正确解析两种组合，也验证 weaver 在两种锚点上都能尊重 pattern。
 */
@Suppress("unused")
@Surgeon
object SurgeonOpcodeSeqAnchorCases {

    @Volatile var headWithPatternHits = 0
    @Volatile var invokeWithPatternHits = 0
    @Volatile var headPatternIfeqHits = 0
    @Volatile var invokeWithGlobHits = 0
    @Volatile var invokeAfterPatternHits = 0

    fun reset() {
        headWithPatternHits = 0
        invokeWithPatternHits = 0
        headPatternIfeqHits = 0
        invokeWithGlobHits = 0
        invokeAfterPatternHits = 0
    }

    /** HEAD 锚点 + 单步 pattern (要求方法体里有 ICONST_5)。 */
    @Lead(
        scope = "method:top.maplex.incisiontest.fixture.ComplexOpcodeFixture#arithmetic(int)int",
        pattern = InsnPattern(steps = [Step(opcode = Op.ICONST_5)]),
        where = ""
    )
    fun onHeadWithPattern(theatre: Theatre) {
        headWithPatternHits++
    }

    /** HEAD 锚点 + IFEQ pattern (要求方法体里有跳转)。 */
    @Lead(
        scope = "method:top.maplex.incisiontest.fixture.ComplexOpcodeFixture#branch(boolean)int",
        pattern = InsnPattern(steps = [Step(opcode = Op.IFEQ)]),
        where = ""
    )
    fun onHeadPatternIfeq(theatre: Theatre) {
        headPatternIfeqHits++
    }

    /** INVOKE 锚点 + target + 显式 INVOKEVIRTUAL pattern。 */
    @Graft(
        method = "top.maplex.incisiontest.fixture.ComplexOpcodeFixture#chainInvoke()int",
        site = Site(
            anchor = Anchor.INVOKE,
            target = "top.maplex.incisiontest.fixture.ComplexOpcodeFixture#helper(int)int",
            shift = Shift.BEFORE,
            ordinal = -1
        ),
        pattern = InsnPattern(
            steps = [
                Step(opcode = Op.INVOKEVIRTUAL, name = "helper"),
            ]
        ),
        where = ""
    )
    fun onInvokeWithPattern(theatre: Theatre) {
        invokeWithPatternHits++
    }

    /** INVOKE 锚点 + target glob (`*ComplexOpcodeFixture*`) + name pattern。 */
    @Graft(
        method = "top.maplex.incisiontest.fixture.ComplexOpcodeFixture#chainInvoke()int",
        site = Site(
            anchor = Anchor.INVOKE,
            target = "*ComplexOpcodeFixture*#helper(*)*",
            shift = Shift.BEFORE,
            ordinal = -1
        ),
        pattern = InsnPattern(
            steps = [
                Step(opcode = Op.INVOKEVIRTUAL, name = "*"),
            ]
        ),
        where = ""
    )
    fun onInvokeWithGlob(theatre: Theatre) {
        invokeWithGlobHits++
    }

    /** INVOKE 锚点 + AFTER + pattern。 */
    @Graft(
        method = "top.maplex.incisiontest.fixture.ComplexOpcodeFixture#chainInvoke()int",
        site = Site(
            anchor = Anchor.INVOKE,
            target = "top.maplex.incisiontest.fixture.ComplexOpcodeFixture#helper(int)int",
            shift = Shift.AFTER,
            ordinal = 0
        ),
        pattern = InsnPattern(steps = [Step(opcode = Op.INVOKEVIRTUAL, name = "helper")]),
        where = ""
    )
    fun onInvokeAfterPattern(theatre: Theatre) {
        invokeAfterPatternHits++
    }
}
