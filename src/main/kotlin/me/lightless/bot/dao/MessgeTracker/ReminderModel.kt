package me.lightless.bot.dao.MessgeTracker


import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.jodatime.datetime


object ReminderModel : LongIdTable("msgtracker_reminder") {
    val sender = long("sender_qq").index()
    val receiver = long("receiver_qq").index()
    val createdTime = datetime("time_created")
    val dueTime = datetime("time_due")
    val content = text("content")
}

class ReminderDAO(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<ReminderDAO>(ReminderModel)

    var sender by ReminderModel.sender
    var receiver by ReminderModel.receiver
    var createdTime by ReminderModel.createdTime
    var dueTime by ReminderModel.dueTime
    var content by ReminderModel.content

}
