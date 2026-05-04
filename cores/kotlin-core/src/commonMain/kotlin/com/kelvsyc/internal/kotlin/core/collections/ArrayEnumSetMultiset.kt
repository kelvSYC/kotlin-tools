package com.kelvsyc.internal.kotlin.core.collections

import com.kelvsyc.kotlin.core.collections.EnumMap
import com.kelvsyc.kotlin.core.collections.Multiset
import com.kelvsyc.kotlin.core.collections.MutableEnumSetMultiset
import kotlin.enums.EnumEntries

@PublishedApi
internal class ArrayEnumSetMultiset<K : Enum<K>>(
    override val enumEntries: EnumEntries<K>,
) : MutableEnumSetMultiset<K> {
    private val counts: IntArray = IntArray(enumEntries.size)
    private var _size: Int = 0

    override val size: Int get() = _size

    override val elements: Set<K> get() = ElementSet()

    override val asMap: EnumMap<K, Int> get() = AsMapView()

    override fun count(element: K): Int = counts[element.ordinal]

    override fun isEmpty(): Boolean = _size == 0

    override fun contains(element: K): Boolean = counts[element.ordinal] > 0

    override fun containsAll(elements: Collection<K>): Boolean = elements.all { contains(it) }

    override fun iterator(): MutableIterator<K> = ExpandedIterator()

    override fun add(element: K): Boolean {
        counts[element.ordinal]++
        _size++
        return true
    }

    override fun add(element: K, count: Int) {
        require(count >= 0) { "count must be non-negative" }
        if (count == 0) return
        counts[element.ordinal] += count
        _size += count
    }

    override fun addAll(elements: Collection<K>): Boolean {
        if (elements.isEmpty()) return false
        elements.forEach { add(it) }
        return true
    }

    override fun remove(element: K): Boolean {
        val ord = element.ordinal
        if (counts[ord] <= 0) return false
        counts[ord]--
        _size--
        return true
    }

    override fun remove(element: K, count: Int): Int {
        require(count >= 0) { "count must be non-negative" }
        if (count == 0) return 0
        val ord = element.ordinal
        val current = counts[ord]
        if (current <= 0) return 0
        val actual = minOf(count, current)
        counts[ord] = current - actual
        _size -= actual
        return actual
    }

    override fun removeAll(elements: Collection<K>): Boolean {
        var changed = false
        for (e in elements) {
            val ord = e.ordinal
            if (counts[ord] > 0) {
                _size -= counts[ord]
                counts[ord] = 0
                changed = true
            }
        }
        return changed
    }

    override fun retainAll(elements: Collection<K>): Boolean {
        val retain = elements.mapTo(HashSet()) { it.ordinal }
        var changed = false
        for (i in counts.indices) {
            if (i !in retain && counts[i] > 0) {
                _size -= counts[i]
                counts[i] = 0
                changed = true
            }
        }
        return changed
    }

    override fun setCount(element: K, count: Int): Int {
        require(count >= 0) { "count must be non-negative" }
        val ord = element.ordinal
        val old = counts[ord]
        counts[ord] = count
        _size += count - old
        return old
    }

    override fun clear() {
        counts.fill(0)
        _size = 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Multiset<*>) return false
        return asMap == other.asMap
    }

    override fun hashCode(): Int = asMap.hashCode()

    override fun toString(): String = asMap.toString()

    private inner class ExpandedIterator : MutableIterator<K> {
        private var ordinal: Int = nextNonZero(0)
        private var remaining: Int = if (ordinal < counts.size) counts[ordinal] else 0
        private var lastOrdinal: Int = -1

        private fun nextNonZero(from: Int): Int {
            var i = from
            while (i < counts.size && counts[i] == 0) i++
            return i
        }

        override fun hasNext(): Boolean = remaining > 0 || nextNonZero(ordinal + 1) < counts.size

        override fun next(): K {
            if (remaining <= 0) {
                ordinal = nextNonZero(ordinal + 1)
                if (ordinal >= counts.size) throw NoSuchElementException()
                remaining = counts[ordinal]
            }
            remaining--
            lastOrdinal = ordinal
            return enumEntries[ordinal]
        }

        override fun remove() {
            check(lastOrdinal >= 0) { "Call next() before remove()" }
            if (counts[lastOrdinal] > 0) {
                counts[lastOrdinal]--
                _size--
            }
            lastOrdinal = -1
        }
    }

    private inner class ElementSet : AbstractSet<K>() {
        override val size: Int get() = counts.count { it > 0 }

        override fun contains(element: K): Boolean = counts[element.ordinal] > 0

        override fun iterator(): Iterator<K> = iterator {
            for (i in counts.indices) {
                if (counts[i] > 0) yield(enumEntries[i])
            }
        }
    }

    private inner class AsMapEntry(private val ordinal: Int) : Map.Entry<K, Int> {
        override val key: K get() = enumEntries[ordinal]
        override val value: Int get() = counts[ordinal]

        override fun equals(other: Any?): Boolean {
            if (other !is Map.Entry<*, *>) return false
            return key == other.key && value == other.value
        }

        override fun hashCode(): Int = key.hashCode() xor value

        override fun toString(): String = "$key=$value"
    }

    private inner class AsMapView : AbstractMap<K, Int>(), EnumMap<K, Int> {
        override val enumEntries: EnumEntries<K> get() = this@ArrayEnumSetMultiset.enumEntries

        override val size: Int get() = counts.count { it > 0 }

        override fun containsKey(key: K): Boolean = counts[key.ordinal] > 0

        override fun get(key: K): Int? {
            val c = counts[key.ordinal]
            return if (c > 0) c else null
        }

        override val entries: Set<Map.Entry<K, Int>> = AsMapEntrySet()

        private inner class AsMapEntrySet : AbstractSet<Map.Entry<K, Int>>() {
            override val size: Int get() = this@AsMapView.size

            override fun iterator(): Iterator<Map.Entry<K, Int>> = iterator {
                for (i in counts.indices) {
                    if (counts[i] > 0) yield(AsMapEntry(i))
                }
            }
        }
    }
}
