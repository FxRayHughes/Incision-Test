package top.maplex.incisiontest

import taboolib.module.incision.annotation.Op
import taboolib.module.incision.annotation.SurgeryDesk
import top.maplex.incisiontest.cases.SurgeonInsnPatternCases
import top.maplex.incisiontest.cases.SurgeonOffsetCases
import top.maplex.incisiontest.cases.SurgeonOpcodeSeqAnchorCases
import top.maplex.incisiontest.fixture.ComplexOpcodeFixture
import top.maplex.incisiontest.fixture.InsnPatternFixture
import top.maplex.incisiontest.fixture.OffsetFixture

/**
 * Task #14 — InsnPattern × Step × Op × Site.offset 全覆盖测试套件。
 *
 * 单独成对象避免与 AllCases 主表频繁合并冲突；由 AllCases.entries 直接引用方法。
 *
 * recording 流水线 (#18) 已接通：scan→match→plan→insert→Replayer.replay。
 * 全面使用硬断言；若失败即暴露 weaver / recording 行为差异。
 */
@SurgeryDesk
object Pattern14Cases {

    // ---- InsnPattern ----

    fun testInsnPatternIconst5(): CaseResult = runCase("insn-pattern-iconst5") { a, _ ->
        SurgeonInsnPatternCases.reset()
        ComplexOpcodeFixture().arithmetic(1)
        a.equal(1, SurgeonInsnPatternCases.arithmeticIconst5Hits, "ICONST_5 单步命中 1 次")
    }

    fun testInsnPatternSeq2(): CaseResult = runCase("insn-pattern-seq2") { a, _ ->
        SurgeonInsnPatternCases.reset()
        ComplexOpcodeFixture().arithmetic(2)
        a.equal(1, SurgeonInsnPatternCases.arithmeticSeq2Hits, "ICONST_5;ISTORE 序列命中 1 次")
    }

    fun testInsnPatternSeq3(): CaseResult = runCase("insn-pattern-seq3") { a, _ ->
        SurgeonInsnPatternCases.reset()
        ComplexOpcodeFixture().loadStrings()
        a.equal(1, SurgeonInsnPatternCases.seq3Hits, "LDC;LDC;INVOKEVIRTUAL 序列命中 1 次")
    }

    fun testInsnPatternSeq4(): CaseResult = runCase("insn-pattern-seq4") { a, _ ->
        SurgeonInsnPatternCases.reset()
        ComplexOpcodeFixture().arithmetic(0)
        a.equal(1, SurgeonInsnPatternCases.arithmeticSeq4Hits, "ICONST_5;ISTORE;ILOAD;IADD 序列命中 1 次")
    }

    fun testInsnPatternAny(): CaseResult = runCase("insn-pattern-any") { a, _ ->
        SurgeonInsnPatternCases.reset()
        ComplexOpcodeFixture().arithmetic(3)
        a.check(SurgeonInsnPatternCases.arithmeticAnyHits >= 1, "Op.ANY 通配应至少命中 1 次")
    }

    fun testInsnPatternRepeat(): CaseResult = runCase("insn-pattern-repeat") { a, _ ->
        SurgeonInsnPatternCases.reset()
        ComplexOpcodeFixture().arithmetic(0)
        a.check(SurgeonInsnPatternCases.iconstFamilyRepeatHits >= 1, "ANY repeat=3 + ICONST_5 应命中")
    }

    fun testInsnPatternLdc(): CaseResult = runCase("insn-pattern-ldc") { a, _ ->
        SurgeonInsnPatternCases.reset()
        ComplexOpcodeFixture().loadStrings()
        a.check(SurgeonInsnPatternCases.ldcStringHits >= 1, "LDC 单步命中 (任意常量)")
    }

    fun testInsnPatternLdcCst(): CaseResult = runCase("insn-pattern-ldc-cst") { a, _ ->
        SurgeonInsnPatternCases.reset()
        ComplexOpcodeFixture().loadStrings()
        a.equal(1, SurgeonInsnPatternCases.ldcCstFilteredHits, "LDC + cst=incision-tag 精准命中 1 次")
    }

