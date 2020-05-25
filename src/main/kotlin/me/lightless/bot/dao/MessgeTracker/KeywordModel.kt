package me.lightless.bot.dao.MessgeTracker


import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable


object KeywordModel : LongIdTable("msgtracker_kw") {
    val text = long("text")
}

class KeywordDAO(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<KeywordDAO>(KeywordModel)

    var text by KeywordModel.text
}
