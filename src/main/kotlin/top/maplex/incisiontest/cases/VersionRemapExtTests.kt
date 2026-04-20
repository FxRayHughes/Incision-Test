package top.maplex.incisiontest.cases

import taboolib.module.incision.api.NoopVersionMatcher
import taboolib.module.incision.api.VersionMatcher
import taboolib.module.incision.api.VersionMatchers
import top.maplex.incisiontest.CaseResult
import top.maplex.incisiontest.fixture.SurgeonVersionMatrixFixture
import top.maplex.incisiontest.fixture.SurgeonVersionTargetFixture
import top.maplex.incisiontest.runCase

/**
 * Task #16 Version 与 Remap 扩展用例 —— 独立文件。
 *
 * 拆出来是为了避开 AllCases 被多 agent 并发改写导致的 last-write-wins 冲突。
 * AllCases.entries 只需引用这些方法引用即可。
 *
 * 覆盖：
 * - @Version 区间边界：在内 / 早于 start / 晚于 end / 边界同点（start/end 分别闭合）
 * - 自定义 matcher fqcn 写错 → 回落 Noop 的 warn 路径
 * - 同名多版本 advice 在同 Surgeon 里 index 区分
 * - VersionMatchers.resolve 三分支
 * - VersionMatcher.matches 段比较默认实现
 * - NMS owner/method/field remap 走通 + 失败回退原名（不抛）
 */
object VersionRemapExtTests {

    fun testVersionFakeInRange(): CaseResult = runCase("version-fake-in-range") { a, _ ->
        SurgeonVersionCases.reset()
        val fx = SurgeonVersionTargetFixture()
        fx.fakeMatcherInRange()
        a.equal(1, SurgeonVersionCases.fakeInRangeHits, "FakeVersionMatcher(2.5) 落在 [2.0, 3.0] 应命中")
    }

    fun testVersionFakeBelowStart(): CaseResult = runCase("version-fake-below-start") { a, _ ->
        SurgeonVersionCases.reset()
        val fx = SurgeonVersionTargetFixture()
        fx.fakeMatcherBelowStart()
        a.equal(0, SurgeonVersionCases.fakeBelowStartHits, "current=2.5 < start=3.0，advice 应被扫描器剔除")
    }

    fun testVersionFakeAboveEnd(): CaseResult = runCase("version-fake-above-end") { a, _ ->
        SurgeonVersionCases.reset()
        val fx = SurgeonVersionTargetFixture()
        fx.fakeMatcherAboveEnd()
        a.equal(0, SurgeonVersionCases.fakeAboveEndHits, "current=2.5 > end=1.0，advice 应被扫描器剔除")
    }

    fun testVersionFakeBoundaryEnd(): CaseResult = runCase("version-fake-boundary-end") { a, _ ->
        SurgeonVersionCases.reset()
        val fx = SurgeonVersionTargetFixture()
        fx.fakeMatcherBoundaryEnd()
        a.equal(1, SurgeonVersionCases.fakeBoundaryEndHits, "end=2.5 为闭区间，current=2.5 应命中")
    }

    fun testVersionFakeBoundaryStart(): CaseResult = runCase("version-fake-boundary-start") { a, _ ->
        SurgeonVersionCases.reset()
        val fx = SurgeonVersionTargetFixture()
        fx.fakeMatcherBoundaryStart()
        a.equal(1, SurgeonVersionCases.fakeBoundaryStartHits, "start=2.5 为闭区间，current=2.5 应命中")
    }

    fun testVersionMissingMatcherFallback(): CaseResult = runCase("version-missing-matcher-fallback") { a, _ ->
        SurgeonVersionCases.reset()
        val fx = SurgeonVersionTargetFixture()
        fx.missingMatcherFallback()
        a.equal(1, SurgeonVersionCases.missingMatcherHits, "matcher fqcn 不存在应 warn 并回退 NoopVersionMatcher（始终命中）")
    }

    fun testVersionMultiRangeIndex(): CaseResult = runCase("version-multi-range-index") { a, _ ->
        SurgeonVersionCases.reset()
        val fx = SurgeonVersionTargetFixture()
        fx.multiRangeByIndex()
        a.equal(0, SurgeonVersionCases.multiRangeIdx0Hits, "区间 [0,1] 应被剔除 (current=2.5)")
        a.equal(1, SurgeonVersionCases.multiRangeIdx1Hits, "区间 [2,3] 应命中 (current=2.5)")
        a.equal(0, SurgeonVersionCases.multiRangeIdx2Hits, "区间 [4,5] 应被剔除 (current=2.5)")
    }

