package common

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class TrieTest {
    @Test
    fun testTrie() {
        val trie = Trie<Char>()
        trie.insert("test", "OK")

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
        // FN
        Assertions.assertTrue(trie.searchPrefix("!").isEmpty())

        // Round2
        trie.insert("toast", 1).insert("ester", listOf(1, 2, 3))

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
        Assertions.assertEquals(3, v5.size)
        Assertions.assertEquals("OK", v5["test"])
        Assertions.assertEquals(1, v5["toast"])
        Assertions.assertEquals(listOf(1, 2, 3), v4["ester"])

        // collection interface
        trie.retainAll(listOf("ester".toList(), "test".toList()))
        Assertions.assertFalse(trie.contains("toast".toList()))
        Assertions.assertEquals(2, trie.size)
        val v6 = trie.searchPrefix("")
        Assertions.assertEquals(listOf(1, 2, 3), v6["ester"])
        Assertions.assertEquals("OK", v6["test"])
    }
}