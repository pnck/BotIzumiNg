package me.lightless.bot.config

import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor

class ConfigParser(val filename: String) {

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

    fun getConfig(): Config {
        return config
    }
}


fun main() {
    val cp = ConfigParser("config.yml")
    cp.parse()
}