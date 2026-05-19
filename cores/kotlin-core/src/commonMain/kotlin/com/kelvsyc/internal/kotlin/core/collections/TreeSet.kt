package com.kelvsyc.internal.kotlin.core.collections

import com.kelvsyc.kotlin.core.collections.MutableSortedMap
import com.kelvsyc.kotlin.core.collections.MutableSortedSet

/**
 * [MutableSortedSet] backed by a [TreeMap]`<E, Unit>`. All set operations delegate to the map's key operations.
 */
internal class TreeSet<E>(
    override val comparator: Comparator<in E>,
) : AbstractMutableSet<E>(), MutableSortedSet<E> {

    private val map = TreeMap<E, Unit>(comparator)

    override val size: Int get() = map.size

    override fun add(element: E): Boolean {
        if (map.containsKey(element)) return false
        map[element] = Unit
        return true
    }

    override fun remove(element: E): Boolean {
        if (!map.containsKey(element)) return false
        map.remove(element)
        return true
    }

    override fun iterator(): MutableIterator<E> {
        val entryIt = map.entries.iterator()
        return object : MutableIterator<E> {
            override fun hasNext() = entryIt.hasNext()
            override fun next() = entryIt.next().key
            override fun remove() = entryIt.remove()
        }
    }

    override fun contains(element: E): Boolean = map.containsKey(element)

    override fun clear() = map.clear()

    override fun first(): E = map.firstKey()
    override fun last(): E = map.lastKey()

    override fun floor(element: E): E? = map.floorKey(element)
    override fun ceiling(element: E): E? = map.ceilingKey(element)
    override fun lower(element: E): E? = map.lowerKey(element)
    override fun higher(element: E): E? = map.higherKey(element)

    override fun headSet(toElement: E, inclusive: Boolean): MutableSortedSet<E> =
        snapshotFromSortedMap(map.headMap(toElement, inclusive))

    override fun tailSet(fromElement: E, inclusive: Boolean): MutableSortedSet<E> =
        snapshotFromSortedMap(map.tailMap(fromElement, inclusive))

    override fun subSet(fromElement: E, fromInclusive: Boolean, toElement: E, toInclusive: Boolean): MutableSortedSet<E> =
        snapshotFromSortedMap(map.subMap(fromElement, fromInclusive, toElement, toInclusive))

    override fun descendingSet(): MutableSortedSet<E> =
        snapshotFromSortedMap(map.descendingMap())

    private fun snapshotFromSortedMap(m: MutableSortedMap<E, *>): MutableSortedSet<E> {
        val s = TreeSet(m.comparator)
        m.keys.forEach { s.add(it) }
        return s
    }
}
