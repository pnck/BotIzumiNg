package common

internal data class TrieNode<ElementT>(val curIndex: ElementT?, var value: Any?) {
    var children: MutableMap<ElementT, TrieNode<ElementT>> = mutableMapOf<ElementT, TrieNode<ElementT>>()
}

internal class TrieIterator<ElementT>(private val root: TrieNode<ElementT>) : MutableIterator<List<ElementT>> {
    private var curSeq = mutableListOf<ElementT>()
    private var lastNode = root
    private var curNode = root
    private var nextNode: TrieNode<ElementT>? = root


    override fun hasNext(): Boolean {
        return nextNode != null
    }

    override fun next(): List<ElementT> {
        return listOf()
    }

    override fun remove() {
        lastNode.children.remove(curNode.curIndex)
    }

}

class Trie<ElementT> : MutableCollection<List<ElementT>> {
    private val root = TrieNode<ElementT>(null, null)

    fun insert(seq: Collection<ElementT>, value: Any? = null): Trie<ElementT> {
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
        size += 1
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

    fun searchPrefix(seq: List<ElementT>): Map<List<ElementT>, *> {
        val result = mutableMapOf<List<ElementT>, Any>()
        run {
            var cur = root
            seq.forEach { e ->
                val next = cur.children[e] ?: return@run
                cur = next
            }
            // sequenced ended
            fun addAll(entry: Map.Entry<ElementT, TrieNode<ElementT>>, curSeq: List<ElementT>) {
                val (e, node) = entry
                val t = mutableListOf<ElementT>()
                t.addAll(curSeq)
                t += e
                if (node.value != null) {
                    result[t] = node.value!!
                }
                node.children.forEach {
                    addAll(it, t)
                }
            }
            // add identical prefix -> value
            if (cur.value != null) {
                result[seq] = cur.value!!
            }
            // recursively add values
            cur.children.forEach {
                addAll(it, seq)
            }
        } // prefix mismatched, break
        return result
    }

    override fun isEmpty(): Boolean {
        return root.children.isEmpty()
    }

    override fun clear() {
        root.children.clear()
    }

    override var size = 0

    override fun contains(element: List<ElementT>): Boolean {
        return searchIdentical(element) != null
    }

    override fun containsAll(elements: Collection<List<ElementT>>): Boolean {
        elements.forEach {
            if (searchIdentical(it) == null) return false
        }
        return true
    }

    override fun add(element: List<ElementT>): Boolean {
        insert(element)
        return true
    }

    override fun addAll(elements: Collection<List<ElementT>>): Boolean {
        elements.forEach {
            insert(it)
        }
        return true
    }

    override operator fun iterator(): MutableIterator<List<ElementT>> {
        return TrieIterator(root)
    }

    override fun remove(element: List<ElementT>): Boolean {
        var cur = root
        var doRemove = { -> false }
        element.forEach { e ->
            val next = cur.children[e] ?: return false
            if (cur.children.size > 1) {
                val captured = cur
                doRemove = {
                    captured.children.remove(e)
                    size -= 1
                    true
                }
            }
            cur = next
        }
        return doRemove()
    }

    override fun removeAll(elements: Collection<List<ElementT>>): Boolean {
        root.children.clear()
        return true
    }

    override fun retainAll(elements: Collection<List<ElementT>>): Boolean {
        var modified = false
        this.forEach {
            if (!elements.contains(it)) {
                this.remove(it)
                modified = true
            }
        }
        return modified
    }

}

fun Trie<Char>.insert(word: String, value: Any? = null): Trie<Char> {
    return this.insert(word.toList(), value)
}

fun Trie<Char>.searchIdentical(word: String): Any? {
    return this.searchIdentical(word.toList())
}

fun Trie<Char>.searchPrefix(word: String): Map<String, *> {
    val result = this.searchPrefix(word.toList())
    val ret = mutableMapOf<String, Any>()
    result.forEach { (k, v) ->
        ret[k.joinToString("")] = v as Any
    }
    return ret
}

