package me.lightless.bot.timers.impl

import kotlinx.coroutines.*
import me.lightless.bot.timers.ITimer
import net.mamoe.mirai.Bot
import net.mamoe.mirai.message.data.buildMessageChain
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.concurrent.schedule


class DrinkTimer(var bot: Bot) : ITimer {

    val logger: Logger = LoggerFactory.getLogger(javaClass)

    override val name: String = "DrinkTimer"
    override val period: Long = 60 * 1000   // 每分钟运行一次
    override val groupNumber: List<Long> = listOf(574255110)

    override suspend fun process() {
        Timer().schedule(0, 1000) {
            groupNumber.forEach {
                bot.launch {
                    bot.getGroup(it).sendMessage(buildMessageChain {
                        add("【喝水提醒小助手】\n该喝水了哦~")
                    })
                }
            }
        }
    }


}