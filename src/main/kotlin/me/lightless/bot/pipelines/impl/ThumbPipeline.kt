package me.lightless.bot.pipelines.impl

import me.lightless.bot.pipelines.IPipeline
import net.mamoe.mirai.message.GroupMessage
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.Face
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.buildMessageChain
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ThumbPipeline : IPipeline {
    override val logger: Logger = LoggerFactory.getLogger(javaClass)
    override val callNext: Boolean = true
    override val order: Int = 50
    override val name: String = "Thumb"

    /*
            self.blacklist = [
                "&#91;强&#93;",
                "[CQ:face,id=76]",
                b"\xf0\x9f\x91\x8d".decode("UTF-8"),
                "[CQ:emoji,id=128077]",
                "4",
        ]
     */

    override suspend fun process(groupMessage: GroupMessage) {

        var thumbCnt = 0

        groupMessage.message.spliterator().forEachRemaining {
            logger.debug("message part: $it -> ${it.javaClass}")
            when (it) {
                is Face -> {
                    if (it.id == 76) {
                        thumbCnt += 1
                    }
                }
                is PlainText -> {
                    var index = it.contentToString().indexOf("\uD83D\uDC4D")
                    while (index != -1) {
                        thumbCnt += 1
                        index = it.contentToString().indexOf("\uD83D\uDC4D", index + 2)
                    }
                }
            }
        }

        logger.debug("thumb count: $thumbCnt")
        if (thumbCnt != 0) {
            groupMessage.group.sendMessage(buildMessageChain {
                add(At(groupMessage.sender))
                add("\n嘤嘤嘤，发现大拇指了呢，呐，大拇指什么的是不可以的呢！")
            })

            // TODO: 禁言
        }

    }


}