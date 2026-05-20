package com.kelvsyc.internal.kotlin.core.collections

import com.kelvsyc.kotlin.core.collections.BiSortedSetMultimap
import com.kelvsyc.kotlin.core.collections.MutableBiSortedSetMultimap
import com.kelvsyc.kotlin.core.collections.MutableSortedSet
import com.kelvsyc.kotlin.core.collections.SortedMap
import com.kelvsyc.kotlin.core.collections.SortedSet

internal class BiTreeSetMultimap<K, V>(
    override val comparator: Comparator<in K>,
    override val valueComparator: Comparator<in V>,
) : MutableBiSortedSetMultimap<K, V> {

    private val map = TreeMap<K, MutableSortedSet<V>>(comparator)

    override val asMap: SortedMap<K, SortedSet<V>> get() = map
    override val size: Int get() = map.values.sumOf { it.size }

    override fun firstKey(): K = map.firstKey()
    override fun lastKey(): K = map.lastKey()
    override fun floorKey(key: K): K? = map.floorKey(key)
    override fun ceilingKey(key: K): K? = map.ceilingKey(key)
    override fun lowerKey(key: K): K? = map.lowerKey(key)
    override fun higherKey(key: K): K? = map.higherKey(key)

    private fun newBucket(): MutableSortedSet<V> = TreeSet(valueComparator)

    override fun put(key: K, value: V): Boolean =
        map.getOrPut(key, ::newBucket).add(value)

    override fun replaceValues(key: K, values: Iterable<V>): Set<V> {
        val newValues = values.toCollection(TreeSet(valueComparator))
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

    override fun headMultimap(toKey: K, inclusive: Boolean): MutableBiSortedSetMultimap<K, V> =
        BiTreeSetMultimap<K, V>(comparator, valueComparator).also { result ->
            map.headMap(toKey, inclusive).forEach { (k, vs) -> vs.forEach { result.put(k, it) } }
        }

    override fun tailMultimap(fromKey: K, inclusive: Boolean): MutableBiSortedSetMultimap<K, V> =
        BiTreeSetMultimap<K, V>(comparator, valueComparator).also { result ->
            map.tailMap(fromKey, inclusive).forEach { (k, vs) -> vs.forEach { result.put(k, it) } }
        }

    override fun subMultimap(fromKey: K, fromInclusive: Boolean, toKey: K, toInclusive: Boolean): MutableBiSortedSetMultimap<K, V> =
        BiTreeSetMultimap<K, V>(comparator, valueComparator).also { result ->
            map.subMap(fromKey, fromInclusive, toKey, toInclusive).forEach { (k, vs) -> vs.forEach { result.put(k, it) } }
        }

    override fun descendingMultimap(): MutableBiSortedSetMultimap<K, V> =
        BiTreeSetMultimap<K, V>(comparator.reversed(), valueComparator).also { result ->
            map.descendingMap().forEach { (k, vs) -> vs.forEach { result.put(k, it) } }
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BiSortedSetMultimap<*, *>) return false
        return map == other.asMap
    }

    override fun hashCode(): Int = map.hashCode()

    override fun toString(): String = map.toString()
}
