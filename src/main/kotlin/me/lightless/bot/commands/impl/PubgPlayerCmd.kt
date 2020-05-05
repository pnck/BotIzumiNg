package me.lightless.bot.commands.impl

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import io.ktor.util.KtorExperimentalAPI
import me.lightless.bot.commands.ICommand
import me.lightless.bot.services.PubgService
import me.lightless.bot.utils.PubgApi
import net.mamoe.mirai.message.GroupMessage
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.buildMessageChain
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class PubgPlayerCmd : ICommand {
    override val logger: Logger
        get() = LoggerFactory.getLogger(javaClass)
    override val command: List<String>
        get() = listOf("/pubg")

    private val errorMessage = "格式：/pubg add PLAYER_NICKNAME\n" +
            "/pubg list\n" +
            "/pubg info PLAYER_NICKNAME"

    override fun checkRole(qq: Long): Boolean {
        // 所有人都可以调用
        return true
    }

    private fun parseParams(groupMessage: GroupMessage): List<String?> {
        return groupMessage.message.contentToString().split("""\s+""".toRegex())
    }

    private suspend fun add(playerName: String?, gm: GroupMessage) {

        if (playerName == null) {
            logger.error("playerName is null!")
            gm.group.sendMessage(buildMessageChain {
                add(At(gm.sender))
                add("\n$errorMessage")
            })
            return
        }

        logger.debug("playerName: $playerName")

        // check if player already exist
        val result = PubgService.getPlayerByName(playerName)
        if (result != null) {
            gm.group.sendMessage(buildMessageChain {
                add(At(gm.sender))
                add("\n用户已存在!")
            })
            return
        }

        val jsonResponse = PubgApi.getPlayerInfoByName(playerName)
        if (jsonResponse == null) {
            gm.group.sendMessage(buildMessageChain {
                add(At(gm.sender))
                add("\n请求超时！")
            })
            return
        }

        val playerId = ((jsonResponse["data"] as JsonArray<*>)[0] as JsonObject)["id"] as String
        logger.debug("get player id: $playerId")

        PubgService.savePlayerInfo(playerName, playerId)
        gm.group.sendMessage(buildMessageChain {
            add(At(gm.sender))
            add("\n添加用户成功!")
        })
    }

    private suspend fun list(gm: GroupMessage) {
        val allPlayers = PubgService.getAllPlayers()
        val result = transaction { allPlayers.joinToString(separator = "\n") { "- ${it.name}" } }
        gm.group.sendMessage(buildMessageChain {
            add(At(gm.sender))
            add("\n$result")
        })
    }

    private suspend fun info(playerName: String?, gm: GroupMessage) {
        if (playerName == null) {
            logger.error("playerName is null!")
            gm.group.sendMessage(buildMessageChain {
                add(At(gm.sender))
                add("\n$errorMessage")
            })
            return
        }

        val result = PubgService.getPlayerByName(playerName)
        if (result == null) {
            logger.error("player not exist!")
            gm.group.sendMessage(buildMessageChain {
                add(At(gm.sender))
                add("\n未找到该玩家！")
            })
            return
        }

        val playerId = transaction { result.playerId }
        val seasonDetailDAO = PubgService.getPlayerSeasonInfo(playerId)
        if (seasonDetailDAO == null) {
            logger.error("no player find!")
            gm.group.sendMessage(buildMessageChain {
                add(At(gm.sender))
                add("\n未找到该玩家！")
            })
            return
        }

        val message = with(seasonDetailDAO) {
            """
            玩家：$playerName 赛季：${season}
            本周数据：
            击杀：${weeklyKills}，吃鸡：${weeklyWins}
            ---
            本赛季数据：
            一共进行了${roundsPlayed}场游戏，吃了${wins}次鸡，${top10s}次进入了前十
            最长存活了${String.format("%.2f", mostSurvivalTime / 60)}分钟，场均生存时间${String.format(
                "%.2f", timeSurvived / roundsPlayed / 60
            )}分钟
            击杀：${kills}，爆头击杀：击倒：${dbnos}，助攻：${assists}
            爆头率：${String.format("%.2f", headshotKills / kills.toDouble() * 100)}%，KD：${String.format(
                "%.2f",
                kills.toDouble() / (roundsPlayed - wins)
            )}
            场均造成了${String.format("%.2f", damageDealt / roundsPlayed.toDouble())}点伤害
            最大连杀：${maxKillStreaks}，最大击杀：${roundMostKills}，最远击杀：${longestKill}米
            
            打了${heals}个绷带/包/箱子，喝了${boosts}瓶可乐/止痛药/针，摸了${revives}次队友，自杀了${suicides}次
            灭了${teamKills}次队，日炸了${vehicleDestroys}辆车
            
            开车行驶了${rideDistance}米，顺便在路上撞死了${roadKills}个人
            游泳了${swimDistance}米，徒步了${walkDistance}米
        """.trimIndent()
        }

        gm.group.sendMessage(message)
    }

    @KtorExperimentalAPI
    override suspend fun handler(cmd: String, groupMessage: GroupMessage) {

        val params = parseParams(groupMessage)
        when (params.getOrNull(1)) {
            "add" -> {
                // 添加一个 player
                add(params.getOrNull(2), groupMessage)
                return
            }
            "list" -> {
                // 列出所有 player
                list(groupMessage)
                return
            }
            "info" -> {
                // 查看某个 player 的信息
                info(params.getOrNull(2), groupMessage)
                return
            }
            else -> {
                groupMessage.group.sendMessage(buildMessageChain {
                    add(At(groupMessage.sender))
                    add("\n${errorMessage}")
                })
                return
            }
        }

    }
}