package me.lightless.bot.timers.impl

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import kotlinx.coroutines.delay
import me.lightless.bot.services.PubgService
import me.lightless.bot.timers.ITimer
import me.lightless.bot.utils.PubgApi
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory


@Suppress("unused")
class PubgUpdateTimer : ITimer {
    override val name: String
        get() = "PubgPlayerUpdater"
    override val period: Long
        get() = 60 * 10 * 3600

    private val logger = LoggerFactory.getLogger(javaClass)

    private fun updatePlayerSeasonInfo(jsonResponse: JsonObject) {
        val data = jsonResponse["data"] as JsonArray<*>
        data.forEach {
            val item = it as JsonObject

            // 获取这个数据关联的player和season
            val playerId =
                (((item["relationships"] as JsonObject)["player"] as JsonObject)["data"] as JsonObject)["id"] as String
            val season =
                (((item["relationships"] as JsonObject)["season"] as JsonObject)["data"] as JsonObject)["id"] as String

            // 获取数据信息
            val seasonData = ((item["attributes"] as JsonObject)["gameModeStats"] as JsonObject)["squad"] as JsonObject

            // 更新玩家的数据
            PubgService.updatePlayerSeasonInfo(season, playerId, seasonData.toMap())
        }
    }

    private fun updateMatchList(jsonResponse: JsonObject) {
        val data = jsonResponse["data"] as JsonArray<*>
        data.forEach {
            val item = it as JsonObject
            val playerId =
                (((item["relationships"] as JsonObject)["player"] as JsonObject)["data"] as JsonObject)["id"] as String

            val matchList =
                ((item["relationships"] as JsonObject)["matchesSquad"] as JsonObject)["data"] as JsonArray<*>

            for (match in matchList) {
                val m = match as JsonObject
                val matchType = m["type"] as String
                val matchId = m["id"] as String

                // 检查 matchId 是否存在，如果不存在就创建，如果存在则更新关联的player
                val matchDao = PubgService.getMatchByMid(matchId)
                if (matchDao == null) {
                    PubgService.saveMatchInfo(matchType, matchId, playerId)
                } else {
                    PubgService.updateMatchPlayerInfo(matchId, playerId)
                }
            }

        }
    }

    override suspend fun process() {
        /**
         * 每隔一段时间，更新一次所有的player信息
         */

        logger.debug("Pubg updater timer start!")

        while (true) {
            val allPlayers = PubgService.getAllPlayers()
            val playerIds = transaction { allPlayers.joinToString(separator = ",") { it.playerId } }

            val seasonResponse = PubgApi.getPlayerCurrentSeasonInfo(playerIds)
            seasonResponse?.let {
                updatePlayerSeasonInfo(it)
                updateMatchList(it)
            } ?: logger.error("seasonResponse is null!")

            // TODO("UPDATE MATCHES DETAILS INFO")
            delay(period)
        }

    }
}
