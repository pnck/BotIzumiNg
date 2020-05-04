package me.lightless.bot.config

import me.lightless.bot.BotContext
import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import java.nio.file.Files
import java.nio.file.NoSuchFileException
import java.nio.file.Paths
import kotlin.system.exitProcess

class ConfigParser(private val filename: String) {

    private val logger = LoggerFactory.getLogger(javaClass)

    private lateinit var config: Config

    companion object {
        private const val TAG = "[ConfigParser]"
    }

    @Deprecated("Use me.lightless.bot.config.ConfigParser.parseExternalConfig instead.")
    fun parse(): Config {
        val yaml = Yaml(Constructor(Config::class.java))
        val content = javaClass.classLoader.getResource(filename)?.readText()
        logger.debug("$TAG content: $content")

        config = yaml.load(content)
        return config
    }

    fun parseExternalConfig(): Config? {
        /**
         * 从外部加载配置文件，非resource内的配置文件
         */
        val cwd = BotContext.cwd ?: return null
        val configPath = Paths.get(cwd, filename)
        val content: String?

        val yaml = Yaml(Constructor(Config::class.java))

        try {
            // 有配置文件了，读进来
            content = String(Files.readAllBytes(configPath))
            config = yaml.load(content)
            return config
        } catch (e: NoSuchFileException) {
            // 没有配置文件，创建配置文件
            logger.error("No config file find, generate a new config file 'izumi-config.yml', please edit it first! exit...")
            val initConfig = yaml.dump(generateInitConfig())
            Files.writeString(configPath, initConfig)
            exitProcess(-1)
        }

    }

    private fun generateInitConfig(): Config {
        val config = Config()
        config.adminQQ = listOf(10000)
        config.qqPassword = "your_secret_password"
        config.qqNumber = 10000
        config.allowedGroups = listOf(1111, 2222)
        config.colorKey = "your_color_key"
        config.dbName = "izumi-bot-db.sqlite"
        config.mosaicSize = 40
        config.pubgKey = "your_pubg_key_here!!!!"

        return config
    }

    fun getConfig(): Config {
        return config
    }
}


fun main() {
    val cp = ConfigParser("config.yml")
    cp.parse()
}