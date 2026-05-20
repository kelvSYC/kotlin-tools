package com.kelvsyc.internal.kotlin.core.collections

import com.kelvsyc.kotlin.core.collections.MutableSortedSetMultimap
import com.kelvsyc.kotlin.core.collections.SetMultimap
import com.kelvsyc.kotlin.core.collections.SortedMap

internal class TreeSetMultimap<K, V>(
    override val comparator: Comparator<in K>,
) : MutableSortedSetMultimap<K, V> {

    private val map = TreeMap<K, MutableSet<V>>(comparator)

    override val asMap: SortedMap<K, Set<V>> get() = map
    override val size: Int get() = map.values.sumOf { it.size }

    override fun firstKey(): K = map.firstKey()
    override fun lastKey(): K = map.lastKey()
    override fun floorKey(key: K): K? = map.floorKey(key)
    override fun ceilingKey(key: K): K? = map.ceilingKey(key)
    override fun lowerKey(key: K): K? = map.lowerKey(key)
    override fun higherKey(key: K): K? = map.higherKey(key)

    override fun put(key: K, value: V): Boolean =
        map.getOrPut(key, ::linkedSetOf).add(value)

    override fun replaceValues(key: K, values: Iterable<V>): Set<V> {
        val newValues = values.toCollection(LinkedHashSet())
        return if (newValues.isEmpty()) {
            map.remove(key) ?: emptySet()
        } else {
            map.put(key, newValues) ?: emptySet()
        }
    }

    override fun remove(key: K): Set<V> = map.remove(key) ?: emptySet()

    override fun remove(key: K, value: V): Boolean {
        val set = map[key] ?: return false
        val removed = set.remove(value)
        if (removed && set.isEmpty()) map.remove(key)
        return removed
    }

    override fun clear() = map.clear()

    override fun headMultimap(toKey: K, inclusive: Boolean): MutableSortedSetMultimap<K, V> =
        TreeSetMultimap<K, V>(comparator).also { result ->
            map.headMap(toKey, inclusive).forEach { (k, vs) -> vs.forEach { result.put(k, it) } }
        }

    override fun tailMultimap(fromKey: K, inclusive: Boolean): MutableSortedSetMultimap<K, V> =
        TreeSetMultimap<K, V>(comparator).also { result ->
            map.tailMap(fromKey, inclusive).forEach { (k, vs) -> vs.forEach { result.put(k, it) } }
        }

    override fun subMultimap(fromKey: K, fromInclusive: Boolean, toKey: K, toInclusive: Boolean): MutableSortedSetMultimap<K, V> =
        TreeSetMultimap<K, V>(comparator).also { result ->
            map.subMap(fromKey, fromInclusive, toKey, toInclusive).forEach { (k, vs) -> vs.forEach { result.put(k, it) } }
        }

    override fun descendingMultimap(): MutableSortedSetMultimap<K, V> =
        TreeSetMultimap<K, V>(comparator.reversed()).also { result ->
            map.descendingMap().forEach { (k, vs) -> vs.forEach { result.put(k, it) } }
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SetMultimap<*, *>) return false
        return map == other.asMap
    }

    override fun hashCode(): Int = map.hashCode()

    override fun toString(): String = map.toString()
}
