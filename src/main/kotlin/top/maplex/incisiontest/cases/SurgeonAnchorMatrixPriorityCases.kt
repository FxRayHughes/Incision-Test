package top.maplex.incisiontest.cases

import taboolib.module.incision.annotation.Lead
import taboolib.module.incision.annotation.Operation
import taboolib.module.incision.annotation.Surgeon
import taboolib.module.incision.api.Theatre

/**
 * Task #13 第二批 —— priority 网格。
 *
 * @Surgeon(priority) 是类级默认优先级，单 object 只能取一个值，因此为 0 / 正 / 负三种
 * 各开一个 object 持有者。@Operation(priority) 可在方法级覆盖，全部挂在同一目标方法上
 * 验证执行顺序写入到 [orderLog]。
 */
@Surgeon(priority = 0)
object SurgeonAnchorMatrixPriorityDefault {

    private const val FIX = "top.maplex.incisiontest.fixture.AnchorMatrixFixture_Priority"

    @JvmStatic
    val orderLog: MutableList<String> = mutableListOf()

    @Volatile var defaultHits = 0

    @Lead(scope = "method:$FIX#prioritySurgeonTarget()java.lang.String")
    fun defaultPriority(theatre: Theatre) {
        defaultHits++
        orderLog += "default(0)"
    }

    fun reset() {
        defaultHits = 0
        orderLog.clear()
    }
}

/** @Surgeon(priority=100) —— 正数 */
@Surgeon(priority = 100)
object SurgeonAnchorMatrixPriorityHigh {

    private const val FIX = "top.maplex.incisiontest.fixture.AnchorMatrixFixture_Priority"

    @Volatile var highHits = 0

    @Lead(scope = "method:$FIX#prioritySurgeonTarget()java.lang.String")
    fun highPriority(theatre: Theatre) {
        highHits++
        SurgeonAnchorMatrixPriorityDefault.orderLog += "high(100)"
    }

    fun reset() { highHits = 0 }
}

/** @Surgeon(priority=-50) —— 负数 */
@Surgeon(priority = -50)
object SurgeonAnchorMatrixPriorityLow {

    private const val FIX = "top.maplex.incisiontest.fixture.AnchorMatrixFixture_Priority"

    @Volatile var lowHits = 0

    @Lead(scope = "method:$FIX#prioritySurgeonTarget()java.lang.String")
    fun lowPriority(theatre: Theatre) {
        lowHits++
        SurgeonAnchorMatrixPriorityDefault.orderLog += "low(-50)"
    }

    fun reset() { lowHits = 0 }
}

/** @Operation(priority) —— 在同一目标方法上挂多条 advice 验证方法级优先级覆盖 */
@Surgeon
object SurgeonAnchorMatrixPriorityOperation {

    private const val FIX = "top.maplex.incisiontest.fixture.AnchorMatrixFixture_Priority"

    @JvmStatic
    val orderLog: MutableList<String> = mutableListOf()

    @Volatile var pos200Hits = 0
    @Volatile var pos50Hits = 0
    @Volatile var neg10Hits = 0
    @Volatile var defaultHits = 0

    @Operation(priority = 200)
    @Lead(scope = "method:$FIX#priorityOperationTarget()java.lang.String")
    fun opPriority200(theatre: Theatre) {
        pos200Hits++
        orderLog += "op-200"
    }

    @Operation(priority = 50)
    @Lead(scope = "method:$FIX#priorityOperationTarget()java.lang.String")
    fun opPriority50(theatre: Theatre) {
        pos50Hits++
        orderLog += "op-50"
    }

    @Operation(priority = -10)
    @Lead(scope = "method:$FIX#priorityOperationTarget()java.lang.String")
    fun opPriorityNeg10(theatre: Theatre) {
        neg10Hits++
        orderLog += "op-neg10"
    }

    @Lead(scope = "method:$FIX#priorityOperationTarget()java.lang.String")
    fun opPriorityDefault(theatre: Theatre) {
        defaultHits++
        orderLog += "op-default"
    }

    fun reset() {
        pos200Hits = 0
        pos50Hits = 0
        neg10Hits = 0
        defaultHits = 0
        orderLog.clear()
    }
}