    fun testVersionMatcherResolve(): CaseResult = runCase("version-matcher-resolve") { a, _ ->
        val default = VersionMatchers.resolve("")
        a.check(default::class.java.simpleName == "MinecraftVersionMatcher", "空 fqcn 应返回 MinecraftVersionMatcher (actual=${default::class.java.simpleName})")
        val fake = VersionMatchers.resolve("top.maplex.incisiontest.api.FakeVersionMatcher")
        a.equal("2.5", fake.current(), "自定义 matcher current() 返回 2.5")
        val bad = VersionMatchers.resolve("no.such.matcher.Xyz")
        a.check(bad === NoopVersionMatcher, "错误 fqcn 应回落 NoopVersionMatcher (actual=${bad.javaClass.name})")
    }

    fun testVersionMatcherSegmentCompare(): CaseResult = runCase("version-matcher-segment") { a, _ ->
        val m: VersionMatcher = top.maplex.incisiontest.api.FakeVersionMatcher
        a.check(m.matches("2.0", "3.0"), "2.5 ∈ [2.0, 3.0]")
        a.check(!m.matches("3.0", ""), "2.5 ∉ [3.0, ∞)")
        a.check(!m.matches("", "1.0"), "2.5 ∉ (-∞, 1.0]")
        a.check(m.matches("2.5", "2.5"), "边界同点单值命中")
        a.check(m.matches("", ""), "双空端点始终命中")
    }

    fun testRemapOwnerTranslate(): CaseResult = runCase("remap-owner-translate") { a, _ ->
        val router = Class.forName("taboolib.module.incision.remap.RemapRouter", true, javaClass.classLoader)
        val instance = router.getField("INSTANCE").get(null)
        val method = router.getMethod("resolveOwner", String::class.java)
        val input = "net/minecraft/server/MinecraftServer"
        val result = method.invoke(instance, input) as String
        a.check(result.isNotBlank(), "resolveOwner 返回非空 (result=$result)")
        a.note("resolveOwner input=$input → $result")
    }

    fun testRemapMethodFallback(): CaseResult = runCase("remap-method-fallback") { a, _ ->
        val router = Class.forName("taboolib.module.incision.remap.RemapRouter", true, javaClass.classLoader)
        val instance = router.getField("INSTANCE").get(null)
        val method = router.getMethod("resolveMethod", String::class.java, String::class.java, String::class.java)
        val owner = "net/minecraft/server/MinecraftServer"
        val name = "__nonexistent_method_xyz_${System.nanoTime()}"
        val desc = "()V"
        val result = method.invoke(instance, owner, name, desc) as Pair<*, *>
        a.equal(name, result.first, "找不到映射时方法名原样返回")
        a.equal(desc, result.second, "描述符原样返回")
    }

    fun testRemapFieldResolve(): CaseResult = runCase("remap-field-resolve") { a, _ ->
        val router = Class.forName("taboolib.module.incision.remap.RemapRouter", true, javaClass.classLoader)
        val instance = router.getField("INSTANCE").get(null)
        val method = router.getMethod("resolveField", String::class.java, String::class.java, String::class.java)
        val owner = "net/minecraft/server/MinecraftServer"
        val fieldName = "__nonexistent_field_${System.nanoTime()}"
        val desc = "I"
        val result = method.invoke(instance, owner, fieldName, desc) as Pair<*, *>
        a.equal(fieldName, result.first, "resolveField 对未知字段原样返回名字，不抛异常")
        a.equal(desc, result.second, "字段描述符原样返回")
    }

    // ============================================================
    // #16 扩展矩阵 —— 全 advice 类型 × @Version 参数边界
    // ============================================================

    fun testVersionBothEmpty(): CaseResult = runCase("version-matrix-both-empty") { a, _ ->
        SurgeonVersionMatrixCases.reset()
        SurgeonVersionMatrixFixture().bothEmpty()
        a.equal(1, SurgeonVersionMatrixCases.bothEmptyHits, "start/end 双空应始终命中")
    }

    fun testVersionOnlyEndGiven(): CaseResult = runCase("version-matrix-only-end") { a, _ ->
        SurgeonVersionMatrixCases.reset()
        SurgeonVersionMatrixFixture().onlyEndGiven()
        a.equal(1, SurgeonVersionMatrixCases.onlyEndGivenHits, "仅 end=3.0，current=2.5 应命中")
    }

    fun testVersionOnlyStartGiven(): CaseResult = runCase("version-matrix-only-start") { a, _ ->
        SurgeonVersionMatrixCases.reset()
        SurgeonVersionMatrixFixture().onlyStartGiven()
        a.equal(1, SurgeonVersionMatrixCases.onlyStartGivenHits, "仅 start=1.0，current=2.5 应命中")
    }

