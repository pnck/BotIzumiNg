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
import java.net.URLConnection


object APIEndpoints {
    private const val API_BASE = "https://api.pubg.com/shards/steam"
    const val playerInfoByName = "${API_BASE}/players?filter[playerNames]="
    const val playerCurrentSeasonInfo = "${API_BASE}/seasons/{seasonId}/gameMode/{gameMode}/players?filter[playerIds]="
}


object PubgApi {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val socksProxy = Proxy(Proxy.Type.SOCKS, InetSocketAddress("127.0.0.1", 9050))
    private val apiKey = BotContext.botConfig!!.pubgKey

    private fun initConnection(connection: URLConnection): URLConnection {
        connection.addRequestProperty("Accept", "application/vnd.api+json")
        connection.addRequestProperty("Authorization", "Bearer $apiKey")
        connection.connectTimeout = 12 * 1000
        connection.readTimeout = 12 * 1000
        return connection
    }

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

    suspend fun getPlayerCurrentSeasonInfo(playerIds: String): JsonObject? = withContext(Dispatchers.IO) {
        val seasonId = "division.bro.official.pc-2018-07"
        val gameMode = "squad"
        val url = "${APIEndpoints.playerCurrentSeasonInfo}$playerIds".replace("{seasonId}", seasonId)
            .replace("{gameMode}", gameMode)

        val connection = URL(url).openConnection(socksProxy)
        initConnection(connection)
        val response = makeRequest(url)

        return@withContext Parser.default().parse(response.reader()) as JsonObject
    }

}