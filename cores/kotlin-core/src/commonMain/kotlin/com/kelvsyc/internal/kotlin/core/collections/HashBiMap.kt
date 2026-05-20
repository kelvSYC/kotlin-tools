package com.kelvsyc.internal.kotlin.core.collections

import com.kelvsyc.kotlin.core.collections.MutableBiMap

@PublishedApi
internal class HashBiMap<K, V> private constructor(
    private val forward: HashMap<K, V>,
    private val backward: HashMap<V, K>,
) : MutableBiMap<K, V> {

    constructor() : this(HashMap(), HashMap())

    // The inverse view shares the same two maps with forward and backward swapped.
    override val inverse: MutableBiMap<V, K> by lazy { HashBiMap(backward, forward) }

    // ── Map / MutableMap ──────────────────────────────────────────────────────

    override val size: Int get() = forward.size

    override fun isEmpty(): Boolean = forward.isEmpty()

    override fun containsKey(key: K): Boolean = forward.containsKey(key)

    override fun containsValue(value: @UnsafeVariance V): Boolean = backward.containsKey(value)

    override fun get(key: K): V? = forward[key]

    override fun put(key: K, value: V): V? {
        val existingValue = forward[key]
        if (existingValue == value) return value  // identical mapping — no-op
        require(!backward.containsKey(value)) {
            "Value $value is already present (mapped from key ${backward[value]})"
        }
        if (existingValue != null) backward.remove(existingValue)
        forward[key] = value
        backward[value] = key
        return existingValue
    }

    override fun forcePut(key: K, value: V): V? {
        val existingValue = forward[key]
        if (existingValue == value) return value  // identical mapping — no-op
        // Remove the key that currently owns this value, if any.
        val displacedKey = backward[value]
        if (displacedKey != null && displacedKey != key) forward.remove(displacedKey)
        // Remove the old value mapping for this key, if any.
        if (existingValue != null) backward.remove(existingValue)
        forward[key] = value
        backward[value] = key
        return existingValue
    }

    override fun remove(key: K): V? {
        val value = forward.remove(key) ?: return null
        backward.remove(value)
        return value
    }

    override fun putAll(from: Map<out K, V>) = from.forEach { (k, v) -> put(k, v) }

    override fun clear() {
        forward.clear()
        backward.clear()
    }

    // ── Views (delegate to forward; removals must keep backward in sync) ───────

    override val keys: MutableSet<K> get() = KeySet()
    override val values: MutableCollection<V> get() = ValueCollection()
    override val entries: MutableSet<MutableMap.MutableEntry<K, V>> get() = EntrySet()

    // ── equals / hashCode / toString ──────────────────────────────────────────

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is Map<*, *>) return false
        return forward == other
    }

    override fun hashCode(): Int = forward.hashCode()

    override fun toString(): String = forward.toString()

    // ── Inner classes ─────────────────────────────────────────────────────────

    private inner class SyncedIterator(
        private val delegate: MutableIterator<MutableMap.MutableEntry<K, V>>,
    ) : MutableIterator<MutableMap.MutableEntry<K, V>> {
        private var lastValue: V? = null
        private var hasLast = false

        override fun hasNext() = delegate.hasNext()

        override fun next(): MutableMap.MutableEntry<K, V> {
            val entry = delegate.next()
            @Suppress("UNCHECKED_CAST")
            lastValue = entry.value as V?
            hasLast = true
            return entry
        }

        override fun remove() {
            check(hasLast) { "Call next() before remove()" }
            delegate.remove()
            @Suppress("UNCHECKED_CAST")
            backward.remove(lastValue as V)
            hasLast = false
        }
    }

    private inner class EntrySet : AbstractMutableSet<MutableMap.MutableEntry<K, V>>() {
        override val size get() = forward.size
        override fun iterator(): MutableIterator<MutableMap.MutableEntry<K, V>> =
            SyncedIterator(forward.entries.iterator())
        override fun add(element: MutableMap.MutableEntry<K, V>): Boolean =
            throw UnsupportedOperationException()
        override fun contains(element: MutableMap.MutableEntry<K, V>): Boolean =
            forward.entries.contains(element)
    }

    private inner class KeySet : AbstractMutableSet<K>() {
        override val size get() = forward.size
        override fun iterator() = object : MutableIterator<K> {
            private val backing = SyncedIterator(forward.entries.iterator())
            override fun hasNext() = backing.hasNext()
            override fun next() = backing.next().key
            override fun remove() = backing.remove()
        }
        override fun add(element: K): Boolean = throw UnsupportedOperationException()
        override fun contains(element: K) = forward.containsKey(element)
    }

    private inner class ValueCollection : AbstractMutableCollection<V>() {
        override val size get() = forward.size
        override fun iterator() = object : MutableIterator<V> {
            private val backing = SyncedIterator(forward.entries.iterator())
            override fun hasNext() = backing.hasNext()
            override fun next() = backing.next().value
            override fun remove() = backing.remove()
        }
        override fun add(element: V): Boolean = throw UnsupportedOperationException()
        override fun contains(element: @UnsafeVariance V) = backward.containsKey(element)
    }
}
