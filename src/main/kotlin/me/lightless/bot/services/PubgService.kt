package me.lightless.bot.services

import me.lightless.bot.dao.pubg.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

object PubgService {

    // ========== PubgPlayer start ========== //

    fun getPlayerByName(playerName: String): PubgPlayerDAO? = transaction {
        val query = PubgPlayerDAO.find { PubgPlayerModel.name eq playerName }
        return@transaction if (query.empty()) null else query.first()
    }

    fun savePlayerInfo(playerName: String, playerId: String) = transaction {
        PubgPlayerDAO.new {
            this.name = playerName
            this.playerId = playerId
        }
    }

    fun getAllPlayers() = transaction {
        return@transaction PubgPlayerDAO.all()
    }

    fun updatePlayerSeasonInfo(season: String, playerId: String, data: Map<String, Any?>) = transaction {
        val query = PubgSeasonDetailDAO.find { PubgSeasonDetailModel.playerId eq playerId }
        val result = query.firstOrNull()
        if (result == null) {
            // 还没有这个玩家的信息，创建一条记录，同时更新数据进去
            PubgSeasonDetailDAO.new {
                this.season = season
                this.playerId = playerId
                assists = data["assists"] as Int
                dbnos = data["dBNOs"] as Int
                kills = data["kills"] as Int
                damageDealt = data["damageDealt"] as Double
                longestKill = data["longestKill"] as Double
                headshotKills = data["headshotKills"] as Int
                maxKillStreaks = data["maxKillStreaks"] as Int
                roundMostKills = data["roundMostKills"] as Int
                revives = data["revives"] as Int
                heals = data["heals"] as Int
                boosts = data["boosts"] as Int
                teamKills = data["teamKills"] as Int
                roadKills = data["roadKills"] as Int
                roundsPlayed = data["roundsPlayed"] as Int
                suicides = data["suicides"] as Int
                vehicleDestroys = data["vehicleDestroys"] as Int
                rideDistance = data["rideDistance"] as Double
                swimDistance = data["swimDistance"] as Double
                walkDistance = data["walkDistance"] as Double
                wins = data["wins"] as Int
                top10s = data["top10s"] as Int
                weeklyKills = data["weeklyKills"] as Int
                weeklyWins = data["weeklyWins"] as Int
                mostSurvivalTime = data["mostSurvivalTime"] as Double
                timeSurvived = data["timeSurvived"] as Double
            }
        } else {
            // 已经有这个玩家的信息了，更新记录即可
            result.season = season
            result.assists = data["assists"] as Int
            result.dbnos = data["dBNOs"] as Int
            result.kills = data["kills"] as Int
            result.damageDealt = data["damageDealt"] as Double
            result.longestKill = data["longestKill"] as Double
            result.headshotKills = data["headshotKills"] as Int
            result.maxKillStreaks = data["maxKillStreaks"] as Int
            result.roundMostKills = data["roundMostKills"] as Int
            result.revives = data["revives"] as Int
            result.heals = data["heals"] as Int
            result.boosts = data["boosts"] as Int
            result.teamKills = data["teamKills"] as Int
            result.roadKills = data["roadKills"] as Int
            result.roundsPlayed = data["roundsPlayed"] as Int
            result.suicides = data["suicides"] as Int
            result.vehicleDestroys = data["vehicleDestroys"] as Int
            result.rideDistance = data["rideDistance"] as Double
            result.swimDistance = data["swimDistance"] as Double
            result.walkDistance = data["walkDistance"] as Double
            result.wins = data["wins"] as Int
            result.top10s = data["top10s"] as Int
            result.weeklyKills = data["weeklyKills"] as Int
            result.weeklyWins = data["weeklyWins"] as Int
            result.mostSurvivalTime = data["mostSurvivalTime"] as Double
            result.timeSurvived = data["timeSurvived"] as Double

            result.updatedTime = DateTime.now()
        }
    }

    fun getPlayerSeasonInfo(playerId: String) = transaction {
        val query = PubgSeasonDetailDAO.find { PubgSeasonDetailModel.playerId eq playerId }
        return@transaction if (query.empty()) null else query.first()
    }

    // ========== PubgPlayer end ========== //

    // ========== PubgMatch start ========== //

    fun getMatchByMid(matchId: String): PubgMatchesDAO? = transaction {
        val query = PubgMatchesDAO.find { PubgMatchesModel.matchId eq matchId }
        return@transaction if (query.empty()) null else query.first()
    }

    fun saveMatchInfo(matchType: String, matchId: String, playerIds: String) = transaction {
        PubgMatchesDAO.new {
            this.matchId = matchId
            this.matchType = matchType
            this.playerIds = playerIds
        }
    }

    fun updateMatchPlayerInfo(matchId: String, playerId: String): PubgMatchesDAO? = transaction {
        val query = PubgMatchesDAO.find { PubgMatchesModel.matchId eq matchId }
        if (query.empty()) {
            return@transaction null
        }

        val match = query.first()
        match.playerIds = match.playerIds + ",$playerId"
        match.updatedTime = DateTime.now()
        return@transaction match
    }

    // ========== PubgMatch end ========== //

}