package me.lightless.bot.commands.impl

import me.lightless.bot.commands.ICommand
import net.mamoe.mirai.message.GroupMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MessageTrackerCmd :ICommand {
    override val logger: Logger
        get() = LoggerFactory.getLogger(javaClass)
    override val command: List<String>
        get() = listOf("/messagetracker")

    override suspend fun handler(cmd: String, groupMessage: GroupMessage) {
        TODO("Not yet implemented")
    }

    override fun checkRole(qq: Long): Boolean {
        TODO("Not yet implemented")
    }

}