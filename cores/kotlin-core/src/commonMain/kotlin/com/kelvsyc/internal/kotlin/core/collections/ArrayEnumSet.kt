package com.kelvsyc.internal.kotlin.core.collections

import com.kelvsyc.kotlin.core.collections.MutableEnumSet
import kotlin.enums.EnumEntries

@PublishedApi
internal class ArrayEnumSet<K : Enum<K>>(
    override val enumEntries: EnumEntries<K>,
) : AbstractMutableSet<K>(), MutableEnumSet<K> {
    private val backingArray: BooleanArray = BooleanArray(enumEntries.size)
    private var _size: Int = 0

    override val size: Int get() = _size

    override fun contains(element: K): Boolean = backingArray[element.ordinal]

    override fun add(element: K): Boolean {
        if (backingArray[element.ordinal]) return false
        backingArray[element.ordinal] = true
        _size++
        return true
    }

    override fun remove(element: K): Boolean {
        if (!backingArray[element.ordinal]) return false
        backingArray[element.ordinal] = false
        _size--
        return true
    }

    override fun clear() {
        backingArray.fill(false)
        _size = 0
    }

    private fun nextSetIndex(from: Int): Int {
        var i = from
        while (i < backingArray.size && !backingArray[i]) i++
        return i
    }

    override fun iterator(): MutableIterator<K> = object : MutableIterator<K> {
        private var cursor = nextSetIndex(0)
        private var lastReturned = -1

        override fun hasNext(): Boolean = cursor < backingArray.size

        override fun next(): K {
            if (!hasNext()) throw NoSuchElementException()
            lastReturned = cursor
            cursor = nextSetIndex(cursor + 1)
            return enumEntries[lastReturned]
        }

        override fun remove() {
            check(lastReturned >= 0) { "Call next() before remove()" }
            if (backingArray[lastReturned]) {
                backingArray[lastReturned] = false
                _size--
            }
            lastReturned = -1
        }
    }
}
