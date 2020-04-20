package me.lightless.bot.pipelines.impl

import me.lightless.bot.pipelines.IPipeline
import net.mamoe.mirai.message.GroupMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ThumbPipeline : IPipeline {
    override val logger: Logger = LoggerFactory.getLogger(javaClass)
    override val callNext: Boolean = true
    override val order: Int = 50
    override val name: String = "Thumb"

    override suspend fun process(groupMessage: GroupMessage) {

        val msg = groupMessage.message.contentToString()
        logger.debug("msg: $msg")



    }


}