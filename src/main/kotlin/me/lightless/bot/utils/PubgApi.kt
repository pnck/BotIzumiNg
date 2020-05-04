package me.lightless.bot.utils

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.lightless.bot.BotContext
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress
import java.net.Proxy
import java.net.URL


object APIEndpoints {
    const val playerInfoByName = "https://api.pubg.com/shards/steam/players?filter[playerNames]="
}


object PubgApi {

    /**
     * CIO Engine not support SOCKS proxy now
     * make request without proxy temp
     */

    private val logger = LoggerFactory.getLogger(javaClass)

    private val socksProxy = Proxy(Proxy.Type.SOCKS, InetSocketAddress("127.0.0.1", 9050))

//    @KtorExperimentalAPI
//    val httpClient = HttpClient {
////        engine { proxy = socksProxy }
//        install(HttpTimeout) {
//            requestTimeoutMillis = 12 * 1000
//            connectTimeoutMillis = 12 * 1000
//            socketTimeoutMillis = 12 * 1000
//        }
//    }

    private val apiKey = BotContext.botConfig!!.pubgKey

    private suspend fun makeRequest(url: String) = withContext(Dispatchers.IO) {
        val connection = URL(url).openConnection(socksProxy)
        connection.addRequestProperty("Accept", "application/vnd.api+json")
        connection.addRequestProperty("Authorization", "Bearer $apiKey")
        connection.connectTimeout = 12 * 1000
        connection.readTimeout = 12 * 1000
        return@withContext connection.getInputStream().reader().readText()
    }

    suspend fun getPlayerInfoByName(nickname: String): JsonObject? {
        val url = "${APIEndpoints.playerInfoByName}$nickname"
        logger.debug("url: $url")
        val response = makeRequest(url)
        return Parser.default().parse(response.reader()) as JsonObject
    }

}