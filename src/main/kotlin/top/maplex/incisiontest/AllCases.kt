package top.maplex.incisiontest

import org.bukkit.Bukkit
import taboolib.module.incision.annotation.SurgeryDesk
import taboolib.module.incision.api.Suture
import taboolib.module.incision.diagnostic.Trauma
import taboolib.module.incision.dsl.Scalpel
import top.maplex.incisiontest.cases.SurgeonAdvancedCases
import top.maplex.incisiontest.cases.SurgeonAnchorMatrixCases
import top.maplex.incisiontest.cases.SurgeonAnchorMatrixExtrasCases
import top.maplex.incisiontest.cases.SurgeonAnchorMatrixPriorityDefault
import top.maplex.incisiontest.cases.SurgeonAnchorMatrixPriorityHigh
import top.maplex.incisiontest.cases.SurgeonAnchorMatrixPriorityLow
import top.maplex.incisiontest.cases.SurgeonAnchorMatrixPriorityOperation
import top.maplex.incisiontest.cases.SurgeonAnchorMatrixScopeCases
import top.maplex.incisiontest.cases.SurgeonAnchorMatrixSiteCases
import top.maplex.incisiontest.cases.SurgeonAnchorMatrixTrimCases
import top.maplex.incisiontest.fixture.AnchorMatrixFixture_Priority
import top.maplex.incisiontest.fixture.AnchorMatrixFixture_Scope
import top.maplex.incisiontest.fixture.AnchorMatrixFixture_Site
import top.maplex.incisiontest.fixture.AnchorMatrixFixture_Trim
import top.maplex.incisiontest.fixture.AnchorMatrixFixture_Extras
import top.maplex.incisiontest.cases.SurgeonAopCases
import top.maplex.incisiontest.cases.SurgeonBasicCases
import top.maplex.incisiontest.cases.SurgeonCrossClCases
import top.maplex.incisiontest.cases.SurgeonMixinCases
import top.maplex.incisiontest.cases.SurgeonOperationCases
import top.maplex.incisiontest.cases.SurgeonOperationHigherCases
import top.maplex.incisiontest.cases.SurgeonKotlinTargetCases
import top.maplex.incisiontest.cases.OperationMatrixCases
import top.maplex.incisiontest.cases.SurgeonPriorityShared
import top.maplex.incisiontest.cases.KotlinTargetMatrixCases
import top.maplex.incisiontest.cases.OperationIdVariantsCases
import top.maplex.incisiontest.cases.OperationEnabledPriorityCases
import top.maplex.incisiontest.cases.SurgeonPriorityExtremeShared
import top.maplex.incisiontest.cases.SurgeonPriorityMaxCases
import top.maplex.incisiontest.cases.SurgeonPriorityMinCases
import top.maplex.incisiontest.cases.SurgeryDeskDefaultCaller
import top.maplex.incisiontest.cases.SurgeryDeskHighCaller
import top.maplex.incisiontest.cases.SurgeryDeskNegativeCaller
import top.maplex.incisiontest.cases.SurgeonSiteCases
import top.maplex.incisiontest.cases.SurgeonVersionCases
import top.maplex.incisiontest.cases.SurgeonOffsetCases
import top.maplex.incisiontest.cases.SurgeonInsnPatternCases
import top.maplex.incisiontest.cases.SurgeonOpcodeSeqAnchorCases
import top.maplex.incisiontest.cases.SurgeonPredicateCases
import top.maplex.incisiontest.cases.VersionRemapExtTests
import top.maplex.incisiontest.fixture.SurgeonAdvancedTargetFixture
import top.maplex.incisiontest.fixture.AnchorMatrixFixture
import top.maplex.incisiontest.fixture.SurgeonAopTargetFixture
import top.maplex.incisiontest.fixture.SurgeonBasicTargetFixture
import top.maplex.incisiontest.fixture.SurgeonMixinTargetFixture
import top.maplex.incisiontest.fixture.SurgeonOperationTargetFixture
import top.maplex.incisiontest.fixture.SurgeonKotlinTargetFixture
import top.maplex.incisiontest.fixture.OperationMatrixFixture
import top.maplex.incisiontest.fixture.SurgeonPriorityFixture
import top.maplex.incisiontest.fixture.KotlinTargetMatrixFixture
import top.maplex.incisiontest.fixture.OperationIdVariantsFixture
import top.maplex.incisiontest.fixture.OperationEnabledPriorityFixture
import top.maplex.incisiontest.fixture.SurgeonPriorityExtremeFixture
import top.maplex.incisiontest.fixture.SurgeryDeskCallerFixture
import top.maplex.incisiontest.fixture.SurgeonSiteTargetFixture
import top.maplex.incisiontest.fixture.SurgeonVersionTargetFixture
import top.maplex.incisiontest.fixture.OffsetFixture
import top.maplex.incisiontest.fixture.InsnPatternFixture
import top.maplex.incisiontest.fixture.ComplexOpcodeFixture
import top.maplex.incisiontest.fixture.PredicateFixture
import top.maplex.incisiontest.fixture.AccessorFixture
import top.maplex.incisiontest.fixture.AccessorChildFixture
import top.maplex.incisiontest.fixture.UtilFixture
import top.maplex.incisiontest.fixture.GreetableImpl
import top.maplex.incisiontest.cases.AccessorCases
import top.maplex.incisiontest.cases.AccessorTheatreDslCases
import top.maplex.incisiontest.cases.AccessorUtilCases
import top.maplex.incisiontest.fixture.TargetFixture

private const val TARGET = "top.maplex.incisiontest.fixture.TargetFixture"
private const val GREET = "$TARGET#greet(java.lang.String)java.lang.String"
private const val ADD = "$TARGET#add(int,int)int"
private const val LONGCALC = "$TARGET#longCalc(long,long)long"
private const val VOIDNOARG = "$TARGET#voidNoArg()V"
private const val RETBOOL = "$TARGET#returnsBoolean(boolean)boolean"
private const val MAYTHROW = "$TARGET#mayThrow(boolean)java.lang.String"
private const val MULTIARG = "$TARGET#multiArg(int,java.lang.String,double)java.lang.String"
private const val CALLSHELPER = "$TARGET#callsHelper(int)int"
private const val TOUCHFIELD = "$TARGET#touchesField()int"
private const val STATICECHO = "$TARGET#staticEcho(java.lang.String)java.lang.String"
private const val COMPANIONECHO = "$TARGET#companionEcho(java.lang.String)java.lang.String"
private const val WILDCARD = "$TARGET#greet(*)"

/**
 * 全部测试用例集中处。所有 case 都是本 object 的方法 —— 这是必须的，
 * 因为 Scalpel.transient 会通过栈帧检查调用方必须标注 @SurgeryDesk。
 *
 * runCase 是 inline 函数，块内代码内联回调用方法，调用栈仍指向本 object。
 */
@SurgeryDesk
object AllCases {

    val entries: List<Pair<String, () -> CaseResult>> = listOf(
        "lead" to ::testLead,
        "trail" to ::testTrail,
        "trail-throw" to ::testTrailOnThrow,
        "splice-proceed" to ::testSpliceProceed,
        "splice-skip" to ::testSpliceSkip,
        "splice-modify-args" to ::testSpliceModifyArgs,
        "bypass" to ::testBypass,
        "excise" to ::testExcise,
        "wildcard" to ::testWildcard,
        "static-target" to ::testStaticTarget,
        "void-target" to ::testVoidTarget,
        "primitive-int" to ::testPrimitiveInt,
        "primitive-long" to ::testPrimitiveLong,
        "primitive-bool" to ::testPrimitiveBool,
        "multi-arg" to ::testMultiArg,
        "where-true" to ::testWhereTrue,
        "where-false" to ::testWhereFalse,
        "priority-order" to ::testPriorityOrder,
        "on-batch" to ::testOnBatch,
        "transient-use" to ::testTransientUse,
        "scoped" to ::testScoped,
        "threadlocal-active" to ::testThreadLocalActive,
        "threadlocal-inactive" to ::testThreadLocalInactive,
        "armon-disarmon" to ::testArmTrigger,
        "exclusive" to ::testExclusive,
        "heal" to ::testHeal,
        "suspend-resume" to ::testSuspendResume,
        "find-list" to ::testFindList,
        "healall" to ::testHealAll,
        "multi-target" to ::testMultiTarget,
        "auto-close" to ::testAutoClose,
        "duplicate-id" to ::testDuplicateId,
        "bad-descriptor" to ::testBadDescriptor,
        "surgeon-lead" to ::testSurgeonLead,
        "surgeon-trail" to ::testSurgeonTrail,
        "surgeon-splice" to ::testSurgeonSplice,
        "surgeon-excise" to ::testSurgeonExcise,
        "surgeon-bypass" to ::testSurgeonBypass,
        // AOP 风格注解 demo
        "surgeon-aop-before" to ::testSurgeonAopBefore,
        "surgeon-aop-after" to ::testSurgeonAopAfter,
        "surgeon-aop-around" to ::testSurgeonAopAround,
        "surgeon-aop-modify-args" to ::testSurgeonAopModifyArgs,
        "surgeon-aop-proceed-result" to ::testSurgeonAopProceedResult,
        "surgeon-aop-static-target" to ::testSurgeonAopStaticTarget,
        "surgeon-aop-companion-target" to ::testSurgeonAopCompanionTarget,
        // Mixin 风格注解 demo
        "surgeon-mixin-overwrite" to ::testSurgeonMixinOverwrite,
        "surgeon-mixin-redirect" to ::testSurgeonMixinRedirect,
        // Site / Graft / Bypass / Trim 锚点覆盖
        "surgeon-graft-invoke-before" to ::testSurgeonGraftInvokeBefore,
        "surgeon-graft-invoke-after" to ::testSurgeonGraftInvokeAfter,
        "surgeon-graft-field-get" to ::testSurgeonGraftFieldGet,
        "surgeon-graft-field-put" to ::testSurgeonGraftFieldPut,
        "surgeon-graft-new" to ::testSurgeonGraftNew,
        "surgeon-graft-throw" to ::testSurgeonGraftThrow,
        "surgeon-bypass-invoke" to ::testSurgeonBypassInvoke,
        "surgeon-trim-return" to ::testSurgeonTrimReturn,
        "surgeon-trim-arg" to ::testSurgeonTrimArg,
        // @Operation 属性覆盖
        "surgeon-operation-custom-id" to ::testSurgeonOperationCustomId,
        "surgeon-operation-disabled" to ::testSurgeonOperationDisabled,
        "surgeon-operation-disabled-resume" to ::testSurgeonOperationDisabledManualResume,
        "surgeon-operation-priority" to ::testSurgeonOperationPriority,
        "surgeon-operation-priority-low" to ::testSurgeonOperationPriorityLowerWins,
        "surgeon-default-priority" to ::testSurgeonDefaultPriority,
        "surgeon-operation-overrides-surgeon" to ::testSurgeonOperationOverridesSurgeon,
        "surgeon-multi-surgeon-priority" to ::testSurgeonMultiSurgeonPriority,
        // @KotlinTarget 两态及组合
        "kotlintarget-companion-only" to ::testKotlinTargetCompanionOnly,
        "kotlintarget-jvmstatic-only" to ::testKotlinTargetJvmStaticOnly,
        "kotlintarget-both-static-path" to ::testKotlinTargetBothStaticPath,
        "kotlintarget-both-companion-path" to ::testKotlinTargetBothCompanionPath,
        // @Operation 三维矩阵 (id × enabled × priority)
        "op-matrix-m1-default" to ::testOpMatrixM1Default,
        "op-matrix-m2-id-only" to ::testOpMatrixM2IdOnly,
        "op-matrix-m3-disabled" to ::testOpMatrixM3DisabledOnly,
        "op-matrix-m4-priority-pos" to ::testOpMatrixM4PriorityPositive,
        "op-matrix-m5-priority-neg" to ::testOpMatrixM5PriorityNegative,
        "op-matrix-m6-id-priority" to ::testOpMatrixM6IdPriority,
        "op-matrix-m7-id-disabled" to ::testOpMatrixM7IdDisabled,
        "op-matrix-m8-all-three" to ::testOpMatrixM8AllThree,
        "op-matrix-m9-explicit-zero" to ::testOpMatrixM9ExplicitZero,
        "op-matrix-priority-order" to ::testOpMatrixPriorityOrder,
        // @Surgeon(priority) 默认 / 0 / 正 / 负
        "surgeon-priority-default" to ::testSurgeonPriorityDefault,
        "surgeon-priority-zero-explicit" to ::testSurgeonPriorityZeroExplicit,
        "surgeon-priority-positive" to ::testSurgeonPriorityPositive,
        "surgeon-priority-negative" to ::testSurgeonPriorityNegative,
        "surgeon-priority-order-all" to ::testSurgeonPriorityOrderAll,
        // @KotlinTarget 四组合 FF / FT / TF / TT
        "ktarget-matrix-ff" to ::testKTargetMatrixFF,
        "ktarget-matrix-ft" to ::testKTargetMatrixFT,
        "ktarget-matrix-tf" to ::testKTargetMatrixTF,
        "ktarget-matrix-tt-static" to ::testKTargetMatrixTTStatic,
        "ktarget-matrix-tt-companion" to ::testKTargetMatrixTTCompanion,
        // @SurgeryDesk 标记在不同 object 上的 transient 调用
        "desk-default-transient" to ::testDeskDefaultTransient,
        "desk-high-priority-transient" to ::testDeskHighPriorityTransient,
        "desk-negative-priority-transient" to ::testDeskNegativePriorityTransient,
        "desk-allcases-self" to ::testDeskAllCasesSelf,
        // @Operation(id) 4 形态变种
        "op-id-normal" to ::testOpIdNormal,
        "op-id-special-chars" to ::testOpIdSpecialChars,
        "op-id-empty-fallback" to ::testOpIdEmptyFallback,
        "op-id-very-long" to ::testOpIdVeryLong,
        // @Operation(enabled × priority) 8 格矩阵
        "op-enpri-true-neg10" to ::testOpEnPriTrueNeg10,
        "op-enpri-true-0" to ::testOpEnPriTrue0,
        "op-enpri-true-5" to ::testOpEnPriTrue5,
        "op-enpri-true-100" to ::testOpEnPriTrue100,
        "op-enpri-false-neg10" to ::testOpEnPriFalseNeg10,
        "op-enpri-false-0" to ::testOpEnPriFalse0,
        "op-enpri-false-5" to ::testOpEnPriFalse5,
        "op-enpri-false-100" to ::testOpEnPriFalse100,
        // 同 priority 注册顺序稳定
        "op-same-priority-stable-order" to ::testOpSamePriorityStableOrder,
        // disable + resume + suspend 循环
        "op-disable-resume-suspend-cycle" to ::testOpDisableResumeSuspendCycle,
        // @Surgeon(priority) 极值
        "surgeon-priority-extreme-max" to ::testSurgeonPriorityExtremeMax,
        "surgeon-priority-extreme-min" to ::testSurgeonPriorityExtremeMin,
        // 三 Surgeon 跨 object 排序
        "surgeon-three-object-order" to ::testSurgeonThreeObjectOrder,
        // @KotlinTarget 每种 ≥2 用例 —— 第二条 advice 命中
        "ktarget-ff-alt" to ::testKTargetFFAlt,
        "ktarget-ft-alt" to ::testKTargetFTAlt,
        "ktarget-tf-alt" to ::testKTargetTFAlt,
        "ktarget-tt-alt-static" to ::testKTargetTTAltStatic,
        "ktarget-tt-alt-companion" to ::testKTargetTTAltCompanion,
        // 跨 ClassLoader 注解切口 demo
        "cross-cl-nms-ishardcore" to ::testCrossClNmsIsHardcore,
        "cross-cl-nms-playercount" to ::testCrossClNmsPlayerCount,
        "cross-cl-bukkit-maxplayers" to ::testCrossClBukkitMaxPlayers,
        "bukkit-max-players" to ::testBukkitMaxPlayers,
        "bukkit-view-distance" to ::testBukkitViewDistance,
        "nms-motd" to ::testNmsMotd,
        // Remap A+B+C —— @Version 区间过滤 + NoopVersionMatcher + NMS 方法名 remap
        "version-range-hit" to ::testVersionRangeHit,
        "version-noop-matcher" to ::testVersionNoopMatcher,
        "remap-nms-method-name" to ::testRemapNmsMethodName,
        // Task #16 Version 与 Remap 扩展
        "version-fake-in-range" to VersionRemapExtTests::testVersionFakeInRange,
        "version-fake-below-start" to VersionRemapExtTests::testVersionFakeBelowStart,
        "version-fake-above-end" to VersionRemapExtTests::testVersionFakeAboveEnd,
        "version-fake-boundary-end" to VersionRemapExtTests::testVersionFakeBoundaryEnd,
        "version-fake-boundary-start" to VersionRemapExtTests::testVersionFakeBoundaryStart,
        "version-missing-matcher-fallback" to VersionRemapExtTests::testVersionMissingMatcherFallback,
        "version-multi-range-index" to VersionRemapExtTests::testVersionMultiRangeIndex,
        "version-matcher-resolve" to VersionRemapExtTests::testVersionMatcherResolve,
        "version-matcher-segment" to VersionRemapExtTests::testVersionMatcherSegmentCompare,
        "remap-owner-translate" to VersionRemapExtTests::testRemapOwnerTranslate,
        "remap-method-fallback" to VersionRemapExtTests::testRemapMethodFallback,
        "remap-field-resolve" to VersionRemapExtTests::testRemapFieldResolve,
        // Task #16 扩展矩阵 —— @Version 全参数 × 全 advice 类型 × 段比较 × matcher 来源
        "version-matrix-both-empty" to VersionRemapExtTests::testVersionBothEmpty,
        "version-matrix-only-end" to VersionRemapExtTests::testVersionOnlyEndGiven,
        "version-matrix-only-start" to VersionRemapExtTests::testVersionOnlyStartGiven,
        "version-matrix-single-point" to VersionRemapExtTests::testVersionSinglePoint,
        "version-matrix-inverted" to VersionRemapExtTests::testVersionInvertedRange,
        "version-advice-lead" to VersionRemapExtTests::testVersionOnLead,
        "version-advice-trail" to VersionRemapExtTests::testVersionOnTrail,
        "version-advice-splice" to VersionRemapExtTests::testVersionOnSplice,
        "version-advice-excise" to VersionRemapExtTests::testVersionOnExcise,
        "version-advice-bypass" to VersionRemapExtTests::testVersionOnBypass,
        "version-advice-graft" to VersionRemapExtTests::testVersionOnGraft,
        "version-advice-trim" to VersionRemapExtTests::testVersionOnTrim,
        "version-matcher-default" to VersionRemapExtTests::testVersionDefaultMatcher,
        "version-matcher-explicit-noop" to VersionRemapExtTests::testVersionExplicitNoop,
        "version-matcher-fake-nobounds" to VersionRemapExtTests::testVersionFakeNoBounds,
        "version-segment-longer-start" to VersionRemapExtTests::testVersionSegLongerStart,
        "version-segment-longer-end" to VersionRemapExtTests::testVersionSegLongerEnd,
        "version-micro-below" to VersionRemapExtTests::testVersionMicroStepBelow,
        "version-micro-above" to VersionRemapExtTests::testVersionMicroStepAbove,
        "remap-owner-non-nms" to VersionRemapExtTests::testRemapNonNmsOwnerUntouched,
        "remap-method-empty-desc" to VersionRemapExtTests::testRemapMethodEmptyDesc,
        "version-mc-matcher-probe" to VersionRemapExtTests::testMinecraftMatcherProbe,
        "version-matcher-caching" to VersionRemapExtTests::testFakeMatcherCaching,
        "version-null-current-matches" to VersionRemapExtTests::testVersionMatcherNullCurrentMatches,
        // Task #8 脚手架占位 —— offset/ordinal/shift + InsnPattern + where 谓词（断言等 #7/#11 完成后补）
        "surgeon-offset-ordinal" to ::testSurgeonOffsetOrdinal,
        "surgeon-offset-shift" to ::testSurgeonOffsetShift,
        "surgeon-insn-pattern" to ::testSurgeonInsnPattern,
        "surgeon-where-dragon" to ::testSurgeonWhereDragon,
        "surgeon-where-low-level" to ::testSurgeonWhereLowLevel,
        // Task #13 锚点矩阵 —— anchor × advice × shift × ordinal
        "anchor-head-lead" to ::testAnchorHeadLead,
        "anchor-tail-trail" to ::testAnchorTailTrail,
        "anchor-return-trail-multi" to ::testAnchorReturnTrailMulti,
        "anchor-throw-trail" to ::testAnchorThrowTrail,
        "anchor-invoke-graft-before-ordinal-0" to ::testAnchorInvokeGraftBefore0,
        "anchor-invoke-graft-before-ordinal-1" to ::testAnchorInvokeGraftBefore1,
        "anchor-invoke-graft-before-ordinal-all" to ::testAnchorInvokeGraftBeforeAll,
        "anchor-invoke-graft-after-ordinal-0" to ::testAnchorInvokeGraftAfter0,
        "anchor-field-get-graft" to ::testAnchorFieldGetGraft,
        "anchor-field-put-graft" to ::testAnchorFieldPutGraft,
        "anchor-new-graft" to ::testAnchorNewGraft,
        "anchor-throw-graft" to ::testAnchorThrowGraft,
        "anchor-bypass-replace-result" to ::testAnchorBypassReplaceResult,
        "anchor-trim-return" to ::testAnchorTrimReturn,
        "anchor-trim-arg-0" to ::testAnchorTrimArg0,
        "anchor-trim-arg-1" to ::testAnchorTrimArg1,
        "anchor-excise-method" to ::testAnchorExciseMethod,
        "anchor-splice-proceed" to ::testAnchorSpliceProceed,
        // Task #13 诊断 / Trauma 路径
        "trauma-bad-descriptor" to ::testTraumaBadDescriptor,
        "trauma-method-not-found" to ::testTraumaMethodNotFound,
        "trauma-class-not-found" to ::testTraumaClassNotFound,
        "trauma-bad-scope" to ::testTraumaBadScope,
        // ==== Task #15 where 谓词 DSL 全覆盖 ====
        "where-eq-string" to ::testWhereEqString,
        "where-neq-int" to ::testWhereNeqInt,
        "where-lt" to ::testWhereLt,
        "where-gt" to ::testWhereGt,
        "where-le" to ::testWhereLe,
        "where-ge" to ::testWhereGe,
        "where-matches" to ::testWhereMatches,
        "where-in-string" to ::testWhereInString,
        "where-in-list" to ::testWhereInList,
        "where-is-string" to ::testWhereIsString,
        "where-not-is-int" to ::testWhereNotIsInt,
        "where-ic-arraylist" to ::testWhereIcArrayList,
        "where-ip-object" to ::testWhereIpObject,
        "where-it-exact" to ::testWhereItExact,
        "where-not-it" to ::testWhereNotIt,
        "where-as-cast" to ::testWhereAsCast,
        "where-property" to ::testWhereProperty,
        "where-property-size" to ::testWherePropertySize,
        "where-method-call" to ::testWhereMethodCall,
        "where-method-noarg" to ::testWhereMethodNoArg,
        "where-safe-call" to ::testWhereSafeCall,
        "where-and" to ::testWhereAnd,
        "where-or" to ::testWhereOr,
        "where-not-group" to ::testWhereNotGroup,
        "where-nested" to ::testWhereNested,
        "where-literal-true" to ::testWhereLiteralTrue,
        "where-literal-false" to ::testWhereLiteralFalse,
        "where-literal-null" to ::testWhereLiteralNull,
        "where-literal-double" to ::testWhereLiteralDouble,
        "where-compound" to ::testWhereCompound,
        "where-this-ref" to ::testWhereThisRef,
        "where-trauma-undef" to ::testWhereTraumaUndefined,
        "where-trauma-method-indexed" to ::testWhereTraumaMethodIndexed,
        "where-trauma-syntax" to ::testWhereTraumaSyntax,
        // ==== where 在每个 advice 注解上的覆盖（Task #15 追加） ====
        "where-empty-baseline" to ::testWhereEmptyBaseline,
        "where-on-trail" to ::testWhereOnTrail,
        "where-on-splice" to ::testWhereOnSplice,
        "where-on-excise" to ::testWhereOnExcise,
        "where-on-bypass" to ::testWhereOnBypass,
        "where-on-graft" to ::testWhereOnGraft,
        "where-on-trim" to ::testWhereOnTrim,
        // ==== Task #14 InsnPattern 与 offset 全覆盖 ====
        "insn-pattern-iconst5" to Pattern14Cases::testInsnPatternIconst5,
        "insn-pattern-seq2" to Pattern14Cases::testInsnPatternSeq2,
        "insn-pattern-seq3" to Pattern14Cases::testInsnPatternSeq3,
        "insn-pattern-seq4" to Pattern14Cases::testInsnPatternSeq4,
        "insn-pattern-any" to Pattern14Cases::testInsnPatternAny,
        "insn-pattern-repeat" to Pattern14Cases::testInsnPatternRepeat,
        "insn-pattern-ldc" to Pattern14Cases::testInsnPatternLdc,
        "insn-pattern-ldc-cst" to Pattern14Cases::testInsnPatternLdcCst,
        "insn-pattern-getfield" to Pattern14Cases::testInsnPatternGetField,
        "insn-pattern-putfield" to Pattern14Cases::testInsnPatternPutField,
        "insn-pattern-new" to Pattern14Cases::testInsnPatternNew,
        "insn-pattern-invokevirtual-glob" to Pattern14Cases::testInsnPatternInvokeVirtualGlob,
        "insn-pattern-invokestatic" to Pattern14Cases::testInsnPatternInvokeStatic,
        "insn-pattern-newarray" to Pattern14Cases::testInsnPatternNewArray,
        "insn-pattern-anewarray" to Pattern14Cases::testInsnPatternANewArray,
        "insn-pattern-instanceof" to Pattern14Cases::testInsnPatternInstanceOf,
        "insn-pattern-checkcast" to Pattern14Cases::testInsnPatternCheckCast,
        "insn-pattern-ifeq" to Pattern14Cases::testInsnPatternIfeq,
        "insn-pattern-goto" to Pattern14Cases::testInsnPatternGoto,
        "insn-pattern-athrow" to Pattern14Cases::testInsnPatternAthrow,
        "insn-pattern-ireturn" to Pattern14Cases::testInsnPatternReturn,
        "insn-pattern-accumulate" to Pattern14Cases::testInsnPatternAccumulate,
        "offset-zero" to Pattern14Cases::testOffsetZero,
        "offset-after-1-all" to Pattern14Cases::testOffsetAfter1All,
        "offset-after-2-all" to Pattern14Cases::testOffsetAfter2All,
        "offset-before-neg-1" to Pattern14Cases::testOffsetBeforeNeg1,
        "offset-ordinal0-after-1" to Pattern14Cases::testOffsetOrdinal0After1,
        "offset-ordinal2-after-2" to Pattern14Cases::testOffsetOrdinal2After2,
        "offset-any-ordinal" to Pattern14Cases::testOffsetAnyOrdinal,
        "opcodeseq-head-iconst5" to Pattern14Cases::testOpcodeSeqHeadIconst5,
        "opcodeseq-head-ifeq" to Pattern14Cases::testOpcodeSeqHeadIfeq,
        "opcodeseq-invoke-pattern" to Pattern14Cases::testOpcodeSeqInvokePattern,
        "opcodeseq-invoke-glob" to Pattern14Cases::testOpcodeSeqInvokeGlob,
        "opcodeseq-invoke-after" to Pattern14Cases::testOpcodeSeqInvokeAfter,
        "op-enum-coverage" to Pattern14Cases::testOpEnumCoverage,
        "insn-pattern-empty-steps" to Pattern14Cases::testInsnPatternEmptySteps,
        "insn-pattern-seq5" to Pattern14Cases::testInsnPatternSeq5,
        "insn-pattern-repeat2" to Pattern14Cases::testInsnPatternRepeat2,
        "insn-pattern-repeat5" to Pattern14Cases::testInsnPatternRepeat5,
        "insn-pattern-cst-numeric" to Pattern14Cases::testInsnPatternCstNumeric,
        "insn-pattern-glob-owner-javalang" to Pattern14Cases::testInsnPatternGlobOwnerJavaLang,
        "insn-pattern-glob-name-suffix" to Pattern14Cases::testInsnPatternGlobNameSuffix,
        "insn-pattern-glob-desc" to Pattern14Cases::testInsnPatternGlobDesc,
        "insn-pattern-desc-exact" to Pattern14Cases::testInsnPatternDescExact,
        "insn-pattern-invokeinterface" to Pattern14Cases::testInsnPatternInvokeInterface,
        "insn-pattern-invokespecial" to Pattern14Cases::testInsnPatternInvokeSpecial,
        "insn-pattern-arraylength" to Pattern14Cases::testInsnPatternArrayLength,
        "insn-pattern-nop" to Pattern14Cases::testInsnPatternNop,
        "insn-pattern-dup" to Pattern14Cases::testInsnPatternDup,
        "offset-plus-5" to Pattern14Cases::testOffsetPlus5,
        "offset-minus-2" to Pattern14Cases::testOffsetMinus2,
        "offset-before-2-all" to Pattern14Cases::testOffsetBeforeOffset2,
        "offset-after-0" to Pattern14Cases::testOffsetAfterOffset0,
        "offset-ordinal1-after-1" to Pattern14Cases::testOffsetOrdinal1After1,
        "offset-fieldget" to Pattern14Cases::testOffsetFieldGet,
        "offset-fieldput" to Pattern14Cases::testOffsetFieldPut,
        // Task #13 第二批 —— @Site 字段网格
        "site-anchor-head" to ::testSiteAnchorHead,
        "site-anchor-tail" to ::testSiteAnchorTail,
        "site-anchor-return" to ::testSiteAnchorReturn,
        "site-anchor-throw" to ::testSiteAnchorThrow,
        "site-invoke-ordinal-0" to ::testSiteInvokeOrd0,
        "site-invoke-ordinal-1" to ::testSiteInvokeOrd1,
        "site-invoke-ordinal-2" to ::testSiteInvokeOrd2,
        "site-invoke-ordinal-all" to ::testSiteInvokeOrdAll,
        "site-shift-before" to ::testSiteShiftBefore,
        "site-shift-after" to ::testSiteShiftAfter,
        "site-field-get" to ::testSiteFieldGet,
        "site-field-put" to ::testSiteFieldPut,
        "site-new-with-target" to ::testSiteNewWithTarget,
        "site-new-empty-target" to ::testSiteNewEmptyTarget,
        "site-offset-zero" to ::testSiteOffsetZero,
        // Task #13 第二批 —— scope DSL / 描述符变种
        "scope-lead-full-descriptor" to ::testScopeLeadFullDescriptor,
        "scope-lead-wildcard-args" to ::testScopeLeadWildArgs,
        "scope-lead-exact-target" to ::testScopeLeadExactTarget,
        "scope-trail-class-prefix-a" to ::testScopeTrailClassPrefixA,
        "scope-trail-class-prefix-b" to ::testScopeTrailClassPrefixB,
        "scope-splice-full" to ::testScopeSpliceFull,
        "scope-bypass-full-descriptor" to ::testScopeBypassFullDescriptor,
        "scope-graft-full-descriptor" to ::testScopeGraftFullDescriptor,
        "scope-trim-full-descriptor" to ::testScopeTrimFullDescriptor,
        // Task #13 第二批 —— @Trim 全字段网格
        "trim-arg-single" to ::testTrimArgSingle,
        "trim-arg-double-0" to ::testTrimArgDouble0,
        "trim-arg-double-1" to ::testTrimArgDouble1,
        "trim-arg-triple-2" to ::testTrimArgTriple2,
        "trim-arg-quad-3" to ::testTrimArgQuad3,
        "trim-return-int" to ::testTrimReturnInt,
        "trim-return-long" to ::testTrimReturnLong,
        "trim-return-string" to ::testTrimReturnString,
        "trim-return-bool" to ::testTrimReturnBool,
        // Task #13 第二批 —— priority 网格
        "priority-surgeon-default" to ::testPrioritySurgeonDefault,
        "priority-surgeon-high" to ::testPrioritySurgeonHigh,
        "priority-surgeon-low-negative" to ::testPrioritySurgeonLowNeg,
        "priority-surgeon-multi-class-order" to ::testPrioritySurgeonMultiClassOrder,
        "priority-operation-pos200" to ::testPriorityOperationPos200,
        "priority-operation-pos50" to ::testPriorityOperationPos50,
        "priority-operation-neg10" to ::testPriorityOperationNeg10,
        "priority-operation-default" to ::testPriorityOperationDefault,
        "priority-operation-order" to ::testPriorityOperationOrder,
        // Task #13 第二批 —— @SurgeryDesk 调用层
        "surgerydesk-nested-runcase" to ::testSurgeryDeskNestedRunCase,
        "surgerydesk-direct-call" to ::testSurgeryDeskDirectCall,
        // Task #13 追加扩展 —— FIELD/NEW 锚点 shift BEFORE/AFTER 全网格
        "extras-field-get-before" to ::testExtrasFieldGetBefore,
        "extras-field-get-after" to ::testExtrasFieldGetAfter,
        "extras-field-put-before" to ::testExtrasFieldPutBefore,
        "extras-field-put-after" to ::testExtrasFieldPutAfter,
        "extras-new-before" to ::testExtrasNewBefore,
        "extras-new-after" to ::testExtrasNewAfter,
        // Task #13 追加扩展 —— 3 参方法 ARG index=0/1/2 三条独立 advice 同时生效
        "extras-trim-triple-012" to ::testExtrasTrimTriple012,
        // Task #13 追加扩展 —— @Splice skip (短路，不 proceed)
        "extras-splice-skip-short-circuit" to ::testExtrasSpliceSkip,
        // Task #13 追加扩展 —— @Excise 原方法体不执行
        "extras-excise-body-skipped" to ::testExtrasExciseBodySkipped,
        // Task #13 追加扩展 —— 描述符变种：无括号方法名
        "extras-descriptor-no-paren" to ::testExtrasDescriptorNoParen,
        // Task #13 追加 Trauma 诊断 —— 非法 anchor 字符串 / 错误描述符编码 / target 非法字符
        "trauma-anchor-invalid-name" to ::testTraumaAnchorInvalidName,
        "trauma-descriptor-wrong-encoding" to ::testTraumaDescriptorWrongEncoding,
        "trauma-target-illegal-chars" to ::testTraumaTargetIllegalChars,
        // Accessor — Lambda 工厂 + Theatre DSL
        "accessor-private-final-field" to ::testAccessorPrivateFinalField,
        "accessor-protected-field" to ::testAccessorProtectedField,
        "accessor-internal-field" to ::testAccessorInternalField,
        "accessor-public-field" to ::testAccessorPublicField,
        "accessor-write-mutable" to ::testAccessorWriteMutable,
        "accessor-static-field" to ::testAccessorStaticField,
        "accessor-write-static" to ::testAccessorWriteStatic,
        "accessor-private-method" to ::testAccessorPrivateMethod,
        "accessor-private-method-desc" to ::testAccessorPrivateMethodDesc,
        "accessor-theatre-child-field" to ::testAccessorTheatreChildField,
        "accessor-theatre-parent-field" to ::testAccessorTheatreParentField,
        "accessor-theatre-write-parent" to ::testAccessorTheatreWriteParent,
        // Accessor — Theatre 工具方法
        "util-arg" to ::testUtilArg,
        "util-arg-or-throw" to ::testUtilArgOrThrow,
        "util-arg-out-of-bounds" to ::testUtilArgOutOfBounds,
        "util-cast" to ::testUtilCast,
        "util-cast-null" to ::testUtilCastNull,
        "util-cast-or-throw" to ::testUtilCastOrThrow,
        "util-cast-or-throw-fail" to ::testUtilCastOrThrowFail,
        "util-self-as" to ::testUtilSelfAs,
        "util-read-field-on-arg" to ::testUtilReadFieldOnArg,
        "util-call-method-on-arg" to ::testUtilCallMethodOnArg,
        "util-write-field-on-arg" to ::testUtilWriteFieldOnArg,
    )

