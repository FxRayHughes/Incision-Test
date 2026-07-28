package top.maplex.incisiontest

import taboolib.common.platform.Plugin
import taboolib.common.platform.function.console
import taboolib.common.platform.function.submit

object IncisionTest : Plugin() {

    override fun onActive() {
        // 性能矩阵必须从未经全量回归与 reload 污染的干净进程开始；常规测试启动仍默认自动执行。
        if (!System.getProperty("incision.test.autoRun", "true").toBoolean()) {
            console().sendMessage("§e[IncisionTest] auto-run disabled for isolated benchmark process")
            return
        }
        console().sendMessage("§e[IncisionTest] onActive, scheduling auto-run in 2s ...")
        submit(delay = 40L) {
            console().sendMessage("§e[IncisionTest] running full suite ...")
            TestRunner.runAll(console())
        }
    }
}
