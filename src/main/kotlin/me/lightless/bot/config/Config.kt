package me.lightless.bot.config

import kotlin.properties.Delegates

class Config {
    var qqNumber by Delegates.notNull<Long>()
    lateinit var qqPassword: String
    lateinit var allowedGroups: List<Long>
    lateinit var adminQQ: List<Long>
    lateinit var colorKey: String

    // db config
    // dbName: ./db.sqlite
    lateinit var dbName: String

    // 马赛克大小
    var mosaicSize: Int = 40

    // pubg key
    lateinit var pubgKey: String

    // for debug
    lateinit var debugQQ: List<Long>
}