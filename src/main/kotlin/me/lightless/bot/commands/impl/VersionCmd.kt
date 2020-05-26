package me.lightless.bot.commands.impl

import me.lightless.bot.commands.ILegacyCommand
import net.mamoe.mirai.message.GroupMessage
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.buildMessageChain
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class VersionCmd() : ILegacyCommand {

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
            |Version: 1.0.4-SNAPSHOT
            |
            |ChangeLog
            |v1.0.4-SNAPSHOT
            |- @GodSu 需求
            |
            |v1.0.3-SNAPSHOT
            |- PUBG查询功能
            |
            |v1.0.2-SNAPSHOT
            |- 涩图添加 anti-NSFW功能
            |
            |v1.0.1-SNAPSHOT
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