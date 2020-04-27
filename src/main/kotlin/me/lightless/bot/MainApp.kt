package me.lightless.bot

import kotlinx.coroutines.runBlocking
import me.lightless.bot.config.Config
import me.lightless.bot.config.ConfigParser
import net.mamoe.mirai.Bot
import net.mamoe.mirai.alsoLogin
import net.mamoe.mirai.event.events.BotOfflineEvent
import net.mamoe.mirai.event.subscribeAlways
import net.mamoe.mirai.event.subscribeGroupMessages
import org.jetbrains.exposed.sql.Database
import org.slf4j.LoggerFactory

private const val TAG = "[MainApp]"

class BotContext {
    companion object {
        var botConfig: Config? = null
        var bot: Bot? = null
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

    // 连接数据库
    val dbName = BotContext.botConfig!!.dbName
    Database.connect("jdbc:sqlite:$dbName", "org.sqlite.JDBC")

    // 启动 Bot 实例
    val bot = Bot(
        BotContext.botConfig!!.qqNumber,
        BotContext.botConfig!!.qqPassword
    ).alsoLogin()

    // 把 bot instance 存起来
    BotContext.bot = bot

    // 开启消息分发
    val dispatcher = Dispatcher()
    bot.subscribeGroupMessages {
        always {
            dispatcher.onGroupMessage(this)
        }
    }
    bot.subscribeAlways<BotOfflineEvent.Force> {
        bot.login()
    }

    // 初始化 timer
    // debug 用，先直接初始化
//    logger.debug("starting init timer...")
//    val drinkTimer = DrinkTimer(bot)
//    drinkTimer.process()
//    logger.info("timers init done.")

    // 防止 bot 过快退出
    bot.join()
}