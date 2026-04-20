package top.maplex.incisiontest

import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand

@CommandHeader(name = "incisiontest", aliases = ["itest"], permission = "incision.test")
object TestCommand {

    @CommandBody
    val main = mainCommand {
        execute<ProxyCommandSender> { sender, _, _ ->
            sender.sendMessage("§e/itest all | list | run <name>")
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

    @CommandBody
    val run = subCommand {
        dynamic("name") {
            suggestion<ProxyCommandSender> { _, _ -> AllCases.entries.map { it.first } }
            execute<ProxyCommandSender> { sender, ctx, _ -> TestRunner.runOne(sender, ctx["name"]) }
        }
    }
}
