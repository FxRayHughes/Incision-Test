package top.maplex.incisiontest.cases

import taboolib.module.incision.annotation.MatchMode

import taboolib.module.incision.annotation.SelectorKind

import taboolib.module.incision.annotation.Selector

import taboolib.module.incision.annotation.Pointcut
import taboolib.module.incision.annotation.PatternMode

import taboolib.module.incision.annotation.InsnPattern
import taboolib.module.incision.annotation.Lead
import taboolib.module.incision.annotation.Op
import taboolib.module.incision.annotation.Step
import taboolib.module.incision.annotation.Surgeon
import taboolib.module.incision.api.Theatre

/**
 * InsnPattern 序列匹配测试 — Task #14。
 *
 * 覆盖：
 * - 单条 Step（精确 opcode / glob owner-name-desc / cst / repeat）
 * - 多 Step 序列（2/3/4 步）
 * - Op.ANY 通配
 * - 各类常见 opcode（NOP/常量族/LDC/INVOKE 各形态/字段读写/对象构造/类型检查/跳转/RETURN）
 *
 * weaver 接通指令录制后，advice 仅在匹配的指令位置触发。
 */
@Suppress("unused")
@Surgeon
object SurgeonInsnPatternCases {

    // 旧字段保留，向后兼容 surgeon-insn-pattern 用例
    @Volatile var patternHits = 0

    // 新增：每条 advice 独立计数，便于断言
    @Volatile var arithmeticIconst5Hits = 0
    @Volatile var arithmeticSeq2Hits = 0
    @Volatile var arithmeticSeq4Hits = 0
    @Volatile var arithmeticAnyHits = 0
    @Volatile var ldcStringHits = 0
    @Volatile var ldcCstFilteredHits = 0
    @Volatile var fieldGetHits = 0
    @Volatile var fieldPutHits = 0
    @Volatile var newStringBuilderHits = 0
    @Volatile var invokeStaticMathHits = 0
    @Volatile var invokeVirtualGlobHits = 0
    @Volatile var newArrayHits = 0
    @Volatile var aNewArrayHits = 0
    @Volatile var instanceOfHits = 0
    @Volatile var checkCastHits = 0
    @Volatile var ifeqHits = 0
    @Volatile var gotoHits = 0
    @Volatile var athrowHits = 0
    @Volatile var returnHits = 0
    @Volatile var iconstFamilyRepeatHits = 0
    @Volatile var seq3Hits = 0

    // 追加覆盖
    @Volatile var emptyStepsHits = 0
    @Volatile var seq5Hits = 0
    @Volatile var repeat2Hits = 0
    @Volatile var repeat5Hits = 0
    @Volatile var cstNumericHits = 0
    @Volatile var globOwnerJavaLangHits = 0
    @Volatile var globNameSuffixHits = 0
    @Volatile var globDescHits = 0
    @Volatile var invokeInterfaceHits = 0
    @Volatile var invokeSpecialHits = 0
    @Volatile var arrayLengthHits = 0
    @Volatile var nopHits = 0
    @Volatile var dupHits = 0
    @Volatile var descExactHits = 0

    fun reset() {
        patternHits = 0
        arithmeticIconst5Hits = 0
        arithmeticSeq2Hits = 0
        arithmeticSeq4Hits = 0
        arithmeticAnyHits = 0
        ldcStringHits = 0
        ldcCstFilteredHits = 0
        fieldGetHits = 0
        fieldPutHits = 0
        newStringBuilderHits = 0
        invokeStaticMathHits = 0
        invokeVirtualGlobHits = 0
        newArrayHits = 0
        aNewArrayHits = 0
        instanceOfHits = 0
        checkCastHits = 0
        ifeqHits = 0
        gotoHits = 0
        athrowHits = 0
        returnHits = 0
        iconstFamilyRepeatHits = 0
        seq3Hits = 0
        emptyStepsHits = 0
        seq5Hits = 0
        repeat2Hits = 0
        repeat5Hits = 0
        cstNumericHits = 0
        globOwnerJavaLangHits = 0
        globNameSuffixHits = 0
        globDescHits = 0
        invokeInterfaceHits = 0
        invokeSpecialHits = 0
        arrayLengthHits = 0
        nopHits = 0
        dupHits = 0
        descExactHits = 0
    }

