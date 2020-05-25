package me.lightless.bot.dao.MessgeTracker


import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.jodatime.datetime


object MessageLogModel : LongIdTable("msgtracker_msglog") {
    val sender = long("sender_qq").index()
    val time = datetime("time")
    val content = text("content")
}

class MessageLogDAO(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<MessageLogDAO>(MessageLogModel)

    var sender by MessageLogModel.sender
    var time by MessageLogModel.time
    var content by MessageLogModel.content
}
