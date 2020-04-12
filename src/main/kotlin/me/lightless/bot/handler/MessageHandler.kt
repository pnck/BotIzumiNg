package me.lightless.bot.handler

import net.mamoe.mirai.message.GroupMessage
import net.mamoe.mirai.message.data.PlainText

class MessageHandler  {
    suspend fun dispatcher(groupMessage: GroupMessage) {

        val group = groupMessage.group
        group.sendMessage(
            PlainText("收到了! ") + groupMessage.message
        )

    }
}