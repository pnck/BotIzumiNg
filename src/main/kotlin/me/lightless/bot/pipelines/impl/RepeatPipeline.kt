package me.lightless.bot.pipelines.impl

import me.lightless.bot.pipelines.IPipeline
import net.mamoe.mirai.contact.mute
import net.mamoe.mirai.message.GroupMessage
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.buildMessageChain
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.random.Random
import kotlin.time.Duration

class RepeatPipeline : IPipeline {
    override val logger: Logger = LoggerFactory.getLogger(javaClass)
    override val callNext: Boolean = true
    override val order: Int = 50
    override val name: String = "Repeat"

    /*
    {
        1111: {"msg": "xxx", "cnt": 1},
        2222: {"msg": "xxx", "cnt": 1},
    }
     */
    private var messageMap = mutableMapOf<Long, MutableMap<String, Any?>>()

    override suspend fun process(groupMessage: GroupMessage) {
        val readableMessage = groupMessage.message.contentToString()
        val groupNumber = groupMessage.group.id

        // 取出对应群组的消息记录
        var map = messageMap[groupNumber]
        if (map == null) {
            map = mutableMapOf("msg" to "", "cnt" to 1)
        }

        // 累加重复消息的次数
        if (map["msg"] == readableMessage) {
            map["cnt"] = map["cnt"] as Int + 1
        } else {
            map["cnt"] = 1
            map["msg"] = readableMessage
        }

        logger.debug("repeat map: $map")

        val repeatCnt: Int = map["cnt"] as Int

        // 开始禁言
        if (repeatCnt == 1 || repeatCnt == 2) {
            return
        } else if (repeatCnt >= 10) {
            groupMessage.sender.mute(10 * 60)
            groupMessage.group.sendMessage(buildMessageChain {
                add(At(groupMessage.sender))
                add("\n您怕不是个复读机吧？\n劝你次根香蕉冷静冷静!")
            })
            return
        } else {
            val prob = 2.0 / (11 - repeatCnt)
            val point = Random.nextFloat()
            logger.info("prob: $prob, point: $point")
            if (point < prob) {
                groupMessage.sender.mute((repeatCnt * repeatCnt / 2) * 60)
                groupMessage.group.sendMessage(buildMessageChain {
                    add(At(groupMessage.sender))
                    add("\n嘤嘤嘤，复读被抓住了呢!")
                })
            }
        }
    }
}