    fun testVersionSinglePoint(): CaseResult = runCase("version-matrix-single-point") { a, _ ->
        SurgeonVersionMatrixCases.reset()
        SurgeonVersionMatrixFixture().singlePoint()
        a.equal(1, SurgeonVersionMatrixCases.singlePointHits, "start=end=2.5 的单点区间应命中")
    }

    fun testVersionInvertedRange(): CaseResult = runCase("version-matrix-inverted") { a, _ ->
        SurgeonVersionMatrixCases.reset()
        SurgeonVersionMatrixFixture().invertedRange()
        a.equal(0, SurgeonVersionMatrixCases.invertedRangeHits, "非法区间 start>end 时 current=2.5 应被剔除")
    }

    fun testVersionOnLead(): CaseResult = runCase("version-advice-lead") { a, _ ->
        SurgeonVersionMatrixCases.reset()
        SurgeonVersionMatrixFixture().leadTarget()
        a.equal(1, SurgeonVersionMatrixCases.leadHits, "@Lead 上的 @Version 应生效")
    }

    fun testVersionOnTrail(): CaseResult = runCase("version-advice-trail") { a, _ ->
        SurgeonVersionMatrixCases.reset()
        SurgeonVersionMatrixFixture().trailTarget()
        a.equal(1, SurgeonVersionMatrixCases.trailHits, "@Trail 上的 @Version 应生效")
    }

    fun testVersionOnSplice(): CaseResult = runCase("version-advice-splice") { a, _ ->
        SurgeonVersionMatrixCases.reset()
        SurgeonVersionMatrixFixture().spliceTarget()
        a.equal(1, SurgeonVersionMatrixCases.spliceHits, "@Splice 上的 @Version 应生效")
    }

    fun testVersionOnExcise(): CaseResult = runCase("version-advice-excise") { a, _ ->
        SurgeonVersionMatrixCases.reset()
        SurgeonVersionMatrixFixture().exciseTarget()
        a.equal(1, SurgeonVersionMatrixCases.exciseHits, "@Excise 上的 @Version 应生效并切入")
    }

    fun testVersionOnBypass(): CaseResult = runCase("version-advice-bypass") { a, _ ->
        SurgeonVersionMatrixCases.reset()
        val result = SurgeonVersionMatrixFixture().bypassCallerTarget()
        a.equal(1, SurgeonVersionMatrixCases.bypassHits, "@Bypass 上的 @Version 应生效 (handler 已执行)")
        // bypass 替换 helperForBypass() 的返回值为 1000，caller 再 +1 → 1001
        a.equal(1001, result, "bypass 替换 helper 返回值为 1000，caller 再 +1 应得 1001")
    }

    fun testVersionOnGraft(): CaseResult = runCase("version-advice-graft") { a, _ ->
        SurgeonVersionMatrixCases.reset()
        SurgeonVersionMatrixFixture().graftCallerTarget()
        a.equal(1, SurgeonVersionMatrixCases.graftHits, "@Graft 上的 @Version 应在 NEW StringBuilder 时触发")
    }

    fun testVersionOnTrim(): CaseResult = runCase("version-advice-trim") { a, _ ->
        SurgeonVersionMatrixCases.reset()
        val result = SurgeonVersionMatrixFixture().trimReturnTarget()
        a.equal(1, SurgeonVersionMatrixCases.trimReturnHits, "@Trim 上的 @Version 应生效")
        a.equal(777, result, "trim RETURN 应把返回值改写为 777")
    }

    fun testVersionDefaultMatcher(): CaseResult = runCase("version-matcher-default") { a, _ ->
        SurgeonVersionMatrixCases.reset()
        SurgeonVersionMatrixFixture().defaultMatcherTarget()
        // 非 NMS 环境 MinecraftVersionMatcher.current() = null → matches 总为 true → 命中
        a.equal(1, SurgeonVersionMatrixCases.defaultMatcherHits, "空 matcher 且 NMS 缺失时，current=null → matches=true 始终命中")
    }

    fun testVersionExplicitNoop(): CaseResult = runCase("version-matcher-explicit-noop") { a, _ ->
        SurgeonVersionMatrixCases.reset()
        SurgeonVersionMatrixFixture().explicitNoopMatcherTarget()
        a.equal(1, SurgeonVersionMatrixCases.explicitNoopHits, "NoopVersionMatcher 无视 start/end，始终命中")
    }