    fun testInsnPatternGetField(): CaseResult = runCase("insn-pattern-getfield") { a, _ ->
        SurgeonInsnPatternCases.reset()
        ComplexOpcodeFixture().touchFields()
        a.check(SurgeonInsnPatternCases.fieldGetHits >= 1, "GETFIELD counter 命中")
    }

    fun testInsnPatternPutField(): CaseResult = runCase("insn-pattern-putfield") { a, _ ->
        SurgeonInsnPatternCases.reset()
        ComplexOpcodeFixture().touchFields()
        a.check(SurgeonInsnPatternCases.fieldPutHits >= 1, "PUTFIELD counter 命中")
    }

    fun testInsnPatternNew(): CaseResult = runCase("insn-pattern-new") { a, _ ->
        SurgeonInsnPatternCases.reset()
        ComplexOpcodeFixture().buildBuffer()
        a.equal(1, SurgeonInsnPatternCases.newStringBuilderHits, "NEW StringBuilder 命中 1 次")
    }

    fun testInsnPatternInvokeVirtualGlob(): CaseResult = runCase("insn-pattern-invokevirtual-glob") { a, _ ->
        SurgeonInsnPatternCases.reset()
        ComplexOpcodeFixture().buildBuffer()
        a.check(SurgeonInsnPatternCases.invokeVirtualGlobHits >= 2, "append 至少命中 2 次")
    }

    fun testInsnPatternInvokeStatic(): CaseResult = runCase("insn-pattern-invokestatic") { a, _ ->
        SurgeonInsnPatternCases.reset()
        ComplexOpcodeFixture().staticCalls()
        a.equal(1, SurgeonInsnPatternCases.invokeStaticMathHits, "INVOKESTATIC Math.max 命中 1 次")
    }

    fun testInsnPatternNewArray(): CaseResult = runCase("insn-pattern-newarray") { a, _ ->
        SurgeonInsnPatternCases.reset()
        ComplexOpcodeFixture().arrays()
        a.check(SurgeonInsnPatternCases.newArrayHits >= 1, "NEWARRAY 命中")
    }

    fun testInsnPatternANewArray(): CaseResult = runCase("insn-pattern-anewarray") { a, _ ->
        SurgeonInsnPatternCases.reset()
        ComplexOpcodeFixture().arrays()
        a.check(SurgeonInsnPatternCases.aNewArrayHits >= 1, "ANEWARRAY 命中")
    }

    fun testInsnPatternInstanceOf(): CaseResult = runCase("insn-pattern-instanceof") { a, _ ->
        SurgeonInsnPatternCases.reset()
        ComplexOpcodeFixture().typeCheck("hi")
        a.check(SurgeonInsnPatternCases.instanceOfHits >= 1, "INSTANCEOF 命中")
    }

    fun testInsnPatternCheckCast(): CaseResult = runCase("insn-pattern-checkcast") { a, _ ->
        SurgeonInsnPatternCases.reset()
        ComplexOpcodeFixture().typeCheck("hi")
        a.check(SurgeonInsnPatternCases.checkCastHits >= 1, "CHECKCAST 命中")
    }

    fun testInsnPatternIfeq(): CaseResult = runCase("insn-pattern-ifeq") { a, _ ->
        SurgeonInsnPatternCases.reset()
        ComplexOpcodeFixture().branch(true)
        a.check(SurgeonInsnPatternCases.ifeqHits >= 1, "IFEQ 命中")
    }

    fun testInsnPatternGoto(): CaseResult = runCase("insn-pattern-goto") { a, _ ->
        SurgeonInsnPatternCases.reset()
        ComplexOpcodeFixture().branch(true)
        // kotlinc 对 if(flag) return X else return Y 直接 IFEQ + IRETURN/IRETURN，无 GOTO
        a.equal(0, SurgeonInsnPatternCases.gotoHits, "branch() 反编译无 GOTO，命中 0 次")
    }

