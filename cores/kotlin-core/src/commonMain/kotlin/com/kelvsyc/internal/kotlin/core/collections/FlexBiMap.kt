package com.kelvsyc.internal.kotlin.core.collections

import com.kelvsyc.kotlin.core.collections.MutableBiMap

@PublishedApi
internal class FlexBiMap<K, V>(
    private val fwd: MapStore<K, V>,
    private val bwd: MapStore<V, K>,
) : MutableBiMap<K, V> {

    override val inverse: MutableBiMap<V, K> by lazy { FlexBiMap(bwd, fwd) }

    // ── Map / MutableMap ──────────────────────────────────────────────────────

    override val size: Int get() = fwd.size

    override fun isEmpty(): Boolean = fwd.size == 0

    override fun containsKey(key: K): Boolean = fwd.containsKey(key)

    override fun containsValue(value: @UnsafeVariance V): Boolean = bwd.containsKey(value)

    override fun get(key: K): V? = fwd.get(key)

    override fun put(key: K, value: V): V? {
        val existingValue = fwd.get(key)
        if (existingValue == value) return value  // identical mapping — no-op
        require(!bwd.containsKey(value)) {
            "Value $value is already present (mapped from key ${bwd.get(value)})"
        }
        if (existingValue != null) bwd.remove(existingValue)
        fwd.put(key, value)
        bwd.put(value, key)
        return existingValue
    }

    override fun forcePut(key: K, value: V): V? {
        val existingValue = fwd.get(key)
        if (existingValue == value) return value  // identical mapping — no-op
        val displacedKey = bwd.get(value)
        if (displacedKey != null && displacedKey != key) fwd.remove(displacedKey)
        if (existingValue != null) bwd.remove(existingValue)
        fwd.put(key, value)
        bwd.put(value, key)
        return existingValue
    }

    override fun remove(key: K): V? {
        val value = fwd.remove(key) ?: return null
        bwd.remove(value)
        return value
    }

    override fun putAll(from: Map<out K, V>) = from.forEach { (k, v) -> put(k, v) }

    override fun clear() {
        fwd.clear()
        bwd.clear()
    }

    // ── Views — fields, not getters, to avoid repeated allocation ─────────────

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>> = EntrySet()
    override val keys: MutableSet<K> = KeySet()
    override val values: MutableCollection<V> = ValueCollection()

    // ── equals / hashCode / toString ──────────────────────────────────────────

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is Map<*, *>) return false
        if (other.size != fwd.size) return false
        for ((k, v) in other) {
            @Suppress("UNCHECKED_CAST")
            if (fwd.get(k as K) != v) return false
        }
        return true
    }

    override fun hashCode(): Int {
        var h = 0
        val it = fwd.entryIterator()
        while (it.hasNext()) {
            val e = it.next()
            h += e.key.hashCode() xor (e.value?.hashCode() ?: 0)
        }
        return h
    }

    override fun toString(): String = buildString {
        append('{')
        var first = true
        val it = fwd.entryIterator()
        while (it.hasNext()) {
            if (!first) append(", ")
            val e = it.next()
            append(e.key); append('='); append(e.value)
            first = false
        }
        append('}')
    }

    // ── Inner classes ─────────────────────────────────────────────────────────

    private inner class SyncedIterator(
        private val delegate: MutableIterator<Map.Entry<K, V>>,
    ) : MutableIterator<MutableMap.MutableEntry<K, V>> {
        private var lastValue: Any? = NONE
        private var lastKey: Any? = NONE

        override fun hasNext() = delegate.hasNext()

        override fun next(): MutableMap.MutableEntry<K, V> {
            val entry = delegate.next()
            lastKey = entry.key
            lastValue = entry.value
            return MutableEntry(entry.key, entry.value)
        }

        override fun remove() {
            check(lastValue !== NONE) { "Call next() before remove()" }
            delegate.remove()
            @Suppress("UNCHECKED_CAST")
            bwd.remove(lastValue as V)
            lastValue = NONE
            lastKey = NONE
        }
    }

    private inner class MutableEntry(
        override val key: K,
        override var value: V,
    ) : MutableMap.MutableEntry<K, V> {
        override fun setValue(newValue: V): V {
            val old = value
            put(key, newValue)
            value = newValue
            return old
        }
        override fun equals(other: Any?): Boolean =
            other is Map.Entry<*, *> && key == other.key && value == other.value
        override fun hashCode(): Int = key.hashCode() xor (value?.hashCode() ?: 0)
        override fun toString(): String = "$key=$value"
    }

    private inner class EntrySet : AbstractMutableSet<MutableMap.MutableEntry<K, V>>() {
        override val size get() = fwd.size
        override fun iterator(): MutableIterator<MutableMap.MutableEntry<K, V>> =
            SyncedIterator(fwd.entryIterator())
        override fun add(element: MutableMap.MutableEntry<K, V>): Boolean =
            throw UnsupportedOperationException()
        override fun contains(element: MutableMap.MutableEntry<K, V>): Boolean =
            fwd.get(element.key) == element.value
    }

    private inner class KeySet : AbstractMutableSet<K>() {
        override val size get() = fwd.size
        override fun iterator() = object : MutableIterator<K> {
            private val backing = SyncedIterator(fwd.entryIterator())
            override fun hasNext() = backing.hasNext()
            override fun next() = backing.next().key
            override fun remove() = backing.remove()
        }
        override fun add(element: K): Boolean = throw UnsupportedOperationException()
        override fun contains(element: K) = fwd.containsKey(element)
    }

    private inner class ValueCollection : AbstractMutableCollection<V>() {
        override val size get() = fwd.size
        override fun iterator() = object : MutableIterator<V> {
            private val backing = SyncedIterator(fwd.entryIterator())
            override fun hasNext() = backing.hasNext()
            override fun next() = backing.next().value
            override fun remove() = backing.remove()
        }
        override fun add(element: V): Boolean = throw UnsupportedOperationException()
        override fun contains(element: @UnsafeVariance V) = bwd.containsKey(element)
    }

    companion object {
        private val NONE = Any()
    }
}
