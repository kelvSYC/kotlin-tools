package com.kelvsyc.internal.kotlin.core.collections

import com.kelvsyc.kotlin.core.collections.ListMultimap
import com.kelvsyc.kotlin.core.collections.MutableListMultimap

internal class LinkedHashListMultimap<K, V> : MutableListMultimap<K, V> {
    private val map: LinkedHashMap<K, MutableList<V>> = LinkedHashMap()

    override val asMap: Map<K, List<V>> get() = map

    override val size: Int get() = map.values.sumOf { it.size }

    override val keys: Set<K> get() = map.keys

    override val values: Collection<V> get() = map.values.flatten()

    override val entries: Collection<Pair<K, V>>
        get() = map.entries.flatMap { (k, vs) -> vs.map { k to it } }

    override fun isEmpty(): Boolean = map.isEmpty()

    override fun containsKey(key: K): Boolean = map.containsKey(key)

    override fun containsValue(value: V): Boolean = map.values.any { it.contains(value) }

    override fun containsEntry(key: K, value: V): Boolean = map[key]?.contains(value) == true

    override fun get(key: K): List<V> = map[key] ?: emptyList()

    override fun put(key: K, value: V) {
        map.getOrPut(key, ::mutableListOf).add(value)
    }

    override fun replaceValues(key: K, values: Iterable<V>): List<V> {
        val newValues = values.toMutableList()
        return if (newValues.isEmpty()) {
            map.remove(key) ?: emptyList()
        } else {
            map.put(key, newValues) ?: emptyList()
        }
    }

    override fun remove(key: K): List<V> = map.remove(key) ?: emptyList()

    override fun remove(key: K, value: V): Boolean {
        val list = map[key] ?: return false
        val removed = list.remove(value)
        if (list.isEmpty()) map.remove(key)
        return removed
    }

    override fun clear() = map.clear()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ListMultimap<*, *>) return false
        return map == other.asMap
    }

    override fun hashCode(): Int = map.hashCode()

    override fun toString(): String = entries.toString()
}