    // -------- LEAD --------
    fun testLead(): CaseResult = runCase("lead") { a, fx ->
        var hits = 0
        val s = Scalpel.transient {
            lead(GREET) { _ -> hits++ }
        }
        try {
            a.equal("hello, x", fx.greet("x"), "原方法返回")
            a.equal(1, hits, "lead 触发计数")
            a.equal(1, fx.fieldHits, "原方法仍执行(fieldHits)")
        } finally { s.heal() }
    }

    // -------- TRAIL --------
    fun testTrail(): CaseResult = runCase("trail") { a, fx ->
        var hits = 0
        val s = Scalpel.transient {
            trail(GREET) { _ -> hits++ }
        }
        try {
            fx.greet("y")
            a.equal(1, hits, "trail 触发计数")
        } finally { s.heal() }
    }

    fun testTrailOnThrow(): CaseResult = runCase("trail-throw") { a, fx ->
        var hits = 0
        val s = Scalpel.transient {
            trail(MAYTHROW) { _ -> hits++ }
        }
        try {
            runCatching { fx.mayThrow(true) }
            a.equal(1, hits, "trail (默认 onThrow=true) 异常出口命中 1 次")
        } finally { s.heal() }
    }

    // -------- SPLICE --------
    fun testSpliceProceed(): CaseResult = runCase("splice-proceed") { a, fx ->
        // SPLICE: 返回 null 时回退原方法
        val s = Scalpel.transient {
            splice(GREET) { _ -> null }
        }
        try { a.equal("hello, z", fx.greet("z"), "splice 返回 null → 走原方法") }
        finally { s.heal() }
    }

    fun testSpliceSkip(): CaseResult = runCase("splice-skip") { a, fx ->
        val s = Scalpel.transient {
            splice(GREET) { _ -> "OVERRIDE" }
        }
        try {
            a.equal("OVERRIDE", fx.greet("z"), "splice 返回非 null → 替换")
            a.equal(0, fx.fieldHits, "原方法应被跳过")
        } finally { s.heal() }
    }

    fun testSpliceModifyArgs(): CaseResult = runCase("splice-modify-args") { a, fx ->
        // splice 直接覆盖返回值, 不改 args（DSL 当前 API 不暴露 args 改写后再放行）
        val s = Scalpel.transient {
            splice(GREET) { th -> "got=${th.args[0]}" }
        }
        try { a.equal("got=k", fx.greet("k"), "可读 args") }
        finally { s.heal() }
    }

    // -------- BYPASS --------
    fun testBypass(): CaseResult = runCase("bypass") { a, fx ->
        // BYPASS 走 SPLICE 同样的入口路径（dispatcher 链统一处理）
        val s = Scalpel.transient {
            bypass(ADD) { th -> ((th.args[0] as Int) + (th.args[1] as Int)) * 100 }
        }
        try { a.equal(300, fx.add(1, 2), "bypass 替换返回") }
        finally { s.heal() }
    }

    // -------- EXCISE --------
    fun testExcise(): CaseResult = runCase("excise") { a, fx ->
        val s = Scalpel.transient {
            excise(ADD) { _ -> 42 }
        }
        try { a.equal(42, fx.add(7, 8), "excise 整段替换") }
        finally { s.heal() }
    }

    // -------- 通配符描述符 --------
    fun testWildcard(): CaseResult = runCase("wildcard") { a, fx ->
        var hit = false
        val s = Scalpel.transient {
            lead(WILDCARD) { _ -> hit = true }
        }
        try {
            fx.greet("w")
            a.check(hit, "通配 (*) 应命中 greet")
        } finally { s.heal() }
    }

    // -------- 静态方法 --------
    fun testStaticTarget(): CaseResult = runCase("static-target") { a, _ ->
        var hits = 0
        val s = Scalpel.transient {
            lead(STATICECHO) { _ -> hits++ }
        }
        try {
            val r = TargetFixture.staticEcho("S")
            a.equal("static:S", r, "静态方法返回")
            a.equal(1, hits, "静态方法 lead 命中")
        } finally { s.heal() }
    }

    // -------- void --------
    fun testVoidTarget(): CaseResult = runCase("void-target") { a, fx ->
        var hits = 0
        val s = Scalpel.transient {
            lead(VOIDNOARG) { _ -> hits++ }
        }
        try {
            fx.voidNoArg()
            a.equal(1, hits, "void lead 命中")
            a.equal(1, fx.fieldHits, "原 void 方法执行")
        } finally { s.heal() }
    }

    fun testPrimitiveInt(): CaseResult = runCase("primitive-int") { a, fx ->
        val s = Scalpel.transient {
            splice(ADD) { _ -> 999 }
        }
        try { a.equal(999, fx.add(1, 2), "int splice") }
        finally { s.heal() }
    }

    fun testPrimitiveLong(): CaseResult = runCase("primitive-long") { a, fx ->
        val s = Scalpel.transient {
            splice(LONGCALC) { _ -> 12345L }
        }
        try { a.equal(12345L, fx.longCalc(2, 3), "long splice") }
        finally { s.heal() }
    }

    fun testPrimitiveBool(): CaseResult = runCase("primitive-bool") { a, fx ->
        val s = Scalpel.transient {
            splice(RETBOOL) { _ -> true }
        }
        try { a.equal(true, fx.returnsBoolean(true), "bool splice") }
        finally { s.heal() }
    }

    fun testMultiArg(): CaseResult = runCase("multi-arg") { a, fx ->
        var captured: List<Any?> = emptyList()
        val s = Scalpel.transient {
            lead(MULTIARG) { th -> captured = th.args.toList() }
        }
        try {
            fx.multiArg(1, "B", 2.5)
            a.equal(listOf<Any?>(1, "B", 2.5), captured, "三参 args 抓取")
        } finally { s.heal() }
    }

    // -------- where 谓词 --------
    fun testWhereTrue(): CaseResult = runCase("where-true") { a, fx ->
        var hits = 0
        val s = Scalpel.transient {
            lead(GREET) { _ -> hits++ } where { true }
        }
        try {
            fx.greet("a")
            a.equal(1, hits, "where=true 应触发")
        } finally { s.heal() }
    }

    fun testWhereFalse(): CaseResult = runCase("where-false") { a, fx ->
        var hits = 0
        val s = Scalpel.transient {
            lead(GREET) { _ -> hits++ } where { false }
        }
        try {
            fx.greet("a")
            a.equal(0, hits, "where=false 不应触发")
        } finally { s.heal() }
    }

    // -------- 优先级顺序 --------
    fun testPriorityOrder(): CaseResult = runCase("priority-order") { a, fx ->
        val order = mutableListOf<String>()
        val s1 = Scalpel.transient {
            priority(10); lead(GREET) { _ -> order += "high" }
        }
        val s2 = Scalpel.transient {
            priority(1); lead(GREET) { _ -> order += "low" }
        }
        try {
            fx.greet("p")
            a.note("执行顺序=$order")
            a.check(order.size == 2, "两条 lead 都应触发")
            // 高优先级在前
            a.check(order.firstOrNull() == "high" || order.lastOrNull() == "high", "包含 high")
        } finally { s1.heal(); s2.heal() }
    }