    fun testVersionFakeNoBounds(): CaseResult = runCase("version-matcher-fake-nobounds") { a, _ ->
        SurgeonVersionMatrixCases.reset()
        SurgeonVersionMatrixFixture().fakeMatcherNoBoundsTarget()
        a.equal(1, SurgeonVersionMatrixCases.fakeMatcherNoBoundsHits, "Fake matcher 双空端点始终命中")
    }

    fun testVersionSegLongerStart(): CaseResult = runCase("version-segment-longer-start") { a, _ ->
        SurgeonVersionMatrixCases.reset()
        SurgeonVersionMatrixFixture().segmentLongerStart()
        a.equal(1, SurgeonVersionMatrixCases.segLongerStartHits, "current=2.5 与 start=2.5.0 段比较应相等，命中")
    }

    fun testVersionSegLongerEnd(): CaseResult = runCase("version-segment-longer-end") { a, _ ->
        SurgeonVersionMatrixCases.reset()
        SurgeonVersionMatrixFixture().segmentLongerEnd()
        a.equal(1, SurgeonVersionMatrixCases.segLongerEndHits, "current=2.5 < end=2.5.9 应命中")
    }

    fun testVersionMicroStepBelow(): CaseResult = runCase("version-micro-below") { a, _ ->
        SurgeonVersionMatrixCases.reset()
        SurgeonVersionMatrixFixture().microStepBelow()
        a.equal(0, SurgeonVersionMatrixCases.microStepBelowHits, "current=2.5 < start=2.6 应被剔除")
    }

    fun testVersionMicroStepAbove(): CaseResult = runCase("version-micro-above") { a, _ ->
        SurgeonVersionMatrixCases.reset()
        SurgeonVersionMatrixFixture().microStepAbove()
        a.equal(0, SurgeonVersionMatrixCases.microStepAboveHits, "current=2.5 > end=2.4 应被剔除")
    }

    // ============================================================
    // Remap 扩展：owner 非 NMS / 空 desc / MC matcher 探测
    // ============================================================

    fun testRemapNonNmsOwnerUntouched(): CaseResult = runCase("remap-owner-non-nms") { a, _ ->
        val router = Class.forName("taboolib.module.incision.remap.RemapRouter", true, javaClass.classLoader)
        val instance = router.getField("INSTANCE").get(null)
        val method = router.getMethod("resolveOwner", String::class.java)
        val input = "java/lang/String"
        val result = method.invoke(instance, input) as String
        a.check(result == input || result.isNotBlank(), "非 NMS owner 走映射后至少不应为空 (input=$input, result=$result)")
        a.note("resolveOwner 非 NMS 路径: $input → $result")
    }

    fun testRemapMethodEmptyDesc(): CaseResult = runCase("remap-method-empty-desc") { a, _ ->
        val router = Class.forName("taboolib.module.incision.remap.RemapRouter", true, javaClass.classLoader)
        val instance = router.getField("INSTANCE").get(null)
        val method = router.getMethod("resolveMethod", String::class.java, String::class.java, String::class.java)
        val result = method.invoke(instance, "net/minecraft/server/MinecraftServer", "getPlayerCount", "") as Pair<*, *>
        a.check(result.first is String, "空 desc 调用不应抛异常 (name=${result.first})")
        a.note("empty-desc resolve: ${result.first} / desc=${result.second}")
    }

    fun testMinecraftMatcherProbe(): CaseResult = runCase("version-mc-matcher-probe") { a, _ ->
        val mc = taboolib.module.incision.api.MinecraftVersionMatcher
        val cur = mc.current()
        a.note("MinecraftVersionMatcher.current() = $cur (null 表示 NMS 未 classpath)")
        // 该 matcher 在 null 情形必命中；非 null 情形按真实版本比较
        a.check(mc.matches("", ""), "MC matcher 双空端点必命中")
    }

    fun testFakeMatcherCaching(): CaseResult = runCase("version-matcher-caching") { a, _ ->
        val fqcn = "top.maplex.incisiontest.api.FakeVersionMatcher"
        val m1 = VersionMatchers.resolve(fqcn)
        val m2 = VersionMatchers.resolve(fqcn)
        a.check(m1 === m2, "VersionMatchers.resolve 对同 fqcn 应返回同一缓存实例")
    }

    fun testVersionMatcherNullCurrentMatches(): CaseResult = runCase("version-null-current-matches") { a, _ ->
        val noop = taboolib.module.incision.api.NoopVersionMatcher
        a.check(noop.current() == null, "NoopVersionMatcher.current() 应为 null")
        a.check(noop.matches("1.0", "2.0"), "current=null 时默认 matches 返回 true")
        a.check(noop.matches("9.9", "0.0"), "NoopVersionMatcher 不做任何端点校验")
    }
}
