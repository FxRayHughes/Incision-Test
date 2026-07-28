package top.maplex.incisiontest.cases

import taboolib.module.incision.annotation.MatchMode

import taboolib.module.incision.annotation.SelectorKind

import taboolib.module.incision.annotation.Selector

import taboolib.module.incision.annotation.Pointcut

import taboolib.module.incision.annotation.Surgeon
import taboolib.module.incision.annotation.Trim
import taboolib.module.incision.api.Theatre

/**
 * Task #13 第二批 —— @Trim 全字段网格。
 *
 * 覆盖：
 * - kind = ARG（index = 0 / 1 / 2 / 3）
 * - kind = RETURN（int / long / String / boolean 各类型）
 *
 * 每条 advice 写不同的覆盖值，断言只看 hits 与 fixture 内 `lastXxx` / 返回值。
 */
@Surgeon
object SurgeonAnchorMatrixTrimCases {

    private const val FIX = "top.maplex.incisiontest.fixture.AnchorMatrixFixture_Trim"

    @Volatile var argSingleHits = 0
    @Volatile var argDouble0Hits = 0
    @Volatile var argDouble1Hits = 0
    @Volatile var argTriple2Hits = 0
    @Volatile var argQuad3Hits = 0
    @Volatile var returnIntHits = 0
    @Volatile var returnLongHits = 0
    @Volatile var returnStringHits = 0
    @Volatile var returnBoolHits = 0

    // ---- ARG × index ----

    @Trim(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "trimSingleArg", descriptor = "(Ljava/lang/String;)Ljava/lang/String;")]),
        kind = Trim.Kind.ARG,
        index = 0,
    )
    fun trimArgSingle(theatre: Theatre): Any? {
        argSingleHits++
        return "single-trimmed"
    }

    @Trim(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "trimDoubleArg", descriptor = "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;")]),
        kind = Trim.Kind.ARG,
        index = 0,
    )
    fun trimArgDouble0(theatre: Theatre): Any? {
        argDouble0Hits++
        return "left-trimmed"
    }

    @Trim(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "trimDoubleArg", descriptor = "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;")]),
        kind = Trim.Kind.ARG,
        index = 1,
    )
    fun trimArgDouble1(theatre: Theatre): Any? {
        argDouble1Hits++
        return "right-trimmed"
    }

    @Trim(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "trimTripleArg", descriptor = "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;")]),
        kind = Trim.Kind.ARG,
        index = 2,
    )
    fun trimArgTriple2(theatre: Theatre): Any? {
        argTriple2Hits++
        return "triple-2-trimmed"
    }

    @Trim(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "trimQuadArg", descriptor = "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;")]),
        kind = Trim.Kind.ARG,
        index = 3,
    )
    fun trimArgQuad3(theatre: Theatre): Any? {
        argQuad3Hits++
        return "quad-3-trimmed"
    }

    // ---- RETURN × 类型 ----

    @Trim(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "trimReturnInt", descriptor = "()I")]),
        kind = Trim.Kind.RETURN,
    )
    fun trimReturnInt(theatre: Theatre): Any? {
        returnIntHits++
        return -10
    }

    @Trim(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "trimReturnLong", descriptor = "()J")]),
        kind = Trim.Kind.RETURN,
    )
    fun trimReturnLong(theatre: Theatre): Any? {
        returnLongHits++
        return -100L
    }

    @Trim(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "trimReturnString", descriptor = "()Ljava/lang/String;")]),
        kind = Trim.Kind.RETURN,
    )
    fun trimReturnString(theatre: Theatre): Any? {
        returnStringHits++
        return "trimmed-string"
    }

    @Trim(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "trimReturnBoolean", descriptor = "()Z")]),
        kind = Trim.Kind.RETURN,
    )
    fun trimReturnBool(theatre: Theatre): Any? {
        returnBoolHits++
        return true
    }

    fun reset() {
        argSingleHits = 0
        argDouble0Hits = 0
        argDouble1Hits = 0
        argTriple2Hits = 0
        argQuad3Hits = 0
        returnIntHits = 0
        returnLongHits = 0
        returnStringHits = 0
        returnBoolHits = 0
    }
}
