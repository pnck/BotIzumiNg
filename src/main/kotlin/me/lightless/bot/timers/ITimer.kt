package me.lightless.bot.timers

import kotlinx.coroutines.Job

interface ITimer {

    val name: String
    val daemon: Boolean
        get() = true
    val period: Long

    suspend fun process()
}