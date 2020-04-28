package me.lightless.bot

import kotlinx.coroutines.runBlocking
import me.lightless.bot.config.Config
import me.lightless.bot.config.ConfigParser
import me.lightless.bot.timers.TimerLoader
import net.mamoe.mirai.Bot
import net.mamoe.mirai.alsoLogin
import net.mamoe.mirai.event.events.BotOfflineEvent
import net.mamoe.mirai.event.subscribeAlways
import net.mamoe.mirai.event.subscribeGroupMessages
import org.jetbrains.exposed.sql.Database
import org.slf4j.LoggerFactory
import kotlin.system.exitProcess

private const val TAG = "[MainApp]"

class BotContext {
    companion object {
        // config
        var botConfig: Config? = null

        // bot 实例
        var bot: Bot? = null

        // 当前的工作路径
        var cwd: String? = null
    }
}


fun main(): Unit = runBlocking {
    val logger = LoggerFactory.getLogger("main")
    logger.info("$TAG Bot Izumi start start.")

    // 获取当前的工作路径
    val cwd: String = System.getProperty("user.dir")
    BotContext.cwd = cwd
    logger.info("Get cwd: $cwd")

    // 加载配置文件
    logger.debug("$TAG loading config file...")
    val parser = ConfigParser("izumi-config.yml")
    BotContext.botConfig = parser.parseExternalConfig()
    if (BotContext.botConfig == null) {
        logger.error("Can't load config file, exit...")
        exitProcess(-1)
    }
    logger.info("$TAG success load config file...")

    // 连接数据库
    val dbName = BotContext.botConfig!!.dbName
    Database.connect("jdbc:sqlite:$dbName", "org.sqlite.JDBC")

    // 启动 Bot 实例
    // 把 bot instance 存起来
    val bot = Bot(
        BotContext.botConfig!!.qqNumber,
        BotContext.botConfig!!.qqPassword
    ).alsoLogin()
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
    logger.debug("starting init timer...")
    TimerLoader.loadAndStartTimer()
    logger.info("timers init done.")

    // 防止 bot 过快退出
    bot.join()
}