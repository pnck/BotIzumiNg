package me.lightless.bot.commands

import common.Trie
import common.get
import common.putAll
import common.set
import net.mamoe.mirai.message.FriendMessage
import net.mamoe.mirai.message.GroupMessage
import kotlin.reflect.KProperty


@Suppress("UNCHECKED_CAST")
class CommandContextManager {
    private val properties = Trie<Char, Any>()

    fun fromMap(map: Map<String, Any>) {
        properties.putAll(map)
    }

    operator fun <V : Any?> getValue(thisRef: Any?, property: KProperty<*>): V =
        properties[property.name] as V

    operator fun <V : Any?> setValue(thisRef: Any, property: KProperty<*>, value: V) {
        println("$value has been assigned to '${property.name}' in $thisRef.")
        if (value != null) {
            properties[property.name] = value
        }
    }
}

// Any concrete command should extends its own context
abstract class AbstractCommandContext {
    var delegate = CommandContextManager()
    abstract fun switch(ctx: AbstractCommandContext)
}


interface CommandAction {
    suspend fun execute()
    operator fun rem()
    fun context(): AbstractCommandContext?
}

interface ICompositeCommand : ICommand {
    val entries: (cmd: String) -> CommandAction
}

fun emptyAction(): CommandAction {
    return object : CommandAction {
        override suspend fun execute() {}
        override fun context(): AbstractCommandContext? = null
    }
}

