package com.kelvsyc.internal.kotlin.core.collections

import com.kelvsyc.kotlin.core.collections.MutableEnumMap
import kotlin.enums.EnumEntries

@PublishedApi
internal class ArrayEnumMap<K : Enum<K>, V>(
    override val enumEntries: EnumEntries<K>,
) : AbstractMutableMap<K, V>(), MutableEnumMap<K, V> {
    private val backingArray: Array<Any?> = Array(enumEntries.size) { ABSENT }
    private var _size: Int = 0

    override val size: Int get() = _size

    override fun containsKey(key: K): Boolean = backingArray[key.ordinal] !== ABSENT

    override fun containsValue(value: @UnsafeVariance V): Boolean =
        backingArray.any { it !== ABSENT && it == value }

    @Suppress("UNCHECKED_CAST")
    override fun get(key: K): V? {
        val v = backingArray[key.ordinal]
        return if (v === ABSENT) null else v as V
    }

    @Suppress("UNCHECKED_CAST")
    override fun put(key: K, value: V): V? {
        val old = backingArray[key.ordinal]
        backingArray[key.ordinal] = value
        if (old === ABSENT) {
            _size++
            return null
        }
        return old as V
    }

    @Suppress("UNCHECKED_CAST")
    override fun remove(key: K): V? {
        val old = backingArray[key.ordinal]
        if (old === ABSENT) return null
        backingArray[key.ordinal] = ABSENT
        _size--
        return old as V
    }

    override fun clear() {
        backingArray.fill(ABSENT)
        _size = 0
    }

    private fun nextSetIndex(from: Int): Int {
        var i = from
        while (i < backingArray.size && backingArray[i] === ABSENT) i++
        return i
    }

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>> = EntrySet()
    override val keys: MutableSet<K> = KeySet()
    override val values: MutableCollection<V> = Values()

    @Suppress("UNCHECKED_CAST")
    private inner class Entry(private val ordinal: Int) : MutableMap.MutableEntry<K, V> {
        override val key: K get() = enumEntries[ordinal]
        override val value: V get() = backingArray[ordinal] as V

        override fun setValue(newValue: V): V {
            val old = backingArray[ordinal] as V
            backingArray[ordinal] = newValue
            return old
        }

        override fun equals(other: Any?): Boolean {
            if (other !is Map.Entry<*, *>) return false
            return key == other.key && value == other.value
        }

        override fun hashCode(): Int = key.hashCode() xor (value?.hashCode() ?: 0)

        override fun toString(): String = "$key=$value"
    }

    private inner class EntryIterator : MutableIterator<MutableMap.MutableEntry<K, V>> {
        private var cursor = nextSetIndex(0)
        private var lastReturned = -1

        override fun hasNext(): Boolean = cursor < backingArray.size

        override fun next(): MutableMap.MutableEntry<K, V> {
            if (!hasNext()) throw NoSuchElementException()
            lastReturned = cursor
            cursor = nextSetIndex(cursor + 1)
            return Entry(lastReturned)
        }

        override fun remove() {
            check(lastReturned >= 0) { "Call next() before remove()" }
            if (backingArray[lastReturned] !== ABSENT) {
                backingArray[lastReturned] = ABSENT
                _size--
            }
            lastReturned = -1
        }
    }

    private inner class EntrySet : AbstractMutableSet<MutableMap.MutableEntry<K, V>>() {
        override val size: Int get() = _size

        override fun iterator(): MutableIterator<MutableMap.MutableEntry<K, V>> = EntryIterator()

        override fun add(element: MutableMap.MutableEntry<K, V>): Boolean =
            throw UnsupportedOperationException()

        override fun contains(element: MutableMap.MutableEntry<K, V>): Boolean {
            val v = backingArray[element.key.ordinal]
            return v !== ABSENT && v == element.value
        }

        override fun remove(element: MutableMap.MutableEntry<K, V>): Boolean {
            val ord = element.key.ordinal
            val v = backingArray[ord]
            if (v === ABSENT || v != element.value) return false
            backingArray[ord] = ABSENT
            _size--
            return true
        }

        override fun clear() = this@ArrayEnumMap.clear()
    }

    private inner class KeySet : AbstractMutableSet<K>() {
        override val size: Int get() = _size

        override fun iterator(): MutableIterator<K> = object : MutableIterator<K> {
            private val backing = EntryIterator()
            override fun hasNext() = backing.hasNext()
            override fun next() = backing.next().key
            override fun remove() = backing.remove()
        }

        override fun add(element: K): Boolean = throw UnsupportedOperationException()

        override fun contains(element: K): Boolean = containsKey(element)

        override fun remove(element: K): Boolean {
            if (!containsKey(element)) return false
            this@ArrayEnumMap.remove(element)
            return true
        }

        override fun clear() = this@ArrayEnumMap.clear()
    }

    private inner class Values : AbstractMutableCollection<V>() {
        override val size: Int get() = _size

        override fun iterator(): MutableIterator<V> = object : MutableIterator<V> {
            private val backing = EntryIterator()
            override fun hasNext() = backing.hasNext()
            override fun next() = backing.next().value
            override fun remove() = backing.remove()
        }

        override fun add(element: V): Boolean = throw UnsupportedOperationException()

        override fun clear() = this@ArrayEnumMap.clear()
    }

    companion object {
        private val ABSENT = Any()
    }
}
