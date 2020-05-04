package me.lightless.bot.timers

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import me.lightless.bot.utils.BotClazzLoader
import org.slf4j.LoggerFactory
import java.lang.Exception
import kotlin.reflect.full.createInstance

object TimerLoader {

    private val logger = LoggerFactory.getLogger(javaClass)

    private val timerImpls: MutableList<ITimer> = arrayListOf()

    suspend fun loadAndStartTimer() {
        val timerClasses = BotClazzLoader.load("me.lightless.bot.timers.impl")
        logger.debug("timer classes: $timerClasses")
        var successCnt = 0
        timerClasses.forEach {
            try {
                timerImpls.add(Class.forName(it).kotlin.createInstance() as ITimer)
                successCnt += 1
            } catch (e: ClassNotFoundException) {
                logger.error("Can't load class: $it")
            }
        }
        logger.debug("timers count: $successCnt")

        // 启动所有的timer，每一个timer在一个单独的协程中启动
        timerImpls.forEach {
            GlobalScope.async {
                try {
                    it.process()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
