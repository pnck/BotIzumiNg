package me.lightless.bot.dao.pubg

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.jodatime.datetime
import org.joda.time.DateTime

object PubgMatchesModel : LongIdTable("pubg_matches") {
    val matchId = varchar("match_id", 40).index()
    val matchType = varchar("match_type", 16)
    val playerIds = text("player_ids")

    val createdTime = datetime("created_time").default(DateTime.now())
    val updatedTime = datetime("updated_time").default(DateTime.now())
}

class PubgMatchesDAO(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<PubgMatchesDAO>(PubgMatchesModel)

    var matchId by PubgMatchesModel.matchId
    var matchType by PubgMatchesModel.matchType
    var playerIds by PubgMatchesModel.playerIds

    var createdTime by PubgMatchesModel.createdTime
    var updatedTime by PubgMatchesModel.updatedTime
}
