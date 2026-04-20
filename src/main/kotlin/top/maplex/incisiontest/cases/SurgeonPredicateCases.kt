package top.maplex.incisiontest.cases

import taboolib.module.incision.annotation.Bypass
import taboolib.module.incision.annotation.Excise
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
 * where 谓词 DSL 全覆盖测试 —— Task #15。
 *
 * 每条 @Lead 对应一种语法算子（比较/类型/布尔/属性/方法/安全调用/字面量/in/matches）。
 * 各自独立计数器，由 AllCases 调用 fixture 方法触发并断言计数是否符合 where 过滤结果。
 *
 * 语法规范：
 * - args[i]：访问第 i 个参数
 * - this：当前目标对象
 * - 类型算子：as / is / !is / ic / !ic / ip / !ip / it / !it
 * - 比较：== != < > <= >= matches in
 * - 布尔：&& || !
 * - 属性访问 `.`、方法调用 `.name()`、安全调用 `?.`、下标 `[...]`
 */
@Surgeon
object SurgeonPredicateCases {

    // ---- 保留旧计数器（AllCases 旧 case 仍引用） ----
    @Volatile var dragonHits = 0
    @Volatile var anyLowLevelHits = 0

    // ---- 各语法算子计数器 ----
    @Volatile var eqStringHits = 0       // == string
    @Volatile var neqIntHits = 0         // != int
    @Volatile var ltHits = 0             // <
    @Volatile var gtHits = 0             // >
    @Volatile var leHits = 0             // <=
    @Volatile var geHits = 0             // >=
    @Volatile var matchesHits = 0        // matches 正则
    @Volatile var inStringHits = 0       // x in "..."
    @Volatile var inListHits = 0         // x in List （通过属性取 List）
    @Volatile var isStringHits = 0       // is java.lang.String
    @Volatile var notIsIntHits = 0       // !is java.lang.Integer
    @Volatile var icListHits = 0         // ic java.util.List （strict subclass）
    @Volatile var ipObjectHits = 0       // ip java.lang.Object
    @Volatile var itExactHits = 0        // it java.lang.String
    @Volatile var notItHits = 0          // !it java.lang.String
    @Volatile var asCastHits = 0         // as java.lang.String
    @Volatile var propertyHits = 0       // args[0].name == "foo"
    @Volatile var propertySizeHits = 0   // args[0].size > 0
    @Volatile var methodCallHits = 0     // args[0].startsWith("foo")
    @Volatile var methodNoArgHits = 0    // args[0].length()
    @Volatile var safeCallHits = 0       // args[0]?.name == "x"
    @Volatile var andHits = 0            // a && b
    @Volatile var orHits = 0             // a || b
    @Volatile var notHits = 0            // !(a == b)
    @Volatile var nestedHits = 0         // !((a && b) || c)
    @Volatile var literalTrueHits = 0    // true 字面量
    @Volatile var literalFalseHits = 0   // false 字面量
    @Volatile var literalNullHits = 0    // == null
    @Volatile var literalDoubleHits = 0  // 浮点字面量
    @Volatile var compoundHits = 0       // 多算子复合
    @Volatile var thisRefHits = 0        // this.javaClass 相关

    fun reset() {
        dragonHits = 0; anyLowLevelHits = 0
        eqStringHits = 0; neqIntHits = 0
        ltHits = 0; gtHits = 0; leHits = 0; geHits = 0
        matchesHits = 0; inStringHits = 0; inListHits = 0
        isStringHits = 0; notIsIntHits = 0
        icListHits = 0; ipObjectHits = 0; itExactHits = 0; notItHits = 0
        asCastHits = 0
        propertyHits = 0; propertySizeHits = 0
        methodCallHits = 0; methodNoArgHits = 0
        safeCallHits = 0
        andHits = 0; orHits = 0; notHits = 0; nestedHits = 0
        literalTrueHits = 0; literalFalseHits = 0; literalNullHits = 0; literalDoubleHits = 0
        compoundHits = 0; thisRefHits = 0
    }

    // ==================== 保留：旧脚手架测试 ====================

    @Lead(
        scope = "method:top.maplex.incisiontest.fixture.PredicateFixture#spawn(java.lang.String,int)java.lang.String",
        where = "args[0] == \"dragon\" && args[1] >= 50"
    )
    fun onSpawnDragon(theatre: Theatre) { dragonHits++ }

