package com.kelvsyc.internal.kotlin.core.collections

import kotlin.enums.EnumEntries

internal class ArrayMapStore<K : Enum<K>, V>(
    private val enumEntries: EnumEntries<K>,
) : MapStore<K, V> {
    private val backingArray: Array<Any?> = Array(enumEntries.size) { ABSENT }
    private var _size: Int = 0

    override val size: Int get() = _size

    @Suppress("UNCHECKED_CAST")
    override fun get(key: K): V? {
        val v = backingArray[key.ordinal]
        return if (v === ABSENT) null else v as V
    }

    override fun put(key: K, value: V) {
        if (backingArray[key.ordinal] === ABSENT) _size++
        backingArray[key.ordinal] = value
    }

    @Suppress("UNCHECKED_CAST")
    override fun remove(key: K): V? {
        val old = backingArray[key.ordinal]
        if (old === ABSENT) return null
        backingArray[key.ordinal] = ABSENT
        _size--
        return old as V
    }

    override fun containsKey(key: K): Boolean = backingArray[key.ordinal] !== ABSENT

    override fun clear() {
        backingArray.fill(ABSENT)
        _size = 0
    }

    override fun entryIterator(): MutableIterator<Map.Entry<K, V>> = OrdinalIterator()

    private fun nextSetIndex(from: Int): Int {
        var i = from
        while (i < backingArray.size && backingArray[i] === ABSENT) i++
        return i
    }

    private inner class Entry(override val key: K, override val value: V) : Map.Entry<K, V>

    private inner class OrdinalIterator : MutableIterator<Map.Entry<K, V>> {
        private var cursor = nextSetIndex(0)
        private var lastReturned = -1

        override fun hasNext(): Boolean = cursor < backingArray.size

        @Suppress("UNCHECKED_CAST")
        override fun next(): Map.Entry<K, V> {
            if (!hasNext()) throw NoSuchElementException()
            lastReturned = cursor
            cursor = nextSetIndex(cursor + 1)
            return Entry(enumEntries[lastReturned], backingArray[lastReturned] as V)
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

    companion object {
        private val ABSENT = Any()
    }
}
