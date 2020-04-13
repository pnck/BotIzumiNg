package me.lightless.bot.config

import kotlin.properties.Delegates

class Config {
    var qqNumber by Delegates.notNull<Long>()
    lateinit var qqPassword: String
    lateinit var allowedGroups: List<Long>
    lateinit var adminQQ: List<Long>
    lateinit var colorKey: String
}