package top.maplex.incisiontest.cases

import taboolib.module.incision.annotation.MatchMode

import taboolib.module.incision.annotation.SelectorKind

import taboolib.module.incision.annotation.Selector

import taboolib.module.incision.annotation.Pointcut

import taboolib.module.incision.annotation.Lead
import taboolib.module.incision.annotation.Operation
import taboolib.module.incision.annotation.Splice
import taboolib.module.incision.annotation.Surgeon
import taboolib.module.incision.api.Theatre

/**
 * @Operation 与 @Surgeon 优先级覆盖测试。
 *
 * 覆盖项：
 * - id      → 用自定义 id 后缀生成 Suture
 * - enabled → 默认禁用，需手动 resume
 * - priority→ 多 advice 执行顺序
 * - @Surgeon(priority) 默认值 vs @Operation(priority) 覆盖
 */
@Surgeon(priority = 5)
object SurgeonOperationCases {

    private const val FIX = "top.maplex.incisiontest.fixture.SurgeonOperationTargetFixture"

    @Volatile var customIdHits = 0
    @Volatile var disabledHits = 0
    val orderLog: MutableList<String> = java.util.Collections.synchronizedList(mutableListOf<String>())
    val surgeonDefaultLog: MutableList<String> = java.util.Collections.synchronizedList(mutableListOf<String>())
    val multiSurgeonLog: MutableList<String> = java.util.Collections.synchronizedList(mutableListOf<String>())

    /** @Operation(id="custom-operation-id") —— 用自定义 id 覆盖方法名 */
    @Operation(id = "custom-operation-id")
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "customIdTarget", descriptor = "()I")]))
    fun onCustomId(theatre: Theatre) {
        customIdHits++
    }

    /** @Operation(enabled=false) —— 默认禁用，不应触发，除非显式 resume */
    @Operation(enabled = false)
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "disabledTarget", descriptor = "()I")]))
    fun disabledAdvice(theatre: Theatre) {
        disabledHits++
    }

    /** 高优先级 —— 先执行 */
    @Operation(priority = 100)
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "priorityTarget", descriptor = "()Ljava/util/List;")]))
    fun highPriority(theatre: Theatre) {
        orderLog += "high"
    }

    /** 低优先级 —— 后执行 */
    @Operation(priority = 1)
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "priorityTarget", descriptor = "()Ljava/util/List;")]))
    fun lowPriority(theatre: Theatre) {
        orderLog += "low"
    }

    // ----------------------------------------------------------------------
    // @Surgeon(priority=5) 默认值 vs @Operation(priority) 方法级覆盖
    // ----------------------------------------------------------------------

    /** 不带 @Operation —— 沿用 @Surgeon(priority=5) 默认值 */
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "surgeonDefaultTarget", descriptor = "()Ljava/util/List;")]))
    fun surgeonDefaultAdvice(theatre: Theatre) {
        surgeonDefaultLog += "surgeon-default-5"
    }

    /** 带 @Operation(priority=20) —— 应覆盖 @Surgeon(priority=5)，先于上面执行 */
    @Operation(priority = 20)
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "surgeonDefaultTarget", descriptor = "()Ljava/util/List;")]))
    fun surgeonOverrideAdvice(theatre: Theatre) {
        surgeonDefaultLog += "operation-override-20"
    }

    /** 本类（@Surgeon priority=5）的 advice —— 与外部 @Surgeon(priority=50) 比较 */
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "multiSurgeonTarget", descriptor = "()Ljava/util/List;")]))
    fun multiSurgeonLowerSide(theatre: Theatre) {
        multiSurgeonLog += "low-surgeon-5"
    }

    fun reset() {
        customIdHits = 0
        disabledHits = 0
        orderLog.clear()
        surgeonDefaultLog.clear()
        multiSurgeonLog.clear()
        SurgeonOperationHigherCases.reset()
    }
}

/**
 * 与 [SurgeonOperationCases] 配对，验证 **不同 @Surgeon priority** 之间的执行顺序。
 *
 * 本对象 priority=50，对手 priority=5 —— 期望本对象先执行。
 */
@Surgeon(priority = 50)
object SurgeonOperationHigherCases {

    private const val FIX = "top.maplex.incisiontest.fixture.SurgeonOperationTargetFixture"

    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "multiSurgeonTarget", descriptor = "()Ljava/util/List;")]))
    fun higherSurgeonAdvice(theatre: Theatre) {
        SurgeonOperationCases.multiSurgeonLog += "high-surgeon-50"
    }

    fun reset() {
        // 共享状态在 SurgeonOperationCases.reset() 内已清理
    }
}
