package me.lightless.bot.handler

import me.lightless.bot.commands.ICommand
import net.mamoe.mirai.message.GroupMessage
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.message.data.firstOrNull
import org.slf4j.LoggerFactory
import java.io.File
import java.util.jar.JarFile
import kotlin.reflect.full.createInstance

class CommandHandler {

    private val logger = LoggerFactory.getLogger(javaClass)
    var commandsImpls: MutableList<ICommand> = arrayListOf()
    private val commandPackageName = "me.lightless.bot.commands.impl"
    private val commandPackagePath = commandPackageName.replace(".", "/")

    init {
        logger.info("Command handler init!")

        // 动态查找所有的command，并且加载进来
        val cnt = loadCommands()
        logger.info(
            "Commands class count: $cnt, command list: " +
                    commandsImpls.flatMap { it.command }.joinToString(
                        ", ", prefix = "[", postfix = "]"
                    )
        )
    }

    // TODO: 改成 utils 里封装好的方法
    private fun loadCommands(): Int {
        var cnt = 0
        val clazzArray: MutableList<String> = arrayListOf()
        val jarFile = File(javaClass.protectionDomain.codeSource.location.path)
        if (jarFile.isFile) {
            logger.debug("Running with JAR file")
            val jar = JarFile(jarFile)
            val e = jar.entries()
            while (e.hasMoreElements()) {
                val name = e.nextElement().name
                if (name.startsWith(commandPackagePath)) {
                    if (name.endsWith(".class") && !name.contains("$")) {
                        clazzArray.add(name.replace(".class", "").replace("/", "."))
                    }
                }
            }
        } else {
            logger.debug("Run with IDE")
            val loader = Thread.currentThread().contextClassLoader
            val url = loader.getResource(commandPackagePath)
            when {
                url == null -> {
                    logger.error("loader.getResource() is null, return")
                    return 0
                }
                url.protocol != "file" -> {
                    logger.error("url protocol is not 'file', return")
                    return 0
                }
            }

            val file = File(url!!.path)
            val files = file.listFiles()
            files!!.forEach { f ->
                val filename = f.name
                if (filename.endsWith(".class") && !filename.contains("$")) {
                    val className = commandPackageName + "." + filename.replace(".class", "")
                    clazzArray.add(className)
                }
            }
        }

        logger.debug("clazzArray: $clazzArray")

        clazzArray.forEach {
            commandsImpls.add(Class.forName(it).kotlin.createInstance() as ICommand)
            cnt += 1
        }

        return cnt
    }


    suspend fun dispatcher(groupMessage: GroupMessage) {
        val message = groupMessage.message
        logger.info("receive command message: $message")
        val msg = message.firstOrNull(PlainText).toString().trim()
        val msgArray = msg.split("""\s+""".toRegex())

        var findCommand = false
        commandsImpls.forEach {
            if (it.command.contains(msgArray[0]) && it.checkRole(groupMessage.sender.id)) {
                it.handler(msgArray[0], groupMessage)
                findCommand = true
            }
        }

        if (!findCommand) {
            groupMessage.group.sendMessage(buildMessageChain {
                add(At(groupMessage.sender))
                add("\nUnknown Command!")
            })
        }
    }
}