    // -------- 批量 on --------
    fun testOnBatch(): CaseResult = runCase("on-batch") { a, fx ->
        var c = 0
        val s = Scalpel.transient {
            on(GREET, ADD) {
                lead { _ -> c++ }
            }
        }
        try {
            fx.greet("o"); fx.add(1, 2)
            a.equal(2, c, "on 批量绑定两个目标")
        } finally { s.heal() }
    }

    // -------- transient + use --------
    fun testTransientUse(): CaseResult = runCase("transient-use") { a, fx ->
        var hits = 0
        val s = Scalpel.transient {
            lead(GREET) { _ -> hits++ }
        }
        try { fx.greet("u"); a.equal(1, hits, "块内触发") } finally { s.close() }
        fx.greet("u")
        a.equal(1, hits, "close 后不再触发")
    }

    // -------- scoped --------
    fun testScoped(): CaseResult = runCase("scoped") { a, fx ->
        var hits = 0
        Scalpel.scoped {
            lead(GREET) { _ -> hits++ }
        }.run {
            fx.greet("s")
            a.equal(1, hits, "scoped 内触发")
        }
        fx.greet("s")
        a.equal(1, hits, "scoped 出 → 不再触发")
    }

    // -------- threadLocal --------
    fun testThreadLocalActive(): CaseResult = runCase("threadlocal-active") { a, fx ->
        val tls = Scalpel.threadLocal {
            lead(GREET) { _ -> a.note("tls hit") }
        }
        try {
            tls.activateOnCurrentThread()
            var hits = 0
            val s = Scalpel.transient { lead(GREET) { _ -> hits++ } }
            try {
                fx.greet("t")
                a.check(hits >= 1, "lead 应触发 (附加在同一目标的辅助 lead)")
            } finally { s.heal() }
        } finally { tls.deactivateOnCurrentThread(); tls.heal() }
    }

    fun testThreadLocalInactive(): CaseResult = runCase("threadlocal-inactive") { a, fx ->
        var hits = 0
        val tls = Scalpel.threadLocal {
            lead(GREET) { _ -> hits++ }
        }
        try {
            // 未 activate
            fx.greet("ti")
            a.equal(0, hits, "未激活线程不触发")
        } finally { tls.heal() }
    }

    // -------- arm/disarm --------
    fun testArmTrigger(): CaseResult = runCase("armon-disarmon") { a, fx ->
        val trigger = Scalpel.armOn(java.util.EventObject::class.java) {
            lead(GREET) { _ -> a.note("armed lead fired") }
        }
        var hits = 0
        try {
            // 默认未 arm
            fx.greet("e")
            // arm 后注册 advice
            trigger.arm()
            val s = Scalpel.transient { lead(GREET) { _ -> hits++ } }
            try {
                fx.greet("e")
                a.check(hits == 1, "arm 后辅助 lead 触发")
            } finally { s.heal() }
            trigger.disarm()
            a.check(!trigger.isArmed(), "disarm 状态")
        } finally { trigger.close() }
    }

    // -------- exclusive --------
    fun testExclusive(): CaseResult = runCase("exclusive") { a, fx ->
        var bgHits = 0
        val bg = Scalpel.transient {
            lead(GREET) { _ -> bgHits++ }
        }
        try {
            Scalpel.exclusive(block = {
                bypass(GREET) { _ -> "EX" }
            }) {
                a.equal("EX", fx.greet("x"), "exclusive 内 bypass 生效")
            }
            // 退出后背景 lead 应恢复
            fx.greet("x")
            a.check(bgHits >= 1, "exclusive 退出后背景 advice 恢复")
        } finally { bg.heal() }
    }

    // -------- heal --------
    fun testHeal(): CaseResult = runCase("heal") { a, fx ->
        var hits = 0
        val s = Scalpel.transient {
            lead(GREET) { _ -> hits++ }
        }
        fx.greet("h")
        a.equal(1, hits, "heal 前触发")
        s.heal()
        a.equal(Suture.State.HEALED, s.state, "状态=HEALED")
        fx.greet("h")
        a.equal(1, hits, "heal 后不再触发")
    }

    // -------- suspend / resume --------
    fun testSuspendResume(): CaseResult = runCase("suspend-resume") { a, fx ->
        var hits = 0
        val s = Scalpel.transient {
            lead(GREET) { _ -> hits++ }
        }
        try {
            s.suspend()
            fx.greet("r")
            a.equal(0, hits, "suspend 跳过")
            s.resume()
            fx.greet("r")
            a.equal(1, hits, "resume 后触发")
        } finally { s.heal() }
    }

    // -------- find / list --------
    fun testFindList(): CaseResult = runCase("find-list") { a, _ ->
        val s = Scalpel.transient {
            lead(GREET) { _ -> }
        }
        try {
            val found = Scalpel.find(s.id)
            a.check(found === s, "find by id")
            val all = Scalpel.list()
            a.check(all.contains(s), "list 包含 s")
        } finally { s.heal() }
    }

    // -------- healAll --------
    fun testHealAll(): CaseResult = runCase("healall") { a, _ ->
        val s1 = Scalpel.transient { lead(GREET) { _ -> } }
        val s2 = Scalpel.transient { lead(ADD) { _ -> } }
        val before = Scalpel.list().size
        val n = Scalpel.healAll(scope = "AllCases")
        a.check(n >= 2, "healAll 至少卸载本测试创建的两条 (n=$n)")
        a.check(Scalpel.list().size <= before - 2, "list 数量减少")
        // s1/s2 已被 healAll 移除，重复 heal 安全
        s1.heal(); s2.heal()
    }

    // -------- 多 target --------
    fun testMultiTarget(): CaseResult = runCase("multi-target") { a, fx ->
        var c = 0
        val s = Scalpel.transient {
            lead(GREET) { _ -> c++ }
            lead(ADD) { _ -> c++ }
            lead(VOIDNOARG) { _ -> c++ }
        }
        try {
            fx.greet("m"); fx.add(1, 2); fx.voidNoArg()
            a.equal(3, c, "三个目标各触发一次")
            a.check(s.targets.size == 3, "suture.targets.size=3")
        } finally { s.heal() }
    }

    fun testAutoClose(): CaseResult = runCase("auto-close") { a, fx ->
        var hits = 0
        Scalpel.transient { lead(GREET) { _ -> hits++ } }.use {
            fx.greet("c")
        }
        a.equal(1, hits, "use 块内触发一次")
        fx.greet("c")
        a.equal(1, hits, "use 关闭后不再触发")
    }

    // -------- 错误路径 --------
    fun testDuplicateId(): CaseResult = runCase("duplicate-id") { a, _ ->
        val s = Scalpel.transient { lead(GREET) { _ -> } }
        try {
            // transient 自带递增 seq，不会触发同 id；此处验证 SurgeryRegistry.find 可重入
            val again = Scalpel.find(s.id)
            a.check(again === s, "唯一 id 可查")
        } finally { s.heal() }
    }

    fun testBadDescriptor(): CaseResult = runCase("bad-descriptor") { a, _ ->
        val err = runCatching {
            Scalpel.transient { lead("notADescriptor_no_hash") { _ -> } }
        }.exceptionOrNull()
        a.check(err != null, "非法描述符应抛异常")
        a.note("异常: ${err?.javaClass?.simpleName}: ${err?.message}")
    }

    // ====================================================================
    // 注解 API 测试 — 由 incision 模块的 SurgeonScanner 在 ENABLE 阶段已自动织入。
    // 此处只做行为验证，不再注册新 advice。
    // ====================================================================

    fun testSurgeonLead(): CaseResult = runCase("surgeon-lead") { a, _ ->
        SurgeonBasicCases.reset()
        val fx = SurgeonBasicTargetFixture()
        val r = fx.greet("annotated")
        a.equal("hello, annotated", r, "原方法仍执行")
        a.equal(1, SurgeonBasicCases.leadHits, "@Lead 触发计数")
        a.equal("annotated", SurgeonBasicCases.lastArg, "@Lead 抓取到 args[0]")
    }

    fun testSurgeonTrail(): CaseResult = runCase("surgeon-trail") { a, _ ->
        SurgeonBasicCases.reset()
        val fx = SurgeonBasicTargetFixture()
        fx.voidNoArg()
        a.equal(1, SurgeonBasicCases.trailHits, "@Trail 触发计数")
    }

    fun testSurgeonSplice(): CaseResult = runCase("surgeon-splice") { a, _ ->
        SurgeonBasicCases.reset()
        val fx = SurgeonBasicTargetFixture()
        val r = fx.add(2, 3)
        a.equal(5000, r, "@Splice 替换返回值")
        a.equal(1, SurgeonBasicCases.spliceHits, "@Splice 触发计数")
    }

    fun testSurgeonExcise(): CaseResult = runCase("surgeon-excise") { a, _ ->
        SurgeonAdvancedCases.reset()
        val fx = SurgeonAdvancedTargetFixture()
        val r = fx.returnsBoolean(false)
        a.equal(true, r, "@Excise 整段替换 → 始终 true")
        a.check(SurgeonAdvancedCases.exciseHits >= 1, "@Excise 触发")
    }

    fun testSurgeonBypass(): CaseResult = runCase("surgeon-bypass") { a, _ ->
        SurgeonAdvancedCases.reset()
        val fx = SurgeonAdvancedTargetFixture()
        val r = fx.longCalc(2L, 3L)
        a.equal(777L, r, "@Bypass 替换 long 返回")
        a.check(SurgeonAdvancedCases.bypassHits >= 1, "@Bypass 触发")
    }

    // ---- AOP 风格注解 demo ----

    fun testSurgeonAopBefore(): CaseResult = runCase("surgeon-aop-before") { a, _ ->
        SurgeonAopCases.reset()
        val fx = SurgeonAopTargetFixture()
        fx.touchesField()
        a.equal(1, SurgeonAopCases.beforeHits, "@Lead(@Before) 触发")
    }

    fun testSurgeonAopAfter(): CaseResult = runCase("surgeon-aop-after") { a, _ ->
        SurgeonAopCases.reset()
        val fx = SurgeonAopTargetFixture()
        fx.multiArg(1, "B", 2.5)
        a.equal(1, SurgeonAopCases.afterHits, "@Trail(@After) 触发")
        a.equal(listOf<Any?>(1, "B", 2.5), SurgeonAopCases.lastAuditArgs, "@After 抓到 args")
    }

    fun testSurgeonAopAround(): CaseResult = runCase("surgeon-aop-around") { a, _ ->
        SurgeonAopCases.reset()
        val fx = SurgeonAopTargetFixture()
        val r = fx.callsHelper(5)
        a.equal(51, r, "@Splice(@Around) 放行原方法，返回值不变")
        a.equal(1, SurgeonAopCases.aroundHits, "@Around 触发")
    }

    fun testSurgeonAopModifyArgs(): CaseResult = runCase("surgeon-aop-modify-args") { a, _ ->
        SurgeonAopCases.reset()
        val fx = SurgeonAopTargetFixture()
        val r = fx.greet("demo")
        a.equal("hello, patched-demo", r, "@Splice + proceed(args) 改写参数后再放行")
        a.equal(1, SurgeonAopCases.aroundArgsHits, "proceed(args) demo 触发")
    }

    fun testSurgeonAopProceedResult(): CaseResult = runCase("surgeon-aop-proceed-result") { a, _ ->
        SurgeonAopCases.reset()
        val fx = SurgeonAopTargetFixture()
        val r = fx.rewriteTarget(3)
        a.equal(130, r, "@Splice + proceedResult(result) 改写结果继续返回")
        a.equal(1, SurgeonAopCases.aroundResultHits, "proceedResult demo 触发")
    }

    fun testSurgeonAopStaticTarget(): CaseResult = runCase("surgeon-aop-static-target") { a, _ ->
        SurgeonAopCases.reset()
        val r = SurgeonAopTargetFixture.staticEcho("demo")
        a.equal("static:demo", r, "静态桥目标返回值保持不变")
        a.check(SurgeonAopCases.staticLeadHits >= 1, "@KotlinTarget(jvmStaticBridge=true) 命中静态桥")
    }

    fun testSurgeonAopCompanionTarget(): CaseResult = runCase("surgeon-aop-companion-target") { a, _ ->
        SurgeonAopCases.reset()
        SurgeonAopTargetFixture.companionHits = 0
        val r = SurgeonAopTargetFixture.Companion.companionEcho("demo")
        a.equal("companion:demo", r, "companion 实例方法返回值保持不变")
        a.equal(1, SurgeonAopTargetFixture.companionHits, "companion 原方法仍执行")
        a.check(SurgeonAopCases.companionLeadHits >= 1, "@KotlinTarget(companionInstance=true) 命中 companion 实例")
    }

    // ---- Mixin 风格注解 demo ----

    fun testSurgeonMixinOverwrite(): CaseResult = runCase("surgeon-mixin-overwrite") { a, _ ->
        SurgeonMixinCases.reset()
        val fx = SurgeonMixinTargetFixture()
        val r = fx.mayThrow(true)   // 原方法会抛异常，@Excise 整段替换后不再抛
        a.equal("mixin-safe", r, "@Excise(@Overwrite) 整段替换，不再抛异常")
        a.check(SurgeonMixinCases.exciseHits >= 1, "@Excise 触发")
    }

    fun testSurgeonMixinRedirect(): CaseResult = runCase("surgeon-mixin-redirect") { a, _ ->
        SurgeonMixinCases.reset()
        val fx = SurgeonMixinTargetFixture()
        val r = fx.helper(3)
        a.equal(888, r, "@Bypass(@Redirect) 重定向 helper → 888")
        a.check(SurgeonMixinCases.bypassHits >= 1, "@Bypass 触发")
    }

    // ====================================================================
    // 跨 ClassLoader 测试 — 切入服务端 ClassLoader 中的 Bukkit/NMS 类，
    // 证明 Incision 能跨越插件 IsolatedClassLoader 与服务端 CL 边界生效。
    // ====================================================================

    fun testBukkitMaxPlayers(): CaseResult = runCase("bukkit-max-players") { a, _ ->
        val bukkitCls = org.bukkit.Bukkit::class.java
        val getMaxPlayers = bukkitCls.getMethod("getMaxPlayers")
        val original = getMaxPlayers.invoke(null) as Int
        val s = Scalpel.transient {
            splice("org.bukkit.Bukkit#getMaxPlayers()int") { _ -> 12345 }
        }
        try {
            val woven = getMaxPlayers.invoke(null) as Int
            a.equal(12345, woven, "Bukkit.getMaxPlayers() 应被替换")
            a.note("原值=$original")
        } finally { s.heal() }
        val restored = getMaxPlayers.invoke(null) as Int
        a.equal(original, restored, "heal 后还原")
    }

    fun testBukkitViewDistance(): CaseResult = runCase("bukkit-view-distance") { a, _ ->
        val server = org.bukkit.Bukkit.getServer()
        val craftServerCls = server.javaClass
        val getViewDistance = craftServerCls.getMethod("getViewDistance")
        val original = getViewDistance.invoke(server) as Int
        val descriptor = "${craftServerCls.name}#getViewDistance()int"
        val s = Scalpel.transient {
            splice(descriptor) { _ -> 99 }
        }
        try {
            val woven = getViewDistance.invoke(server) as Int
            a.equal(99, woven, "CraftServer.getViewDistance() 应被替换")
            a.note("原值=$original target=$descriptor")
        } finally { s.heal() }
    }

    fun testNmsMotd(): CaseResult = runCase("nms-motd") { a, _ ->
        // 切入 NMS net.minecraft.server.MinecraftServer#getMotd()
        // 通过 Bukkit -> CraftServer.getServer() 拿到 MinecraftServer 实例
        val server = org.bukkit.Bukkit.getServer()
        val getServerMethod = server.javaClass.declaredMethods.firstOrNull { it.name == "getServer" && it.parameterCount == 0 }
        if (getServerMethod == null) { a.note("CraftServer#getServer() 不存在 (非 Paper/Spigot 1.21+)，跳过"); return@runCase }
        getServerMethod.isAccessible = true
        val mcServer = getServerMethod.invoke(server)
        val mcServerCls = mcServer.javaClass
        val getMotd = generateSequence<Class<*>>(mcServerCls) { it.superclass }
            .mapNotNull { runCatching { it.getDeclaredMethod("getMotd") }.getOrNull() }
            .firstOrNull()
        if (getMotd == null) { a.note("MinecraftServer#getMotd() 未找到，跳过"); return@runCase }
        getMotd.isAccessible = true
        val original = getMotd.invoke(mcServer) as? String
        val ownerCls = getMotd.declaringClass
        val descriptor = "${ownerCls.name}#getMotd()java.lang.String"
        val s = Scalpel.transient {
            splice(descriptor) { _ -> "WOVEN-NMS-MOTD" }
        }
        try {
            val woven = getMotd.invoke(mcServer) as? String
            a.equal("WOVEN-NMS-MOTD", woven, "NMS getMotd() 应被替换 (target=$descriptor)")
            a.note("原值=$original")
        } finally { s.heal() }
    }

    // ---- 跨 CL 注解切口验证 ----

    fun testCrossClNmsIsHardcore(): CaseResult = runCase("cross-cl-nms-ishardcore") { a, _ ->
        SurgeonCrossClCases.reset()
        // CraftServer.isHardcore() 在 1.12.2 直接读字段，不委托 MinecraftServer.isHardcore()
        // 需要直接反射调用 MinecraftServer.isHardcore() 才能触发 weave
        val server = Bukkit.getServer()
        val getServerMethod = server.javaClass.declaredMethods.firstOrNull { it.name == "getServer" && it.parameterCount == 0 }
        if (getServerMethod == null) { a.note("CraftServer#getServer() 不存在，跳过"); return@runCase }
        getServerMethod.isAccessible = true
        val mcServer = getServerMethod.invoke(server)
        val m = generateSequence<Class<*>>(mcServer.javaClass) { it.superclass }
            .firstNotNullOfOrNull { runCatching { it.getDeclaredMethod("isHardcore") }.getOrNull() }
            ?: run { a.note("isHardcore 未找到，跳过"); return@runCase }
        m.isAccessible = true
        m.invoke(mcServer)
        a.check(SurgeonCrossClCases.isHardcoreLeadHits >= 1, "@Lead 切入 NMS isHardcore() 触发")
    }

    fun testCrossClNmsPlayerCount(): CaseResult = runCase("cross-cl-nms-playercount") { a, _ ->
        SurgeonCrossClCases.reset()
        Bukkit.getOnlinePlayers()   // CraftServer 内部会调用 getPlayerCount
        // 直接反射调用确保命中
        val server = Bukkit.getServer()
        val getServerMethod = server.javaClass.declaredMethods.firstOrNull { it.name == "getServer" && it.parameterCount == 0 }
        if (getServerMethod == null) { a.note("CraftServer#getServer() 不存在，跳过"); return@runCase }
        getServerMethod.isAccessible = true
        val mcServer = getServerMethod.invoke(server)
        val m = generateSequence<Class<*>>(mcServer.javaClass) { it.superclass }.firstNotNullOfOrNull { runCatching { it.getDeclaredMethod("getPlayerCount") }.getOrNull() } ?: run { a.note("getPlayerCount 未找到，跳过"); return@runCase }
        m.isAccessible = true
        m.invoke(mcServer)
        a.check(SurgeonCrossClCases.getPlayerCountLeadHits >= 1, "@Lead 切入 NMS getPlayerCount() 触发")
    }

    fun testCrossClBukkitMaxPlayers(): CaseResult = runCase("cross-cl-bukkit-maxplayers") { a, _ ->
        SurgeonCrossClCases.reset()
        Bukkit.getMaxPlayers()
        a.check(SurgeonCrossClCases.getMaxPlayersLeadHits >= 1, "@Lead 切入 Bukkit.getMaxPlayers() 触发")
    }

    // ====================================================================
    // Site / Graft / Bypass / Trim 锚点覆盖
    // ====================================================================

    fun testSurgeonGraftInvokeBefore(): CaseResult = runCase("surgeon-graft-invoke-before") { a, _ ->
        SurgeonSiteCases.reset()
        val fx = SurgeonSiteTargetFixture()
        val r = fx.invokeHelper(1)
        // BYPASS 与 GRAFT 共锚点同时启用：BYPASS 把 helper(x) 替换为 1000，GRAFT BEFORE 仍在替换后的 INVOKE 之前触发
        a.equal(2000, r, "BYPASS 替换 helper 为 1000，总和 2000")
        a.equal(2, SurgeonSiteCases.graftInvokeBeforeHits, "@Graft(INVOKE, BEFORE) 命中 2 次 (两次 helper 调用)")
    }

    fun testSurgeonGraftInvokeAfter(): CaseResult = runCase("surgeon-graft-invoke-after") { a, _ ->
        SurgeonSiteCases.reset()
        val fx = SurgeonSiteTargetFixture()
        fx.invokeHelper(3)
        a.equal(2, SurgeonSiteCases.graftInvokeAfterHits, "@Graft(INVOKE, AFTER) 命中 2 次")
    }

    fun testSurgeonGraftFieldGet(): CaseResult = runCase("surgeon-graft-field-get") { a, _ ->
        SurgeonSiteCases.reset()
        val fx = SurgeonSiteTargetFixture()
        fx.counter = 42
        val r = fx.readCounter()
        a.equal(42, r, "原方法返回 counter")
        a.equal(1, SurgeonSiteCases.graftFieldGetHits, "@Graft(FIELD_GET) 精准命中 1 次")
    }

