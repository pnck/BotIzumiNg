package me.lightless.bot.timers.impl

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.lightless.bot.BotContext
import me.lightless.bot.timers.ITimer
import net.mamoe.mirai.Bot
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.buildMessageChain
import org.jetbrains.exposed.sql.jodatime.CurrentDateTime
import org.joda.time.DateTime
import org.joda.time.DateTimeConstants
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.concurrent.schedule


class DrinkTimer : ITimer {

    val logger: Logger = LoggerFactory.getLogger(javaClass)

    override val name: String = "DrinkTimer"
    override val period: Long = 60 * 1000   // 每分钟运行一次

    override suspend fun process() {

        val bot = BotContext.bot
        val groupNumber = BotContext.botConfig?.allowedGroups ?: listOf()
        logger.debug("groupNumber: $groupNumber")

        val weekend = listOf(DateTimeConstants.SUNDAY, DateTimeConstants.SATURDAY)

        val task = object: TimerTask() {
            override fun run() {

                val datetime = DateTime()
                // 周末不提醒
                if (datetime.dayOfWeek in weekend) {
                    Thread.sleep(1000 * 3600)
                    return
                }

                val h = datetime.hourOfDay
                val m = datetime.minuteOfHour
                logger.debug("h: $h, m: $m")




            }
        }
        Timer().schedule(task, 0, period)

//        Timer().schedule(0, period) {
//
//            val datetime = DateTime()
//            if (datetime.dayOfWeek in weekend) {
//                // 结束当前lambda
//            } else {
//                val h = datetime.hourOfDay
//                val m = datetime.minuteOfHour
//
//            }
//
//
////            groupNumber.forEach {
////                bot?.launch {
////                    bot.getGroup(it).sendMessage(buildMessageChain {
////                        add("【喝水提醒小助手】\n该喝水了哦~")
////                    })
////                }
////                    ?: logger.error("bot instance is null!")
////            }
//        }
    }

}