    fun testInsnPatternAthrow(): CaseResult = runCase("insn-pattern-athrow") { a, _ ->
        SurgeonInsnPatternCases.reset()
        ComplexOpcodeFixture().throwIt()
        a.check(SurgeonInsnPatternCases.athrowHits >= 1, "ATHROW 命中")
    }

    fun testInsnPatternReturn(): CaseResult = runCase("insn-pattern-ireturn") { a, _ ->
        SurgeonInsnPatternCases.reset()
        ComplexOpcodeFixture().arithmetic(1)
        a.check(SurgeonInsnPatternCases.returnHits >= 1, "IRETURN 命中")
    }

    fun testInsnPatternAccumulate(): CaseResult = runCase("insn-pattern-accumulate") { a, _ ->
        SurgeonInsnPatternCases.reset()
        val r = InsnPatternFixture().accumulate(10)
        a.equal(15, r, "原方法返回 input + 5")
        a.equal(1, SurgeonInsnPatternCases.patternHits, "ICONST_5;ISTORE;ILOAD;IADD 序列命中 1 次")
    }

    // ---- Offset ----

    fun testOffsetZero(): CaseResult = runCase("offset-zero") { a, _ ->
        SurgeonOffsetCases.reset()
        OffsetFixture().chain()
        a.equal(1, SurgeonOffsetCases.offsetZeroHits, "offset=0 等价于无偏移，命中 1 次")
    }

    fun testOffsetAfter1All(): CaseResult = runCase("offset-after-1-all") { a, _ ->
        SurgeonOffsetCases.reset()
        OffsetFixture().chainWithGap()
        a.equal(3, SurgeonOffsetCases.afterOffset1Hits, "AFTER+offset=+1 ordinal=-1 命中 3 次")
    }

    fun testOffsetAfter2All(): CaseResult = runCase("offset-after-2-all") { a, _ ->
        SurgeonOffsetCases.reset()
        OffsetFixture().chainWithGap()
        a.equal(3, SurgeonOffsetCases.afterOffset2Hits, "AFTER+offset=+2 ordinal=-1 命中 3 次")
    }

    fun testOffsetBeforeNeg1(): CaseResult = runCase("offset-before-neg-1") { a, _ ->
        SurgeonOffsetCases.reset()
        OffsetFixture().chainWithGap()
        a.check(SurgeonOffsetCases.beforeNegOffsetHits >= 1, "BEFORE+offset=-1 至少命中 1 次")
    }

    fun testOffsetOrdinal0After1(): CaseResult = runCase("offset-ordinal0-after-1") { a, _ ->
        SurgeonOffsetCases.reset()
        OffsetFixture().chainWithGap()
        a.equal(1, SurgeonOffsetCases.ordinal0AfterOffset1Hits, "ordinal=0 + AFTER + offset=+1 命中 1 次")
    }

    fun testOffsetOrdinal2After2(): CaseResult = runCase("offset-ordinal2-after-2") { a, _ ->
        SurgeonOffsetCases.reset()
        OffsetFixture().chainWithGap()
        a.equal(1, SurgeonOffsetCases.ordinal2AfterOffset2Hits, "ordinal=2 + AFTER + offset=+2 命中 1 次")
    }

    fun testOffsetAnyOrdinal(): CaseResult = runCase("offset-any-ordinal") { a, _ ->
        SurgeonOffsetCases.reset()
        OffsetFixture().chain()
        a.equal(3, SurgeonOffsetCases.anyOrdinalHits, "ordinal=-1 命中所有 3 次 helper 调用")
    }

    // ---- OpcodeSeq + anchor ----

