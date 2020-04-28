package me.lightless.bot.config

import me.lightless.bot.BotContext
import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.NoSuchFileException
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.system.exitProcess

class ConfigParser(private val filename: String) {

    private val logger = LoggerFactory.getLogger(javaClass)

    private lateinit var config: Config

    companion object {
        private const val TAG = "[ConfigParser]"
    }

    fun parse(): Config {
        val yaml = Yaml(Constructor(Config::class.java))
        val content = javaClass.classLoader.getResource(filename)?.readText()
        logger.debug("$TAG content: $content")

        config = yaml.load(content)
        return config
    }

    fun parseExternalConfig(): Config? {
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
            logger.error("No config file find, generate a new file, please edit it first! exit...")
            yaml.dump(config)
            exitProcess(-1)
        }

    }

    fun getConfig(): Config {
        return config
    }
}


fun main() {
    val cp = ConfigParser("config.yml")
    cp.parse()
}