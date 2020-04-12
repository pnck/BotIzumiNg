import java.io.File
import java.net.URI

class TestFile {

    fun test() {
        val p = "jar:file:/D:/program/bot-izumi-ng/build/libs/bot-izumi-ng-1.0.0-SNAPSHOT-all.jar!/me/lightless/bot/commands/impl"
        val p2 = "file:/D:/program/bot-izumi-ng/build/libs/bot-izumi-ng-1.0.0-SNAPSHOT-all.jar"

        val x = javaClass.protectionDomain.codeSource.location.path
        println("x: $x")
    }

}

fun main() {
    val t = TestFile()
    t.test()
}