    fun testOpcodeSeqHeadIconst5(): CaseResult = runCase("opcodeseq-head-iconst5") { a, _ ->
        SurgeonOpcodeSeqAnchorCases.reset()
        ComplexOpcodeFixture().arithmetic(0)
        a.check(SurgeonOpcodeSeqAnchorCases.headWithPatternHits >= 1, "HEAD+ICONST_5 pattern 应命中")
    }

    fun testOpcodeSeqHeadIfeq(): CaseResult = runCase("opcodeseq-head-ifeq") { a, _ ->
        SurgeonOpcodeSeqAnchorCases.reset()
        ComplexOpcodeFixture().branch(false)
        a.check(SurgeonOpcodeSeqAnchorCases.headPatternIfeqHits >= 1, "HEAD+IFEQ pattern 应命中")
    }

    fun testOpcodeSeqInvokePattern(): CaseResult = runCase("opcodeseq-invoke-pattern") { a, _ ->
        SurgeonOpcodeSeqAnchorCases.reset()
        val r = ComplexOpcodeFixture().chainInvoke()
        a.equal(11 + 21 + 31 + 41, r, "原方法返回 (helper 各 +1)")
        a.equal(4, SurgeonOpcodeSeqAnchorCases.invokeWithPatternHits, "INVOKE+pattern 命中 4 次 helper")
    }

    fun testOpcodeSeqInvokeGlob(): CaseResult = runCase("opcodeseq-invoke-glob") { a, _ ->
        SurgeonOpcodeSeqAnchorCases.reset()
        ComplexOpcodeFixture().chainInvoke()
        a.equal(4, SurgeonOpcodeSeqAnchorCases.invokeWithGlobHits, "INVOKE+glob 命中 4 次 helper")
    }

    fun testOpcodeSeqInvokeAfter(): CaseResult = runCase("opcodeseq-invoke-after") { a, _ ->
        SurgeonOpcodeSeqAnchorCases.reset()
        ComplexOpcodeFixture().chainInvoke()
        a.equal(1, SurgeonOpcodeSeqAnchorCases.invokeAfterPatternHits, "INVOKE+AFTER+ordinal=0 命中 1 次")
    }

    fun testOpEnumCoverage(): CaseResult = runCase("op-enum-coverage") { a, _ ->
        val samples = listOf(
            Op.NOP, Op.ICONST_0, Op.ICONST_1, Op.ICONST_2, Op.ICONST_3, Op.ICONST_4, Op.ICONST_5,
            Op.LDC, Op.INVOKEVIRTUAL, Op.INVOKESTATIC, Op.INVOKEINTERFACE, Op.INVOKESPECIAL,
            Op.GETFIELD, Op.PUTFIELD, Op.NEW, Op.NEWARRAY, Op.ANEWARRAY, Op.ATHROW,
            Op.CHECKCAST, Op.INSTANCEOF, Op.IFEQ, Op.GOTO, Op.RETURN
        )
        // incision 模块直接依赖 ASM，运行时必然加载得到
        val opcodesCls = Class.forName("org.objectweb.asm.Opcodes")
        var matched = 0
        for (op in samples) {
            val field = opcodesCls.getDeclaredField(op.name)
            val v = field.getInt(null)
            a.equal(v, op.opcode, "Op.${op.name}.opcode 与 ASM Opcodes.${op.name} 一致")
            matched++
        }
        a.check(matched >= 20, "至少抽样 20 个 Op 与 ASM 对齐 (matched=$matched)")
        a.equal(-1, Op.ANY.opcode, "Op.ANY 通配 opcode=-1")
    }

    // ---- 追加：覆盖新增参数变种 ----

    fun testInsnPatternEmptySteps(): CaseResult = runCase("insn-pattern-empty-steps") { a, _ ->
        SurgeonInsnPatternCases.reset()
        ComplexOpcodeFixture().emptyBody()
        // OpcodeSeqMatcher.match: `if (seq.isEmpty()) continue` —— 空 steps 永不命中
        a.equal(0, SurgeonInsnPatternCases.emptyStepsHits, "空 steps 数组永不产生 MatchEvent")
    }

