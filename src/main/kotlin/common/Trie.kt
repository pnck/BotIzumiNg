package common

internal data class TrieNode<ET, VT>(val curIndex: ET?, var value: VT?) {
    var children: MutableMap<ET, TrieNode<ET, VT>> = mutableMapOf()
}

typealias TheEntryType<T, U> = MutableMap.MutableEntry<List<T>, U>

internal class TrieIterator<ET, VT>(root: TrieNode<ET, VT>) :
    MutableIterator<TheEntryType<ET, VT>> {
    private var lastNode: TrieNode<ET, VT> = root
    private var lastIndex: ET? = null
    private fun yieldNext(
        stackNode: TrieNode<ET, VT>,
        seq: List<ET>
    ): Iterator<TheEntryType<ET, VT>> =
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

    override operator fun next(): MutableMap.MutableEntry<List<ET>, VT> {
        return internalIter.next()
    }

    override fun remove() {
        lastNode.children.remove(lastIndex)
    }

}

open class Trie<ET, VT : Any> : MutableMap<List<ET>, VT> {
    private val root = TrieNode<ET, VT>(null, null)
    override val size: Int
        get() = this.iterator().asSequence().toSet().size
    override val entries: MutableSet<TheEntryType<ET, VT>>
        get() = this.iterator().asSequence().toMutableSet()
    override val keys: MutableSet<List<ET>>
        get() = this.iterator().asSequence().map { it.key }.toMutableSet()
    override val values: MutableCollection<VT>
        get() = this.iterator().asSequence().map { it.value }.toMutableSet()

    override fun isEmpty(): Boolean {
        return root.children.isEmpty()
    }

    override fun clear() {
        root.children.clear()
    }

    open operator fun iterator(): MutableIterator<TheEntryType<ET, VT>> {
        return TrieIterator(root)
    }

    open fun insert(seq: Collection<ET>, value: VT): Trie<ET, VT> {
        if (seq.isEmpty()) {
            return this
        }
        var cur = root
        seq.forEach { e ->
            val next = cur.children[e]
            if (next == null) {
                val t = TrieNode<ET, VT>(e, null)
                cur.children[e] = t
                cur = t
            } else {
                cur = next
            }
        }
        cur.value = value
        return this
    }

    open fun searchIdentical(seq: List<ET>): VT? {
        var cur = root
        seq.forEach { e ->
            val next = cur.children[e] ?: return null
            cur = next
        }
        return cur.value
    }

    open fun searchPrefix(seq: List<ET>): Map<List<ET>, VT> {
        val result = mutableMapOf<List<ET>, VT>()
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

            fun addAll(stackNode: TrieNode<ET, VT>, curSeq: List<ET>) {
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

    override fun remove(key: List<ET>): VT? {
        var cur = root
        var doRemove: () -> VT? = { -> null }
        key.forEach { e ->
            val next = cur.children[e] ?: return null
            val removing = next
            // do recursive remove
            // if branched here, capture the remove operation into closure
            // so that it can be invoked later after removed child
            val captured = cur
            if (cur.children.size == 1 /*orphan*/) {
                val doLastRemove = doRemove
                doRemove = {
                    captured.children.remove(e)
                    // recursively remove the parent node
                    // since the removing is the last leaf node
                    // return value of doLastRemove should be ignored
                    doLastRemove()
                    removing.value
                }
            } else {
                // last doRemove is removing a branched sub tree
                // should be reset and try doRemove the single branch
                doRemove = {
                    captured.children.remove(e)
                    // no more recursive doLastRemove
                    removing.value
                }
            }
            cur = next
        }
        return doRemove()
    }


    override fun containsKey(key: List<ET>): Boolean {
        return searchIdentical(key) != null
    }

    operator fun contains(key: List<ET>): Boolean {
        return containsKey(key)
    }

    override fun containsValue(value: VT): Boolean {
        return values.contains(value)
    }

    override operator fun get(key: List<ET>): VT? {
        return searchIdentical(key)
    }

    override fun put(key: List<ET>, value: VT): VT? {
        val r = searchIdentical(key)
        if (r != null) return r
        insert(key, value)
        return null
    }

    override fun putAll(from: Map<out List<ET>, VT>) {
        from.forEach { (key, value) -> put(key, value) }
    }

    operator fun set(key: List<ET>, value: VT) {
        insert(key, value)
    }
}

inline fun <reified VT : Any> Trie<Char, VT>.insert(word: String, value: VT): Trie<Char, VT> {
    return insert(word.toList(), value)
}

inline fun <reified VT : Any> Trie<Char, VT>.searchIdentical(word: String): VT? {
    return searchIdentical(word.toList())
}

inline fun <reified VT : Any> Trie<Char, VT>.searchPrefix(word: String): Map<String, VT> {
    val ret = mutableMapOf<String, VT>()
    searchPrefix(word.toList()).forEach { (k, v) -> ret[k.joinToString("")] = v as VT }
    return ret
}

inline fun <reified VT : Any> Trie<Char, VT>.remove(word: String): VT? {
    return remove(word.toList())
}

inline fun <reified VT : Any> Trie<Char, VT>.containsKey(word: String): Boolean {
    return containsKey(word.toList())
}

inline operator fun <reified VT : Any> Trie<Char, VT>.contains(word: String): Boolean {
    return contains(word.toList())
}

inline operator fun <reified VT : Any> Trie<Char, VT>.get(word: String): VT? {
    return get(word.toList())
}

inline operator fun <reified VT : Any> Trie<Char, VT>.set(word: String, value: VT) {
    set(word.toList(), value)
}

inline fun <reified VT : Any> Trie<Char, VT>.put(word: String, value: VT): VT? {
    return put(word.toList(), value)
}

inline fun <reified VT : Any> Trie<Char, VT>.putAll(from: Map<String, VT>) {
    from.forEach { (key, value) -> put(key, value) }
}