    @Lead(
        scope = "method:top.maplex.incisiontest.fixture.PredicateFixture#spawn(java.lang.String,int)java.lang.String",
        where = "args[1] < 10"
    )
    fun onSpawnLowLevel(theatre: Theatre) { anyLowLevelHits++ }

    // ==================== 比较算子 ====================

    @Lead(
        scope = "method:top.maplex.incisiontest.fixture.PredicateFixture#log(java.lang.String)java.lang.String",
        where = "args[0] == \"hello\""
    )
    fun onLogEqString(theatre: Theatre) { eqStringHits++ }

    @Lead(
        scope = "method:top.maplex.incisiontest.fixture.PredicateFixture#spawn(java.lang.String,int)java.lang.String",
        where = "args[1] != 0"
    )
    fun onSpawnNeqInt(theatre: Theatre) { neqIntHits++ }

    @Lead(
        scope = "method:top.maplex.incisiontest.fixture.PredicateFixture#measure(double)java.lang.String",
        where = "args[0] < 10"
    )
    fun onMeasureLt(theatre: Theatre) { ltHits++ }

    @Lead(
        scope = "method:top.maplex.incisiontest.fixture.PredicateFixture#measure(double)java.lang.String",
        where = "args[0] > 100"
    )
    fun onMeasureGt(theatre: Theatre) { gtHits++ }

    @Lead(
        scope = "method:top.maplex.incisiontest.fixture.PredicateFixture#spawn(java.lang.String,int)java.lang.String",
        where = "args[1] <= 5"
    )
    fun onSpawnLe(theatre: Theatre) { leHits++ }

    @Lead(
        scope = "method:top.maplex.incisiontest.fixture.PredicateFixture#spawn(java.lang.String,int)java.lang.String",
        where = "args[1] >= 100"
    )
    fun onSpawnGe(theatre: Theatre) { geHits++ }

    // ==================== matches / in ====================

    @Lead(
        scope = "method:top.maplex.incisiontest.fixture.PredicateFixture#log(java.lang.String)java.lang.String",
        where = "args[0] matches \"hello.*\""
    )
    fun onLogMatches(theatre: Theatre) { matchesHits++ }

    @Lead(
        scope = "method:top.maplex.incisiontest.fixture.PredicateFixture#log(java.lang.String)java.lang.String",
        where = "\"lo\" in args[0]"
    )
    fun onLogInString(theatre: Theatre) { inStringHits++ }

    @Lead(
        scope = "method:top.maplex.incisiontest.fixture.PredicateFixture#handle(java.util.List)java.lang.String",
        where = "\"key\" in args[0]"
    )
    fun onHandleInList(theatre: Theatre) { inListHits++ }

    // ==================== 类型算子 ====================

    @Lead(
        scope = "method:top.maplex.incisiontest.fixture.PredicateFixture#accept(java.lang.Object)java.lang.String",
        where = "args[0] is java.lang.String"
    )
    fun onAcceptIsString(theatre: Theatre) { isStringHits++ }

    @Lead(
        scope = "method:top.maplex.incisiontest.fixture.PredicateFixture#accept(java.lang.Object)java.lang.String",
        where = "args[0] !is java.lang.Integer"
    )
    fun onAcceptNotIsInt(theatre: Theatre) { notIsIntHits++ }

    @Lead(
        scope = "method:top.maplex.incisiontest.fixture.PredicateFixture#handle(java.util.List)java.lang.String",
        where = "args[0] ic java.util.List"
    )
    fun onHandleIcList(theatre: Theatre) { icListHits++ }

    @Lead(
        scope = "method:top.maplex.incisiontest.fixture.PredicateFixture#accept(java.lang.Object)java.lang.String",
        where = "args[0] ip java.lang.Object"
    )
    fun onAcceptIpObject(theatre: Theatre) { ipObjectHits++ }

    @Lead(
        scope = "method:top.maplex.incisiontest.fixture.PredicateFixture#trigger(java.lang.String)java.lang.String",
        where = "args[0] it java.lang.String"
    )
    fun onTriggerItExact(theatre: Theatre) { itExactHits++ }

    @Lead(
        scope = "method:top.maplex.incisiontest.fixture.PredicateFixture#accept(java.lang.Object)java.lang.String",
        where = "args[0] !it java.lang.String"
    )
    fun onAcceptNotIt(theatre: Theatre) { notItHits++ }

    @Lead(
        scope = "method:top.maplex.incisiontest.fixture.PredicateFixture#accept(java.lang.Object)java.lang.String",
        where = "(args[0] as java.lang.String) == \"casted\""
    )
    fun onAcceptAsCast(theatre: Theatre) { asCastHits++ }