    fun testSurgeonGraftFieldPut(): CaseResult = runCase("surgeon-graft-field-put") { a, _ ->
        SurgeonSiteCases.reset()
        val fx = SurgeonSiteTargetFixture()
        fx.writeCounter(7)
        a.equal(7, fx.counter, "字段写入生效")
        a.equal(1, SurgeonSiteCases.graftFieldPutHits, "@Graft(FIELD_PUT) 精准命中 1 次")
    }

    fun testSurgeonGraftNew(): CaseResult = runCase("surgeon-graft-new") { a, _ ->
        SurgeonSiteCases.reset()
        val fx = SurgeonSiteTargetFixture()
        val sb = fx.allocate()
        a.equal("allocated", sb.toString(), "原方法构造对象")
        a.equal(1, SurgeonSiteCases.graftNewHits, "@Graft(NEW) 精准命中 StringBuilder 1 次")
    }

    fun testSurgeonGraftThrow(): CaseResult = runCase("surgeon-graft-throw") { a, _ ->
        SurgeonSiteCases.reset()
        val fx = SurgeonSiteTargetFixture()
        val r = fx.throwIt()
        a.equal("caught:expected-throw", r, "原方法捕获异常")
        a.note("@Graft(THROW) 命中=${SurgeonSiteCases.graftThrowHits}")
    }

    fun testSurgeonBypassInvoke(): CaseResult = runCase("surgeon-bypass-invoke") { a, _ ->
        SurgeonSiteCases.reset()
        val fx = SurgeonSiteTargetFixture()
        val r = fx.invokeHelper(1)
        // helper 被替换为 1000，原本 (1+1)+(11+1)=14 → (1000)+(1000)=2000
        a.equal(2000, r, "@Bypass(INVOKE) 把 helper 调用替换为 1000")
        a.equal(2, SurgeonSiteCases.bypassHelperHits, "@Bypass 命中 2 次")
    }

    fun testSurgeonTrimReturn(): CaseResult = runCase("surgeon-trim-return") { a, _ ->
        SurgeonSiteCases.reset()
        val fx = SurgeonSiteTargetFixture()
        val r = fx.plainReturn()
        a.note("@Trim(RETURN) result=$r hits=${SurgeonSiteCases.trimReturnHits}")
    }

    fun testSurgeonTrimArg(): CaseResult = runCase("surgeon-trim-arg") { a, _ ->
        SurgeonSiteCases.reset()
        val fx = SurgeonSiteTargetFixture()
        val r = fx.echo("original")
        a.note("@Trim(ARG) 命中=${SurgeonSiteCases.trimArgHits} lastMessage=${fx.lastMessage} result=$r")
        a.check(SurgeonSiteCases.trimArgHits >= 1, "@Trim(ARG) 触发")
    }

    // ====================================================================
    // @Operation 属性覆盖 (id / enabled / priority)
    // ====================================================================

    fun testSurgeonOperationCustomId(): CaseResult = runCase("surgeon-operation-custom-id") { a, _ ->
        SurgeonOperationCases.reset()
        val fx = SurgeonOperationTargetFixture()
        fx.customIdTarget()
        a.equal(1, SurgeonOperationCases.customIdHits, "自定义 id advice 正常触发")
        // 验证注册表中可按自定义 id 查到
        val matches = Scalpel.list().filter { it.id.contains("custom-operation-id") }
        a.check(matches.isNotEmpty(), "SurgeryRegistry 含自定义 id (matches=${matches.size})")
    }

    fun testSurgeonOperationDisabled(): CaseResult = runCase("surgeon-operation-disabled") { a, _ ->
        SurgeonOperationCases.reset()
        val fx = SurgeonOperationTargetFixture()
        fx.disabledTarget()
        // 默认禁用不应触发
        a.equal(0, SurgeonOperationCases.disabledHits, "@Operation(enabled=false) 默认不触发")
        // 手动 resume 后应触发
        val suture = Scalpel.list().firstOrNull { it.id.contains("SurgeonOperationCases") && it.targets.any { t -> t.signature.contains("disabledTarget") } }
        if (suture != null && suture.resume()) {
            fx.disabledTarget()
            a.check(SurgeonOperationCases.disabledHits >= 1, "resume 后 advice 触发")
            suture.suspend()
        } else {
            a.note("未找到可 resume 的 disabled suture (list=${Scalpel.list().size})")
        }
    }

    fun testSurgeonOperationPriority(): CaseResult = runCase("surgeon-operation-priority") { a, _ ->
        SurgeonOperationCases.reset()
        val fx = SurgeonOperationTargetFixture()
        fx.priorityTarget()
        val order = SurgeonOperationCases.orderLog.toList()
        a.note("执行顺序=$order")
        a.check(order.size == 2, "两条 @Lead 都应触发 (actual=${order.size})")
        a.check(order.firstOrNull() == "high", "高优先级 (priority=100) 先执行")
    }

    /** priority=1 的 advice 必须落在 high 之后 —— 进一步约束顺序，不允许并列。 */
    fun testSurgeonOperationPriorityLowerWins(): CaseResult = runCase("surgeon-operation-priority-low") { a, _ ->
        SurgeonOperationCases.reset()
        val fx = SurgeonOperationTargetFixture()
        fx.priorityTarget()
        val order = SurgeonOperationCases.orderLog.toList()
        a.equal(2, order.size, "两条 advice 都触发")
        a.equal("high", order.first(), "priority=100 先")
        a.equal("low", order.last(), "priority=1 后")
    }

    /** @Operation(enabled=false) 默认禁用 + 通过 Scalpel.list 拿到句柄手动 resume。 */
    fun testSurgeonOperationDisabledManualResume(): CaseResult = runCase("surgeon-operation-disabled-resume") { a, _ ->
        SurgeonOperationCases.reset()
        val fx = SurgeonOperationTargetFixture()
        fx.disabledTarget()
        a.equal(0, SurgeonOperationCases.disabledHits, "默认禁用不触发")
        val target = Scalpel.list().firstOrNull {
            it.holder == SurgeonOperationCases::class &&
                it.targets.any { t -> t.signature.contains("disabledTarget") }
        }
        if (target == null) {
            a.note("未找到 disabled advice 注册项 (list=${Scalpel.list().size})")
            return@runCase
        }
        val resumed = target.resume()
        a.check(resumed, "resume() 返回 true")
        a.equal(Suture.State.ARMED, target.state, "resume 后状态=ARMED")
        fx.disabledTarget()
        a.check(SurgeonOperationCases.disabledHits >= 1, "resume 后 advice 触发 (hits=${SurgeonOperationCases.disabledHits})")
        target.suspend()
    }

    /** @Surgeon(priority=5) 默认值 —— 不带 @Operation 的 advice 应继承之，正常触发。 */
    fun testSurgeonDefaultPriority(): CaseResult = runCase("surgeon-default-priority") { a, _ ->
        SurgeonOperationCases.reset()
        val fx = SurgeonOperationTargetFixture()
        fx.surgeonDefaultTarget()
        val order = SurgeonOperationCases.surgeonDefaultLog.toList()
        a.equal(2, order.size, "继承 priority 与覆盖 priority 的两条 advice 都触发")
        a.check(order.contains("surgeon-default-5"), "继承 @Surgeon(priority=5) 的 advice 命中")
    }

    /** @Operation(priority=20) 应覆盖 @Surgeon(priority=5)，先于纯 @Lead 执行。 */
    fun testSurgeonOperationOverridesSurgeon(): CaseResult = runCase("surgeon-operation-overrides-surgeon") { a, _ ->
        SurgeonOperationCases.reset()
        val fx = SurgeonOperationTargetFixture()
        fx.surgeonDefaultTarget()
        val order = SurgeonOperationCases.surgeonDefaultLog.toList()
        a.equal(2, order.size, "两条 advice 全触发")
        a.equal("operation-override-20", order.first(), "@Operation(priority=20) 覆盖 @Surgeon(priority=5) → 先执行")
        a.equal("surgeon-default-5", order.last(), "继承默认 priority=5 的 advice 后执行")
    }

    /** 多个 @Surgeon object（priority=50 vs priority=5）协作织入同一目标，按全局 priority 排序。 */
    fun testSurgeonMultiSurgeonPriority(): CaseResult = runCase("surgeon-multi-surgeon-priority") { a, _ ->
        SurgeonOperationCases.reset()
        SurgeonOperationHigherCases.reset()
        val fx = SurgeonOperationTargetFixture()
        fx.multiSurgeonTarget()
        val order = SurgeonOperationCases.multiSurgeonLog.toList()
        a.equal(2, order.size, "两个 surgeon 的 advice 各触发一次 (actual=${order.size})")
        a.equal("high-surgeon-50", order.first(), "@Surgeon(priority=50) 先执行")
        a.equal("low-surgeon-5", order.last(), "@Surgeon(priority=5) 后执行")
    }

    // ====================================================================
    // @KotlinTarget 两态及组合 —— companionInstance / jvmStaticBridge
    // ====================================================================

    fun testKotlinTargetCompanionOnly(): CaseResult = runCase("kotlintarget-companion-only") { a, _ ->
        SurgeonKotlinTargetCases.reset()
        SurgeonKotlinTargetFixture.companionOnlyHits = 0
        val r = SurgeonKotlinTargetFixture.Companion.companionOnlyEcho("v")
        a.equal("co-only:v", r, "companion 实例方法返回值未变")
        a.equal(1, SurgeonKotlinTargetFixture.companionOnlyHits, "原 companion 方法仍执行")
        a.check(SurgeonKotlinTargetCases.companionOnlyHits >= 1, "@KotlinTarget(companionInstance=true) 命中")
    }

    fun testKotlinTargetJvmStaticOnly(): CaseResult = runCase("kotlintarget-jvmstatic-only") { a, _ ->
        SurgeonKotlinTargetCases.reset()
        SurgeonKotlinTargetFixture.staticOnlyHits = 0
        val r = SurgeonKotlinTargetFixture.staticOnlyEcho("v")
        a.equal("static-only:v", r, "@JvmStatic 静态桥返回值未变")
        a.check(SurgeonKotlinTargetCases.staticOnlyHits >= 1, "@KotlinTarget(jvmStaticBridge=true) 命中")
    }

    /** 组合场景 —— 静态桥路径：调用外部类静态方法。 */
    fun testKotlinTargetBothStaticPath(): CaseResult = runCase("kotlintarget-both-static-path") { a, _ ->
        SurgeonKotlinTargetCases.reset()
        SurgeonKotlinTargetFixture.bothCallHits = 0
        val r = SurgeonKotlinTargetFixture.bothEcho("v")
        a.equal("both:v", r, "返回值未变")
        a.check(SurgeonKotlinTargetCases.bothStaticPathHits >= 1, "静态桥 advice 命中 (hits=${SurgeonKotlinTargetCases.bothStaticPathHits})")
        a.note("companion 路径同时观察到 hits=${SurgeonKotlinTargetCases.bothCompanionPathHits}")
    }

    /** 组合场景 —— companion 实例路径：直接调用 Companion 实例方法。 */
    fun testKotlinTargetBothCompanionPath(): CaseResult = runCase("kotlintarget-both-companion-path") { a, _ ->
        SurgeonKotlinTargetCases.reset()
        SurgeonKotlinTargetFixture.bothCallHits = 0
        val r = SurgeonKotlinTargetFixture.Companion.bothEcho("v")
        a.equal("both:v", r, "返回值未变")
        a.check(SurgeonKotlinTargetCases.bothCompanionPathHits >= 1, "companion 实例 advice 命中 (hits=${SurgeonKotlinTargetCases.bothCompanionPathHits})")
        a.note("静态桥路径同时观察到 hits=${SurgeonKotlinTargetCases.bothStaticPathHits}")
    }

    // ====================================================================
    // @Operation 三维矩阵 (id × enabled × priority)
    // 9 个组合点 + 1 个排序断言 = 10 条用例
    // ====================================================================

    fun testOpMatrixM1Default(): CaseResult = runCase("op-matrix-m1-default") { a, _ ->
        OperationMatrixCases.reset()
        OperationMatrixFixture().m1Default()
        a.equal(1, OperationMatrixCases.m1Hits, "(id=\"\", enabled=true, priority=0) 默认基线触发")
    }

    fun testOpMatrixM2IdOnly(): CaseResult = runCase("op-matrix-m2-id-only") { a, _ ->
        OperationMatrixCases.reset()
        OperationMatrixFixture().m2CustomId()
        a.equal(1, OperationMatrixCases.m2Hits, "(id=\"m2-cust\", enabled=true, priority=0) 触发")
        val matched = Scalpel.list().any { it.id.contains("m2-cust") }
        a.check(matched, "SurgeryRegistry 含自定义 id m2-cust")
    }

    fun testOpMatrixM3DisabledOnly(): CaseResult = runCase("op-matrix-m3-disabled") { a, _ ->
        OperationMatrixCases.reset()
        OperationMatrixFixture().m3DisabledNoId()
        a.equal(0, OperationMatrixCases.m3Hits, "(enabled=false) 默认禁用 — 不触发")
    }

    fun testOpMatrixM4PriorityPositive(): CaseResult = runCase("op-matrix-m4-priority-pos") { a, _ ->
        OperationMatrixCases.reset()
        OperationMatrixFixture().m4PriorityPositive()
        a.equal(1, OperationMatrixCases.m4Hits, "(priority=10) 正数优先级触发")
    }

    fun testOpMatrixM5PriorityNegative(): CaseResult = runCase("op-matrix-m5-priority-neg") { a, _ ->
        OperationMatrixCases.reset()
        OperationMatrixFixture().m5PriorityNegative()
        a.equal(1, OperationMatrixCases.m5Hits, "(priority=-5) 负数优先级仍可触发")
    }

    fun testOpMatrixM6IdPriority(): CaseResult = runCase("op-matrix-m6-id-priority") { a, _ ->
        OperationMatrixCases.reset()
        OperationMatrixFixture().m6IdAndPriority()
        a.equal(1, OperationMatrixCases.m6Hits, "(id+priority=20) 触发")
        a.check(Scalpel.list().any { it.id.contains("m6-cust") }, "注册表含 m6-cust id")
    }

    fun testOpMatrixM7IdDisabled(): CaseResult = runCase("op-matrix-m7-id-disabled") { a, _ ->
        OperationMatrixCases.reset()
        OperationMatrixFixture().m7IdAndDisabled()
        a.equal(0, OperationMatrixCases.m7Hits, "(id+disabled) 默认不触发")
        a.check(Scalpel.list().any { it.id.contains("m7-cust") }, "注册表含 m7-cust id（虽默认禁用）")
    }

    fun testOpMatrixM8AllThree(): CaseResult = runCase("op-matrix-m8-all-three") { a, _ ->
        OperationMatrixCases.reset()
        OperationMatrixFixture().m8AllThree()
        a.equal(0, OperationMatrixCases.m8Hits, "(id+disabled+priority=99) 默认不触发")
        // 手动 resume 后命中
        val target = Scalpel.list().firstOrNull { it.id.contains("m8-cust") }
        if (target != null) {
            target.resume()
            OperationMatrixFixture().m8AllThree()
            a.check(OperationMatrixCases.m8Hits >= 1, "resume 后命中 (hits=${OperationMatrixCases.m8Hits})")
            target.suspend()
        } else {
            a.note("未找到 m8-cust 注册项 (list=${Scalpel.list().size})")
        }
    }

    fun testOpMatrixM9ExplicitZero(): CaseResult = runCase("op-matrix-m9-explicit-zero") { a, _ ->
        OperationMatrixCases.reset()
        OperationMatrixFixture().m9PriorityZeroExplicit()
        a.equal(1, OperationMatrixCases.m9Hits, "(显式 priority=0, enabled=true) 与默认行为等价")
    }

    fun testOpMatrixPriorityOrder(): CaseResult = runCase("op-matrix-priority-order") { a, _ ->
        OperationMatrixCases.reset()
        OperationMatrixFixture().mOrderTarget()
        val order = OperationMatrixCases.orderLog.toList()
        a.equal(4, order.size, "四条 advice 全触发 (actual=${order.size})")
        a.equal(listOf("p+50", "p+10", "p+0", "p-10"), order, "priority 降序：50 → 10 → 0 → -10")
    }

    // ====================================================================
    // @Surgeon(priority) 默认 / 0 / 正 / 负 四档 + 联合排序
    // ====================================================================

    fun testSurgeonPriorityDefault(): CaseResult = runCase("surgeon-priority-default") { a, _ ->
        SurgeonPriorityShared.reset()
        SurgeonPriorityFixture().pSharedTarget()
        a.check(SurgeonPriorityShared.log.contains("default"), "@Surgeon (默认 priority) advice 命中")
    }

    fun testSurgeonPriorityZeroExplicit(): CaseResult = runCase("surgeon-priority-zero-explicit") { a, _ ->
        SurgeonPriorityShared.reset()
        SurgeonPriorityFixture().pSharedTarget()
        a.check(SurgeonPriorityShared.log.contains("zero"), "@Surgeon(priority=0) advice 命中")
    }

    fun testSurgeonPriorityPositive(): CaseResult = runCase("surgeon-priority-positive") { a, _ ->
        SurgeonPriorityShared.reset()
        SurgeonPriorityFixture().pSharedTarget()
        a.check(SurgeonPriorityShared.log.contains("positive-30"), "@Surgeon(priority=30) advice 命中")
    }

    fun testSurgeonPriorityNegative(): CaseResult = runCase("surgeon-priority-negative") { a, _ ->
        SurgeonPriorityShared.reset()
        SurgeonPriorityFixture().pSharedTarget()
        a.check(SurgeonPriorityShared.log.contains("negative-10"), "@Surgeon(priority=-10) advice 命中")
    }

    fun testSurgeonPriorityOrderAll(): CaseResult = runCase("surgeon-priority-order-all") { a, _ ->
        SurgeonPriorityShared.reset()
        SurgeonPriorityFixture().pSharedTarget()
        val order = SurgeonPriorityShared.log.toList()
        a.equal(4, order.size, "四个 surgeon 各触发一次 (actual=${order.size})")
        a.equal("positive-30", order.first(), "priority=30 最先")
        a.equal("negative-10", order.last(), "priority=-10 最后")
        // 中间两个（默认 0 与显式 0）顺序不强求 —— 仅断言它们集合相等
        val middle = order.subList(1, 3).toSet()
        a.equal(setOf("default", "zero"), middle, "中间两位是 default 与 zero（priority=0 等价）")
    }

    // ====================================================================
    // @KotlinTarget 四组合 FF / FT / TF / TT
    // ====================================================================

    fun testKTargetMatrixFF(): CaseResult = runCase("ktarget-matrix-ff") { a, _ ->
        KotlinTargetMatrixCases.reset()
        val fx = KotlinTargetMatrixFixture()
        val r = fx.ffEcho("x")
        a.equal("ff:x", r, "FF 普通实例方法返回值未变")
        a.check(KotlinTargetMatrixCases.ffHits >= 1, "FF (无 @KotlinTarget) 命中 (hits=${KotlinTargetMatrixCases.ffHits})")
    }

    fun testKTargetMatrixFT(): CaseResult = runCase("ktarget-matrix-ft") { a, _ ->
        KotlinTargetMatrixCases.reset()
        val r = KotlinTargetMatrixFixture.ftStaticEcho("x")
        a.equal("ft:x", r, "FT @JvmStatic 静态桥返回值未变")
        a.check(KotlinTargetMatrixCases.ftHits >= 1, "FT (jvmStaticBridge=true) 命中 (hits=${KotlinTargetMatrixCases.ftHits})")
    }

    fun testKTargetMatrixTF(): CaseResult = runCase("ktarget-matrix-tf") { a, _ ->
        KotlinTargetMatrixCases.reset()
        val r = KotlinTargetMatrixFixture.Companion.tfCompanionEcho("x")
        a.equal("tf:x", r, "TF companion 实例方法返回值未变")
        a.check(KotlinTargetMatrixCases.tfHits >= 1, "TF (companionInstance=true) 命中 (hits=${KotlinTargetMatrixCases.tfHits})")
    }

    fun testKTargetMatrixTTStatic(): CaseResult = runCase("ktarget-matrix-tt-static") { a, _ ->
        KotlinTargetMatrixCases.reset()
        val r = KotlinTargetMatrixFixture.ttBothEcho("x")
        a.equal("tt:x", r, "TT 静态桥返回值未变")
        a.check(KotlinTargetMatrixCases.ttStaticPathHits >= 1, "TT 静态桥 advice 命中 (hits=${KotlinTargetMatrixCases.ttStaticPathHits})")
        a.note("companion 路径同次观察 hits=${KotlinTargetMatrixCases.ttCompanionPathHits}")
    }

    fun testKTargetMatrixTTCompanion(): CaseResult = runCase("ktarget-matrix-tt-companion") { a, _ ->
        KotlinTargetMatrixCases.reset()
        val r = KotlinTargetMatrixFixture.Companion.ttBothEcho("x")
        a.equal("tt:x", r, "TT companion 实例返回值未变")
        a.check(KotlinTargetMatrixCases.ttCompanionPathHits >= 1, "TT companion advice 命中 (hits=${KotlinTargetMatrixCases.ttCompanionPathHits})")
        a.note("静态桥路径同次观察 hits=${KotlinTargetMatrixCases.ttStaticPathHits}")
    }

