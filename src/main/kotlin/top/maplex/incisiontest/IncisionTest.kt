package top.maplex.incisiontest

import taboolib.common.platform.Plugin
import taboolib.common.platform.function.console
import taboolib.common.platform.function.submit

object IncisionTest : Plugin() {

    override fun onActive() {
        console().sendMessage("§e[IncisionTest] onActive, scheduling auto-run in 2s ...")
        submit(delay = 40L) {
            console().sendMessage("§e[IncisionTest] running full suite ...")
            TestRunner.runAll(console())
        }
    }
}
