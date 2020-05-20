import kotlinx.coroutines.runBlocking
import me.lightless.bot.config.Config
import net.mamoe.mirai.Bot
import net.mamoe.mirai.alsoLogin
import org.slf4j.LoggerFactory

// TODO: USE unit test frameworks


@Suppress("unused")
class TestFile {

    fun test() {
        val p = "jar:file:/D:/program/bot-izumi-ng/build/libs/bot-izumi-ng-1.0.0-SNAPSHOT-all.jar!/me/lightless/bot/commands/impl"
        val p2 = "file:/D:/program/bot-izumi-ng/build/libs/bot-izumi-ng-1.0.0-SNAPSHOT-all.jar"

        val x = javaClass.protectionDomain.codeSource.location.path
        println("x: $x")
    }
}

class TestLoginAndEcho constructor(loginQQ: Long, loginPassword: String, reportTo: Long) {
    val logger = LoggerFactory.getLogger("Test")
    val config = Config()

    init {
        config.qqNumber = loginQQ
        config.debugQQ = List<Long>(1) { reportTo }
        config.qqPassword = loginPassword
    }

    suspend fun test() {
        val bot = Bot(
            config.qqNumber,
            config.qqPassword
        ).alsoLogin()
        config.debugQQ.forEach {
            logger.info("echo to $it")
            bot.friends[it].sendMessage("[izumi] it works")
            logger.info("echo to $it done")
        }
    }
}

fun main(args: Array<String>) {
    runBlocking {
        TestLoginAndEcho(args[0].toLong(), args[1], args[2].toLong()).test()
    }
}