package top.maplex.incisiontest.cases

import org.bukkit.command.CommandSender
import taboolib.module.incision.annotation.Lead
import taboolib.module.incision.annotation.Surgeon
import taboolib.module.incision.annotation.Trail
import taboolib.module.incision.api.Theatre
import taboolib.module.incision.api.callMethod

@Surgeon
object EssentialsListHook {

    private const val TARGET = "method:com.earth2me.essentials.commands.Commandlist#run(*)"

    private fun getSender(theatre: Theatre): CommandSender? {
        val source = theatre.arg<Any>(1) ?: return null
        return source.callMethod<CommandSender>("getSender")
    }

//    @Lead(scope = TARGET)
//    fun beforeList(theatre: Theatre) {
//        getSender(theatre)?.sendMessage("§a[Incision] 即将执行 /list 命令...")
//    }
//
//    @Trail(scope = TARGET)
//    fun afterList(theatre: Theatre) {
//        getSender(theatre)?.sendMessage("§a[Incision] /list 命令执行完毕！")
//    }
}