    // ==================== 属性访问 / 方法调用 ====================

    @Lead(
        scope = "method:top.maplex.incisiontest.fixture.PredicateFixture#register(top.maplex.incisiontest.fixture.PredicateFixture\$Named)java.lang.String",
        where = "args[0].name == \"foo\""
    )
    fun onRegisterProperty(theatre: Theatre) { propertyHits++ }

    @Lead(
        scope = "method:top.maplex.incisiontest.fixture.PredicateFixture#register(top.maplex.incisiontest.fixture.PredicateFixture\$Named)java.lang.String",
        where = "args[0].size > 0"
    )
    fun onRegisterPropertySize(theatre: Theatre) { propertySizeHits++ }

    @Lead(
        scope = "method:top.maplex.incisiontest.fixture.PredicateFixture#register(top.maplex.incisiontest.fixture.PredicateFixture\$Named)java.lang.String",
        where = "args[0].startsWith(\"foo\")"
    )
    fun onRegisterMethodCall(theatre: Theatre) { methodCallHits++ }

    @Lead(
        scope = "method:top.maplex.incisiontest.fixture.PredicateFixture#register(top.maplex.incisiontest.fixture.PredicateFixture\$Named)java.lang.String",
        where = "args[0].length() >= 3"
    )
    fun onRegisterMethodNoArg(theatre: Theatre) { methodNoArgHits++ }

    // 安全调用 `?.`：null 入参时 name 返回 null，整体 `== "x"` → false，不命中
    @Lead(
        scope = "method:top.maplex.incisiontest.fixture.PredicateFixture#touch(top.maplex.incisiontest.fixture.PredicateFixture\$Named)java.lang.String",
        where = "args[0]?.name == \"x\""
    )
    fun onTouchSafeCall(theatre: Theatre) { safeCallHits++ }

    // ==================== 布尔组合 ====================

    @Lead(
        scope = "method:top.maplex.incisiontest.fixture.PredicateFixture#spawn(java.lang.String,int)java.lang.String",
        where = "args[0] == \"combo\" && args[1] > 5"
    )
    fun onSpawnAnd(theatre: Theatre) { andHits++ }

    @Lead(
        scope = "method:top.maplex.incisiontest.fixture.PredicateFixture#spawn(java.lang.String,int)java.lang.String",
        where = "args[0] == \"x\" || args[1] > 1000"
    )
    fun onSpawnOr(theatre: Theatre) { orHits++ }

    @Lead(
        scope = "method:top.maplex.incisiontest.fixture.PredicateFixture#spawn(java.lang.String,int)java.lang.String",
        where = "!(args[0] == \"skip\")"
    )
    fun onSpawnNot(theatre: Theatre) { notHits++ }

    @Lead(
        scope = "method:top.maplex.incisiontest.fixture.PredicateFixture#spawn(java.lang.String,int)java.lang.String",
        where = "!((args[0] == \"a\" && args[1] < 10) || args[0] == \"b\")"
    )
    fun onSpawnNested(theatre: Theatre) { nestedHits++ }

    // ==================== 字面量 ====================

    @Lead(
        scope = "method:top.maplex.incisiontest.fixture.PredicateFixture#flag(java.lang.Object)java.lang.String",
        where = "true"
    )
    fun onFlagTrue(theatre: Theatre) { literalTrueHits++ }

    @Lead(
        scope = "method:top.maplex.incisiontest.fixture.PredicateFixture#flag(java.lang.Object)java.lang.String",
        where = "false"
    )
    fun onFlagFalse(theatre: Theatre) { literalFalseHits++ }

    @Lead(
        scope = "method:top.maplex.incisiontest.fixture.PredicateFixture#flag(java.lang.Object)java.lang.String",
        where = "args[0] == null"
    )
    fun onFlagNull(theatre: Theatre) { literalNullHits++ }

    @Lead(
        scope = "method:top.maplex.incisiontest.fixture.PredicateFixture#measure(double)java.lang.String",
        where = "args[0] == 3.14"
    )
    fun onMeasureDouble(theatre: Theatre) { literalDoubleHits++ }

    // ==================== 复合与 this ====================

    @Lead(
        scope = "method:top.maplex.incisiontest.fixture.PredicateFixture#spawn(java.lang.String,int)java.lang.String",
        where = "args[0] matches \"mob_.*\" && args[1] >= 10 && args[1] <= 100 && !(args[0] == \"mob_ignored\")"
    )
    fun onSpawnCompound(theatre: Theatre) { compoundHits++ }

