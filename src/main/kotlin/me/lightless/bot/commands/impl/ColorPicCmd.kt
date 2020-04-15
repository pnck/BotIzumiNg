package me.lightless.bot.commands.impl

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.lightless.bot.BotContext
import me.lightless.bot.commands.ICommand
import net.mamoe.mirai.message.GroupMessage
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.message.sendImage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress
import java.net.Proxy
import java.net.URL

class ColorPicCmd : ICommand {
    override val logger: Logger = LoggerFactory.getLogger(javaClass)
    override val command: List<String> = listOf("/冲", "/涩图", "/还没好", "/不够涩", "/还有吗")
    private val r18Command = listOf("/不够涩")

    private val api = "https://api.lolicon.app/setu/?size1200=true"
    private val apiKey = BotContext.botConfig!!.colorKey

    override suspend fun handler(cmd: String, groupMessage: GroupMessage) {

        logger.debug("ColorPicCmd called.")

        // 解析出search keyword
        val message = groupMessage.message.contentToString()
        val msgArray = message.split("""\s+""".toRegex())
        val keyword = msgArray.getOrNull(1)

        // 判断 r18 标志位
        val r18: Int = when {
            r18Command.contains(cmd) -> 1
            else -> 0
        }

        // 设置代理，拼接URL
        val socksProxy = Proxy(Proxy.Type.SOCKS, InetSocketAddress("127.0.0.1", 9050))
        var url = "${api}&r18=${r18}&apikey=${apiKey}"
        if (keyword != null) {
            url = "$url&keyword=$keyword"
        }
        logger.debug("Final URL: $url")

        val response = withContext(Dispatchers.IO) {
            val connection = URL(url).openConnection(socksProxy)
            connection.getInputStream().reader().readText()
        }
        logger.debug("response: $response")
        val obj = Parser.default().parse(response.reader()) as JsonObject
        logger.debug("obj: $obj")

        when {
            obj["code"] == 429 -> {
                // 到限制了
                groupMessage.group.sendMessage(buildMessageChain {
                    add("调用次数已达当日上限，请稍后再试~")
                })
                return
            }
            obj["code"] != 0 -> {
                // 出错了
                groupMessage.group.sendMessage(buildMessageChain {
                    add("API 出问题了，快点修复吧~")
                })
            }
        }

        val data = (obj["data"] as JsonArray<*>)[0] as JsonObject
        val colorUrl = data["url"] as String
        val author = data["author"] as String
        val pid = data["pid"] as Int
        val title = data["title"] as String

        logger.debug("colorUrl: $colorUrl, title: $title, author: $author, pid: $pid")

        // 发送
        withContext(Dispatchers.IO) {

            val connection = URL(colorUrl).openConnection(socksProxy)
            connection.addRequestProperty("Referer", "https://www.pixiv.net/")
            // 别问为啥要设置成 curl 的 UA，他就是可以用
            connection.addRequestProperty("User-Agent", "curl/7.67.0")
            groupMessage.group.sendImage(connection.getInputStream())
            groupMessage.group.sendMessage(buildMessageChain {
                add(
                    "Author: $author\nTitle: $title\npixiv id: $pid\n" +
                            "当前展示缩略图，喜欢该图请去P站支持原作者哦~"
                )
            })
        }
    }

    override fun checkRole(qq: Long): Boolean {
        // 所有人都可以调用
        return true
    }
}