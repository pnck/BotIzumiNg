package me.lightless.bot.services

import me.lightless.bot.dao.PubgMatchesDAO
import me.lightless.bot.dao.PubgMatchesModel
import me.lightless.bot.dao.PubgPlayerDAO
import me.lightless.bot.dao.PubgPlayerModel
import org.jetbrains.exposed.sql.SqlLogger
import org.jetbrains.exposed.sql.addLogger
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