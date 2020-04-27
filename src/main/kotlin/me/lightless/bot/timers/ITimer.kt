package me.lightless.bot.timers

import kotlinx.coroutines.Job

interface ITimer {

    val name: String
    val daemon: Boolean
        get() = true
    val period: Long

    // 需要一个优雅的方案把生效的QQ群传进来
    // 一个办法是从配置中取
    val groupNumber: List<Long>

    suspend fun process()
}