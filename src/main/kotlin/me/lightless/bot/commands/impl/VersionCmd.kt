package me.lightless.bot.commands.impl

import me.lightless.bot.commands.ICommand
import net.mamoe.mirai.message.GroupMessage
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.buildMessageChain
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class VersionCmd() : ICommand {

    override val logger: Logger = LoggerFactory.getLogger(javaClass)
    override val command: List<String> = listOf("/version", "/about")

    override fun checkRole(qq: Long): Boolean {
        logger.info("check role called!")
        return true
    }

    override suspend fun handler(cmd: String, groupMessage: GroupMessage) {
        logger.debug("receive version command!")
        val str = """
            |BotIzumi-NG Project
            |Version: 0.1.0
            |
            |ChangeLog:
            |v0.1.0
            |- 添加涩图功能
            |
            |Github: https://github.com/lightless233/BotIzumiNg
            |If you find any bug, please report at: https://github.com/lightless233/BotIzumiNg/issues
        """.trimMargin()

        groupMessage.group.sendMessage(buildMessageChain {
            add(At(groupMessage.sender))
            add("\n")
            add(str)
        })
    }
}