package me.lightless.bot.timers.impl

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.lightless.bot.BotContext
import me.lightless.bot.timers.ITimer
import net.mamoe.mirai.message.data.buildMessageChain
import org.joda.time.DateTime
import org.joda.time.DateTimeConstants
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class DrinkTimer : ITimer {

    val logger: Logger = LoggerFactory.getLogger(javaClass)

    override val name: String = "DrinkTimer"
    override val period: Long = 60 * 1000   // 每分钟运行一次

    override suspend fun process() {

        val groupNumber = BotContext.botConfig?.allowedGroups ?: listOf()
        logger.debug("groupNumber: $groupNumber")
        val weekend = listOf(DateTimeConstants.SUNDAY, DateTimeConstants.SATURDAY)

        while (true) {

            val datetime = DateTime()
            val h = datetime.hourOfDay
            val m = datetime.minuteOfHour
//            logger.debug("h: $h, m: $m")

            // debug code
//            if (h == 13 && m == 3) {
//                sendDrinkMsg("【喝水提醒小助手】\n该喝水了哦~", groupNumber)
//                delay(1000 * 30)
//                continue
//            }

            // 跳过周末
            if (datetime.dayOfWeek in weekend) {
                delay(1000 * 3600 * 2)
                continue
            }

            if (h < 10 || h > 18 || h == 13) {
                delay(1000 * 65)
                continue
            }

            if (m == 0) {
                if (h == 18) {
                    sendDrinkMsg("【喝水提醒小助手】\n该喝水了哦~\n晚上也要多~喝~水~哦~", groupNumber)
                } else {
                    sendDrinkMsg("【喝水提醒小助手】\n该喝水了哦~", groupNumber)
                }
                delay(1000 * 65)
                continue
            }

            delay(1000 * 60)

        }
    }

    private suspend fun sendDrinkMsg(msg: String, groupNumber: List<Long>) {

        val bot = BotContext.bot

        groupNumber.forEach {
            bot?.launch {
                bot.getGroup(it).sendMessage(buildMessageChain {
                    add(msg)
                })
            } ?: logger.error("bot instance is null!")
        }
    }

}