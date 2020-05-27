package me.lightless.bot.commands.impl

import me.lightless.bot.commands.AbstractCommandContext
import me.lightless.bot.commands.CommandAction
import me.lightless.bot.commands.ICompositeCommand
import me.lightless.bot.commands.emptyAction
import net.mamoe.mirai.message.FriendMessage
import net.mamoe.mirai.message.GroupMessage
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.buildMessageChain

class MessageTrackerCmd : ICompositeCommand {
    class MessageTrackerContext : AbstractCommandContext() {
        var groupMessage: GroupMessage? = null
        var friendMessage: FriendMessage? = null
        var scope: String by delegate
        var subCommands: MessageTrackerCmd by delegate

        override fun switch(ctx: AbstractCommandContext) {
            val t = ctx.delegate
            ctx.delegate = this.delegate
            this.delegate = t
        }

    }


    override val entries: (String) -> CommandAction = {
        when (it) {
            "/tracker" -> object : CommandAction {
                override suspend fun execute() = topHelp(context())
                override fun context() = topContext
            }
            else -> emptyAction()
        }
    }

    suspend fun topHelp(msg: GroupMessage) {
        msg.group.sendMessage(buildMessageChain {
            add(At(msg.sender))
            add("\n")
            add("Worked")
        })
    }

}
