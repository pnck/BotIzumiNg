package common

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class TrieTest {
    @Test
    fun testTrie() {
        val trie = Trie<Char>()
        trie.insert("", 1)
        Assertions.assertTrue(trie.isEmpty())
        Assertions.assertEquals(0, trie.size)
        trie.insert("test", "OK")
        Assertions.assertTrue(trie.isNotEmpty())

        // TP
        Assertions.assertEquals(trie.searchIdentical("test") as String, "OK")
        // FN
        Assertions.assertNull(trie.searchIdentical("tes".toList()))
        Assertions.assertNull(trie.searchIdentical("test1".toList()))

        // TP
        val v1 = trie.searchPrefix("t".toList())
        Assertions.assertEquals("OK", v1["test".toList()])
        val v2 = trie.searchPrefix("t")
        Assertions.assertEquals("OK", v2["test"])
        Assertions.assertEquals("OK", trie.searchPrefix("test".toList())["test".toList()])
        // FN
        Assertions.assertTrue(trie.searchPrefix("!").isEmpty())

        // Round2
        trie.insert("toast", 1)
            .putAll(
                mapOf<List<Char>, Any>(
                    Pair("ester".toList(), listOf(1, 2, 3)),
                    Pair("wW".toList(), { 1 })
                )
            )

        // TP
        Assertions.assertEquals(1, trie.searchIdentical("toast"))
        Assertions.assertEquals(listOf(1, 2, 3), trie.searchIdentical("ester"))
        Assertions.assertEquals("OK", trie.searchIdentical("test"))
        // FN
        Assertions.assertNull(trie.searchIdentical("t"))
        Assertions.assertNull(trie.searchIdentical("tes"))
        Assertions.assertNull(trie.searchIdentical("toa"))

        // TP
        val v3 = trie.searchPrefix("t")
        Assertions.assertEquals(2, v3.size)
        Assertions.assertEquals("OK", v3["test"])
        Assertions.assertEquals(1, v3["toast"])
        val v4 = trie.searchPrefix("est")
        Assertions.assertEquals(1, v4.size)
        Assertions.assertEquals(listOf(1, 2, 3), v4["ester"])
        // FN
        Assertions.assertTrue(trie.searchPrefix("o").isEmpty())

        // empty prefix
        val v5 = trie.searchPrefix("")
        Assertions.assertEquals(4, v5.size)
        Assertions.assertEquals("OK", v5["test"])
        Assertions.assertEquals(1, v5["toast"])
        Assertions.assertEquals(listOf(1, 2, 3), v5["ester"])
        @Suppress("UNCHECKED_CAST")
        Assertions.assertEquals(1, (v5["wW"] as () -> Int)())

        // Map interfaces
        trie.remove("toast")

        Assertions.assertEquals(3, trie.keys.size)
        Assertions.assertEquals(3, trie.entries.size)
        Assertions.assertEquals(3, trie.values.size)

        Assertions.assertFalse("toast".toList() in trie)
        Assertions.assertFalse(trie.filter { it.key.joinToString("") != "wW" }.containsKey("wW".toList()))
        Assertions.assertEquals(3, trie.size)
        Assertions.assertEquals(listOf(1, 2, 3), trie["ester".toList()])
        Assertions.assertEquals("OK", trie["test"])
        Assertions.assertTrue(trie.containsValue(listOf(1, 2, 3)))

        val it = trie.iterator()
        for (i in 1..2) {
            it.next()
            it.remove()
        }
        Assertions.assertEquals(1, trie.size)
        trie.clear()
        Assertions.assertTrue(trie.isEmpty())
        Assertions.assertEquals(0, trie.size)
        trie["/c"] = 1
        trie["/cmd".toList()] = 2
        trie.putAll(mapOf(Pair("/show", 3), Pair("/list", 4)))
        Assertions.assertTrue(trie.containsKey("/c"))
        Assertions.assertTrue("/cmd" in trie)
        Assertions.assertEquals(4, trie.size)
        Assertions.assertEquals(1, trie["/c"])
        Assertions.assertEquals(2, trie["/cmd"])
        Assertions.assertEquals(3, trie["/show"])
        Assertions.assertEquals(4, trie["/list"])
    }

}