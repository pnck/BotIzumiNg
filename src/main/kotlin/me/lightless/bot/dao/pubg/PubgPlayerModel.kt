package me.lightless.bot.dao.pubg

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.jodatime.datetime
import org.joda.time.DateTime

object PubgPlayerModel : LongIdTable("pubg_player") {

    val name = varchar("name", 64).index()
    val playerId = varchar("player_id", 64).index()

    val createdTime = datetime("created_time").default(DateTime.now())
    val updatedTime = datetime("updated_time").default(DateTime.now())
}


class PubgPlayerDAO(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<PubgPlayerDAO>(PubgPlayerModel)

    var name by PubgPlayerModel.name
    var playerId by PubgPlayerModel.playerId
    var createdTime by PubgPlayerModel.createdTime
    var updatedTime by PubgPlayerModel.updatedTime
}