    fun testInsnPatternSeq5(): CaseResult = runCase("insn-pattern-seq5") { a, _ ->
        SurgeonInsnPatternCases.reset()
        ComplexOpcodeFixture().densePattern()
        a.check(SurgeonInsnPatternCases.seq5Hits >= 1, "5 步序列命中")
    }

    fun testInsnPatternRepeat2(): CaseResult = runCase("insn-pattern-repeat2") { a, _ ->
        SurgeonInsnPatternCases.reset()
        ComplexOpcodeFixture().multiFieldPut()
        a.check(SurgeonInsnPatternCases.repeat2Hits >= 1, "PUTFIELD repeat=2 命中")
    }

    fun testInsnPatternRepeat5(): CaseResult = runCase("insn-pattern-repeat5") { a, _ ->
        SurgeonInsnPatternCases.reset()
        ComplexOpcodeFixture().multiFieldPut()
        a.check(SurgeonInsnPatternCases.repeat5Hits >= 1, "PUTFIELD repeat=5 命中")
    }

    fun testInsnPatternCstNumeric(): CaseResult = runCase("insn-pattern-cst-numeric") { a, _ ->
        SurgeonInsnPatternCases.reset()
        ComplexOpcodeFixture().numberLdc()
        a.check(SurgeonInsnPatternCases.cstNumericHits >= 1, "LDC cst=12345678901 命中")
    }

    fun testInsnPatternGlobOwnerJavaLang(): CaseResult = runCase("insn-pattern-glob-owner-javalang") { a, _ ->
        SurgeonInsnPatternCases.reset()
        ComplexOpcodeFixture().staticCalls()
        a.check(SurgeonInsnPatternCases.globOwnerJavaLangHits >= 2, "glob 匹配 Math 两次调用")
    }

    fun testInsnPatternGlobNameSuffix(): CaseResult = runCase("insn-pattern-glob-name-suffix") { a, _ ->
        SurgeonInsnPatternCases.reset()
        ComplexOpcodeFixture().staticCalls()
        a.check(SurgeonInsnPatternCases.globNameSuffixHits >= 1, "name 后缀 glob 匹配 Math.max")
    }

    fun testInsnPatternGlobDesc(): CaseResult = runCase("insn-pattern-glob-desc") { a, _ ->
        SurgeonInsnPatternCases.reset()
        ComplexOpcodeFixture().staticCalls()
        // InsnStepMatcher.globOk: `(*)int` 既非纯前缀/后缀/两端 → 走精确比较，与 (II)I 不等 → 0 命中
        // 此用例验证 descFilter 字段在 advice 上可填（即便当前 glob 实现限制无法匹配）
        a.equal(0, SurgeonInsnPatternCases.globDescHits, "desc glob 当前不支持中段 `*`，命中 0 次")
    }

    fun testInsnPatternDescExact(): CaseResult = runCase("insn-pattern-desc-exact") { a, _ ->
        SurgeonInsnPatternCases.reset()
        ComplexOpcodeFixture().staticCalls()
        a.check(SurgeonInsnPatternCases.descExactHits >= 2, "desc 精确匹配 Math.max/min")
    }

    fun testInsnPatternInvokeInterface(): CaseResult = runCase("insn-pattern-invokeinterface") { a, _ ->
        SurgeonInsnPatternCases.reset()
        ComplexOpcodeFixture().interfaceCall(java.util.ArrayList<String>() as java.util.List<String>)
        a.check(SurgeonInsnPatternCases.invokeInterfaceHits >= 1, "INVOKEINTERFACE 命中")
    }

    fun testInsnPatternInvokeSpecial(): CaseResult = runCase("insn-pattern-invokespecial") { a, _ ->
        SurgeonInsnPatternCases.reset()
        ComplexOpcodeFixture().buildBuffer()
        a.check(SurgeonInsnPatternCases.invokeSpecialHits >= 1, "INVOKESPECIAL <init> 命中")
    }

