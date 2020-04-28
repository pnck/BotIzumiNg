package me.lightless.bot.timers

import me.lightless.bot.utils.BotClazzLoader
import org.slf4j.LoggerFactory
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

        // 启动所有的timer
        timerImpls.forEach {
            it.process()
        }
    }
}
