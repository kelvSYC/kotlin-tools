package com.kelvsyc.internal.kotlin.core.collections

import com.kelvsyc.kotlin.core.collections.ListMultiset
import com.kelvsyc.kotlin.core.collections.MutableListMultiset

internal class LinkedHashListMultiset<E>(initialCapacity: Int = -1) : MutableListMultiset<E> {
    private val insertionOrder = mutableListOf<E>()
    private val counts: LinkedHashMap<E, Int> =
        if (initialCapacity >= 0) LinkedHashMap(initialCapacity) else LinkedHashMap()

    override val size: Int get() = insertionOrder.size
    override val elements: Set<E> get() = counts.keys
    override val asMap: Map<E, Int> get() = counts

    override fun count(element: E): Int = counts[element] ?: 0

    override fun isEmpty(): Boolean = insertionOrder.isEmpty()

    override fun contains(element: E): Boolean = counts.containsKey(element)

    override fun containsAll(elements: Collection<E>): Boolean = elements.all { contains(it) }

    override fun iterator(): MutableIterator<E> {
        val inner = insertionOrder.listIterator()
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

    override fun add(element: E): Boolean {
        insertionOrder.add(element)
        counts[element] = (counts[element] ?: 0) + 1
        return true
    }

    override fun add(element: E, count: Int) {
        require(count >= 0) { "count must be non-negative" }
        if (count == 0) return
        repeat(count) { insertionOrder.add(element) }
        counts[element] = (counts[element] ?: 0) + count
    }

    override fun addAll(elements: Collection<E>): Boolean {
        if (elements.isEmpty()) return false
        elements.forEach { add(it) }
        return true
    }

    override fun remove(element: E): Boolean = remove(element, 1) > 0

    override fun remove(element: E, count: Int): Int {
        require(count >= 0) { "count must be non-negative" }
        if (count == 0) return 0
        val current = counts[element] ?: return 0
        val actual = minOf(count, current)
        var removed = 0
        val it = insertionOrder.iterator()
        while (it.hasNext() && removed < actual) {
            if (it.next() == element) {
                it.remove()
                removed++
            }
        }
        if (actual >= current) counts.remove(element) else counts[element] = current - actual
        return actual
    }

    override fun removeAll(elements: Collection<E>): Boolean {
        val toRemove = elements.toHashSet()
        val changed = insertionOrder.removeAll { it in toRemove }
        if (changed) counts.keys.removeAll(toRemove)
        return changed
    }

    override fun retainAll(elements: Collection<E>): Boolean {
        val toKeep = elements.toHashSet()
        val changed = insertionOrder.removeAll { it !in toKeep }
        if (changed) counts.keys.retainAll(toKeep)
        return changed
    }

    override fun setCount(element: E, count: Int): Int {
        require(count >= 0) { "count must be non-negative" }
        val old = counts[element] ?: 0
        when {
            count > old -> add(element, count - old)
            count < old -> remove(element, old - count)
        }
        return old
    }

    override fun clear() {
        insertionOrder.clear()
        counts.clear()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ListMultiset<*>) return false
        return counts == other.asMap
    }

    override fun hashCode(): Int = counts.hashCode()

    override fun toString(): String = insertionOrder.toString()

    companion object {
        private val UNSET = Any()
    }
}
