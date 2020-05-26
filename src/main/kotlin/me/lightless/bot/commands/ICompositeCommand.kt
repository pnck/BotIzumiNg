package me.lightless.bot.commands

import common.Trie
import kotlin.reflect.KProperty

class CommandContextManager<in RefT, PropertyT> {
    private val properties = mutableMapOf<KProperty<*>, PropertyT>()
    operator fun getValue(thisRef: RefT?, property: KProperty<*>): PropertyT {
        return properties[property]!!
    }

    operator fun setValue(thisRef: RefT, property: KProperty<*>, value: PropertyT) {
        println("$value has been assigned to '${property.name}' in $thisRef.")
        properties[property] = value
    }
}

interface ICompositeCommand:ICommand {


    interface AbstractCommandContext {
        fun switch(ctx: AbstractCommandContext)
    }

    interface CommandAction {
        fun invoke()
        fun context(): AbstractCommandContext
    }

    val entries: Trie<Char, CommandAction>
}