    // ---- 旧 advice：保留以驱动 surgeon-insn-pattern 用例 ----
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "top/maplex/incisiontest/fixture/InsnPatternFixture", name = "accumulate", descriptor = "(I)I")]),
        pattern = InsnPattern(
            mode = PatternMode.ORDERED_SUBSEQUENCE,
            steps = [
                Step(opcode = Op.ICONST_5),
                Step(opcode = Op.ISTORE),
                Step(opcode = Op.ILOAD),
                Step(opcode = Op.IADD),
            ]
        ),
        predicate = ""
    )
    fun onAccumulatePattern(theatre: Theatre) {
        patternHits++
    }

    // ---- 单 Step：精确 opcode ----
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "top/maplex/incisiontest/fixture/ComplexOpcodeFixture", name = "arithmetic", descriptor = "(I)I")]),
        pattern = InsnPattern(steps = [Step(opcode = Op.ICONST_5)]),
        predicate = ""
    )
    fun onArithmeticIconst5(theatre: Theatre) {
        arithmeticIconst5Hits++
    }

    // ---- 多 Step：2 步序列 ----
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "top/maplex/incisiontest/fixture/ComplexOpcodeFixture", name = "arithmetic", descriptor = "(I)I")]),
        pattern = InsnPattern(
            mode = PatternMode.ORDERED_SUBSEQUENCE,
            steps = [
                Step(opcode = Op.ICONST_5),
                Step(opcode = Op.ISTORE),
            ]
        ),
        predicate = ""
    )
    fun onArithmeticSeq2(theatre: Theatre) {
        arithmeticSeq2Hits++
    }

    // ---- 多 Step：4 步序列 ----
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "top/maplex/incisiontest/fixture/ComplexOpcodeFixture", name = "arithmetic", descriptor = "(I)I")]),
        pattern = InsnPattern(
            mode = PatternMode.ORDERED_SUBSEQUENCE,
            steps = [
                Step(opcode = Op.ICONST_5),
                Step(opcode = Op.ISTORE),
                Step(opcode = Op.ILOAD),
                Step(opcode = Op.IADD),
            ]
        ),
        predicate = ""
    )
    fun onArithmeticSeq4(theatre: Theatre) {
        arithmeticSeq4Hits++
    }

    // ---- 多 Step：3 步 + ANY 通配 ----
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "top/maplex/incisiontest/fixture/ComplexOpcodeFixture", name = "arithmetic", descriptor = "(I)I")]),
        pattern = InsnPattern(
            mode = PatternMode.ORDERED_SUBSEQUENCE,
            steps = [
                Step(opcode = Op.ICONST_5),
                Step(opcode = Op.ANY),
                Step(opcode = Op.ILOAD),
            ]
        ),
        predicate = ""
    )
    fun onArithmeticAny(theatre: Theatre) {
        arithmeticAnyHits++
    }

    // ---- repeat>1：连续多个 ICONST_* / ISTORE 模式 ----
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "top/maplex/incisiontest/fixture/ComplexOpcodeFixture", name = "arithmetic", descriptor = "(I)I")]),
        pattern = InsnPattern(
            mode = PatternMode.ORDERED_SUBSEQUENCE,
            steps = [
                Step(opcode = Op.ANY, repeat = 3),
                Step(opcode = Op.ICONST_5),
            ]
        ),
        predicate = ""
    )
    fun onIconstFamilyRepeat(theatre: Theatre) {
        iconstFamilyRepeatHits++
    }

    // ---- LDC：纯 opcode ----
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "top/maplex/incisiontest/fixture/ComplexOpcodeFixture", name = "loadStrings", descriptor = "()Ljava/lang/String;")]),
        pattern = InsnPattern(steps = [Step(opcode = Op.LDC)]),
        predicate = ""
    )
    fun onLdcString(theatre: Theatre) {
        ldcStringHits++
    }

    // ---- LDC + cst 常量过滤 ----
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "top/maplex/incisiontest/fixture/ComplexOpcodeFixture", name = "loadStrings", descriptor = "()Ljava/lang/String;")]),
        pattern = InsnPattern(steps = [Step(opcode = Op.LDC, cst = "incision-tag")]),
        predicate = ""
    )
    fun onLdcCstFiltered(theatre: Theatre) {
        ldcCstFilteredHits++
    }

    // ---- GETFIELD / PUTFIELD ----
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "top/maplex/incisiontest/fixture/ComplexOpcodeFixture", name = "touchFields", descriptor = "()I")]),
        pattern = InsnPattern(steps = [Step(opcode = Op.GETFIELD, name = "counter")]),
        predicate = ""
    )
    fun onFieldGet(theatre: Theatre) {
        fieldGetHits++
    }

    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "top/maplex/incisiontest/fixture/ComplexOpcodeFixture", name = "touchFields", descriptor = "()I")]),
        pattern = InsnPattern(steps = [Step(opcode = Op.PUTFIELD, name = "counter")]),
        predicate = ""
    )
    fun onFieldPut(theatre: Theatre) {
        fieldPutHits++
    }

    // ---- NEW + glob owner ----
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "top/maplex/incisiontest/fixture/ComplexOpcodeFixture", name = "buildBuffer", descriptor = "()Ljava/lang/String;")]),
        pattern = InsnPattern(steps = [Step(opcode = Op.NEW, owner = "*StringBuilder*")]),
        predicate = ""
    )
    fun onNewStringBuilder(theatre: Theatre) {
        newStringBuilderHits++
    }

    // ---- INVOKEVIRTUAL + name glob ----
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "top/maplex/incisiontest/fixture/ComplexOpcodeFixture", name = "buildBuffer", descriptor = "()Ljava/lang/String;")]),
        pattern = InsnPattern(steps = [Step(opcode = Op.INVOKEVIRTUAL, name = "append")]),
        predicate = ""
    )
    fun onInvokeVirtualGlob(theatre: Theatre) {
        invokeVirtualGlobHits++
    }

    // ---- INVOKESTATIC + 精确 owner-name ----
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "top/maplex/incisiontest/fixture/ComplexOpcodeFixture", name = "staticCalls", descriptor = "()I")]),
        pattern = InsnPattern(
            steps = [
                Step(opcode = Op.INVOKESTATIC, owner = "java/lang/Math", name = "max"),
            ]
        ),
        predicate = ""
    )
    fun onInvokeStaticMath(theatre: Theatre) {
        invokeStaticMathHits++
    }

    // ---- NEWARRAY ----
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "top/maplex/incisiontest/fixture/ComplexOpcodeFixture", name = "arrays", descriptor = "()I")]),
        pattern = InsnPattern(steps = [Step(opcode = Op.NEWARRAY)]),
        predicate = ""
    )
    fun onNewArray(theatre: Theatre) {
        newArrayHits++
    }

    // ---- ANEWARRAY ----
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "top/maplex/incisiontest/fixture/ComplexOpcodeFixture", name = "arrays", descriptor = "()I")]),
        pattern = InsnPattern(steps = [Step(opcode = Op.ANEWARRAY)]),
        predicate = ""
    )
    fun onANewArray(theatre: Theatre) {
        aNewArrayHits++
    }

    // ---- INSTANCEOF / CHECKCAST ----
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "top/maplex/incisiontest/fixture/ComplexOpcodeFixture", name = "typeCheck", descriptor = "(Ljava/lang/Object;)I")]),
        pattern = InsnPattern(steps = [Step(opcode = Op.INSTANCEOF)]),
        predicate = ""
    )
    fun onInstanceOf(theatre: Theatre) {
        instanceOfHits++
    }

    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "top/maplex/incisiontest/fixture/ComplexOpcodeFixture", name = "typeCheck", descriptor = "(Ljava/lang/Object;)I")]),
        pattern = InsnPattern(steps = [Step(opcode = Op.CHECKCAST)]),
        predicate = ""
    )
    fun onCheckCast(theatre: Theatre) {
        checkCastHits++
    }

    // ---- IFEQ / GOTO ----
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "top/maplex/incisiontest/fixture/ComplexOpcodeFixture", name = "branch", descriptor = "(Z)I")]),
        pattern = InsnPattern(steps = [Step(opcode = Op.IFEQ)]),
        predicate = ""
    )
    fun onIfeq(theatre: Theatre) {
        ifeqHits++
    }

    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "top/maplex/incisiontest/fixture/ComplexOpcodeFixture", name = "branch", descriptor = "(Z)I")]),
        pattern = InsnPattern(steps = [Step(opcode = Op.GOTO)]),
        predicate = ""
    )
    fun onGoto(theatre: Theatre) {
        gotoHits++
    }

    // ---- ATHROW ----
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "top/maplex/incisiontest/fixture/ComplexOpcodeFixture", name = "throwIt", descriptor = "()Ljava/lang/String;")]),
        pattern = InsnPattern(steps = [Step(opcode = Op.ATHROW)]),
        predicate = ""
    )
    fun onAthrow(theatre: Theatre) {
        athrowHits++
    }

    // ---- RETURN（IRETURN）----
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "top/maplex/incisiontest/fixture/ComplexOpcodeFixture", name = "arithmetic", descriptor = "(I)I")]),
        pattern = InsnPattern(steps = [Step(opcode = Op.IRETURN)]),
        predicate = ""
    )
    fun onReturn(theatre: Theatre) {
        returnHits++
    }

    // ---- 3 步序列：LDC; LDC; INVOKEVIRTUAL ----
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "top/maplex/incisiontest/fixture/ComplexOpcodeFixture", name = "loadStrings", descriptor = "()Ljava/lang/String;")]),
        pattern = InsnPattern(
            mode = PatternMode.ORDERED_SUBSEQUENCE,
            steps = [
                Step(opcode = Op.LDC),
                Step(opcode = Op.LDC),
                Step(opcode = Op.INVOKEVIRTUAL),
            ]
        ),
        predicate = ""
    )
    fun onSeq3(theatre: Theatre) {
        seq3Hits++
    }

    // ---- 空 steps 数组（退化为无模式匹配） ----
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "top/maplex/incisiontest/fixture/ComplexOpcodeFixture", name = "emptyBody", descriptor = "()V")]),
        pattern = InsnPattern(steps = []),
        predicate = ""
    )
    fun onEmptyPattern(theatre: Theatre) {
        emptyStepsHits++
    }

    // ---- 5 步序列 ----
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "top/maplex/incisiontest/fixture/ComplexOpcodeFixture", name = "densePattern", descriptor = "()I")]),
        pattern = InsnPattern(
            mode = PatternMode.ORDERED_SUBSEQUENCE,
            steps = [
                Step(opcode = Op.ICONST_0),
                Step(opcode = Op.ICONST_1),
                Step(opcode = Op.IADD),
                Step(opcode = Op.ISTORE),
                Step(opcode = Op.ILOAD),
            ]
        ),
        predicate = ""
    )
    fun onSeq5(theatre: Theatre) {
        seq5Hits++
    }

    // ---- repeat = 2 ----
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "top/maplex/incisiontest/fixture/ComplexOpcodeFixture", name = "multiFieldPut", descriptor = "()I")]),
        pattern = InsnPattern(mode = PatternMode.ORDERED_SUBSEQUENCE, steps = [Step(opcode = Op.PUTFIELD, repeat = 2)]),
        predicate = ""
    )
    fun onRepeat2(theatre: Theatre) {
        repeat2Hits++
    }

    // ---- repeat = 5 ----
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "top/maplex/incisiontest/fixture/ComplexOpcodeFixture", name = "multiFieldPut", descriptor = "()I")]),
        pattern = InsnPattern(mode = PatternMode.ORDERED_SUBSEQUENCE, steps = [Step(opcode = Op.PUTFIELD, repeat = 5)]),
        predicate = ""
    )
    fun onRepeat5(theatre: Theatre) {
        repeat5Hits++
    }

    // ---- cst 数字过滤 ----
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "top/maplex/incisiontest/fixture/ComplexOpcodeFixture", name = "numberLdc", descriptor = "()J")]),
        pattern = InsnPattern(steps = [Step(opcode = Op.LDC, cst = "12345678901")]),
        predicate = ""
    )
    fun onCstNumeric(theatre: Theatre) {
        cstNumericHits++
    }

    // ---- glob owner = `java/lang/*` ----
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "top/maplex/incisiontest/fixture/ComplexOpcodeFixture", name = "staticCalls", descriptor = "()I")]),
        pattern = InsnPattern(steps = [Step(opcode = Op.INVOKESTATIC, owner = "java/lang/*")]),
        predicate = ""
    )
    fun onGlobOwnerJavaLang(theatre: Theatre) {
        globOwnerJavaLangHits++
    }

    // ---- glob name 后缀 `*max` ----
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "top/maplex/incisiontest/fixture/ComplexOpcodeFixture", name = "staticCalls", descriptor = "()I")]),
        pattern = InsnPattern(steps = [Step(opcode = Op.INVOKESTATIC, name = "*max")]),
        predicate = ""
    )
    fun onGlobNameSuffix(theatre: Theatre) {
        globNameSuffixHits++
    }

    // ---- glob desc `(*)int` ----
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "top/maplex/incisiontest/fixture/ComplexOpcodeFixture", name = "staticCalls", descriptor = "()I")]),
        pattern = InsnPattern(steps = [Step(opcode = Op.INVOKESTATIC, desc = "(*)int")]),
        predicate = ""
    )
    fun onGlobDesc(theatre: Theatre) {
        globDescHits++
    }

    // ---- desc 精确匹配 `(II)I` ----
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "top/maplex/incisiontest/fixture/ComplexOpcodeFixture", name = "staticCalls", descriptor = "()I")]),
        pattern = InsnPattern(steps = [Step(opcode = Op.INVOKESTATIC, desc = "(II)I")]),
        predicate = ""
    )
    fun onDescExact(theatre: Theatre) {
        descExactHits++
    }

    // ---- INVOKEINTERFACE ----
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "top/maplex/incisiontest/fixture/ComplexOpcodeFixture", name = "interfaceCall", descriptor = "(Ljava/util/List;)I")]),
        pattern = InsnPattern(steps = [Step(opcode = Op.INVOKEINTERFACE)]),
        predicate = ""
    )
    fun onInvokeInterface(theatre: Theatre) {
        invokeInterfaceHits++
    }

    // ---- INVOKESPECIAL（constructor） ----
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "top/maplex/incisiontest/fixture/ComplexOpcodeFixture", name = "buildBuffer", descriptor = "()Ljava/lang/String;")]),
        pattern = InsnPattern(steps = [Step(opcode = Op.INVOKESPECIAL, name = "<init>")]),
        predicate = ""
    )
    fun onInvokeSpecial(theatre: Theatre) {
        invokeSpecialHits++
    }

    // ---- ARRAYLENGTH ----
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "top/maplex/incisiontest/fixture/ComplexOpcodeFixture", name = "arrays", descriptor = "()I")]),
        pattern = InsnPattern(mode = PatternMode.ORDERED_SUBSEQUENCE, steps = [Step(opcode = Op.ARRAYLENGTH, repeat = 2)]),
        predicate = ""
    )
    fun onArrayLength(theatre: Theatre) {
        arrayLengthHits++
    }

    // ---- NOP（Op 枚举存在但通常无 NOP 指令，作为冷门 opcode 样本） ----
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "top/maplex/incisiontest/fixture/ComplexOpcodeFixture", name = "emptyBody", descriptor = "()V")]),
        pattern = InsnPattern(steps = [Step(opcode = Op.NOP)]),
        predicate = ""
    )
    fun onNop(theatre: Theatre) {
        nopHits++
    }

    // ---- DUP ----
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "top/maplex/incisiontest/fixture/ComplexOpcodeFixture", name = "buildBuffer", descriptor = "()Ljava/lang/String;")]),
        pattern = InsnPattern(steps = [Step(opcode = Op.DUP)]),
        predicate = ""
    )
    fun onDup(theatre: Theatre) {
        dupHits++
    }
}
