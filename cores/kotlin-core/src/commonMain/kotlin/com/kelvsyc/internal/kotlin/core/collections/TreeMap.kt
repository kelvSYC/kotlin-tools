package com.kelvsyc.internal.kotlin.core.collections

import com.kelvsyc.kotlin.core.collections.MutableSortedMap
import com.kelvsyc.kotlin.core.collections.MutableSortedSet
import com.kelvsyc.kotlin.core.collections.SortedMap
import com.kelvsyc.kotlin.core.collections.SortedSet

/**
 * Red-black tree implementation of [MutableSortedMap].
 *
 * Does not extend [AbstractMutableMap] to avoid JVM `AbstractMap.keySet()` dispatch overriding our
 * `keys: MutableSortedSet<K>` return type. All [Map] / [MutableMap] contract methods are implemented directly.
 *
 * Red-black tree invariants (CLRS):
 *  1. Every node is RED or BLACK.
 *  2. The root is BLACK.
 *  3. Every NIL leaf is conceptually BLACK (represented by `null`).
 *  4. If a node is RED, both children are BLACK.
 *  5. All simple paths from any node to NIL leaves have the same number of BLACK nodes.
 */
internal class TreeMap<K, V>(
    override val comparator: Comparator<in K>,
) : MutableSortedMap<K, V> {

    private enum class Color { RED, BLACK }

    private inner class Node(
        var key: K,
        var value: V,
        var color: Color = Color.RED,
        var left: Node? = null,
        var right: Node? = null,
        var parent: Node? = null,
    )

    private var root: Node? = null
    private var _size = 0

    // ── MutableMap / Map ───────────────────────────────────────────────────────

    override val size: Int get() = _size

    override fun isEmpty(): Boolean = _size == 0

    override fun get(key: K): V? = findNode(key)?.value

    override fun containsKey(key: K): Boolean = findNode(key) != null

    override fun containsValue(value: V): Boolean {
        var found = false
        inOrderNodes { if (it.value == value) found = true }
        return found
    }

    override fun put(key: K, value: V): V? {
        val existing = findNode(key)
        if (existing != null) {
            val old = existing.value
            existing.value = value
            return old
        }
        insertNode(Node(key, value))
        return null
    }

    override fun putAll(from: Map<out K, V>) = from.forEach { (k, v) -> put(k, v) }

    override fun remove(key: K): V? {
        val node = findNode(key) ?: return null
        val value = node.value
        deleteNode(node)
        return value
    }

    override fun clear() {
        root = null
        _size = 0
    }

    override val keys: MutableSortedSet<K>
        get() = TreeSet(comparator).also { s -> inOrderNodes { s.add(it.key) } }

    override val values: MutableCollection<V>
        get() = object : AbstractMutableCollection<V>() {
            override val size get() = _size
            override fun iterator(): MutableIterator<V> {
                val it = entryIterator()
                return object : MutableIterator<V> {
                    override fun hasNext() = it.hasNext()
                    override fun next() = it.next().value
                    override fun remove() = it.remove()
                }
            }
            override fun add(element: V): Boolean = throw UnsupportedOperationException()
        }

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = object : AbstractMutableSet<MutableMap.MutableEntry<K, V>>() {
            override val size get() = _size
            override fun iterator(): MutableIterator<MutableMap.MutableEntry<K, V>> = entryIterator()
            override fun add(element: MutableMap.MutableEntry<K, V>): Boolean =
                put(element.key, element.value) == null
        }

    // ── SortedMap key navigation ───────────────────────────────────────────────

    override fun firstKey(): K = minNode(root)?.key ?: throw NoSuchElementException("Map is empty")
    override fun lastKey(): K = maxNode(root)?.key ?: throw NoSuchElementException("Map is empty")

    override fun floorKey(key: K): K? = floorNode(key)?.key
    override fun ceilingKey(key: K): K? = ceilingNode(key)?.key
    override fun lowerKey(key: K): K? = lowerNode(key)?.key
    override fun higherKey(key: K): K? = higherNode(key)?.key

    // ── Range views (snapshots) ────────────────────────────────────────────────

    override fun headMap(toKey: K, inclusive: Boolean): MutableSortedMap<K, V> =
        collectSnapshot { cmp(it.key, toKey).let { c -> if (inclusive) c <= 0 else c < 0 } }

    override fun tailMap(fromKey: K, inclusive: Boolean): MutableSortedMap<K, V> =
        collectSnapshot { cmp(it.key, fromKey).let { c -> if (inclusive) c >= 0 else c > 0 } }

    override fun subMap(fromKey: K, fromInclusive: Boolean, toKey: K, toInclusive: Boolean): MutableSortedMap<K, V> {
        require(cmp(fromKey, toKey) <= 0) { "fromKey must be <= toKey" }
        return collectSnapshot { node ->
            val lo = cmp(node.key, fromKey).let { c -> if (fromInclusive) c >= 0 else c > 0 }
            val hi = cmp(node.key, toKey).let { c -> if (toInclusive) c <= 0 else c < 0 }
            lo && hi
        }
    }

    override fun descendingMap(): MutableSortedMap<K, V> {
        val result = TreeMap<K, V>(comparator.reversed())
        inOrderNodes { result[it.key] = it.value }
        return result
    }

    override fun descendingKeySet(): MutableSortedSet<K> =
        TreeSet(comparator.reversed()).also { s -> inOrderNodes { s.add(it.key) } }

    // ── equals / hashCode / toString ──────────────────────────────────────────

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is Map<*, *>) return false
        if (other.size != _size) return false
        for ((k, v) in other) {
            @Suppress("UNCHECKED_CAST")
            val node = findNode(k as K) ?: return false
            if (node.value != v) return false
        }
        return true
    }

    override fun hashCode(): Int {
        var h = 0
        inOrderNodes { h += it.key.hashCode() xor it.value.hashCode() }
        return h
    }

    override fun toString(): String = buildString {
        append('{')
        var first = true
        inOrderNodes {
            if (!first) append(", ")
            append(it.key); append('='); append(it.value)
            first = false
        }
        append('}')
    }

    // ── Red-black tree internals ───────────────────────────────────────────────

    private fun cmp(a: K, b: K): Int = comparator.compare(a, b)

    private fun findNode(key: K): Node? {
        var n = root
        while (n != null) {
            val c = cmp(key, n.key)
            n = when {
                c < 0 -> n.left
                c > 0 -> n.right
                else -> return n
            }
        }
        return null
    }

    private fun minNode(n: Node?): Node? {
        var cur = n ?: return null
        while (cur.left != null) cur = cur.left!!
        return cur
    }

    private fun maxNode(n: Node?): Node? {
        var cur = n ?: return null
        while (cur.right != null) cur = cur.right!!
        return cur
    }

    private fun successor(n: Node): Node? {
        if (n.right != null) return minNode(n.right)
        var x = n; var y = x.parent
        while (y != null && x === y.right) { x = y; y = y.parent }
        return y
    }

    private fun floorNode(key: K): Node? {
        var result: Node? = null; var n = root
        while (n != null) {
            val c = cmp(key, n.key)
            when { c == 0 -> return n; c > 0 -> { result = n; n = n.right }; else -> n = n.left }
        }
        return result
    }

    private fun ceilingNode(key: K): Node? {
        var result: Node? = null; var n = root
        while (n != null) {
            val c = cmp(key, n.key)
            when { c == 0 -> return n; c < 0 -> { result = n; n = n.left }; else -> n = n.right }
        }
        return result
    }

    private fun lowerNode(key: K): Node? {
        var result: Node? = null; var n = root
        while (n != null) {
            if (cmp(key, n.key) > 0) { result = n; n = n.right } else n = n.left
        }
        return result
    }

    private fun higherNode(key: K): Node? {
        var result: Node? = null; var n = root
        while (n != null) {
            if (cmp(key, n.key) < 0) { result = n; n = n.left } else n = n.right
        }
        return result
    }

    // ── Insertion ─────────────────────────────────────────────────────────────

    private fun insertNode(z: Node) {
        var y: Node? = null; var x = root
        while (x != null) { y = x; x = if (cmp(z.key, x.key) < 0) x.left else x.right }
        z.parent = y
        when { y == null -> root = z; cmp(z.key, y.key) < 0 -> y.left = z; else -> y.right = z }
        z.color = Color.RED
        insertFixup(z)
        _size++
    }

    private fun insertFixup(zIn: Node) {
        var z = zIn
        while (z.parent?.color == Color.RED) {
            val p = z.parent!!
            val gp = p.parent ?: break
            if (p === gp.left) {
                val uncle = gp.right
                if (uncle?.color == Color.RED) {
                    p.color = Color.BLACK; uncle.color = Color.BLACK; gp.color = Color.RED; z = gp
                } else {
                    if (z === p.right) { z = p; rotateLeft(z) }
                    z.parent!!.color = Color.BLACK; z.parent!!.parent!!.color = Color.RED
                    rotateRight(z.parent!!.parent!!)
                }
            } else {
                val uncle = gp.left
                if (uncle?.color == Color.RED) {
                    p.color = Color.BLACK; uncle.color = Color.BLACK; gp.color = Color.RED; z = gp
                } else {
                    if (z === p.left) { z = p; rotateRight(z) }
                    z.parent!!.color = Color.BLACK; z.parent!!.parent!!.color = Color.RED
                    rotateLeft(z.parent!!.parent!!)
                }
            }
        }
        root!!.color = Color.BLACK
    }

    // ── Deletion ──────────────────────────────────────────────────────────────

    private fun deleteNode(z: Node) {
        var y = z; var yOriginalColor = y.color
        val x: Node?; val xParent: Node?
        if (z.left == null) {
            x = z.right; xParent = z.parent; transplant(z, z.right)
        } else if (z.right == null) {
            x = z.left; xParent = z.parent; transplant(z, z.left)
        } else {
            y = minNode(z.right)!!; yOriginalColor = y.color; x = y.right
            if (y.parent === z) { xParent = y }
            else { xParent = y.parent; transplant(y, y.right); y.right = z.right; y.right!!.parent = y }
            transplant(z, y); y.left = z.left; y.left!!.parent = y; y.color = z.color
        }
        _size--
        if (yOriginalColor == Color.BLACK) deleteFixup(x, xParent)
    }

    private fun deleteFixup(xIn: Node?, xParentIn: Node?) {
        var x = xIn; var xParent = xParentIn
        while (x !== root && (x == null || x.color == Color.BLACK)) {
            val p = xParent ?: break
            if (x === p.left) {
                var w = p.right
                if (w?.color == Color.RED) {
                    w.color = Color.BLACK; p.color = Color.RED; rotateLeft(p); w = p.right
                }
                if ((w?.left == null || w.left!!.color == Color.BLACK) &&
                    (w?.right == null || w.right!!.color == Color.BLACK)) {
                    w?.color = Color.RED; x = p; xParent = p.parent
                } else {
                    if (w?.right == null || w.right!!.color == Color.BLACK) {
                        w?.left?.color = Color.BLACK; w?.color = Color.RED
                        w?.let { rotateRight(it) }; w = p.right
                    }
                    w?.color = p.color; p.color = Color.BLACK
                    w?.right?.color = Color.BLACK; rotateLeft(p); x = root; xParent = null
                }
            } else {
                var w = p.left
                if (w?.color == Color.RED) {
                    w.color = Color.BLACK; p.color = Color.RED; rotateRight(p); w = p.left
                }
                if ((w?.right == null || w.right!!.color == Color.BLACK) &&
                    (w?.left == null || w.left!!.color == Color.BLACK)) {
                    w?.color = Color.RED; x = p; xParent = p.parent
                } else {
                    if (w?.left == null || w.left!!.color == Color.BLACK) {
                        w?.right?.color = Color.BLACK; w?.color = Color.RED
                        w?.let { rotateLeft(it) }; w = p.left
                    }
                    w?.color = p.color; p.color = Color.BLACK
                    w?.left?.color = Color.BLACK; rotateRight(p); x = root; xParent = null
                }
            }
        }
        x?.color = Color.BLACK
    }

    // ── Rotations ─────────────────────────────────────────────────────────────

    private fun rotateLeft(x: Node) {
        val y = x.right ?: return
        x.right = y.left; y.left?.parent = x; y.parent = x.parent
        when { x.parent == null -> root = y; x === x.parent!!.left -> x.parent!!.left = y; else -> x.parent!!.right = y }
        y.left = x; x.parent = y
    }

    private fun rotateRight(x: Node) {
        val y = x.left ?: return
        x.left = y.right; y.right?.parent = x; y.parent = x.parent
        when { x.parent == null -> root = y; x === x.parent!!.right -> x.parent!!.right = y; else -> x.parent!!.left = y }
        y.right = x; x.parent = y
    }

    private fun transplant(u: Node, v: Node?) {
        when { u.parent == null -> root = v; u === u.parent!!.left -> u.parent!!.left = v; else -> u.parent!!.right = v }
        if (v != null) v.parent = u.parent
    }

    // ── Traversal helpers ─────────────────────────────────────────────────────

    private fun inOrderNodes(action: (Node) -> Unit) {
        fun visit(n: Node?) { if (n == null) return; visit(n.left); action(n); visit(n.right) }
        visit(root)
    }

    private fun entryIterator(): MutableIterator<MutableMap.MutableEntry<K, V>> =
        object : MutableIterator<MutableMap.MutableEntry<K, V>> {
            private var next: Node? = minNode(root)
            private var lastReturned: Node? = null

            override fun hasNext() = next != null

            override fun next(): MutableMap.MutableEntry<K, V> {
                val n = next ?: throw NoSuchElementException()
                lastReturned = n; next = successor(n)
                return NodeEntry(n)
            }

            override fun remove() {
                val n = lastReturned ?: throw IllegalStateException()
                deleteNode(n); lastReturned = null
            }
        }

    private inner class NodeEntry(private val node: Node) : MutableMap.MutableEntry<K, V> {
        override val key: K get() = node.key
        override val value: V get() = node.value
        override fun setValue(newValue: V): V {
            val old = node.value; node.value = newValue; return old
        }
        override fun hashCode(): Int = node.key.hashCode() xor node.value.hashCode()
        override fun equals(other: Any?): Boolean {
            if (other !is Map.Entry<*, *>) return false
            return node.key == other.key && node.value == other.value
        }
        override fun toString(): String = "${node.key}=${node.value}"
    }

    private fun collectSnapshot(predicate: (Node) -> Boolean): TreeMap<K, V> {
        val result = TreeMap<K, V>(comparator)
        inOrderNodes { if (predicate(it)) result[it.key] = it.value }
        return result
    }
}
