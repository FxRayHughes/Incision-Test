package top.maplex.incisiontest.cases

import taboolib.module.incision.annotation.Lead
import taboolib.module.incision.annotation.Operation
import taboolib.module.incision.annotation.Surgeon
import taboolib.module.incision.api.Theatre

/**
 * @Operation(enabled × priority) 8 格矩阵 + 同 priority 稳定顺序 + disable/resume/suspend 循环。
 *
 * 8 格：enabled ∈ {true, false} × priority ∈ {-10, 0, 5, 100}
 * 命名规则：cellT/F + Pri{N}
 */
@Surgeon
object OperationEnabledPriorityCases {

    private const val FIX = "top.maplex.incisiontest.fixture.OperationEnabledPriorityFixture"

    @Volatile var tNeg10Hits = 0
    @Volatile var t0Hits = 0
    @Volatile var t5Hits = 0
    @Volatile var t100Hits = 0
    @Volatile var fNeg10Hits = 0
    @Volatile var f0Hits = 0
    @Volatile var f5Hits = 0
    @Volatile var f100Hits = 0
    @Volatile var cycleHits = 0
    val samePriorityLog: MutableList<String> = java.util.Collections.synchronizedList(mutableListOf<String>())

    // ---- enabled=true 行 ----
    @Operation(enabled = true, priority = -10)
    @Lead(scope = "method:$FIX#cellTPriNeg10()int")
    fun aTNeg10(theatre: Theatre) { tNeg10Hits++ }

    @Operation(enabled = true, priority = 0)
    @Lead(scope = "method:$FIX#cellTPri0()int")
    fun aT0(theatre: Theatre) { t0Hits++ }

    @Operation(enabled = true, priority = 5)
    @Lead(scope = "method:$FIX#cellTPri5()int")
    fun aT5(theatre: Theatre) { t5Hits++ }

    @Operation(enabled = true, priority = 100)
    @Lead(scope = "method:$FIX#cellTPri100()int")
    fun aT100(theatre: Theatre) { t100Hits++ }

    // ---- enabled=false 行（默认禁用） ----
    @Operation(enabled = false, priority = -10)
    @Lead(scope = "method:$FIX#cellFPriNeg10()int")
    fun aFNeg10(theatre: Theatre) { fNeg10Hits++ }

    @Operation(enabled = false, priority = 0)
    @Lead(scope = "method:$FIX#cellFPri0()int")
    fun aF0(theatre: Theatre) { f0Hits++ }

    @Operation(enabled = false, priority = 5)
    @Lead(scope = "method:$FIX#cellFPri5()int")
    fun aF5(theatre: Theatre) { f5Hits++ }

    @Operation(enabled = false, priority = 100)
    @Lead(scope = "method:$FIX#cellFPri100()int")
    fun aF100(theatre: Theatre) { f100Hits++ }

    // ---- 同 priority 稳定顺序：两条 priority=42 advice ----
    @Operation(priority = 42, id = "same-pri-first")
    @Lead(scope = "method:$FIX#samePriorityTarget()java.util.List")
    fun samePriorityFirst(theatre: Theatre) { samePriorityLog += "first" }

    @Operation(priority = 42, id = "same-pri-second")
    @Lead(scope = "method:$FIX#samePriorityTarget()java.util.List")
    fun samePrioritySecond(theatre: Theatre) { samePriorityLog += "second" }

    // ---- disable→resume→suspend 循环测试目标 ----
    @Operation(enabled = false, id = "cycle-advice")
    @Lead(scope = "method:$FIX#cycleTarget()int")
    fun cycleAdvice(theatre: Theatre) { cycleHits++ }

    fun reset() {
        tNeg10Hits = 0; t0Hits = 0; t5Hits = 0; t100Hits = 0
        fNeg10Hits = 0; f0Hits = 0; f5Hits = 0; f100Hits = 0
        cycleHits = 0
        samePriorityLog.clear()
    }
}
