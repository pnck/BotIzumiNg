package me.lightless.bot.handler

import me.lightless.bot.pipelines.IPipeline
import me.lightless.bot.utils.BotClazzLoader
import net.mamoe.mirai.message.GroupMessage
import org.slf4j.LoggerFactory
import kotlin.reflect.full.createInstance

class MessageHandler {

    private val logger = LoggerFactory.getLogger(javaClass)

    private val pipelinePackageName = "me.lightless.bot.pipelines.impl"

    var pipelinesImpls: MutableList<IPipeline> = arrayListOf()

    init {
        logger.info("Message handler init!")
        logger.info("start init pipelines...")

        // 获取所有的 pipeline，并且初始化
        val pipelineClassName = BotClazzLoader.load(pipelinePackageName)
        logger.debug("pipeline class name: $pipelineClassName")
        var cnt = 0
        pipelineClassName.forEach {
            try {
                pipelinesImpls.add(Class.forName(it).kotlin.createInstance() as IPipeline)
                cnt += 1
            } catch (e: ClassNotFoundException) {
                logger.error("can't load class: $it")
            }
        }

        // 给pipelines根据order排序
        // order 越大优先级越高
        pipelinesImpls.sortedByDescending { it.order }

        logger.info("Pipelines class count: $cnt, pipeline list: ${pipelinesImpls.joinToString { "${it.name} | ${it.order}" }}")
    }

    suspend fun dispatcher(groupMessage: GroupMessage) {

        pipelinesImpls.forEach {
            it.process(groupMessage)
            if (!it.callNext) {
                logger.debug("abort process pipeline by: ${it.name}")
                return
            }
        }

    }
}