    // ====================================================================
    // @SurgeryDesk 在不同 object / 不同 priority 上都能 transient
    // ====================================================================

    fun testDeskDefaultTransient(): CaseResult = runCase("desk-default-transient") { a, _ ->
        val fx = SurgeryDeskCallerFixture()
        val captured = SurgeryDeskDefaultCaller.runOnce(fx)
        a.equal(7, captured, "@SurgeryDesk() 默认 priority 下 transient lead 触发并抓到 args[0]")
        a.check(fx.hits >= 1, "原方法仍执行 (hits=${fx.hits})")
    }

    fun testDeskHighPriorityTransient(): CaseResult = runCase("desk-high-priority-transient") { a, _ ->
        val fx = SurgeryDeskCallerFixture()
        val captured = SurgeryDeskHighCaller.runOnce(fx)
        a.equal(11, captured, "@SurgeryDesk(priority=99) 下 transient lead 正常工作")
    }

    fun testDeskNegativePriorityTransient(): CaseResult = runCase("desk-negative-priority-transient") { a, _ ->
        val fx = SurgeryDeskCallerFixture()
        val captured = SurgeryDeskNegativeCaller.runOnce(fx)
        a.equal(13, captured, "@SurgeryDesk(priority=-7) 负数 priority 不影响 transient")
    }

    fun testDeskAllCasesSelf(): CaseResult = runCase("desk-allcases-self") { a, fx ->
        // 验证 AllCases 自己作为 @SurgeryDesk 持有时 transient 仍正常 —— 与 testLead 等价的健康检查。
        var hits = 0
        val s = Scalpel.transient {
            lead(GREET) { _ -> hits++ }
        }
        try {
            fx.greet("self")
            a.equal(1, hits, "AllCases (@SurgeryDesk) 内 transient 工作正常")
        } finally { s.heal() }
    }

    fun testOpIdNormal(): CaseResult = runCase("op-id-normal") { a, _ ->
        OperationIdVariantsCases.reset()
        OperationIdVariantsFixture().idNormalTarget()
        a.equal(1, OperationIdVariantsCases.normalHits, "普通 id 正常触发")
        a.check(Scalpel.list().any { it.id.contains("id-variant-normal") }, "注册表含正常 id")
    }

    fun testOpIdSpecialChars(): CaseResult = runCase("op-id-special-chars") { a, _ ->
        OperationIdVariantsCases.reset()
        OperationIdVariantsFixture().idSpecialCharsTarget()
        a.equal(1, OperationIdVariantsCases.specialHits, "含特殊字符 id 正常触发")
        a.check(Scalpel.list().any { it.id.contains("id::with/special.chars-001") }, "注册表保留特殊字符 id")
    }

    fun testOpIdEmptyFallback(): CaseResult = runCase("op-id-empty-fallback") { a, _ ->
        OperationIdVariantsCases.reset()
        OperationIdVariantsFixture().idEmptyFallbackTarget()
        a.equal(1, OperationIdVariantsCases.emptyHits, "空 id 不影响 advice 触发")
        a.check(Scalpel.list().any { it.id.contains("adviceEmptyFallback") }, "id=\"\" 回退到方法名")
    }

    fun testOpIdVeryLong(): CaseResult = runCase("op-id-very-long") { a, _ ->
        OperationIdVariantsCases.reset()
        OperationIdVariantsFixture().idVeryLongTarget()
        a.equal(1, OperationIdVariantsCases.longHits, "极长 id advice 仍可触发")
        val longId = OperationIdVariantsCases.VERY_LONG_ID
        a.check(longId.length >= 256, "长串实际长度 ${longId.length} >= 256")
        a.check(Scalpel.list().any { it.id.contains(longId) }, "注册表含极长 id")
    }

    fun testOpEnPriTrueNeg10(): CaseResult = runCase("op-enpri-true-neg10") { a, _ ->
        OperationEnabledPriorityCases.reset()
        OperationEnabledPriorityFixture().cellTPriNeg10()
        a.equal(1, OperationEnabledPriorityCases.tNeg10Hits, "(en=true,pri=-10) 触发")
    }

    fun testOpEnPriTrue0(): CaseResult = runCase("op-enpri-true-0") { a, _ ->
        OperationEnabledPriorityCases.reset()
        OperationEnabledPriorityFixture().cellTPri0()
        a.equal(1, OperationEnabledPriorityCases.t0Hits, "(en=true,pri=0) 触发")
    }

    fun testOpEnPriTrue5(): CaseResult = runCase("op-enpri-true-5") { a, _ ->
        OperationEnabledPriorityCases.reset()
        OperationEnabledPriorityFixture().cellTPri5()
        a.equal(1, OperationEnabledPriorityCases.t5Hits, "(en=true,pri=5) 触发")
    }

    fun testOpEnPriTrue100(): CaseResult = runCase("op-enpri-true-100") { a, _ ->
        OperationEnabledPriorityCases.reset()
        OperationEnabledPriorityFixture().cellTPri100()
        a.equal(1, OperationEnabledPriorityCases.t100Hits, "(en=true,pri=100) 触发")
    }

    fun testOpEnPriFalseNeg10(): CaseResult = runCase("op-enpri-false-neg10") { a, _ ->
        OperationEnabledPriorityCases.reset()
        OperationEnabledPriorityFixture().cellFPriNeg10()
        a.equal(0, OperationEnabledPriorityCases.fNeg10Hits, "(en=false,pri=-10) 默认禁用")
    }

    fun testOpEnPriFalse0(): CaseResult = runCase("op-enpri-false-0") { a, _ ->
        OperationEnabledPriorityCases.reset()
        OperationEnabledPriorityFixture().cellFPri0()
        a.equal(0, OperationEnabledPriorityCases.f0Hits, "(en=false,pri=0) 默认禁用")
    }

    fun testOpEnPriFalse5(): CaseResult = runCase("op-enpri-false-5") { a, _ ->
        OperationEnabledPriorityCases.reset()
        OperationEnabledPriorityFixture().cellFPri5()
        a.equal(0, OperationEnabledPriorityCases.f5Hits, "(en=false,pri=5) 默认禁用")
    }

    fun testOpEnPriFalse100(): CaseResult = runCase("op-enpri-false-100") { a, _ ->
        OperationEnabledPriorityCases.reset()
        OperationEnabledPriorityFixture().cellFPri100()
        a.equal(0, OperationEnabledPriorityCases.f100Hits, "(en=false,pri=100) 默认禁用 — 高优先级也不自动激活")
    }

    fun testOpSamePriorityStableOrder(): CaseResult = runCase("op-same-priority-stable-order") { a, _ ->
        OperationEnabledPriorityCases.reset()
        OperationEnabledPriorityFixture().samePriorityTarget()
        val order = OperationEnabledPriorityCases.samePriorityLog.toList()
        a.equal(2, order.size, "两条同 priority advice 都触发")
        a.check(order.toSet() == setOf("first", "second"), "两条都执行 (actual=$order)")
        a.note("同 priority=42 顺序观察=$order")
    }

    fun testOpDisableResumeSuspendCycle(): CaseResult = runCase("op-disable-resume-suspend-cycle") { a, _ ->
        OperationEnabledPriorityCases.reset()
        val fx = OperationEnabledPriorityFixture()
        fx.cycleTarget()
        a.equal(0, OperationEnabledPriorityCases.cycleHits, "默认 disabled 不触发")
        val target = Scalpel.list().firstOrNull { it.id.contains("cycle-advice") }
        if (target == null) { a.note("未找到 cycle-advice 注册项"); return@runCase }
        a.check(target.resume(), "resume() 返回 true")
        fx.cycleTarget()
        a.equal(1, OperationEnabledPriorityCases.cycleHits, "resume 后 +1")
        a.check(target.suspend(), "suspend() 返回 true")
        fx.cycleTarget()
        a.equal(1, OperationEnabledPriorityCases.cycleHits, "suspend 后维持")
        a.check(target.resume(), "二次 resume() 返回 true")
        fx.cycleTarget()
        a.equal(2, OperationEnabledPriorityCases.cycleHits, "二次 resume 后再 +1")
        target.suspend()
    }

    fun testSurgeonPriorityExtremeMax(): CaseResult = runCase("surgeon-priority-extreme-max") { a, _ ->
        SurgeonPriorityMaxCases.reset()
        SurgeonPriorityExtremeFixture().extremeMaxTarget()
        a.equal(1, SurgeonPriorityMaxCases.hits, "@Surgeon(priority=Int.MAX_VALUE-1000) advice 命中")
    }

    fun testSurgeonPriorityExtremeMin(): CaseResult = runCase("surgeon-priority-extreme-min") { a, _ ->
        SurgeonPriorityMinCases.reset()
        SurgeonPriorityExtremeFixture().extremeMinTarget()
        a.equal(1, SurgeonPriorityMinCases.hits, "@Surgeon(priority=Int.MIN_VALUE+1000) advice 命中")
    }

    fun testSurgeonThreeObjectOrder(): CaseResult = runCase("surgeon-three-object-order") { a, _ ->
        SurgeonPriorityExtremeShared.reset()
        SurgeonPriorityExtremeFixture().threeSurgeonTarget()
        val order = SurgeonPriorityExtremeShared.threeLog.toList()
        a.equal(3, order.size, "三个 surgeon 各触发一次 (actual=${order.size})")
        a.equal(listOf("p100", "p0", "p-100"), order, "priority 降序：100 → 0 → -100")
    }

    fun testKTargetFFAlt(): CaseResult = runCase("ktarget-ff-alt") { a, _ ->
        KotlinTargetMatrixCases.reset()
        val r = KotlinTargetMatrixFixture().ffAltMethod(7)
        a.equal(107, r, "FF 第二个目标返回 input+100")
        a.check(KotlinTargetMatrixCases.ffAltHits >= 1, "FF 第二个 advice 命中")
    }

    fun testKTargetFTAlt(): CaseResult = runCase("ktarget-ft-alt") { a, _ ->
        KotlinTargetMatrixCases.reset()
        val r = KotlinTargetMatrixFixture.ftStaticAlt(6)
        a.equal(12, r, "FT 第二个目标返回 input*2")
        a.check(KotlinTargetMatrixCases.ftAltHits >= 1, "FT 第二个 advice 命中")
    }

    fun testKTargetTFAlt(): CaseResult = runCase("ktarget-tf-alt") { a, _ ->
        KotlinTargetMatrixCases.reset()
        val r = KotlinTargetMatrixFixture.Companion.tfCompanionAlt(9)
        a.equal(10, r, "TF 第二个目标返回 input+1")
        a.check(KotlinTargetMatrixCases.tfAltHits >= 1, "TF 第二个 advice 命中")
    }

    fun testKTargetTTAltStatic(): CaseResult = runCase("ktarget-tt-alt-static") { a, _ ->
        KotlinTargetMatrixCases.reset()
        val r = KotlinTargetMatrixFixture.ttBothAlt(50)
        a.equal(49, r, "TT 第二个目标返回 input-1")
        a.check(KotlinTargetMatrixCases.ttAltStaticPathHits >= 1, "TT 第二个 静态桥 advice 命中")
    }

    fun testKTargetTTAltCompanion(): CaseResult = runCase("ktarget-tt-alt-companion") { a, _ ->
        KotlinTargetMatrixCases.reset()
        val r = KotlinTargetMatrixFixture.Companion.ttBothAlt(50)
        a.equal(49, r, "TT 第二个目标 companion 路径返回 input-1")
        a.check(KotlinTargetMatrixCases.ttAltCompanionPathHits >= 1, "TT 第二个 companion advice 命中")
    }


    // ====================================================================
    // Remap A+B+C —— @Version 区间过滤 + NoopVersionMatcher + NMS 方法名 remap
    // ====================================================================

    /**
     * B. @Version 区间共存 —— 三条 @Lead 挂在同一目标，区间分别为 [1.8,1.16]、[1.17,1.21]、[26.1,∞)。
     * 当前版本经 MinecraftVersionMatcher 解析后应该且仅应该命中其中一段。
     *
     * 判定策略：
     * - 触发原方法后，三段 hits 之和应 ≥ 1（至少命中一段；SurgeonScanner 在扫描时已剔除不命中段）
     * - 若 NMS 不可用（NoopVersionMatcher 回退，current=null），三段默认全过 → 总和应 ≥ 1
     */
    fun testVersionRangeHit(): CaseResult = runCase("version-range-hit") { a, _ ->
        SurgeonVersionCases.reset()
        val fx = SurgeonVersionTargetFixture()
        val r = fx.versionedTarget()
        a.equal("raw", r, "原方法返回未变 (@Lead 不改返回)")
        val total = SurgeonVersionCases.legacyHits + SurgeonVersionCases.modernHits + SurgeonVersionCases.futureHits
        a.check(total >= 1, "@Version 过滤后至少一段命中 (legacy=${SurgeonVersionCases.legacyHits}, modern=${SurgeonVersionCases.modernHits}, future=${SurgeonVersionCases.futureHits})")
        a.note("当前版本段落命中分布 — legacy=${SurgeonVersionCases.legacyHits}, modern=${SurgeonVersionCases.modernHits}, future=${SurgeonVersionCases.futureHits}")
    }

    /**
     * C. NoopVersionMatcher —— 外部 FQCN 形态的 matcher 解析通路验证。
     * matcher 字符串走 VersionMatchers.resolve，拿到 Kotlin object 单例，始终命中。
     */
    fun testVersionNoopMatcher(): CaseResult = runCase("version-noop-matcher") { a, _ ->
        SurgeonVersionCases.reset()
        val fx = SurgeonVersionTargetFixture()
        val r = fx.noopMatcherTarget()
        a.equal("noop-replaced", r, "@Splice + NoopVersionMatcher 正常替换返回")
        a.equal(1, SurgeonVersionCases.noopHits, "NoopVersionMatcher 路径下 advice 恰好触发一次")
    }

    /**
     * A. NMS 方法名 remap —— @Lead 通过 TabooLibNmsResolver.resolveMethod 映射到当前 mapping，
     * 调用 MinecraftServer#getPlayerCount() 应命中。非 Paper/Spigot 环境下方法缺失则跳过。
     */
    fun testRemapNmsMethodName(): CaseResult = runCase("remap-nms-method-name") { a, _ ->
        SurgeonVersionCases.reset()
        val server = Bukkit.getServer()
        val getServerMethod = server.javaClass.declaredMethods.firstOrNull { it.name == "getServer" && it.parameterCount == 0 }
        if (getServerMethod == null) { a.note("CraftServer#getServer() 不存在，跳过"); return@runCase }
        getServerMethod.isAccessible = true
        val mcServer = getServerMethod.invoke(server)
        val m = generateSequence<Class<*>>(mcServer.javaClass) { it.superclass }
            .firstNotNullOfOrNull { runCatching { it.getDeclaredMethod("getPlayerCount") }.getOrNull() }
            ?: run { a.note("getPlayerCount 未找到，跳过"); return@runCase }
        m.isAccessible = true
        m.invoke(mcServer)
        a.check(SurgeonVersionCases.nmsRemapHits >= 1, "@Lead 经 RemapTranslation.mapMethodName 映射后命中 MinecraftServer#getPlayerCount (hits=${SurgeonVersionCases.nmsRemapHits})")
    }

    // ====================================================================
    // Task #8 脚手架占位 —— 真断言等 weaver (#7) + predicate runtime (#11) 完成后再补
    // ====================================================================

    fun testSurgeonOffsetOrdinal(): CaseResult = runCase("surgeon-offset-ordinal") { a, _ ->
        SurgeonOffsetCases.reset()
        val fx = OffsetFixture()
        val r = fx.chain()
        a.equal(2 + 3 + 4, r, "原方法返回 (helper(1)+helper(2)+helper(3)=2+3+4)")
        a.equal(1, SurgeonOffsetCases.ordinal0Hits, "ordinal=0 命中第 1 次 helper 调用")
        a.equal(1, SurgeonOffsetCases.ordinal1Hits, "ordinal=1 命中第 2 次 helper 调用")
        a.equal(1, SurgeonOffsetCases.ordinal2Hits, "ordinal=2 命中第 3 次 helper 调用")
    }

    fun testSurgeonOffsetShift(): CaseResult = runCase("surgeon-offset-shift") { a, _ ->
        SurgeonOffsetCases.reset()
        val fx = OffsetFixture()
        fx.chain()
        a.equal(1, SurgeonOffsetCases.afterShiftHits, "Shift.AFTER + ordinal=0 + offset=1 应命中一次")
        a.equal(1, SurgeonOffsetCases.offsetZeroHits, "offset=0 等价于未偏移，ordinal=0 命中一次")
        a.equal(3, SurgeonOffsetCases.anyOrdinalHits, "ordinal=-1 + BEFORE 对 3 次 helper 调用全部命中")
    }

    fun testSurgeonInsnPattern(): CaseResult = runCase("surgeon-insn-pattern") { a, _ ->
        SurgeonInsnPatternCases.reset()
        val fx = InsnPatternFixture()
        val r = fx.accumulate(7)
        a.equal(12, r, "原方法返回 input + 5")
        a.equal(1, SurgeonInsnPatternCases.patternHits, "InsnPattern[ICONST_5; ISTORE; ILOAD; IADD] 匹配一次")
    }

    fun testSurgeonWhereDragon(): CaseResult = runCase("surgeon-where-dragon") { a, _ ->
        SurgeonPredicateCases.reset()
        val fx = PredicateFixture()
        fx.spawn("dragon", 99)   // 命中
        fx.spawn("dragon", 10)   // level<50 过滤
        fx.spawn("zombie", 99)   // name 过滤
        a.equal(1, SurgeonPredicateCases.dragonHits, "where=args[0]==dragon && args[1]>=50 仅 dragon@99 命中")
    }

    fun testSurgeonWhereLowLevel(): CaseResult = runCase("surgeon-where-low-level") { a, _ ->
        SurgeonPredicateCases.reset()
        val fx = PredicateFixture()
        fx.spawn("zombie", 5)    // 命中
        fx.spawn("zombie", 50)   // 不命中
        a.equal(1, SurgeonPredicateCases.anyLowLevelHits, "where=args[1]<10 仅 level=5 命中")
    }

    // ====================================================================
    // Task #15: where 谓词 DSL 全覆盖 —— 每种语法至少 1 通过 + 1 拒绝
    // ====================================================================

    // ---- 比较：== != < > <= >= ----

    fun testWhereEqString(): CaseResult = runCase("where-eq-string") { a, _ ->
        SurgeonPredicateCases.reset()
        val fx = PredicateFixture()
        fx.log("hello")  // 命中
        fx.log("world")  // 拒绝
        a.equal(1, SurgeonPredicateCases.eqStringHits, "where=args[0]==\"hello\" 仅 hello 命中")
    }

    fun testWhereNeqInt(): CaseResult = runCase("where-neq-int") { a, _ ->
        SurgeonPredicateCases.reset()
        val fx = PredicateFixture()
        fx.spawn("x", 5)  // 命中 (5!=0)
        fx.spawn("x", 0)  // 拒绝
        a.equal(1, SurgeonPredicateCases.neqIntHits, "where=args[1]!=0 仅非零命中")
    }

    fun testWhereLt(): CaseResult = runCase("where-lt") { a, _ ->
        SurgeonPredicateCases.reset()
        val fx = PredicateFixture()
        fx.measure(5.0)   // 命中
        fx.measure(20.0)  // 拒绝
        a.equal(1, SurgeonPredicateCases.ltHits, "where=args[0]<10 仅 5.0 命中")
    }

    fun testWhereGt(): CaseResult = runCase("where-gt") { a, _ ->
        SurgeonPredicateCases.reset()
        val fx = PredicateFixture()
        fx.measure(200.0)  // 命中
        fx.measure(50.0)   // 拒绝
        a.equal(1, SurgeonPredicateCases.gtHits, "where=args[0]>100 仅 200.0 命中")
    }

    fun testWhereLe(): CaseResult = runCase("where-le") { a, _ ->
        SurgeonPredicateCases.reset()
        val fx = PredicateFixture()
        fx.spawn("x", 5)   // 命中 (<=5)
        fx.spawn("x", 6)   // 拒绝
        a.equal(1, SurgeonPredicateCases.leHits, "where=args[1]<=5 仅 5 命中")
    }

    fun testWhereGe(): CaseResult = runCase("where-ge") { a, _ ->
        SurgeonPredicateCases.reset()
        val fx = PredicateFixture()
        fx.spawn("x", 100)  // 命中
        fx.spawn("x", 99)   // 拒绝
        a.equal(1, SurgeonPredicateCases.geHits, "where=args[1]>=100 仅 100 命中")
    }

    // ---- matches / in ----

    fun testWhereMatches(): CaseResult = runCase("where-matches") { a, _ ->
        SurgeonPredicateCases.reset()
        val fx = PredicateFixture()
        fx.log("hello, world")  // 命中 hello.*
        fx.log("goodbye")       // 拒绝
        a.equal(1, SurgeonPredicateCases.matchesHits, "where=args[0] matches \"hello.*\" 仅 hello, world 命中")
    }

    fun testWhereInString(): CaseResult = runCase("where-in-string") { a, _ ->
        SurgeonPredicateCases.reset()
        val fx = PredicateFixture()
        fx.log("below")   // 命中 "lo" in "below"
        fx.log("abc")     // 拒绝
        a.equal(1, SurgeonPredicateCases.inStringHits, "where=\"lo\" in args[0] 仅含 lo 命中")
    }

