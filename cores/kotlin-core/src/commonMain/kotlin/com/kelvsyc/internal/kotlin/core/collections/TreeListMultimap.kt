package com.kelvsyc.internal.kotlin.core.collections

import com.kelvsyc.kotlin.core.collections.ListMultimap
import com.kelvsyc.kotlin.core.collections.MutableSortedListMultimap
import com.kelvsyc.kotlin.core.collections.SortedMap

internal class TreeListMultimap<K, V>(
    override val comparator: Comparator<in K>,
) : MutableSortedListMultimap<K, V> {

    private val map = TreeMap<K, MutableList<V>>(comparator)
    private var _size = 0

    override val asMap: SortedMap<K, List<V>> get() = map
    override val size: Int get() = _size

    override fun firstKey(): K = map.firstKey()
    override fun lastKey(): K = map.lastKey()
    override fun floorKey(key: K): K? = map.floorKey(key)
    override fun ceilingKey(key: K): K? = map.ceilingKey(key)
    override fun lowerKey(key: K): K? = map.lowerKey(key)
    override fun higherKey(key: K): K? = map.higherKey(key)

    override fun put(key: K, value: V) {
        map.getOrPut(key, ::mutableListOf).add(value)
        _size++
    }

    override fun putAll(key: K, values: Iterable<V>) {
        val list = values.toList()
        if (list.isEmpty()) return
        map.getOrPut(key, ::mutableListOf).addAll(list)
        _size += list.size
    }

    override fun replaceValues(key: K, values: Iterable<V>): List<V> {
        val newValues = values.toMutableList()
        return if (newValues.isEmpty()) {
            val old = map.remove(key) ?: return emptyList()
            _size -= old.size
            old
        } else {
            val old = map.put(key, newValues) ?: emptyList()
            _size += newValues.size - old.size
            old
        }
    }

    override fun remove(key: K): List<V> {
        val removed = map.remove(key) ?: return emptyList()
        _size -= removed.size
        return removed
    }

    override fun remove(key: K, value: V): Boolean {
        val list = map[key] ?: return false
        val removed = list.remove(value)
        if (removed) {
            _size--
            if (list.isEmpty()) map.remove(key)
        }
        return removed
    }

    override fun clear() {
        map.clear()
        _size = 0
    }

    override fun headMultimap(toKey: K, inclusive: Boolean): MutableSortedListMultimap<K, V> =
        TreeListMultimap<K, V>(comparator).also { result ->
            map.headMap(toKey, inclusive).forEach { (k, vs) -> vs.forEach { result.put(k, it) } }
        }

    override fun tailMultimap(fromKey: K, inclusive: Boolean): MutableSortedListMultimap<K, V> =
        TreeListMultimap<K, V>(comparator).also { result ->
            map.tailMap(fromKey, inclusive).forEach { (k, vs) -> vs.forEach { result.put(k, it) } }
        }

    override fun subMultimap(fromKey: K, fromInclusive: Boolean, toKey: K, toInclusive: Boolean): MutableSortedListMultimap<K, V> =
        TreeListMultimap<K, V>(comparator).also { result ->
            map.subMap(fromKey, fromInclusive, toKey, toInclusive).forEach { (k, vs) -> vs.forEach { result.put(k, it) } }
        }

    override fun descendingMultimap(): MutableSortedListMultimap<K, V> =
        TreeListMultimap<K, V>(comparator.reversed()).also { result ->
            map.descendingMap().forEach { (k, vs) -> vs.forEach { result.put(k, it) } }
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ListMultimap<*, *>) return false
        return map == other.asMap
    }

    override fun hashCode(): Int = map.hashCode()

    override fun toString(): String = map.toString()
}
