package me.lightless.bot.dao

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.jodatime.datetime
import org.joda.time.DateTime

object Ryuo : Table("bot_ryuo") {

    /*
    qq = models.BigIntegerField(default=0, null=False)
    cnt = models.IntegerField(default=0, null=False)
    nickname = models.TextField(default="", null=False)

    created_time = models.DateTimeField(auto_now_add=True)
    updated_time = models.DateTimeField(auto_now=True)
     */

    val id = long("id").autoIncrement()
    val qq = long("qq").default(0)
    val nickname = text("nickname").default("")

    val createdTime = datetime("created_time").default(DateTime.now())
    val updatedTime = datetime("updated_time").default(DateTime.now())

    override val primaryKey: PrimaryKey = PrimaryKey(id, name = "pk")

}