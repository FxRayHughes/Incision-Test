package top.maplex.incisiontest

import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.function.submit

@CommandHeader(name = "incisiontest", aliases = ["itest"], permission = "incision.test")
object TestCommand {

    @CommandBody
    val main = mainCommand {
        execute<ProxyCommandSender> { sender, _, _ ->
            sender.sendMessage("§e/itest all | list | run <name> | perf | jmh")
            TestRunner.list(sender)
        }
    }

    @CommandBody
    val all = subCommand {
        execute<ProxyCommandSender> { sender, _, _ -> TestRunner.runAll(sender) }
    }

    @CommandBody
    val list = subCommand {
        execute<ProxyCommandSender> { sender, _, _ -> TestRunner.list(sender) }
    }

    /** 性能基准在独立线程运行，避免长热循环阻塞服务端 tick 与 watchdog。 */
    @CommandBody
    val perf = subCommand {
        execute<ProxyCommandSender> { sender, _, _ ->
            submit(async = true) { PerformanceRunner.run(sender) }
        }
    }

    /** JMH 会占用数十秒 CPU 时间，必须在独立线程运行，避免阻塞服务器主线程与 watchdog。 */
    @CommandBody
    val jmh = subCommand {
        execute<ProxyCommandSender> { sender, _, _ ->
            submit(async = true) { JmhPerformanceRunner.run(sender) }
        }
    }

    @CommandBody
    val run = subCommand {
        dynamic("name") {
            suggestion<ProxyCommandSender> { _, _ -> AllCases.entries.map { it.first } }
            execute<ProxyCommandSender> { sender, ctx, _ -> TestRunner.runOne(sender, ctx["name"]) }
        }
    }
}
