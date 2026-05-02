package com.kelvsyc.internal.kotlin.core.collections

import com.kelvsyc.kotlin.core.collections.MutableSetMultimap
import com.kelvsyc.kotlin.core.collections.SetMultimap

internal class LinkedHashSetMultimap<K, V>(initialCapacity: Int = -1) : MutableSetMultimap<K, V> {
    private val map: LinkedHashMap<K, MutableSet<V>> =
        if (initialCapacity >= 0) LinkedHashMap(initialCapacity) else LinkedHashMap()

    override val asMap: Map<K, Set<V>> get() = map

    override val size: Int get() = map.values.sumOf { it.size }

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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SetMultimap<*, *>) return false
        return map == other.asMap
    }

    override fun hashCode(): Int = map.hashCode()

    override fun toString(): String = map.toString()
}