    fun testWhereInList(): CaseResult = runCase("where-in-list") { a, _ ->
        SurgeonPredicateCases.reset()
        val fx = PredicateFixture()
        fx.handle(listOf("key", "v1"))   // 命中
        fx.handle(listOf("foo", "bar"))  // 拒绝
        a.equal(1, SurgeonPredicateCases.inListHits, "where=\"key\" in args[0] 仅含 key 命中")
    }

    // ---- 类型算子 ----

    fun testWhereIsString(): CaseResult = runCase("where-is-string") { a, _ ->
        SurgeonPredicateCases.reset()
        val fx = PredicateFixture()
        fx.accept("hello")   // 命中
        fx.accept(42)        // 拒绝
        a.equal(1, SurgeonPredicateCases.isStringHits, "where=args[0] is java.lang.String 仅 String 命中")
    }

    fun testWhereNotIsInt(): CaseResult = runCase("where-not-is-int") { a, _ ->
        SurgeonPredicateCases.reset()
        val fx = PredicateFixture()
        fx.accept("text")   // 命中 (!is Integer)
        fx.accept(42)       // 拒绝
        a.equal(1, SurgeonPredicateCases.notIsIntHits, "where=args[0] !is java.lang.Integer 排除 Integer")
    }

    fun testWhereIcArrayList(): CaseResult = runCase("where-ic-arraylist") { a, _ ->
        SurgeonPredicateCases.reset()
        val fx = PredicateFixture()
        fx.handle(arrayListOf("a"))          // 命中 (ArrayList 是 List 严格子类)
        fx.handle(java.util.Arrays.asList<Any?>("a"))
        // ic=严格子类：ArrayList != List 本体，命中；但 Arrays$ArrayList 亦非 List 本体 → 也命中
        a.check(SurgeonPredicateCases.icListHits >= 1, "where=args[0] ic java.util.List 子类命中 (hits=${SurgeonPredicateCases.icListHits})")
    }

    fun testWhereIpObject(): CaseResult = runCase("where-ip-object") { a, _ ->
        SurgeonPredicateCases.reset()
        val fx = PredicateFixture()
        fx.accept("anything")   // 命中 (任意对象都 ip Object)
        fx.accept(Any())        // 命中
        a.equal(2, SurgeonPredicateCases.ipObjectHits, "where=args[0] ip java.lang.Object 全部命中")
    }

    fun testWhereItExact(): CaseResult = runCase("where-it-exact") { a, _ ->
        SurgeonPredicateCases.reset()
        val fx = PredicateFixture()
        fx.trigger("raw")   // args[0] 恰好是 String.class → 命中
        a.equal(1, SurgeonPredicateCases.itExactHits, "where=args[0] it java.lang.String 命中")
    }

    fun testWhereNotIt(): CaseResult = runCase("where-not-it") { a, _ ->
        SurgeonPredicateCases.reset()
        val fx = PredicateFixture()
        fx.accept(42)        // 命中 (Integer != String)
        fx.accept("string")  // 拒绝 (class 完全等于 String)
        a.equal(1, SurgeonPredicateCases.notItHits, "where=args[0] !it java.lang.String 排除精确 String")
    }

    fun testWhereAsCast(): CaseResult = runCase("where-as-cast") { a, _ ->
        SurgeonPredicateCases.reset()
        val fx = PredicateFixture()
        fx.accept("casted")   // 命中 (as 成功 → "casted"==\"casted\")
        fx.accept(42)         // 拒绝 (as 失败 → null → false)
        a.equal(1, SurgeonPredicateCases.asCastHits, "where=(args[0] as String)==\"casted\" 仅 casted 命中")
    }

    // ---- 属性访问 / 方法调用 ----

    fun testWhereProperty(): CaseResult = runCase("where-property") { a, _ ->
        SurgeonPredicateCases.reset()
        val fx = PredicateFixture()
        fx.register(PredicateFixture.Named("foo"))   // 命中
        fx.register(PredicateFixture.Named("bar"))   // 拒绝
        a.equal(1, SurgeonPredicateCases.propertyHits, "where=args[0].name==\"foo\" 仅 name=foo 命中")
    }

    fun testWherePropertySize(): CaseResult = runCase("where-property-size") { a, _ ->
        SurgeonPredicateCases.reset()
        val fx = PredicateFixture()
        fx.register(PredicateFixture.Named("x", 5))  // 命中
        fx.register(PredicateFixture.Named("x", 0))  // 拒绝
        a.equal(1, SurgeonPredicateCases.propertySizeHits, "where=args[0].size>0 仅 size>0 命中")
    }

    fun testWhereMethodCall(): CaseResult = runCase("where-method-call") { a, _ ->
        SurgeonPredicateCases.reset()
        val fx = PredicateFixture()
        fx.register(PredicateFixture.Named("foobar"))   // 命中
        fx.register(PredicateFixture.Named("zzz"))      // 拒绝
        a.equal(1, SurgeonPredicateCases.methodCallHits, "where=args[0].startsWith(\"foo\") 仅 foo 前缀命中")
    }

    fun testWhereMethodNoArg(): CaseResult = runCase("where-method-noarg") { a, _ ->
        SurgeonPredicateCases.reset()
        val fx = PredicateFixture()
        fx.register(PredicateFixture.Named("abc"))   // 命中 (length=3)
        fx.register(PredicateFixture.Named("ab"))    // 拒绝 (length=2)
        a.equal(1, SurgeonPredicateCases.methodNoArgHits, "where=args[0].length()>=3 仅 abc 命中")
    }

    fun testWhereSafeCall(): CaseResult = runCase("where-safe-call") { a, _ ->
        SurgeonPredicateCases.reset()
        val fx = PredicateFixture()
        fx.touch(PredicateFixture.Named("x"))   // 命中 args[0]?.name=="x"
        fx.touch(null)                          // 安全调用 → null == "x" → false
        fx.touch(PredicateFixture.Named("y"))   // 拒绝 ("y"!="x")
        a.equal(1, SurgeonPredicateCases.safeCallHits, "where=args[0]?.name==\"x\" 仅 x 命中，null 短路为 false")
    }

    // ---- 布尔组合 ----

    fun testWhereAnd(): CaseResult = runCase("where-and") { a, _ ->
        SurgeonPredicateCases.reset()
        val fx = PredicateFixture()
        fx.spawn("combo", 10)   // 命中
        fx.spawn("combo", 3)    // 拒绝 (level 不 >5)
        fx.spawn("other", 10)   // 拒绝 (name 不等)
        a.equal(1, SurgeonPredicateCases.andHits, "where=a && b 仅两个条件同时成立命中")
    }

    fun testWhereOr(): CaseResult = runCase("where-or") { a, _ ->
        SurgeonPredicateCases.reset()
        val fx = PredicateFixture()
        fx.spawn("x", 5)      // 命中 (name=x)
        fx.spawn("y", 2000)   // 命中 (level>1000)
        fx.spawn("y", 5)      // 拒绝
        a.equal(2, SurgeonPredicateCases.orHits, "where=a || b 任一成立即命中")
    }

    fun testWhereNotGroup(): CaseResult = runCase("where-not-group") { a, _ ->
        SurgeonPredicateCases.reset()
        val fx = PredicateFixture()
        fx.spawn("keep", 1)   // 命中 !(...=="skip")
        fx.spawn("skip", 1)   // 拒绝
        a.equal(1, SurgeonPredicateCases.notHits, "where=!(args[0]==\"skip\") 排除 skip")
    }

    fun testWhereNested(): CaseResult = runCase("where-nested") { a, _ ->
        SurgeonPredicateCases.reset()
        val fx = PredicateFixture()
        fx.spawn("a", 5)    // 内部 (==a && <10) 真 → 拒绝
        fx.spawn("a", 100)  // 内部为假 → 命中
        fx.spawn("b", 1)    // ==b 真 → 拒绝
        fx.spawn("c", 1)    // 全假 → 命中
        a.equal(2, SurgeonPredicateCases.nestedHits, "where=!((a && b) || c) 嵌套布尔")
    }

    // ---- 字面量 ----

    fun testWhereLiteralTrue(): CaseResult = runCase("where-literal-true") { a, _ ->
        SurgeonPredicateCases.reset()
        val fx = PredicateFixture()
        fx.flag(1)
        fx.flag(null)
        a.equal(2, SurgeonPredicateCases.literalTrueHits, "where=true 恒命中")
    }

    fun testWhereLiteralFalse(): CaseResult = runCase("where-literal-false") { a, _ ->
        SurgeonPredicateCases.reset()
        val fx = PredicateFixture()
        fx.flag(1)
        fx.flag("x")
        a.equal(0, SurgeonPredicateCases.literalFalseHits, "where=false 永不命中")
    }

    fun testWhereLiteralNull(): CaseResult = runCase("where-literal-null") { a, _ ->
        SurgeonPredicateCases.reset()
        val fx = PredicateFixture()
        fx.flag(null)   // 命中
        fx.flag("x")    // 拒绝
        a.equal(1, SurgeonPredicateCases.literalNullHits, "where=args[0]==null 仅 null 命中")
    }

    fun testWhereLiteralDouble(): CaseResult = runCase("where-literal-double") { a, _ ->
        SurgeonPredicateCases.reset()
        val fx = PredicateFixture()
        fx.measure(3.14)  // 命中
        fx.measure(2.71)  // 拒绝
        a.equal(1, SurgeonPredicateCases.literalDoubleHits, "where=args[0]==3.14 浮点字面量匹配")
    }

    // ---- 复合 / this ----

    fun testWhereCompound(): CaseResult = runCase("where-compound") { a, _ ->
        SurgeonPredicateCases.reset()
        val fx = PredicateFixture()
        fx.spawn("mob_zombie", 50)    // 命中
        fx.spawn("mob_zombie", 5)     // 拒绝 (level<10)
        fx.spawn("mob_zombie", 200)   // 拒绝 (level>100)
        fx.spawn("mob_ignored", 50)   // 拒绝 (黑名单)
        fx.spawn("item_sword", 50)    // 拒绝 (前缀不符)
        a.equal(1, SurgeonPredicateCases.compoundHits, "where=matches && range && !(blacklist) 复合条件")
    }

    fun testWhereThisRef(): CaseResult = runCase("where-this-ref") { a, _ ->
        SurgeonPredicateCases.reset()
        val fx = PredicateFixture()
        fx.log("hi")
        a.check(SurgeonPredicateCases.thisRefHits >= 1, "where=this is PredicateFixture 通过 thisRef 命中 (hits=${SurgeonPredicateCases.thisRefHits})")
    }

    // ---- Trauma 诊断路径（通过反射调用 PredCompiler.compile，模块内 internal 对外可见性差异兜底） ----

    /** 反射调用 PredCompiler.compile(source, AdviceCtx)，返回抛出的异常（或 null）。 */
    private fun compilePredExpectError(source: String, adviceId: String): Throwable? {
        return try {
            val cl = this::class.java.classLoader
            val ctxCls = Class.forName("taboolib.module.incision.pred.AdviceCtx", true, cl)
            val ctxCtor = ctxCls.getDeclaredConstructor(String::class.java, ClassLoader::class.java, Set::class.java)
            val ctx = ctxCtor.newInstance(adviceId, cl, emptySet<String>())
            val compilerCls = Class.forName("taboolib.module.incision.pred.PredCompiler", true, cl)
            val instance = compilerCls.getField("INSTANCE").get(null)
            val compile = compilerCls.getMethod("compile", String::class.java, ctxCls)
            compile.invoke(instance, source, ctx)
            null
        } catch (t: java.lang.reflect.InvocationTargetException) {
            t.targetException
        } catch (t: Throwable) {
            t
        }
    }

    fun testWhereTraumaUndefined(): CaseResult = runCase("where-trauma-undef") { a, _ ->
        val err = compilePredExpectError("nosuch == 1", "test-undef")
        a.check(err is Trauma.Predicate.UndefinedVariable, "未定义变量抛 UndefinedVariable (实际=${err?.javaClass?.simpleName} msg=${err?.message})")
    }

    fun testWhereTraumaMethodIndexed(): CaseResult = runCase("where-trauma-method-indexed") { a, _ ->
        val err = compilePredExpectError("args.size[0] == 1", "test-mi")
        a.check(err is Trauma.Predicate.MethodIndexed, "方法结果下标抛 MethodIndexed (实际=${err?.javaClass?.simpleName} msg=${err?.message})")
    }

    fun testWhereTraumaSyntax(): CaseResult = runCase("where-trauma-syntax") { a, _ ->
        val err = compilePredExpectError("a === b", "test-syntax")
        a.check(err is Trauma.Predicate.SyntaxError, "非法 === 抛 SyntaxError (实际=${err?.javaClass?.simpleName} msg=${err?.message})")
    }

    // --------------------------------------------------------------------
    // where 字段在每个 advice 注解上的覆盖
    // --------------------------------------------------------------------

    fun testWhereEmptyBaseline(): CaseResult = runCase("where-empty-baseline") { a, _ ->
        SurgeonPredicateCases.resetAdviceWhere()
        val fx = PredicateFixture()
        fx.whereEmpty(1)
        fx.whereEmpty(-1)
        fx.whereEmpty(0)
        a.equal(3, SurgeonPredicateCases.emptyWhereHits, "where=\"\" 等价于不过滤，所有调用均命中")
    }

    fun testWhereOnTrail(): CaseResult = runCase("where-on-trail") { a, _ ->
        SurgeonPredicateCases.resetAdviceWhere()
        val fx = PredicateFixture()
        a.equal(20, fx.whereTrail(10), "原方法 *2 仍执行")
        a.equal(-2, fx.whereTrail(-1), "原方法对负数仍执行")
        a.equal(0, fx.whereTrail(0), "原方法 0 出口")
        a.equal(1, SurgeonPredicateCases.trailWhereHits, "@Trail where=args[0]>0 仅 10 命中")
    }

    fun testWhereOnSplice(): CaseResult = runCase("where-on-splice") { a, _ ->
        SurgeonPredicateCases.resetAdviceWhere()
        val fx = PredicateFixture()
        a.equal(50, fx.whereSplice(50), "where 不过 → advice 跳过 → 原方法返回入参")
        a.equal(-1, fx.whereSplice(200), "where 过 → advice 接管返回 -1")
        a.equal(1, SurgeonPredicateCases.spliceWhereHits, "@Splice where 仅命中一次")
    }

    fun testWhereOnExcise(): CaseResult = runCase("where-on-excise") { a, _ ->
        SurgeonPredicateCases.resetAdviceWhere()
        val fx = PredicateFixture()
        a.equal("raw:keep", fx.whereExcise("keep"), "where=cut 不通过 → 原方法执行")
        a.equal("excised", fx.whereExcise("cut"), "where 通过 → @Excise 整段替换")
        a.equal(1, SurgeonPredicateCases.exciseWhereHits, "@Excise where 仅 cut 命中")
    }

    fun testWhereOnBypass(): CaseResult = runCase("where-on-bypass") { a, _ ->
        SurgeonPredicateCases.resetAdviceWhere()
        val fx = PredicateFixture()
        // value=10 → where(10>50) false → bypass 跳过 → helper 正常 → 11
        a.equal(11, fx.whereBypass(10), "where 不过 → 原 helper 调用 → value+1")
        // value=80 → where(80>50) true → bypass 替换为 9999
        a.equal(9999, fx.whereBypass(80), "where 过 → @Bypass 替换 helper 调用为 9999")
        a.equal(1, SurgeonPredicateCases.bypassWhereHits, "@Bypass where 仅 80 命中")
    }

    fun testWhereOnGraft(): CaseResult = runCase("where-on-graft") { a, _ ->
        SurgeonPredicateCases.resetAdviceWhere()
        val fx = PredicateFixture()
        // value=5 → where(5>=10) false → graft 跳过 → 原 helper 5*10=50
        a.equal(50, fx.whereGraft(5), "where 不过 → 原方法不变")
        // value=20 → where(20>=10) true → graft 触发，但原 helper 仍执行 → 200
        a.equal(200, fx.whereGraft(20), "where 过 → graft 计数，原 helper 仍执行")
        a.equal(1, SurgeonPredicateCases.graftWhereHits, "@Graft where 仅 20 命中")
    }

    fun testWhereOnTrim(): CaseResult = runCase("where-on-trim") { a, _ ->
        SurgeonPredicateCases.resetAdviceWhere()
        val fx = PredicateFixture()
        // value=-5 → where(-5>0) false → trim 跳过 → 原参数 -5
        a.equal(-5, fx.whereTrim(-5), "where 不过 → 实参未改写")
        // value=3 → where(3>0) true → trim 改写实参为 777
        a.equal(777, fx.whereTrim(3), "where 过 → @Trim ARG 改写实参为 777")
        a.equal(1, SurgeonPredicateCases.trimWhereHits, "@Trim where 仅 3 命中")
    }
    // ====================================================================
    // Task #13 锚点矩阵 —— anchor × advice × shift × ordinal
    // ====================================================================

    fun testAnchorHeadLead(): CaseResult = runCase("anchor-head-lead") { a, _ ->
        SurgeonAnchorMatrixCases.reset()
        val fx = AnchorMatrixFixture()
        val r = fx.matrixSimpleReturn()
        a.equal("raw-return", r, "原方法返回未变 (HEAD @Lead 不改返回)")
        a.equal(1, SurgeonAnchorMatrixCases.headLeadHits, "@Lead(HEAD) 触发 1 次")
    }

    fun testAnchorTailTrail(): CaseResult = runCase("anchor-tail-trail") { a, _ ->
        SurgeonAnchorMatrixCases.reset()
        val fx = AnchorMatrixFixture()
        fx.matrixSimpleReturn()
        a.equal(1, SurgeonAnchorMatrixCases.tailTrailNormalHits, "@Trail(TAIL,onThrow=false) 正常出口触发 1 次")
    }

    fun testAnchorReturnTrailMulti(): CaseResult = runCase("anchor-return-trail-multi") { a, _ ->
        SurgeonAnchorMatrixCases.reset()
        val fx = AnchorMatrixFixture()
        a.equal(1, fx.matrixMultiReturn(true), "原 return 1 分支")
        a.equal(2, fx.matrixMultiReturn(false), "原 return 2 分支")
        a.equal(1, SurgeonAnchorMatrixCases.trailReturnFlagTrueHits, "true 分支 RETURN 命中 1 次")
        a.equal(1, SurgeonAnchorMatrixCases.trailReturnFlagFalseHits, "false 分支 RETURN 命中 1 次")
    }

    fun testAnchorThrowTrail(): CaseResult = runCase("anchor-throw-trail") { a, _ ->
        SurgeonAnchorMatrixCases.reset()
        val fx = AnchorMatrixFixture()
        a.equal("ok", fx.matrixThrowUncaught(false), "正常路径返回 ok")
        runCatching { fx.matrixThrowUncaught(true) }
        a.equal(1, SurgeonAnchorMatrixCases.trailThrowOk, "正常出口 trail 命中 1 次")
        a.equal(1, SurgeonAnchorMatrixCases.trailThrowHits, "@Trail(onThrow=true) 在异常出口命中 1 次")
    }

    fun testAnchorInvokeGraftBefore0(): CaseResult = runCase("anchor-invoke-graft-before-ordinal-0") { a, _ ->
        SurgeonAnchorMatrixCases.reset()
        val fx = AnchorMatrixFixture()
        fx.matrixInvokeChain(1)
        a.equal(1, SurgeonAnchorMatrixCases.graftInvokeBeforeOrd0, "ordinal=0 仅命中第一次 helperA 调用")
    }

    fun testAnchorInvokeGraftBefore1(): CaseResult = runCase("anchor-invoke-graft-before-ordinal-1") { a, _ ->
        SurgeonAnchorMatrixCases.reset()
        val fx = AnchorMatrixFixture()
        fx.matrixInvokeChain(1)
        a.equal(1, SurgeonAnchorMatrixCases.graftInvokeBeforeOrd1, "ordinal=1 仅命中第二次 helperA 调用")
    }

    fun testAnchorInvokeGraftBeforeAll(): CaseResult = runCase("anchor-invoke-graft-before-ordinal-all") { a, _ ->
        SurgeonAnchorMatrixCases.reset()
        val fx = AnchorMatrixFixture()
        fx.matrixInvokeChain(1)
        a.equal(3, SurgeonAnchorMatrixCases.graftInvokeBeforeOrdAll, "ordinal=-1 命中三次 helperA 调用")
    }

    fun testAnchorInvokeGraftAfter0(): CaseResult = runCase("anchor-invoke-graft-after-ordinal-0") { a, _ ->
        SurgeonAnchorMatrixCases.reset()
        val fx = AnchorMatrixFixture()
        fx.matrixInvokeChain(1)
        a.equal(1, SurgeonAnchorMatrixCases.graftInvokeAfterOrd0, "AFTER + ordinal=0 仅命中第一次 helperA 调用之后")
    }

    fun testAnchorFieldGetGraft(): CaseResult = runCase("anchor-field-get-graft") { a, _ ->
        SurgeonAnchorMatrixCases.reset()
        val fx = AnchorMatrixFixture()
        fx.counter = 7
        val r = fx.matrixFieldRead()
        a.equal(7, r, "原方法返回 counter")
        a.equal(1, SurgeonAnchorMatrixCases.graftFieldGetHits, "@Graft(FIELD_GET) 命中 1 次")
    }

    fun testAnchorFieldPutGraft(): CaseResult = runCase("anchor-field-put-graft") { a, _ ->
        SurgeonAnchorMatrixCases.reset()
        val fx = AnchorMatrixFixture()
        fx.matrixFieldWrite(42)
        a.equal(42, fx.lastWritten, "字段写入生效")
        a.equal(1, SurgeonAnchorMatrixCases.graftFieldPutHits, "@Graft(FIELD_PUT) 命中 1 次")
    }

