package me.lightless.bot.timers.impl

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import kotlinx.coroutines.delay
import me.lightless.bot.services.PubgService
import me.lightless.bot.timers.ITimer
import me.lightless.bot.utils.PubgApi
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import java.lang.Exception

class PubgUpdateTimer : ITimer {
    override val name: String
        get() = "PubgPlayerUpdater"
    override val period: Long
        get() = 60 * 10 * 3600

    private val logger = LoggerFactory.getLogger(javaClass)

    suspend fun updatePlayerSeasonInfo(jsonResponse: JsonObject) {
        val data = jsonResponse["data"] as JsonArray<*>

    }

    override suspend fun process() {
        /**
         * 每隔一段时间，更新一次所有的player信息
         */

        logger.debug("Pubg updater timer start!")

        while (true) {
            val allPlayers = PubgService.getAllPlayers()
            val names = transaction { allPlayers.joinToString(separator = ",") { it.name } }
            // FIXME 这个API一次只能查10个用户，以后要改掉，目前没有那么多人
            // CALL_1
            val response = PubgApi.getPlayerInfoByName(names)
            val dataSet = response?.get("data") as JsonArray<*>
            logger.debug("dataSet: $dataSet")

            dataSet.forEach {
                val item = it as JsonObject
                val id: String = item["id"] as String
                val matches = (item["relationships"] as JsonObject)["matches"] as JsonObject
                val matchData = matches["data"] as JsonArray<*>

                matchData.forEach { mit ->
                    val matchItem = mit as JsonObject
                    val matchType = matchItem["type"] as String
                    val matchId = matchItem["id"] as String

                    // 检查 match id 是否存在，如果不存在，就新建一条
                    // 如果存在，检查 player 字段中，是否有当前用户，如果没有就添加进去
                    val matchDao = PubgService.getMatchByMid(matchId)
                    if (matchDao == null) {
                        PubgService.saveMatchInfo(matchType, matchId, id)
                    } else {
                        val ids = matchDao.playerIds
                        if (!ids.contains(id)) {
                            PubgService.updateMatchPlayerInfo(matchId, id)
                        }
                    }

                }

            }
            // TODO("UPDATE SEASON PLAYER INFO")
            val ids = transaction { allPlayers.joinToString(separator = ",") { it.playerId } }
            val seasonResponse = PubgApi.getPlayerCurrentSeasonInfo(ids)



            // TODO("UPDATE MATCHES DETAILS INFO")

            delay(period)
        }

    }
}
