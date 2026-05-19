package com.kelvsyc.internal.kotlin.core.collections

import com.kelvsyc.kotlin.core.collections.Multiset
import com.kelvsyc.kotlin.core.collections.MutableSortedMultiset
import com.kelvsyc.kotlin.core.collections.SortedMap
import com.kelvsyc.kotlin.core.collections.SortedSet

internal class TreeMultiset<E>(override val comparator: Comparator<in E>) : MutableSortedMultiset<E> {
    private val counts: TreeMap<E, Int> = TreeMap(comparator)

    override val size: Int get() = counts.values.sum()
    override fun isEmpty(): Boolean = counts.isEmpty()

    override val elements: SortedSet<E> get() = counts.keys
    override val asMap: SortedMap<E, Int> get() = counts

    override fun count(element: E): Int = counts[element] ?: 0
    override fun contains(element: E): Boolean = counts.containsKey(element)
    override fun containsAll(elements: Collection<E>): Boolean = elements.all { contains(it) }

    override fun iterator(): MutableIterator<E> {
        val expanded = counts.entries.flatMap { (e, c) -> List(c) { e } }.toMutableList()
        val inner = expanded.listIterator()
        return object : MutableIterator<E> {
            private var lastReturned: Any? = UNSET

            override fun hasNext(): Boolean = inner.hasNext()

            override fun next(): E = inner.next().also { lastReturned = it }

            override fun remove() {
                check(lastReturned !== UNSET) { "Call next() before remove()" }
                inner.remove()
                @Suppress("UNCHECKED_CAST")
                val element = lastReturned as E
                val newCount = (counts[element] ?: 0) - 1
                if (newCount <= 0) counts.remove(element) else counts[element] = newCount
                lastReturned = UNSET
            }
        }
    }

    override fun first(): E = counts.firstKey()
    override fun last(): E = counts.lastKey()
    override fun floor(element: E): E? = counts.floorKey(element)
    override fun ceiling(element: E): E? = counts.ceilingKey(element)
    override fun lower(element: E): E? = counts.lowerKey(element)
    override fun higher(element: E): E? = counts.higherKey(element)

    override fun add(element: E): Boolean {
        counts[element] = (counts[element] ?: 0) + 1
        return true
    }

    override fun add(element: E, count: Int) {
        require(count >= 0) { "count must be non-negative" }
        if (count == 0) return
        counts[element] = (counts[element] ?: 0) + count
    }

    override fun addAll(elements: Collection<E>): Boolean {
        if (elements.isEmpty()) return false
        elements.forEach { add(it) }
        return true
    }

    override fun remove(element: E): Boolean {
        val current = counts[element] ?: return false
        if (current <= 1) counts.remove(element) else counts[element] = current - 1
        return true
    }

    override fun remove(element: E, count: Int): Int {
        require(count >= 0) { "count must be non-negative" }
        if (count == 0) return 0
        val current = counts[element] ?: return 0
        val actual = minOf(count, current)
        if (actual >= current) counts.remove(element) else counts[element] = current - actual
        return actual
    }

    override fun removeAll(elements: Collection<E>): Boolean {
        var changed = false
        for (e in elements.toHashSet()) {
            if (counts.remove(e) != null) changed = true
        }
        return changed
    }

    override fun retainAll(elements: Collection<E>): Boolean {
        val toRetain = elements.toHashSet()
        val toRemove = counts.keys.filter { it !in toRetain }
        if (toRemove.isEmpty()) return false
        toRemove.forEach { counts.remove(it) }
        return true
    }

    override fun setCount(element: E, count: Int): Int {
        require(count >= 0) { "count must be non-negative" }
        val old = counts[element] ?: 0
        if (count == 0) counts.remove(element) else counts[element] = count
        return old
    }

    override fun clear() = counts.clear()

    override fun headMultiset(toElement: E, inclusive: Boolean): MutableSortedMultiset<E> =
        TreeMultiset<E>(comparator).also { r ->
            counts.headMap(toElement, inclusive).forEach { (e, c) -> r.counts[e] = c }
        }

    override fun tailMultiset(fromElement: E, inclusive: Boolean): MutableSortedMultiset<E> =
        TreeMultiset<E>(comparator).also { r ->
            counts.tailMap(fromElement, inclusive).forEach { (e, c) -> r.counts[e] = c }
        }

    override fun subMultiset(fromElement: E, fromInclusive: Boolean, toElement: E, toInclusive: Boolean): MutableSortedMultiset<E> =
        TreeMultiset<E>(comparator).also { r ->
            counts.subMap(fromElement, fromInclusive, toElement, toInclusive).forEach { (e, c) -> r.counts[e] = c }
        }

    override fun descendingMultiset(): MutableSortedMultiset<E> =
        TreeMultiset<E>(comparator.reversed()).also { r ->
            counts.descendingMap().forEach { (e, c) -> r.counts[e] = c }
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Multiset<*>) return false
        return counts == other.asMap
    }

    override fun hashCode(): Int = counts.hashCode()

    override fun toString(): String = counts.toString()

    companion object {
        private val UNSET = Any()
    }
}
