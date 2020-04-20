package me.lightless.bot.pipelines

import net.mamoe.mirai.message.GroupMessage
import org.slf4j.Logger

interface IPipeline {

    val logger: Logger
    val callNext: Boolean
    val order: Int
    val name: String

    suspend fun process(groupMessage: GroupMessage)
}