    @Lead(
        scope = "method:top.maplex.incisiontest.fixture.PredicateFixture#log(java.lang.String)java.lang.String",
        where = "this is top.maplex.incisiontest.fixture.PredicateFixture"
    )
    fun onLogThisRef(theatre: Theatre) { thisRefHits++ }

    // ==================== where 字段在每个注解上的覆盖 ====================
    // 目标：验证 where 不仅 @Lead 可用，对 @Trail/@Splice/@Excise/@Bypass/@Graft/@Trim 同样生效。

    @Volatile var emptyWhereHits = 0   // where="" 基线：不过滤
    @Volatile var trailWhereHits = 0   // @Trail + where
    @Volatile var spliceWhereHits = 0  // @Splice + where
    @Volatile var exciseWhereHits = 0  // @Excise + where
    @Volatile var bypassWhereHits = 0  // @Bypass + where
    @Volatile var graftWhereHits = 0   // @Graft + where
    @Volatile var trimWhereHits = 0    // @Trim + where

    fun resetAdviceWhere() {
        emptyWhereHits = 0
        trailWhereHits = 0
        spliceWhereHits = 0
        exciseWhereHits = 0
        bypassWhereHits = 0
        graftWhereHits = 0
        trimWhereHits = 0
    }

    /** where="" 基线：所有调用都命中（不过滤）。 */
    @Lead(
        scope = "method:top.maplex.incisiontest.fixture.PredicateFixture#whereEmpty(int)int",
        where = ""
    )
    fun onEmptyWhere(theatre: Theatre) { emptyWhereHits++ }

    /** @Trail + where：方法返回时，仅当 args[0] > 0 命中。 */
    @Trail(
        scope = "method:top.maplex.incisiontest.fixture.PredicateFixture#whereTrail(int)int",
        where = "args[0] > 0"
    )
    fun onTrailWithWhere(theatre: Theatre) { trailWhereHits++ }

    /** @Splice + where：仅 args[0] >= 100 时由 advice 接管，其余 proceed 放行。 */
    @Splice(
        scope = "method:top.maplex.incisiontest.fixture.PredicateFixture#whereSplice(int)int",
        where = "args[0] >= 100"
    )
    fun onSpliceWithWhere(theatre: Theatre): Any? {
        spliceWhereHits++
        // 返回非 null → 替换原方法返回值
        return -1
    }

    /** @Excise + where：仅 args[0]=="cut" 时整段替换返回 "excised"，否则原方法执行。 */
    @Excise(
        scope = "method:top.maplex.incisiontest.fixture.PredicateFixture#whereExcise(java.lang.String)java.lang.String",
        where = "args[0] == \"cut\""
    )
    fun onExciseWithWhere(theatre: Theatre): Any? {
        exciseWhereHits++
        return "excised"
    }

    /** @Bypass + where：替换 whereBypass 内对 whereBypassHelper 的调用，仅 args[0] > 50 启用。 */
    @Bypass(
        method = "top.maplex.incisiontest.fixture.PredicateFixture#whereBypass(int)int",
        site = Site(
            anchor = Anchor.INVOKE,
            target = "top.maplex.incisiontest.fixture.PredicateFixture#whereBypassHelper(int)int",
        ),
        where = "args[0] > 50"
    )
    fun onBypassWithWhere(theatre: Theatre): Any? {
        bypassWhereHits++
        return 9999
    }

    /** @Graft + where：在 whereGraft 调用 whereGraftHelper 之前植入，仅 args[0] >= 10 触发。 */
    @Graft(
        method = "top.maplex.incisiontest.fixture.PredicateFixture#whereGraft(int)int",
        site = Site(
            anchor = Anchor.INVOKE,
            target = "top.maplex.incisiontest.fixture.PredicateFixture#whereGraftHelper(int)int",
        ),
        where = "args[0] >= 10"
    )
    fun onGraftWithWhere(theatre: Theatre) { graftWhereHits++ }

    /** @Trim ARG + where：仅当原参数 > 0 时改写 index=0。 */
    @Trim(
        method = "top.maplex.incisiontest.fixture.PredicateFixture#whereTrim(int)int",
        kind = Trim.Kind.ARG,
        index = 0,
        where = "args[0] > 0"
    )
    fun onTrimArgWithWhere(theatre: Theatre): Any? {
        trimWhereHits++
        return 777
    }
}
