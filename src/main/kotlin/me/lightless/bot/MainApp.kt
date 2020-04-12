package me.lightless.bot

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.lightless.bot.config.Config
import me.lightless.bot.config.ConfigParser
import net.mamoe.mirai.Bot
import net.mamoe.mirai.alsoLogin
import net.mamoe.mirai.event.events.BotOfflineEvent
import net.mamoe.mirai.event.subscribe
import net.mamoe.mirai.event.subscribeAlways
import net.mamoe.mirai.event.subscribeGroupMessages
import org.slf4j.LoggerFactory

private const val TAG = "[MainApp]"

class BotContext {
    companion object {
        var botConfig: Config? = null
    }
}


fun main(): Unit = runBlocking {
    val logger = LoggerFactory.getLogger("main")
    logger.info("$TAG Bot Izumi start start.")

    // 加载配置文件
    logger.info("$TAG loading config file...")
    val parser = ConfigParser("config.yml")
    BotContext.botConfig = parser.parse()
    if (BotContext.botConfig == null) {
        logger.error("Can't load config file, exit...")
        return@runBlocking
    }
    logger.info("$TAG success load config file...")

    // 启动 Bot 实例
    val bot = Bot(
        BotContext.botConfig!!.qqNumber,
        BotContext.botConfig!!.qqPassword
    ).alsoLogin()

    //
    val dispatcher = Dispatcher()

    bot.subscribeGroupMessages {
        always {
            dispatcher.onGroupMessage(this)
        }
    }

    bot.subscribeAlways<BotOfflineEvent.Force> {
        bot.login()
    }

    bot.join()
}