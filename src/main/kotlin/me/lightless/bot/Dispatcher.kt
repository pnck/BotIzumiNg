package me.lightless.bot

import me.lightless.bot.handler.CommandHandler
import me.lightless.bot.handler.MessageHandler
import net.mamoe.mirai.message.GroupMessage
import org.slf4j.LoggerFactory

class Dispatcher {

    private val logger = LoggerFactory.getLogger(javaClass)
    private var allowedGroups: List<Long>
    private val commandHandler = CommandHandler()
    private val messageHandler = MessageHandler()

    init {
        logger.info("Dispatcher start!")
        allowedGroups = BotContext.botConfig!!.allowedGroups
    }

    private fun isCommand(message: String): Boolean = message.startsWith("/")

    suspend fun onGroupMessage(groupMessage: GroupMessage) {

        val group = groupMessage.group
        val message = groupMessage.message
        val sender = groupMessage.sender

        if (group.id !in allowedGroups) {
            return
        }

        logger.debug("group: $group, sender: $sender, msg: $message")

        when {
            isCommand(message.contentToString()) -> commandHandler.dispatcher(groupMessage)
            else -> messageHandler.dispatcher(groupMessage)
        }

    }

    fun onPrivateMessage() {
        TODO()
    }

}