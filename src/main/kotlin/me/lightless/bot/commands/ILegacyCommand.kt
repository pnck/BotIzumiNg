package me.lightless.bot.commands

import me.lightless.bot.BotContext
import net.mamoe.mirai.message.GroupMessage
import org.slf4j.Logger

interface ILegacyCommand :ICommand {
    val logger: Logger
    val command: List<String>

    suspend fun handler(cmd: String, groupMessage: GroupMessage)

    fun checkRole(qq: Long): Boolean

    fun isAdmin(qq: Long): Boolean {
        return try {
            BotContext.botConfig!!.adminQQ.contains(qq)
        } catch (e: Exception) {
            logger.error("isAdmin failed, return false...")
            false
        }
    }
}