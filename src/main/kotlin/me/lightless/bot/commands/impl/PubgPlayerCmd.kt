package me.lightless.bot.commands.impl

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.lightless.bot.BotContext
import me.lightless.bot.commands.ICommand
import me.lightless.bot.services.PubgService
import me.lightless.bot.utils.PubgApi
import net.mamoe.mirai.message.GroupMessage
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.buildMessageChain
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class PubgPlayerCmd : ICommand {
    override val logger: Logger
        get() = LoggerFactory.getLogger(javaClass)
    override val command: List<String>
        get() = listOf("/pubg_add")

    private val errorMessage = "格式：/pubg_add playerName"

    override fun checkRole(qq: Long): Boolean {
        // 所有人都可以调用
        return true
    }

    private fun parseParams(groupMessage: GroupMessage): List<String?> {
        return groupMessage.message.contentToString().split("""\s+""".toRegex())
    }

    @KtorExperimentalAPI
    override suspend fun handler(cmd: String, groupMessage: GroupMessage) {

        val playerName: String? = parseParams(groupMessage).getOrNull(1) ?: {
            logger.error("player can't be null.")
            BotContext.bot?.launch {
                groupMessage.group.sendMessage(buildMessageChain {
                    add(At(groupMessage.sender))
                    add("\n$errorMessage")
                })
            } ?: logger.error("bot instance is null!")
            null
        }()
        logger.debug("playerName: $playerName")
        if (playerName == null) {
            return
        }

        val jsonObject = PubgApi.getPlayerInfoByName(playerName)
        logger.debug("jsonObj: $jsonObject")
        if (jsonObject == null) {
            groupMessage.group.sendMessage("API 请求超时！")
            return
        }

        val data = (jsonObject["data"] as JsonArray<*>)[0] as JsonObject
        logger.debug("data: $data")
        val playerId = data["id"] as String
        logger.debug("playerId: $playerId")

        // check if player already exist
        val result = PubgService.getPlayerByName(playerName)
        logger.debug("result: $result")
        if (result == null) {
            PubgService.savePlayerInfo(playerName, playerId)
            groupMessage.group.sendMessage(buildMessageChain {
                add(At(groupMessage.sender))
                add("\n添加用户成功!")
            })
        }

        // update matches record
        // parse matches record
        val relationships = data["relationships"] as JsonObject
        val wrapperMatches = relationships["matches"] as JsonObject
        val matchesData = wrapperMatches["data"] as JsonArray<*>
        logger.debug("matchesData: $matchesData")
        matchesData.forEach {
            val temp = it as JsonObject
            val matchType = temp["type"] as String
            val matchId = temp["id"] as String

            // check if already has this match
            // if already has, update playerIds else insert this match
            if (PubgService.getMatchByMid(matchId) != null) {
                PubgService.updateMatchPlayerInfo(matchId, playerId)
            } else {
                // null
                PubgService.saveMatchInfo(matchType, matchId, playerId)
            }
        }

        groupMessage.group.sendMessage(buildMessageChain {
            add(At(groupMessage.sender))
            add("\n更新比赛id成功!")
        })

    }

}