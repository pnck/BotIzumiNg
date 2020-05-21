import kotlinx.coroutines.runBlocking
import me.lightless.bot.config.Config
import me.lightless.bot.config.ConfigParser
import net.mamoe.mirai.Bot
import net.mamoe.mirai.alsoLogin
import net.mamoe.mirai.closeAndJoin
import net.mamoe.mirai.message.recallIn
import org.junit.jupiter.api.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class BotTest {

    private val config: Config
    private lateinit var bot: Bot

    init {
        val cfg = ConfigParser("test-config.yml").parseExternalConfig()
        Assertions.assertNotNull(cfg)
        config = cfg!!
    }

    @BeforeAll
    fun setUp() {
        runBlocking {
            bot = Bot(
                config.qqNumber,
                config.qqPassword
            ).alsoLogin()
        }

    }

    @AfterAll
    fun tearDown() {
        runBlocking { bot.closeAndJoin() }
    }

    @Test
    fun testEcho() {
        runBlocking {
            config.debugQQ.forEach {
                val receipt = bot.friends[it].sendMessage("[izumi] it works")
            }
        }
    }
}