package common

internal data class TrieNode<ElementT>(val curIndex: ElementT?, var value: Any?) {
    var children: MutableMap<ElementT, TrieNode<ElementT>> = mutableMapOf<ElementT, TrieNode<ElementT>>()
}

typealias TheEntryType<T> = MutableMap.MutableEntry<List<T>, Any>

internal class TrieIterator<ElementT>(root: TrieNode<ElementT>) : MutableIterator<TheEntryType<ElementT>> {
    private var lastNode: TrieNode<ElementT> = root
    private var lastIndex: ElementT? = null
    private fun yieldNext(stackNode: TrieNode<ElementT>, seq: List<ElementT>): Iterator<TheEntryType<ElementT>> =
        iterator {
            lastNode = stackNode
            for ((e, node) in stackNode.children) {
                val nextSeq = seq + e
                if (node.value != null) {
                    lastIndex = e
                    yieldAll(mutableMapOf(Pair(nextSeq, node.value!!)).iterator())
                }
                yieldAll(this@TrieIterator.yieldNext(node, nextSeq))
            }
        }

    private val internalIter = yieldNext(root, listOf())

    override operator fun hasNext(): Boolean {
        return internalIter.hasNext()
    }

    override operator fun next(): MutableMap.MutableEntry<List<ElementT>, Any> {
        return internalIter.next()
    }

    override fun remove() {
        lastNode.children.remove(lastIndex)
    }

}

class Trie<ElementT> : MutableMap<List<ElementT>, Any> {
    private val root = TrieNode<ElementT>(null, null)
    override val size: Int
        get() = this.iterator().asSequence().toSet().size
    override val entries: MutableSet<TheEntryType<ElementT>>
        get() = this.iterator().asSequence().toMutableSet()
    override val keys: MutableSet<List<ElementT>>
        get() = this.iterator().asSequence().map { it.key }.toMutableSet()
    override val values: MutableCollection<Any>
        get() = this.iterator().asSequence().map { it.value }.toMutableSet()

    override fun isEmpty(): Boolean {
        return root.children.isEmpty()
    }

    override fun clear() {
        root.children.clear()
    }

    operator fun iterator(): MutableIterator<TheEntryType<ElementT>> {
        return TrieIterator(root)
    }

    fun insert(seq: Collection<ElementT>, value: Any): Trie<ElementT> {
        if (seq.isEmpty()) {
            return this
        }
        var cur = root
        seq.forEach { e ->
            val next = cur.children[e]
            if (next == null) {
                val t = TrieNode<ElementT>(e, null)
                cur.children[e] = t
                cur = t
            } else {
                cur = next
            }
        }
        cur.value = value
        return this
    }

    fun searchIdentical(seq: List<ElementT>): Any? {
        var cur = root
        seq.forEach { e ->
            val next = cur.children[e] ?: return null
            cur = next
        }
        return cur.value
    }

    fun searchPrefix(seq: List<ElementT>): Map<List<ElementT>, Any> {
        val result = mutableMapOf<List<ElementT>, Any>()
        run {
            var cur = root
            seq.forEach { e ->
                val next = cur.children[e] ?: return@run
                cur = next
            }
            // sequenced ended; add identical prefix -> value
            if (cur.value != null) {
                result[seq] = cur.value!!
            }

            fun addAll(stackNode: TrieNode<ElementT>, curSeq: List<ElementT>) {
                for ((e, node) in stackNode.children) {
                    val nextSeq = curSeq + e
                    if (node.value != null) {
                        result[nextSeq] = node.value!!
                    }
                    addAll(node, nextSeq)
                }
            }
            // recursively add values
            addAll(cur, seq)
        } // prefix mismatched, break
        return result
    }

    override fun remove(key: List<ElementT>): Boolean {
        var cur = root
        var doRemove = { -> false }
        key.forEach { e ->
            val next = cur.children[e] ?: return false
            if (cur.children.size > 1) {
                val captured = cur
                doRemove = {
                    captured.children.remove(e)
                    true
                }
            }
            cur = next
        }
        return doRemove()
    }


    override fun containsKey(key: List<ElementT>): Boolean {
        return searchIdentical(key) != null
    }

    operator fun contains(key: List<ElementT>): Boolean {
        return containsKey(key)
    }

    override fun containsValue(value: Any): Boolean {
        return values.contains(value)
    }

    override operator fun get(key: List<ElementT>): Any? {
        return searchIdentical(key)
    }

    override fun put(key: List<ElementT>, value: Any): Any? {
        val r = searchIdentical(key)
        if (r != null) return r
        insert(key, value)
        return null
    }

    override fun putAll(from: Map<out List<ElementT>, Any>) {
        from.forEach { (key, value) -> put(key, value) }
    }

    operator fun set(key: List<ElementT>, value: Any) {
        insert(key, value)
    }
}

fun Trie<Char>.insert(word: String, value: Any): Trie<Char> {
    return insert(word.toList(), value)
}

fun Trie<Char>.searchIdentical(word: String): Any? {
    return searchIdentical(word.toList())
}

fun Trie<Char>.searchPrefix(word: String): Map<String, Any> {
    val ret = mutableMapOf<String, Any>()
    searchPrefix(word.toList()).forEach { (k, v) -> ret[k.joinToString("")] = v }
    return ret
}

fun Trie<Char>.remove(word: String): Boolean {
    return remove(word.toList())
}

fun Trie<Char>.containsKey(word: String): Boolean {
    return containsKey(word.toList())
}

operator fun Trie<Char>.contains(word: String): Boolean {
    return contains(word.toList())
}

operator fun Trie<Char>.get(word: String): Any? {
    return get(word.toList())
}

operator fun Trie<Char>.set(word: String, value: Any) {
    set(word.toList(), value)
}

fun Trie<Char>.put(word: String, value: Any): Any? {
    return put(word.toList(), value)
}

fun Trie<Char>.putAll(from: Map<String, Any>) {
    from.forEach { (key, value) -> put(key, value) }
}
