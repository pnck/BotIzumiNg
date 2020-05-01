package me.lightless.bot.utils

import me.lightless.bot.BotContext
import org.slf4j.LoggerFactory
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageTypeSpecifier
import javax.imageio.metadata.IIOMetadataNode
import javax.imageio.stream.ImageOutputStream

object ColorPicUtil {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun doMosaic(image: BufferedImage): BufferedImage {
        val mosaicSize = BotContext.botConfig!!.mosaicSize
//        val image = ImageIO.read(pis)

        val width = image.width
        val height = image.height
        logger.debug("width: $width, height: $height")

        val mosaicImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)

        val xCount = if (width % mosaicSize == 0) {
            width / mosaicSize
        } else {
            width / mosaicSize + 1
        }
        val yCount = if (height % mosaicSize == 0) {
            height / mosaicSize
        } else {
            height / mosaicSize + 1
        }

        var x = 0
        var y = 0
        val g = mosaicImage.createGraphics()

        logger.debug("xCount: $xCount, yCount: $yCount")


        for (i in 0 until xCount) {
            for (j in 0 until yCount) {

                var mWidth = mosaicSize
                var mHeight = mosaicSize
                if (i == xCount - 1) {
                    mWidth = width - x
                }
                if (j == xCount - 1) {
                    mHeight = height - y
                }

                var centerX = x
                var centerY = y

                centerX += if (mWidth % 2 == 0) {
                    mWidth / 2;
                } else {
                    (mWidth - 1) / 2;
                }
                centerY += if (mHeight % 2 == 0) {
                    mHeight / 2;
                } else {
                    (mHeight - 1) / 2;
                }

//                logger.debug("centerX: $centerX, centerY: $centerY")
                centerX = if (centerX > width) width - 1 else centerX
                centerY = if (centerY > height) height - 1 else centerY
                val color = Color(image.getRGB(centerX, centerY))
                g.color = color
                g.fillRect(x, y, mWidth, mHeight)
                y += mosaicSize // 计算下一个矩形的y坐标
            }

            y = 0
            x += mosaicSize
        }

        g.dispose()
//        ImageIO.write(
//            mosaicImage,
//            "png",
//            File("/Users/lightless/program/kt-test-2/src/main/resources/mosaic.png")
//        )

        return mosaicImage
    }

    private fun getAes(): IIOMetadataNode {
        val uo = byteArrayOf(0x01, 0x00, 0x00)
        val aes = IIOMetadataNode("ApplicationExtensions")
        val ae = IIOMetadataNode("ApplicationExtension")
        ae.setAttribute("applicationID", "NETSCAPE")
        ae.setAttribute("authenticationCode", "2.0")
        ae.userObject = uo
        aes.appendChild(ae)

        return aes
    }

    fun doGif(frames: List<BufferedImage>, delay: List<String>): ByteArrayOutputStream {

        val imageWriter = ImageIO.getImageWritersByFormatName("GIF").next()
        val outputStream = ByteArrayOutputStream()
        val imageOutputStream = ImageIO.createImageOutputStream(outputStream)
        imageWriter.output = imageOutputStream
        imageWriter.prepareWriteSequence(null)

        val iwp = imageWriter.defaultWriteParam


        frames.forEachIndexed { idx, it ->
            val metadata = imageWriter.getDefaultImageMetadata(ImageTypeSpecifier(it), iwp)
            val metaFormat = metadata.nativeMetadataFormatName

            val root = metadata.getAsTree(metaFormat)
            var child = root.firstChild
            while (child != null) {
                if (child.nodeName == "GraphicControlExtension") {
                    break
                }
                child = child.nextSibling
            }

            val gce = child as IIOMetadataNode
            gce.setAttribute("userDelay", "FALSE")
            gce.setAttribute("delayTime", delay[idx])

            if (idx == 0) {
                root.appendChild(getAes())
            }

            metadata.setFromTree(metaFormat, root)
            val iioImage = IIOImage(it, null, metadata)
            imageWriter.writeToSequence(iioImage, null)
        }

        imageWriter.endWriteSequence()
        imageOutputStream.close()
        outputStream.close()
        return outputStream
    }

}

fun main() {
    val pic = ImageIO.read(File("D:\\1.jpg"))
    ColorPicUtil.doMosaic(pic)
}