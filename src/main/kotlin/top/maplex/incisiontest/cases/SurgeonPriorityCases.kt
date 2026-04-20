package top.maplex.incisiontest.cases

import taboolib.module.incision.annotation.Lead
import taboolib.module.incision.annotation.Surgeon
import taboolib.module.incision.api.Theatre

/**
 * @Surgeon(priority) 默认 / 0 / 正 / 负 四档协作。
 *
 * 共享目标：[top.maplex.incisiontest.fixture.SurgeonPriorityFixture#pSharedTarget]
 *
 * 期望执行顺序（priority 大者先）：
 *   PriPositive(30) → PriDefault(0) ≈ PriZero(0) → PriNegative(-10)
 *
 * 共享日志列表统一存放在 [SurgeonPriorityShared.log] 里以便断言。
 */
object SurgeonPriorityShared {
    val log: MutableList<String> = java.util.Collections.synchronizedList(mutableListOf<String>())
    fun reset() { log.clear() }
}

private const val PFIX = "top.maplex.incisiontest.fixture.SurgeonPriorityFixture"

/** 默认 priority —— 不显式写 priority 字段 */
@Surgeon
object SurgeonPriorityDefaultCases {
    @Lead(scope = "method:$PFIX#pSharedTarget()java.util.List")
    fun adviceDefault(theatre: Theatre) {
        SurgeonPriorityShared.log += "default"
    }
}

/** 显式 priority = 0 */
@Surgeon(priority = 0)
object SurgeonPriorityZeroCases {
    @Lead(scope = "method:$PFIX#pSharedTarget()java.util.List")
    fun adviceZero(theatre: Theatre) {
        SurgeonPriorityShared.log += "zero"
    }
}

/** 正 priority = 30 */
@Surgeon(priority = 30)
object SurgeonPriorityPositiveCases {
    @Lead(scope = "method:$PFIX#pSharedTarget()java.util.List")
    fun advicePositive(theatre: Theatre) {
        SurgeonPriorityShared.log += "positive-30"
    }
}

/** 负 priority = -10 */
@Surgeon(priority = -10)
object SurgeonPriorityNegativeCases {
    @Lead(scope = "method:$PFIX#pSharedTarget()java.util.List")
    fun adviceNegative(theatre: Theatre) {
        SurgeonPriorityShared.log += "negative-10"
    }
}
