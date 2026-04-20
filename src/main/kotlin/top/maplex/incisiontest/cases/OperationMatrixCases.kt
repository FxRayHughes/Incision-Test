package top.maplex.incisiontest.cases

import taboolib.module.incision.annotation.Lead
import taboolib.module.incision.annotation.Operation
import taboolib.module.incision.annotation.Surgeon
import taboolib.module.incision.api.Theatre

/**
 * @Operation 三维组合矩阵 (id × enabled × priority)。
 *
 * 目标：[top.maplex.incisiontest.fixture.OperationMatrixFixture]
 *
 * 全组合点（共 9 个 + 4 个排序 advice）：
 * | # | id          | enabled | priority |
 * |---|-------------|---------|----------|
 * | 1 | ""          | true    | 0 (默认) |
 * | 2 | "m2-cust"   | true    | 0        |
 * | 3 | ""          | false   | 0        |
 * | 4 | ""          | true    | 10       |
 * | 5 | ""          | true    | -5       |
 * | 6 | "m6-cust"   | true    | 20       |
 * | 7 | "m7-cust"   | false   | 0        |
 * | 8 | "m8-cust"   | false   | 99       |
 * | 9 | ""          | true    | 0 (显式) |
 *
 * 排序矩阵：mOrderTarget 上挂 priority = +50 / +10 / 0 / -10 四条 advice。
 */
@Surgeon
object OperationMatrixCases {

    private const val FIX = "top.maplex.incisiontest.fixture.OperationMatrixFixture"

    @Volatile var m1Hits = 0
    @Volatile var m2Hits = 0
    @Volatile var m3Hits = 0
    @Volatile var m4Hits = 0
    @Volatile var m5Hits = 0
    @Volatile var m6Hits = 0
    @Volatile var m7Hits = 0
    @Volatile var m8Hits = 0
    @Volatile var m9Hits = 0
    val orderLog: MutableList<String> = java.util.Collections.synchronizedList(mutableListOf<String>())

    // ---- 1: 完全默认 (基线) ----
    @Lead(scope = "method:$FIX#m1Default()int")
    fun adviceM1(theatre: Theatre) { m1Hits++ }

    // ---- 2: 仅自定义 id ----
    @Operation(id = "m2-cust")
    @Lead(scope = "method:$FIX#m2CustomId()int")
    fun adviceM2(theatre: Theatre) { m2Hits++ }

    // ---- 3: 仅 enabled=false ----
    @Operation(enabled = false)
    @Lead(scope = "method:$FIX#m3DisabledNoId()int")
    fun adviceM3(theatre: Theatre) { m3Hits++ }

    // ---- 4: 仅 priority 正数 ----
    @Operation(priority = 10)
    @Lead(scope = "method:$FIX#m4PriorityPositive()int")
    fun adviceM4(theatre: Theatre) { m4Hits++ }

    // ---- 5: 仅 priority 负数 ----
    @Operation(priority = -5)
    @Lead(scope = "method:$FIX#m5PriorityNegative()int")
    fun adviceM5(theatre: Theatre) { m5Hits++ }

    // ---- 6: id + 正 priority ----
    @Operation(id = "m6-cust", priority = 20)
    @Lead(scope = "method:$FIX#m6IdAndPriority()int")
    fun adviceM6(theatre: Theatre) { m6Hits++ }

    // ---- 7: id + disabled ----
    @Operation(id = "m7-cust", enabled = false)
    @Lead(scope = "method:$FIX#m7IdAndDisabled()int")
    fun adviceM7(theatre: Theatre) { m7Hits++ }

    // ---- 8: id + disabled + 高 priority （三维齐全） ----
    @Operation(id = "m8-cust", enabled = false, priority = 99)
    @Lead(scope = "method:$FIX#m8AllThree()int")
    fun adviceM8(theatre: Theatre) { m8Hits++ }

    // ---- 9: 显式 priority=0 + 显式 enabled=true (与默认等价但写明) ----
    @Operation(priority = 0, enabled = true)
    @Lead(scope = "method:$FIX#m9PriorityZeroExplicit()int")
    fun adviceM9(theatre: Theatre) { m9Hits++ }

    // ---- 排序矩阵：高 / 中正 / 0 / 负 ----
    @Operation(priority = 50)
    @Lead(scope = "method:$FIX#mOrderTarget()java.util.List")
    fun orderHigh(theatre: Theatre) { orderLog += "p+50" }

    @Operation(priority = 10)
    @Lead(scope = "method:$FIX#mOrderTarget()java.util.List")
    fun orderMid(theatre: Theatre) { orderLog += "p+10" }

    @Operation(priority = 0)
    @Lead(scope = "method:$FIX#mOrderTarget()java.util.List")
    fun orderZero(theatre: Theatre) { orderLog += "p+0" }

    @Operation(priority = -10)
    @Lead(scope = "method:$FIX#mOrderTarget()java.util.List")
    fun orderLow(theatre: Theatre) { orderLog += "p-10" }

    fun reset() {
        m1Hits = 0; m2Hits = 0; m3Hits = 0; m4Hits = 0; m5Hits = 0
        m6Hits = 0; m7Hits = 0; m8Hits = 0; m9Hits = 0
        orderLog.clear()
    }
}
