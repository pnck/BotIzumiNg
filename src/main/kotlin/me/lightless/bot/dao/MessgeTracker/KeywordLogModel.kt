package me.lightless.bot.dao.MessgeTracker


import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable


object KeywordLogModel : LongIdTable("msgtracker_kwlog") {
    val msgId = long("msg_id")
    val keywordId = long("kw_id")
}

class KeywordLogDAO(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<KeywordLogDAO>(KeywordLogModel)

    var msgId by KeywordLogModel.msgId
    var keywordId by KeywordLogModel.keywordId
}