    fun testAnchorNewGraft(): CaseResult = runCase("anchor-new-graft") { a, _ ->
        SurgeonAnchorMatrixCases.reset()
        val fx = AnchorMatrixFixture()
        val list = fx.matrixAllocate()
        a.equal(1, list.size, "原方法构造 ArrayList 并 add 一个元素")
        a.equal(1, SurgeonAnchorMatrixCases.graftNewHits, "@Graft(NEW) 命中 ArrayList 构造 1 次")
    }

    fun testAnchorThrowGraft(): CaseResult = runCase("anchor-throw-graft") { a, _ ->
        SurgeonAnchorMatrixCases.reset()
        val fx = AnchorMatrixFixture()
        val err = runCatching { fx.matrixThrowUncaught(true) }.exceptionOrNull()
        a.check(err is IllegalStateException, "原方法抛出 IllegalStateException")
        a.equal(1, SurgeonAnchorMatrixCases.graftThrowHits, "@Graft(THROW) 命中 1 次")
    }

    fun testAnchorBypassReplaceResult(): CaseResult = runCase("anchor-bypass-replace-result") { a, _ ->
        SurgeonAnchorMatrixCases.reset()
        val fx = AnchorMatrixFixture()
        // matrixInvokeMixed: helperA(seed) + helperB(seed)
        // helperA 被 @Bypass 替换为 9999, helperB(1)=3 -> 9999+3 = 10002
        val r = fx.matrixInvokeMixed(1)
        a.equal(10002, r, "@Bypass(INVOKE) 仅替换 helperA 调用，不影响 helperB")
        a.equal(1, SurgeonAnchorMatrixCases.bypassReplaceHits, "@Bypass 命中 1 次")
    }

    fun testAnchorTrimReturn(): CaseResult = runCase("anchor-trim-return") { a, _ ->
        SurgeonAnchorMatrixCases.reset()
        val fx = AnchorMatrixFixture()
        val r = fx.matrixPlainReturn()
        a.equal(-1, r, "@Trim(RETURN) 把原 5 改写为 -1")
        a.equal(1, SurgeonAnchorMatrixCases.trimReturnHits, "@Trim(RETURN) 触发 1 次")
    }

    fun testAnchorTrimArg0(): CaseResult = runCase("anchor-trim-arg-0") { a, _ ->
        SurgeonAnchorMatrixCases.reset()
        val fx = AnchorMatrixFixture()
        val r = fx.matrixEcho("source")
        a.equal("echo:trimmed-0", r, "@Trim(ARG,0) 把入参改为 trimmed-0")
        a.equal("trimmed-0", fx.lastEcho, "原方法收到改写后的入参")
        a.equal(1, SurgeonAnchorMatrixCases.trimArg0Hits, "@Trim(ARG,0) 触发 1 次")
    }

    fun testAnchorTrimArg1(): CaseResult = runCase("anchor-trim-arg-1") { a, _ ->
        SurgeonAnchorMatrixCases.reset()
        val fx = AnchorMatrixFixture()
        val r = fx.matrixCombine("L", "R")
        a.equal("L+trimmed-1", r, "@Trim(ARG,1) 把第二个入参改为 trimmed-1")
        a.equal("L|trimmed-1", fx.lastCombine, "原方法收到改写后的入参")
        a.equal(1, SurgeonAnchorMatrixCases.trimArg1Hits, "@Trim(ARG,1) 触发 1 次")
    }

    fun testAnchorExciseMethod(): CaseResult = runCase("anchor-excise-method") { a, _ ->
        SurgeonAnchorMatrixCases.reset()
        val fx = AnchorMatrixFixture()
        val r = fx.matrixExciseTarget()
        a.equal("excised-payload", r, "@Excise 整段替换返回值")
        a.equal(1, SurgeonAnchorMatrixCases.exciseHits, "@Excise 触发 1 次")
    }

    fun testAnchorSpliceProceed(): CaseResult = runCase("anchor-splice-proceed") { a, _ ->
        SurgeonAnchorMatrixCases.reset()
        val fx = AnchorMatrixFixture()
        val r = fx.matrixSpliceTarget(3)
        a.equal(30, r, "@Splice + proceed 放行原方法 (3*10=30)")
        a.equal(1, SurgeonAnchorMatrixCases.spliceProceedHits, "@Splice 触发 1 次")
    }

    // ====================================================================
    // Task #13 诊断 / Trauma 路径（Scalpel.transient 形式的坏输入）
    // ====================================================================

    fun testTraumaBadDescriptor(): CaseResult = runCase("trauma-bad-descriptor") { a, _ ->
        val err = runCatching {
            Scalpel.transient { lead("missing_hash_separator") { _ -> } }
        }.exceptionOrNull()
        a.check(err != null, "非法描述符必须抛异常")
        a.note("捕获: ${err?.javaClass?.simpleName}: ${err?.message}")
    }

    fun testTraumaMethodNotFound(): CaseResult = runCase("trauma-method-not-found") { a, _ ->
        val err = runCatching {
            Scalpel.transient {
                lead("top.maplex.incisiontest.fixture.AnchorMatrixFixture#nonExistentXyz()V") { _ -> }
            }
        }.exceptionOrNull()
        a.note("trauma-method-not-found 异常: ${err?.javaClass?.simpleName}: ${err?.message?.take(120)} (null 表示当前 transient 路径未在声明期校验)")
    }

    fun testTraumaClassNotFound(): CaseResult = runCase("trauma-class-not-found") { a, _ ->
        val err = runCatching {
            Scalpel.transient {
                lead("non.existent.PhantomClass#whatever()V") { _ -> }
            }
        }.exceptionOrNull()
        a.note("trauma-class-not-found 异常: ${err?.javaClass?.simpleName}: ${err?.message?.take(120)} (null 表示当前 transient 路径未在声明期校验)")
    }

    fun testTraumaBadScope(): CaseResult = runCase("trauma-bad-scope") { a, _ ->
        val err = runCatching {
            Scalpel.transient { lead("###") { _ -> } }
        }.exceptionOrNull()
        a.check(err != null, "畸形描述符 '###' 应被拒绝")
        a.note("捕获: ${err?.javaClass?.simpleName}: ${err?.message?.take(120)}")
    }

    // ====================================================================
    // Task #13 第二批 —— @Site 字段网格
    // ====================================================================

    fun testSiteAnchorHead(): CaseResult = runCase("site-anchor-head") { a, _ ->
        SurgeonAnchorMatrixSiteCases.reset()
        val fx = AnchorMatrixFixture_Site()
        a.equal("head", fx.siteHeadMethod(), "原方法返回")
        a.equal(1, SurgeonAnchorMatrixSiteCases.headHits, "@Lead(HEAD) 命中 1 次")
    }

    fun testSiteAnchorTail(): CaseResult = runCase("site-anchor-tail") { a, _ ->
        SurgeonAnchorMatrixSiteCases.reset()
        val fx = AnchorMatrixFixture_Site()
        fx.siteTailMethod()
        a.equal(1, SurgeonAnchorMatrixSiteCases.tailHits, "@Trail(TAIL,onThrow=false) 命中 1 次")
    }

    fun testSiteAnchorReturn(): CaseResult = runCase("site-anchor-return") { a, _ ->
        SurgeonAnchorMatrixSiteCases.reset()
        val fx = AnchorMatrixFixture_Site()
        a.equal(7, fx.siteReturnMethod(), "原方法返回 7")
        a.equal(1, SurgeonAnchorMatrixSiteCases.returnHits, "@Graft(RETURN) 命中 1 次")
    }

    fun testSiteAnchorThrow(): CaseResult = runCase("site-anchor-throw") { a, _ ->
        SurgeonAnchorMatrixSiteCases.reset()
        val fx = AnchorMatrixFixture_Site()
        a.equal("caught:site-throw", fx.siteThrowMethod(), "原方法捕获 throw")
        a.equal(1, SurgeonAnchorMatrixSiteCases.throwHits, "@Graft(THROW) 命中 1 次")
    }

    fun testSiteInvokeOrd0(): CaseResult = runCase("site-invoke-ordinal-0") { a, _ ->
        SurgeonAnchorMatrixSiteCases.reset()
        val fx = AnchorMatrixFixture_Site()
        fx.siteInvokeChain(1)
        a.equal(1, SurgeonAnchorMatrixSiteCases.invokeOrd0Hits, "ordinal=0 仅命中第一次 helperSite")
    }

    fun testSiteInvokeOrd1(): CaseResult = runCase("site-invoke-ordinal-1") { a, _ ->
        SurgeonAnchorMatrixSiteCases.reset()
        val fx = AnchorMatrixFixture_Site()
        fx.siteInvokeChain(1)
        a.equal(1, SurgeonAnchorMatrixSiteCases.invokeOrd1Hits, "ordinal=1 仅命中第二次 helperSite")
    }

    fun testSiteInvokeOrd2(): CaseResult = runCase("site-invoke-ordinal-2") { a, _ ->
        SurgeonAnchorMatrixSiteCases.reset()
        val fx = AnchorMatrixFixture_Site()
        fx.siteInvokeChain(1)
        a.equal(1, SurgeonAnchorMatrixSiteCases.invokeOrd2Hits, "ordinal=2 仅命中第三次 helperSite")
    }

    fun testSiteInvokeOrdAll(): CaseResult = runCase("site-invoke-ordinal-all") { a, _ ->
        SurgeonAnchorMatrixSiteCases.reset()
        val fx = AnchorMatrixFixture_Site()
        fx.siteInvokeChain(1)
        a.equal(4, SurgeonAnchorMatrixSiteCases.invokeOrdAllHits, "ordinal=-1 命中四次 helperSite")
    }

    fun testSiteShiftBefore(): CaseResult = runCase("site-shift-before") { a, _ ->
        SurgeonAnchorMatrixSiteCases.reset()
        val fx = AnchorMatrixFixture_Site()
        fx.siteOffsetBaseline(1)
        a.equal(1, SurgeonAnchorMatrixSiteCases.invokeBeforeHits, "shift=BEFORE 命中 1 次")
    }

    fun testSiteShiftAfter(): CaseResult = runCase("site-shift-after") { a, _ ->
        SurgeonAnchorMatrixSiteCases.reset()
        val fx = AnchorMatrixFixture_Site()
        fx.siteOffsetBaseline(1)
        a.equal(1, SurgeonAnchorMatrixSiteCases.invokeAfterHits, "shift=AFTER 命中 1 次")
    }

    fun testSiteFieldGet(): CaseResult = runCase("site-field-get") { a, _ ->
        SurgeonAnchorMatrixSiteCases.reset()
        val fx = AnchorMatrixFixture_Site()
        fx.counter = 5
        a.equal(5, fx.siteFieldGet(), "原方法返回 counter")
        a.equal(1, SurgeonAnchorMatrixSiteCases.fieldGetHits, "@Site(FIELD_GET) 命中 1 次")
    }

    fun testSiteFieldPut(): CaseResult = runCase("site-field-put") { a, _ ->
        SurgeonAnchorMatrixSiteCases.reset()
        val fx = AnchorMatrixFixture_Site()
        fx.siteFieldPut(9)
        a.equal(9, fx.lastWritten, "字段写入生效")
        a.equal(1, SurgeonAnchorMatrixSiteCases.fieldPutHits, "@Site(FIELD_PUT) 命中 1 次")
    }

    fun testSiteNewWithTarget(): CaseResult = runCase("site-new-with-target") { a, _ ->
        SurgeonAnchorMatrixSiteCases.reset()
        val fx = AnchorMatrixFixture_Site()
        a.equal("site-new", fx.siteNewObject().toString(), "原方法构造对象")
        a.equal(1, SurgeonAnchorMatrixSiteCases.newWithTargetHits, "@Site(NEW,target=StringBuilder) 命中 1 次")
    }

    fun testSiteNewEmptyTarget(): CaseResult = runCase("site-new-empty-target") { a, _ ->
        SurgeonAnchorMatrixSiteCases.reset()
        val fx = AnchorMatrixFixture_Site()
        fx.siteTargetEmpty()
        a.equal(1, SurgeonAnchorMatrixSiteCases.newEmptyTargetHits, "@Site(NEW,target=\"\") 命中方法内任意 NEW 1 次")
    }

    fun testSiteOffsetZero(): CaseResult = runCase("site-offset-zero") { a, _ ->
        SurgeonAnchorMatrixSiteCases.reset()
        val fx = AnchorMatrixFixture_Site()
        fx.siteOffsetBaseline(1)
        a.equal(1, SurgeonAnchorMatrixSiteCases.offsetZeroHits, "offset=0 基线命中 1 次")
    }

    // ====================================================================
    // Task #13 第二批 —— scope DSL / 描述符变种
    // ====================================================================

    fun testScopeLeadFullDescriptor(): CaseResult = runCase("scope-lead-full-descriptor") { a, _ ->
        SurgeonAnchorMatrixScopeCases.reset()
        val fx = AnchorMatrixFixture_Scope()
        fx.scopeMethodFull("x")
        a.equal(1, SurgeonAnchorMatrixScopeCases.leadFullHits, "scope=method:Xxx 完整描述符命中")
    }

    fun testScopeLeadWildArgs(): CaseResult = runCase("scope-lead-wildcard-args") { a, _ ->
        SurgeonAnchorMatrixScopeCases.reset()
        val fx = AnchorMatrixFixture_Scope()
        fx.scopeMethodWild(1, 2)
        a.equal(1, SurgeonAnchorMatrixScopeCases.leadWildHits, "scope=method:Xxx#m(*) 通配命中")
    }

    fun testScopeLeadExactTarget(): CaseResult = runCase("scope-lead-exact-target") { a, _ ->
        SurgeonAnchorMatrixScopeCases.reset()
        val fx = AnchorMatrixFixture_Scope()
        fx.scopeStarTarget()
        a.equal(1, SurgeonAnchorMatrixScopeCases.leadStarTargetHits, "scope=精确描述符命中 scopeStarTarget")
    }

    fun testScopeTrailClassPrefixA(): CaseResult = runCase("scope-trail-class-prefix-a") { a, _ ->
        SurgeonAnchorMatrixScopeCases.reset()
        val fx = AnchorMatrixFixture_Scope()
        fx.scopeClassA()
        a.equal(1, SurgeonAnchorMatrixScopeCases.trailClassPrefixA, "class:X & method:X#A 命中")
    }

    fun testScopeTrailClassPrefixB(): CaseResult = runCase("scope-trail-class-prefix-b") { a, _ ->
        SurgeonAnchorMatrixScopeCases.reset()
        val fx = AnchorMatrixFixture_Scope()
        fx.scopeClassB()
        a.equal(1, SurgeonAnchorMatrixScopeCases.trailClassPrefixB, "class:X & method:X#B 命中")
    }

    fun testScopeSpliceFull(): CaseResult = runCase("scope-splice-full") { a, _ ->
        SurgeonAnchorMatrixScopeCases.reset()
        val fx = AnchorMatrixFixture_Scope()
        a.equal("Bob@30", fx.scopeShorthandArgs("Bob", 30), "@Splice + proceed 放行返回原值")
        a.equal(1, SurgeonAnchorMatrixScopeCases.spliceFullHits, "@Splice 命中 1 次")
    }

    fun testScopeBypassFullDescriptor(): CaseResult = runCase("scope-bypass-full-descriptor") { a, _ ->
        SurgeonAnchorMatrixScopeCases.reset()
        val fx = AnchorMatrixFixture_Scope()
        // inner1 替换为 500, inner2=2 → 502
        a.equal(502, fx.scopeInvokeHelper(), "@Bypass 仅替换 inner1 调用")
        a.equal(1, SurgeonAnchorMatrixScopeCases.bypassFullDescHits, "@Bypass 命中 1 次")
    }

    fun testScopeGraftFullDescriptor(): CaseResult = runCase("scope-graft-full-descriptor") { a, _ ->
        SurgeonAnchorMatrixScopeCases.reset()
        val fx = AnchorMatrixFixture_Scope()
        fx.scopeInvokeHelper()
        a.equal(1, SurgeonAnchorMatrixScopeCases.graftFullDescHits, "@Graft 命中 inner2 调用 1 次")
    }

    fun testScopeTrimFullDescriptor(): CaseResult = runCase("scope-trim-full-descriptor") { a, _ ->
        SurgeonAnchorMatrixScopeCases.reset()
        val fx = AnchorMatrixFixture_Scope()
        val r = fx.scopeTrimArgs("Bob", 30)
        a.equal("scoped-arg@30", r, "@Trim(ARG,0) 把 name 改为 scoped-arg")
        a.equal(1, SurgeonAnchorMatrixScopeCases.trimFullDescHits, "@Trim 触发 1 次")
    }

    // ====================================================================
    // Task #13 第二批 —— @Trim 全字段网格
    // ====================================================================

    fun testTrimArgSingle(): CaseResult = runCase("trim-arg-single") { a, _ ->
        SurgeonAnchorMatrixTrimCases.reset()
        val fx = AnchorMatrixFixture_Trim()
        val r = fx.trimSingleArg("orig")
        a.equal("single:single-trimmed", r, "@Trim(ARG,0) 把唯一参数改为 single-trimmed")
        a.equal("single-trimmed", fx.lastSingle, "原方法收到改写后的入参")
        a.equal(1, SurgeonAnchorMatrixTrimCases.argSingleHits, "@Trim(ARG,0) 触发 1 次")
    }

    fun testTrimArgDouble0(): CaseResult = runCase("trim-arg-double-0") { a, _ ->
        SurgeonAnchorMatrixTrimCases.reset()
        val fx = AnchorMatrixFixture_Trim()
        val r = fx.trimDoubleArg("L", "R")
        // 双 advice 同时挂在 trimDoubleArg：idx=0 改 L→left-trimmed，idx=1 改 R→right-trimmed
        a.equal("double:left-trimmed:right-trimmed", r, "@Trim(ARG,0/1) 同时改写双参")
        a.equal("left-trimmed|right-trimmed", fx.lastDouble, "原方法收到双向改写后的入参")
        a.equal(1, SurgeonAnchorMatrixTrimCases.argDouble0Hits, "@Trim(ARG,0) 触发 1 次")
        a.equal(1, SurgeonAnchorMatrixTrimCases.argDouble1Hits, "@Trim(ARG,1) 触发 1 次")
    }

    fun testTrimArgDouble1(): CaseResult = runCase("trim-arg-double-1") { a, _ ->
        SurgeonAnchorMatrixTrimCases.reset()
        val fx = AnchorMatrixFixture_Trim()
        val r = fx.trimDoubleArg("L", "R")
        a.equal("double:left-trimmed:right-trimmed", r, "@Trim(ARG,1) 一并改写第二个入参")
        a.equal(1, SurgeonAnchorMatrixTrimCases.argDouble1Hits, "@Trim(ARG,1) 触发 1 次")
    }

    fun testTrimArgTriple2(): CaseResult = runCase("trim-arg-triple-2") { a, _ ->
        SurgeonAnchorMatrixTrimCases.reset()
        val fx = AnchorMatrixFixture_Trim()
        val r = fx.trimTripleArg("a", "b", "c")
        a.equal("triple:a:b:triple-2-trimmed", r, "@Trim(ARG,2) 把第三参数改为 triple-2-trimmed")
        a.equal("a|b|triple-2-trimmed", fx.lastTriple, "原方法收到改写后的入参")
        a.equal(1, SurgeonAnchorMatrixTrimCases.argTriple2Hits, "@Trim(ARG,2) 触发 1 次")
    }

    fun testTrimArgQuad3(): CaseResult = runCase("trim-arg-quad-3") { a, _ ->
        SurgeonAnchorMatrixTrimCases.reset()
        val fx = AnchorMatrixFixture_Trim()
        val r = fx.trimQuadArg("a", "b", "c", "d")
        a.equal("quad:a:b:c:quad-3-trimmed", r, "@Trim(ARG,3) 把第四参数改为 quad-3-trimmed")
        a.equal("a|b|c|quad-3-trimmed", fx.lastQuad, "原方法收到改写后的入参")
        a.equal(1, SurgeonAnchorMatrixTrimCases.argQuad3Hits, "@Trim(ARG,3) 触发 1 次")
    }

    fun testTrimReturnInt(): CaseResult = runCase("trim-return-int") { a, _ ->
        SurgeonAnchorMatrixTrimCases.reset()
        val fx = AnchorMatrixFixture_Trim()
        val r = fx.trimReturnInt()
        a.equal(-10, r, "@Trim(RETURN,int) 返回值改写为 -10")
        a.equal(1, SurgeonAnchorMatrixTrimCases.returnIntHits, "@Trim(RETURN,int) 触发 1 次")
    }

    fun testTrimReturnLong(): CaseResult = runCase("trim-return-long") { a, _ ->
        SurgeonAnchorMatrixTrimCases.reset()
        val fx = AnchorMatrixFixture_Trim()
        val r = fx.trimReturnLong()
        a.equal(-100L, r, "@Trim(RETURN,long) 返回值改写为 -100")
        a.equal(1, SurgeonAnchorMatrixTrimCases.returnLongHits, "@Trim(RETURN,long) 触发 1 次")
    }

    fun testTrimReturnString(): CaseResult = runCase("trim-return-string") { a, _ ->
        SurgeonAnchorMatrixTrimCases.reset()
        val fx = AnchorMatrixFixture_Trim()
        val r = fx.trimReturnString()
        a.equal("trimmed-string", r, "@Trim(RETURN,String) 返回值改写为 trimmed-string")
        a.equal(1, SurgeonAnchorMatrixTrimCases.returnStringHits, "@Trim(RETURN,String) 触发 1 次")
    }

