package top.maplex.incisiontest.cases

import taboolib.module.incision.annotation.Lead
import taboolib.module.incision.annotation.Surgeon
import taboolib.module.incision.api.Theatre

/**
 * @Surgeon(priority) 极值 + 三 Surgeon 跨 object 排序。
 *
 * 极值：使用接近 Int.MAX_VALUE / Int.MIN_VALUE 的数值（保留 -1000 留余地避免在加法上溢/下溢）。
 *
 * 三 Surgeon：[Pri100Surgeon] / [Pri0Surgeon] / [PriNeg100Surgeon] 共享 threeSurgeonTarget。
 */

object SurgeonPriorityExtremeShared {
    val threeLog: MutableList<String> = java.util.Collections.synchronizedList(mutableListOf<String>())
    fun reset() { threeLog.clear() }
}

private const val EFIX = "top.maplex.incisiontest.fixture.SurgeonPriorityExtremeFixture"

@Surgeon(priority = Int.MAX_VALUE - 1000)
object SurgeonPriorityMaxCases {
    @Volatile var hits = 0

    @Lead(scope = "method:$EFIX#extremeMaxTarget()int")
    fun adviceMax(theatre: Theatre) { hits++ }

    fun reset() { hits = 0 }
}

@Surgeon(priority = Int.MIN_VALUE + 1000)
object SurgeonPriorityMinCases {
    @Volatile var hits = 0

    @Lead(scope = "method:$EFIX#extremeMinTarget()int")
    fun adviceMin(theatre: Theatre) { hits++ }

    fun reset() { hits = 0 }
}

@Surgeon(priority = 100)
object Pri100Surgeon {
    @Lead(scope = "method:$EFIX#threeSurgeonTarget()java.util.List")
    fun pri100(theatre: Theatre) {
        SurgeonPriorityExtremeShared.threeLog += "p100"
    }
}

@Surgeon(priority = 0)
object Pri0Surgeon {
    @Lead(scope = "method:$EFIX#threeSurgeonTarget()java.util.List")
    fun pri0(theatre: Theatre) {
        SurgeonPriorityExtremeShared.threeLog += "p0"
    }
}

@Surgeon(priority = -100)
object PriNeg100Surgeon {
    @Lead(scope = "method:$EFIX#threeSurgeonTarget()java.util.List")
    fun priNeg100(theatre: Theatre) {
        SurgeonPriorityExtremeShared.threeLog += "p-100"
    }
}
