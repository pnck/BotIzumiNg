package me.lightless.bot.commands.impl

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.lightless.bot.BotContext
import me.lightless.bot.commands.ICommand
import me.lightless.bot.utils.ColorPicUtil
import net.mamoe.mirai.message.GroupMessage
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.message.sendImage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.awt.image.BufferedImage
import java.net.HttpURLConnection
import java.net.InetSocketAddress
import java.net.Proxy
import java.net.URL
import javax.imageio.ImageIO

class ColorPicCmd : ICommand {
    override val logger: Logger = LoggerFactory.getLogger(javaClass)
    override val command: List<String> = listOf("/冲", "/涩图", "/还没好", "/不够涩", "/还有吗", "/r18")
    private val r18Command = listOf("/r18")
    private val spc = listOf("/不够涩", "/还没好")

    private val api = "https://api.lolicon.app/setu/?size1200=true"
    private val apiKey = BotContext.botConfig!!.colorKey

    private var lastPicUrl: String? = null
    private var r18Switch = false

    private val socksProxy = Proxy(Proxy.Type.SOCKS, InetSocketAddress("127.0.0.1", 9050))

    private suspend fun getImage(url: String): BufferedImage? {
        return withContext(Dispatchers.IO) {
            val connection: HttpURLConnection = URL(url).openConnection(socksProxy) as HttpURLConnection
            connection.addRequestProperty("Referer", "https://www.pixiv.net/")
            // 别问为啥要设置成 curl 的 UA，他就是可以用
            connection.addRequestProperty("User-Agent", "curl/7.67.0")
            connection.connectTimeout = 12 * 1000
            connection.readTimeout = 12 * 1000

            val result: BufferedImage?
            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                return@withContext null
            } else {
                result = ImageIO.read(connection.inputStream)
                connection.inputStream.close()
                connection.disconnect()
                return@withContext result
            }
        }
    }

    override suspend fun handler(cmd: String, groupMessage: GroupMessage) {

        logger.debug("ColorPicCmd called.")

        // 解析出search keyword
        val message = groupMessage.message.contentToString()
        val msgArray = message.split("""\s+""".toRegex())
        val keyword = msgArray.getOrNull(1)

        // 判断 r18 标志位
        if (cmd in r18Command) {
            this.r18Switch = when (keyword) {
                "enable" -> true
                "disable" -> false
                "status" -> {
                    groupMessage.group.sendMessage(buildMessageChain {
                        add(At(groupMessage.sender))
                        add("\n当前R18状态：${r18Switch}")
                    })
                    return
                }
                else -> false
            }
            groupMessage.group.sendMessage(buildMessageChain {
                add(At(groupMessage.sender))
                add("\n操作成功")
            })
            return
        }

        if (cmd in spc) {
            if (lastPicUrl == null) {
                groupMessage.group.sendMessage(buildMessageChain {
                    add(At(groupMessage.sender))
                    add("\n嗯？你想看什么？")
                })
            } else {
                val image = getImage(lastPicUrl!!)
                if (image != null) {
                    groupMessage.group.sendImage(image)
                } else {
                    groupMessage.group.sendMessage(buildMessageChain {
                        add(At(groupMessage.sender))
                        add("\n出错了...换一张涩图吧!")
                    })
                }
            }
            return
        }

        // 普通命令
        var url = "${api}&apikey=${apiKey}"
        url = if (this.r18Switch) {
            "${url}&r18=1"
        } else {
            "${url}&r18=0"
        }
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
            obj["code"] == 404 -> {
                groupMessage.group.sendMessage(buildMessageChain {
                    add("没有找到你要的涩图呢~")
                })
                return
            }
            obj["code"] != 0 -> {
                // 出错了
                groupMessage.group.sendMessage(buildMessageChain {
                    add("API 出问题了，快点修复吧~")
                })
                return
            }
        }

        val data = (obj["data"] as JsonArray<*>)[0] as JsonObject
        val colorUrl = data["url"] as String
        val author = data["author"] as String
        val pid = data["pid"] as Int
        val title = data["title"] as String
        val tags = data["tags"] as List<*>
        val readableTags: String = tags.joinToString()

        logger.debug("colorUrl: $colorUrl, title: $title, author: $author, pid: $pid")
        this.lastPicUrl = colorUrl
        val image = this.getImage(colorUrl)
        if (image != null) {
            val mosaicImage = ColorPicUtil.doMosaic(image)
            groupMessage.group.sendImage(mosaicImage)
            groupMessage.group.sendMessage(buildMessageChain {
                add(
                    "Author: $author\nTitle: $title\nPixivId: $pid\nTags: $readableTags\n" +
                            "\n当前展示缩略图，喜欢该图请去P站支持原作者哦~\n" +
                            "原图链接：$colorUrl"
                )
            })
        }
    }

    override fun checkRole(qq: Long): Boolean {
        // 所有人都可以调用
        return true
    }
}