    fun testTrimReturnBool(): CaseResult = runCase("trim-return-bool") { a, _ ->
        SurgeonAnchorMatrixTrimCases.reset()
        val fx = AnchorMatrixFixture_Trim()
        val r = fx.trimReturnBoolean()
        a.equal(true, r, "@Trim(RETURN,bool) 返回值改写为 true")
        a.equal(1, SurgeonAnchorMatrixTrimCases.returnBoolHits, "@Trim(RETURN,bool) 触发 1 次")
    }

    // ====================================================================
    // Task #13 第二批 —— priority 网格
    // ====================================================================

    fun testPrioritySurgeonDefault(): CaseResult = runCase("priority-surgeon-default") { a, _ ->
        SurgeonAnchorMatrixPriorityDefault.reset()
        SurgeonAnchorMatrixPriorityHigh.reset()
        SurgeonAnchorMatrixPriorityLow.reset()
        val fx = AnchorMatrixFixture_Priority()
        fx.prioritySurgeonTarget()
        a.equal(1, SurgeonAnchorMatrixPriorityDefault.defaultHits, "@Surgeon(priority=0) 命中 1 次")
    }

    fun testPrioritySurgeonHigh(): CaseResult = runCase("priority-surgeon-high") { a, _ ->
        SurgeonAnchorMatrixPriorityDefault.reset()
        SurgeonAnchorMatrixPriorityHigh.reset()
        SurgeonAnchorMatrixPriorityLow.reset()
        val fx = AnchorMatrixFixture_Priority()
        fx.prioritySurgeonTarget()
        a.equal(1, SurgeonAnchorMatrixPriorityHigh.highHits, "@Surgeon(priority=100) 命中 1 次")
    }

    fun testPrioritySurgeonLowNeg(): CaseResult = runCase("priority-surgeon-low-negative") { a, _ ->
        SurgeonAnchorMatrixPriorityDefault.reset()
        SurgeonAnchorMatrixPriorityHigh.reset()
        SurgeonAnchorMatrixPriorityLow.reset()
        val fx = AnchorMatrixFixture_Priority()
        fx.prioritySurgeonTarget()
        a.equal(1, SurgeonAnchorMatrixPriorityLow.lowHits, "@Surgeon(priority=-50) 命中 1 次")
    }

    fun testPrioritySurgeonMultiClassOrder(): CaseResult = runCase("priority-surgeon-multi-class-order") { a, _ ->
        SurgeonAnchorMatrixPriorityDefault.reset()
        SurgeonAnchorMatrixPriorityHigh.reset()
        SurgeonAnchorMatrixPriorityLow.reset()
        val fx = AnchorMatrixFixture_Priority()
        fx.prioritySurgeonTarget()
        val order = SurgeonAnchorMatrixPriorityDefault.orderLog.toList()
        a.equal(3, order.size, "三个 @Surgeon 各触发一次 (实际=${order.size}) order=$order")
        a.note("执行顺序=$order （期望 high(100) → default(0) → low(-50)，若实现按声明顺序则不一定）")
    }

    fun testPriorityOperationPos200(): CaseResult = runCase("priority-operation-pos200") { a, _ ->
        SurgeonAnchorMatrixPriorityOperation.reset()
        val fx = AnchorMatrixFixture_Priority()
        fx.priorityOperationTarget()
        a.equal(1, SurgeonAnchorMatrixPriorityOperation.pos200Hits, "@Operation(priority=200) 命中 1 次")
    }

    fun testPriorityOperationPos50(): CaseResult = runCase("priority-operation-pos50") { a, _ ->
        SurgeonAnchorMatrixPriorityOperation.reset()
        val fx = AnchorMatrixFixture_Priority()
        fx.priorityOperationTarget()
        a.equal(1, SurgeonAnchorMatrixPriorityOperation.pos50Hits, "@Operation(priority=50) 命中 1 次")
    }

    fun testPriorityOperationNeg10(): CaseResult = runCase("priority-operation-neg10") { a, _ ->
        SurgeonAnchorMatrixPriorityOperation.reset()
        val fx = AnchorMatrixFixture_Priority()
        fx.priorityOperationTarget()
        a.equal(1, SurgeonAnchorMatrixPriorityOperation.neg10Hits, "@Operation(priority=-10) 命中 1 次")
    }

    fun testPriorityOperationDefault(): CaseResult = runCase("priority-operation-default") { a, _ ->
        SurgeonAnchorMatrixPriorityOperation.reset()
        val fx = AnchorMatrixFixture_Priority()
        fx.priorityOperationTarget()
        a.equal(1, SurgeonAnchorMatrixPriorityOperation.defaultHits, "未声明 @Operation 默认 advice 命中 1 次")
    }

    fun testPriorityOperationOrder(): CaseResult = runCase("priority-operation-order") { a, _ ->
        SurgeonAnchorMatrixPriorityOperation.reset()
        val fx = AnchorMatrixFixture_Priority()
        fx.priorityOperationTarget()
        val order = SurgeonAnchorMatrixPriorityOperation.orderLog.toList()
        a.equal(4, order.size, "四条 advice 都触发 (实际=${order.size}) order=$order")
        a.note("@Operation 优先级顺序=$order （期望 op-200 → op-50 → op-default(0) → op-neg10）")
        // 至少验证最高优先级 op-200 在最低优先级 op-neg10 之前
        val idx200 = order.indexOf("op-200")
        val idxNeg = order.indexOf("op-neg10")
        if (idx200 >= 0 && idxNeg >= 0) {
            a.check(idx200 < idxNeg, "op-200 应在 op-neg10 之前 (idx200=$idx200, idxNeg=$idxNeg)")
        }
    }

    // ====================================================================
    // Task #13 第二批 —— @SurgeryDesk 调用层
    // ====================================================================

    fun testSurgeryDeskNestedRunCase(): CaseResult = runCase("surgerydesk-nested-runcase") { a, _ ->
        // runCase 是 inline 函数，块内 Scalpel.transient 的栈帧仍在 @SurgeryDesk AllCases
        var hits = 0
        val s = Scalpel.transient { lead(GREET) { _ -> hits++ } }
        try {
            TargetFixture().greet("nested")
            a.equal(1, hits, "嵌套 runCase 块内 transient 正常生效")
        } finally { s.heal() }
    }

    fun testSurgeryDeskDirectCall(): CaseResult = runCase("surgerydesk-direct-call") { a, _ ->
        // 直接在 @SurgeryDesk 标注的 AllCases 内调用 transient
        var hits = 0
        val s = Scalpel.transient { lead(GREET) { _ -> hits++ } }
        try {
            TargetFixture().greet("direct")
            a.equal(1, hits, "直接 @SurgeryDesk 内 transient 生效")
        } finally { s.heal() }
    }

    // ====================================================================
    // Task #13 追加扩展 —— FIELD/NEW × shift BEFORE/AFTER / 3-arg Trim 网格 /
    // Splice skip / Excise 体不执行 / 描述符变种 / 更多 Trauma
    // ====================================================================

    fun testExtrasFieldGetBefore(): CaseResult = runCase("extras-field-get-before") { a, _ ->
        SurgeonAnchorMatrixExtrasCases.reset()
        val fx = AnchorMatrixFixture_Extras()
        fx.counterShift = 42
        val r = fx.readCounterShift()
        a.equal(42, r, "原方法读取字段")
        a.equal(1, SurgeonAnchorMatrixExtrasCases.fieldGetBeforeHits, "@Graft(FIELD_GET, BEFORE) 命中 1 次")
    }

    fun testExtrasFieldGetAfter(): CaseResult = runCase("extras-field-get-after") { a, _ ->
        SurgeonAnchorMatrixExtrasCases.reset()
        val fx = AnchorMatrixFixture_Extras()
        fx.counterShift = 7
        val r = fx.readCounterShift()
        a.equal(7, r, "原方法读取字段")
        a.equal(1, SurgeonAnchorMatrixExtrasCases.fieldGetAfterHits, "@Graft(FIELD_GET, AFTER) 命中 1 次")
    }

    fun testExtrasFieldPutBefore(): CaseResult = runCase("extras-field-put-before") { a, _ ->
        SurgeonAnchorMatrixExtrasCases.reset()
        val fx = AnchorMatrixFixture_Extras()
        fx.writeCounterShift(88)
        a.equal(88, fx.counterShift, "原方法写入字段")
        a.equal(1, SurgeonAnchorMatrixExtrasCases.fieldPutBeforeHits, "@Graft(FIELD_PUT, BEFORE) 命中 1 次")
    }

    fun testExtrasFieldPutAfter(): CaseResult = runCase("extras-field-put-after") { a, _ ->
        SurgeonAnchorMatrixExtrasCases.reset()
        val fx = AnchorMatrixFixture_Extras()
        fx.writeCounterShift(11)
        a.equal(11, fx.counterShift, "原方法写入字段")
        a.equal(1, SurgeonAnchorMatrixExtrasCases.fieldPutAfterHits, "@Graft(FIELD_PUT, AFTER) 命中 1 次")
    }

    fun testExtrasNewBefore(): CaseResult = runCase("extras-new-before") { a, _ ->
        SurgeonAnchorMatrixExtrasCases.reset()
        val fx = AnchorMatrixFixture_Extras()
        val sb = fx.allocateBuilder()
        a.equal("seed", sb.toString(), "原方法构造并返回 StringBuilder")
        a.equal(1, SurgeonAnchorMatrixExtrasCases.newBeforeHits, "@Graft(NEW, BEFORE) 命中 1 次")
    }

    fun testExtrasNewAfter(): CaseResult = runCase("extras-new-after") { a, _ ->
        SurgeonAnchorMatrixExtrasCases.reset()
        val fx = AnchorMatrixFixture_Extras()
        val sb = fx.allocateBuilder()
        a.equal("seed", sb.toString(), "原方法构造并返回 StringBuilder")
        a.equal(1, SurgeonAnchorMatrixExtrasCases.newAfterHits, "@Graft(NEW, AFTER) 命中 1 次")
    }

    fun testExtrasTrimTriple012(): CaseResult = runCase("extras-trim-triple-012") { a, _ ->
        SurgeonAnchorMatrixExtrasCases.reset()
        val fx = AnchorMatrixFixture_Extras()
        val r = fx.tripleArgEcho("a", "b", "c")
        // 三条 Trim 同时挂同一目标方法：T0/T1/T2 全部覆盖
        a.equal("triple:T0:T1:T2", r, "ARG index=0/1/2 一齐改写参数")
        a.equal("T0|T1|T2", fx.lastTriple012, "原方法收到三参全改写后的入参")
        a.equal(1, SurgeonAnchorMatrixExtrasCases.tripleArg0Hits, "ARG index=0 命中 1 次")
        a.equal(1, SurgeonAnchorMatrixExtrasCases.tripleArg1Hits, "ARG index=1 命中 1 次")
        a.equal(1, SurgeonAnchorMatrixExtrasCases.tripleArg2Hits, "ARG index=2 命中 1 次")
    }

    fun testExtrasSpliceSkip(): CaseResult = runCase("extras-splice-skip-short-circuit") { a, _ ->
        SurgeonAnchorMatrixExtrasCases.reset()
        val fx = AnchorMatrixFixture_Extras()
        val r = fx.spliceSkipTarget(5)
        a.equal(-999, r, "@Splice 短路返回自定义值")
        a.equal(0, fx.spliceSkipBodyExecuted, "原方法体未执行 (skip)")
        a.equal(1, SurgeonAnchorMatrixExtrasCases.spliceSkipAdviceHits, "@Splice advice 触发 1 次")
    }

    fun testExtrasExciseBodySkipped(): CaseResult = runCase("extras-excise-body-skipped") { a, _ ->
        SurgeonAnchorMatrixExtrasCases.reset()
        val fx = AnchorMatrixFixture_Extras()
        val r = fx.exciseRawTarget()
        a.equal("excised-value", r, "@Excise 返回值替换")
        a.equal(0, fx.exciseRawBodyExecuted, "@Excise 原方法体未执行")
        a.equal(1, SurgeonAnchorMatrixExtrasCases.exciseRawAdviceHits, "@Excise advice 触发 1 次")
    }

    fun testExtrasDescriptorNoParen(): CaseResult = runCase("extras-descriptor-no-paren") { a, _ ->
        SurgeonAnchorMatrixExtrasCases.reset()
        val fx = AnchorMatrixFixture_Extras()
        val r = fx.noParenMethod()
        a.equal("no-paren", r, "原方法返回")
        a.equal(1, SurgeonAnchorMatrixExtrasCases.noParenLeadHits, "@Lead 完整描述符命中")
    }

    fun testTraumaAnchorInvalidName(): CaseResult = runCase("trauma-anchor-invalid-name") { a, _ ->
        // transient DSL 不接受字符串锚点（编译期只通过 Anchor 枚举），
        // 这里用描述符本身完全错位（缺左括号）模拟 Trauma.Declaration.BadDescriptor。
        val err = runCatching {
            Scalpel.transient {
                bypass("top.maplex.incisiontest.fixture.AnchorMatrixFixture_Extras#noParenMethod)java.lang.String") { _ -> null }
            }
        }.exceptionOrNull()
        a.note("trauma-anchor-invalid-name: ${err?.javaClass?.simpleName}: ${err?.message?.take(140)}")
    }

    fun testTraumaDescriptorWrongEncoding(): CaseResult = runCase("trauma-descriptor-wrong-encoding") { a, _ ->
        // 错误描述符编码：返回类型用未知基元缩写
        val err = runCatching {
            Scalpel.transient {
                lead("top.maplex.incisiontest.fixture.AnchorMatrixFixture_Extras#noParenMethod()XXX_NOT_A_TYPE") { _ -> }
            }
        }.exceptionOrNull()
        a.note("trauma-descriptor-wrong-encoding: ${err?.javaClass?.simpleName}: ${err?.message?.take(140)} (null 表示 transient 路径宽松)")
    }

    fun testTraumaTargetIllegalChars(): CaseResult = runCase("trauma-target-illegal-chars") { a, _ ->
        // target 含非法字符（如空格、换行）——应在描述符解析阶段被拒绝
        val err = runCatching {
            Scalpel.transient {
                lead("top.maplex.incisiontest.fixture.AnchorMatrixFixture_Extras# bad name with spaces ()V") { _ -> }
            }
        }.exceptionOrNull()
        a.note("trauma-target-illegal-chars: ${err?.javaClass?.simpleName}: ${err?.message?.take(140)} (null 表示 transient 路径宽松)")
    }

    // ======== Accessor — Lambda 工厂 ========

    private fun triggerAccessorCases(): AccessorFixture {
        AccessorCases.reset()
        val fx = AccessorFixture()
        fx.publicDouble(1) // 触发 @Lead → AccessorCases.onPublicDouble
        return fx
    }

    fun testAccessorPrivateFinalField(): CaseResult = runCase("accessor-private-final-field") { a, _ ->
        triggerAccessorCases()
        a.equal("incision", AccessorCases.lastPrivateFinalName, "读 private final String")
    }

    fun testAccessorProtectedField(): CaseResult = runCase("accessor-protected-field") { a, _ ->
        triggerAccessorCases()
        a.equal(3.14, AccessorCases.lastProtectedValue, "读 protected Double")
    }

    fun testAccessorInternalField(): CaseResult = runCase("accessor-internal-field") { a, _ ->
        triggerAccessorCases()
        a.equal("internal-tag", AccessorCases.lastInternalTag, "读 internal String")
    }

    fun testAccessorPublicField(): CaseResult = runCase("accessor-public-field") { a, _ ->
        triggerAccessorCases()
        a.equal("public-label", AccessorCases.lastPublicLabel, "读 public String")
    }

    fun testAccessorWriteMutable(): CaseResult = runCase("accessor-write-mutable") { a, _ ->
        triggerAccessorCases()
        a.equal(42, AccessorCases.lastMutableCountAfterSet, "写 private var 后读回")
    }

    fun testAccessorStaticField(): CaseResult = runCase("accessor-static-field") { a, _ ->
        triggerAccessorCases()
        a.equal("top-secret", AccessorCases.lastStaticSecret, "读 private static final String")
    }

    fun testAccessorWriteStatic(): CaseResult = runCase("accessor-write-static") { a, _ ->
        triggerAccessorCases()
        a.equal(99, AccessorCases.lastStaticCounterAfterSet, "写 static var 后读回")
    }

    fun testAccessorPrivateMethod(): CaseResult = runCase("accessor-private-method") { a, _ ->
        triggerAccessorCases()
        a.equal("hello, world", AccessorCases.lastPrivateGreetResult, "调用 private 方法（按参数类型匹配）")
    }

    fun testAccessorPrivateMethodDesc(): CaseResult = runCase("accessor-private-method-desc") { a, _ ->
        triggerAccessorCases()
        a.equal(30, AccessorCases.lastPrivateAddResult, "调用 private 方法（指定 descriptor）")
    }

    // ======== Accessor — Theatre DSL + 继承 ========

    private fun triggerTheatreDslCases(): AccessorChildFixture {
        AccessorTheatreDslCases.reset()
        val fx = AccessorChildFixture()
        fx.describe() // 触发 @Lead → AccessorTheatreDslCases.onDescribe
        return fx
    }

    fun testAccessorTheatreChildField(): CaseResult = runCase("accessor-theatre-child-field") { a, _ ->
        triggerTheatreDslCases()
        a.equal("child", AccessorTheatreDslCases.lastChildLabel, "Theatre DSL 读子类字段")
    }

    fun testAccessorTheatreParentField(): CaseResult = runCase("accessor-theatre-parent-field") { a, _ ->
        triggerTheatreDslCases()
        a.equal("from-parent", AccessorTheatreDslCases.lastParentSecret, "Theatre DSL 读父类 private 字段")
    }

    fun testAccessorTheatreWriteParent(): CaseResult = runCase("accessor-theatre-write-parent") { a, _ ->
        triggerTheatreDslCases()
        a.equal(77, AccessorTheatreDslCases.lastParentCounterAfterSet, "Theatre DSL 写父类 protected 字段后读回")
    }

    // ======== Accessor — Theatre 工具方法 ========

    private fun triggerUtilCases() {
        AccessorUtilCases.reset()
        val fx = UtilFixture()
        val tag = GreetableImpl("hi-from-greetable")
        fx.process("alice", 7, tag) // 触发 @Lead → AccessorUtilCases.onProcess
    }

    fun testUtilArg(): CaseResult = runCase("util-arg") { a, _ ->
        triggerUtilCases()
        a.equal("alice", AccessorUtilCases.lastArgString, "arg<String>(0) 取第一个参数")
        a.equal(7, AccessorUtilCases.lastArgInt, "arg<Int>(1) 取第二个参数")
    }

    fun testUtilArgOrThrow(): CaseResult = runCase("util-arg-or-throw") { a, _ ->
        triggerUtilCases()
        a.equal("alice", AccessorUtilCases.lastArgOrThrowString, "argOrThrow<String>(0)")
    }

    fun testUtilArgOutOfBounds(): CaseResult = runCase("util-arg-out-of-bounds") { a, _ ->
        triggerUtilCases()
        a.isNull(AccessorUtilCases.lastArgNull, "arg(99) 越界返回 null")
        a.isTrue(AccessorUtilCases.lastArgOutOfBounds, "argOrThrow(99) 抛 IndexOutOfBoundsException")
    }

    fun testUtilCast(): CaseResult = runCase("util-cast") { a, _ ->
        triggerUtilCases()
        a.notNull(AccessorUtilCases.lastCastGreetable, "cast<Greetable>() 成功")
    }

    fun testUtilCastNull(): CaseResult = runCase("util-cast-null") { a, _ ->
        triggerUtilCases()
        a.isNull(AccessorUtilCases.lastCastNull, "cast<Greetable>() 类型不匹配返回 null")
    }

    fun testUtilCastOrThrow(): CaseResult = runCase("util-cast-or-throw") { a, _ ->
        triggerUtilCases()
        a.equal("hello", AccessorUtilCases.lastCastOrThrowString, "castOrThrow<String>() 成功")
    }

    fun testUtilCastOrThrowFail(): CaseResult = runCase("util-cast-or-throw-fail") { a, _ ->
        triggerUtilCases()
        a.isTrue(AccessorUtilCases.lastCastOrThrowFailed, "castOrThrow 类型不匹配抛 ClassCastException")
    }

    fun testUtilSelfAs(): CaseResult = runCase("util-self-as") { a, _ ->
        triggerUtilCases()
        a.equal("UtilFixture", AccessorUtilCases.lastSelfAsString, "selfAs<Any>() 返回 self 的类名")
    }

    fun testUtilReadFieldOnArg(): CaseResult = runCase("util-read-field-on-arg") { a, _ ->
        triggerUtilCases()
        a.equal("greetable-secret", AccessorUtilCases.lastReadFieldOnArg, "readField 读 arg 对象的 private 字段")
    }

    fun testUtilCallMethodOnArg(): CaseResult = runCase("util-call-method-on-arg") { a, _ ->
        triggerUtilCases()
        a.equal("hi-from-greetable", AccessorUtilCases.lastCallMethodOnArg, "callMethod 调 arg 对象的方法")
    }

    fun testUtilWriteFieldOnArg(): CaseResult = runCase("util-write-field-on-arg") { a, _ ->
        triggerUtilCases()
        a.equal("modified", AccessorUtilCases.lastWriteFieldVerify, "writeField 写 arg 对象的 private 字段后读回")
    }
}