    fun testInsnPatternArrayLength(): CaseResult = runCase("insn-pattern-arraylength") { a, _ ->
        SurgeonInsnPatternCases.reset()
        ComplexOpcodeFixture().arrays()
        a.check(SurgeonInsnPatternCases.arrayLengthHits >= 1, "ARRAYLENGTH repeat=2 命中")
    }

    fun testInsnPatternNop(): CaseResult = runCase("insn-pattern-nop") { a, _ ->
        SurgeonInsnPatternCases.reset()
        ComplexOpcodeFixture().emptyBody()
        // emptyBody() 仅 RETURN，无 NOP 指令；kotlinc 不主动插 NOP
        a.equal(0, SurgeonInsnPatternCases.nopHits, "emptyBody 无 NOP 指令，命中 0 次")
    }

    fun testInsnPatternDup(): CaseResult = runCase("insn-pattern-dup") { a, _ ->
        SurgeonInsnPatternCases.reset()
        ComplexOpcodeFixture().buildBuffer()
        a.check(SurgeonInsnPatternCases.dupHits >= 1, "DUP 命中 (new+dup 构造模式)")
    }

    // ---- Offset 追加 ----

    fun testOffsetPlus5(): CaseResult = runCase("offset-plus-5") { a, _ ->
        SurgeonOffsetCases.reset()
        OffsetFixture().chainWithGap()
        // skipForward 越界停在末尾，仍能 insertAfter，期望命中 1 次（ordinal=0 single）
        a.equal(1, SurgeonOffsetCases.offsetPlus5Hits, "AFTER + offset=+5 ordinal=0 命中 1 次")
    }

    fun testOffsetMinus2(): CaseResult = runCase("offset-minus-2") { a, _ ->
        SurgeonOffsetCases.reset()
        OffsetFixture().chainWithGap()
        // skipBackward 跨越 PUTFIELD;GETFIELD;ICONST_1;IADD;PUTFIELD —— ordinal=1 单点
        a.equal(1, SurgeonOffsetCases.offsetMinus2Hits, "BEFORE + offset=-2 ordinal=1 命中 1 次")
    }

    fun testOffsetBeforeOffset2(): CaseResult = runCase("offset-before-2-all") { a, _ ->
        SurgeonOffsetCases.reset()
        OffsetFixture().chainWithGap()
        a.check(SurgeonOffsetCases.beforeOffset2Hits >= 1, "BEFORE + offset=+2 至少命中 1 次")
    }

    fun testOffsetAfterOffset0(): CaseResult = runCase("offset-after-0") { a, _ ->
        SurgeonOffsetCases.reset()
        OffsetFixture().chain()
        a.equal(3, SurgeonOffsetCases.afterOffset0Hits, "AFTER + offset=0 命中 3 次")
    }

    fun testOffsetOrdinal1After1(): CaseResult = runCase("offset-ordinal1-after-1") { a, _ ->
        SurgeonOffsetCases.reset()
        OffsetFixture().chainWithGap()
        a.equal(1, SurgeonOffsetCases.ordinal1AfterOffset1Hits, "ordinal=1 + AFTER + offset=+1 命中 1 次")
    }

    fun testOffsetFieldGet(): CaseResult = runCase("offset-fieldget") { a, _ ->
        SurgeonOffsetCases.reset()
        OffsetFixture().chainWithGap()
        a.check(SurgeonOffsetCases.fieldGetOffsetHits >= 1, "FIELD_GET + AFTER + offset=+1 命中")
    }

    fun testOffsetFieldPut(): CaseResult = runCase("offset-fieldput") { a, _ ->
        SurgeonOffsetCases.reset()
        OffsetFixture().chainWithGap()
        a.check(SurgeonOffsetCases.fieldPutOffsetHits >= 1, "FIELD_PUT + BEFORE + offset=0 命中")
    }
}
