package com.kelvsyc.internal.kotlin.core.collections

import com.kelvsyc.kotlin.core.collections.ListMultimap
import com.kelvsyc.kotlin.core.collections.MutableListMultimap

internal class LinkedHashListMultimap<K, V>(initialCapacity: Int = -1) : MutableListMultimap<K, V> {
    private val map: LinkedHashMap<K, MutableList<V>> =
        if (initialCapacity >= 0) LinkedHashMap(initialCapacity) else LinkedHashMap()
    private val insertionOrder = mutableListOf<Pair<K, V>>()

    override val asMap: Map<K, List<V>> get() = map

    override val size: Int get() = insertionOrder.size

    override val keys: Set<K> get() = map.keys

    override val values: Collection<V> get() = insertionOrder.map { it.second }

    override val entries: Collection<Pair<K, V>> get() = insertionOrder

    override fun isEmpty(): Boolean = insertionOrder.isEmpty()

    override fun containsKey(key: K): Boolean = map.containsKey(key)

    override fun containsValue(value: V): Boolean = insertionOrder.any { it.second == value }

    override fun containsEntry(key: K, value: V): Boolean = map[key]?.contains(value) == true

    override fun get(key: K): List<V> = map[key] ?: emptyList()

    override fun put(key: K, value: V) {
        map.getOrPut(key, ::mutableListOf).add(value)
        insertionOrder.add(key to value)
    }

    override fun replaceValues(key: K, values: Iterable<V>): List<V> {
        val newValues = values.toMutableList()
        insertionOrder.removeAll { it.first == key }
        return if (newValues.isEmpty()) {
            map.remove(key) ?: emptyList()
        } else {
            insertionOrder.addAll(newValues.map { key to it })
            map.put(key, newValues) ?: emptyList()
        }
    }

    override fun remove(key: K): List<V> {
        insertionOrder.removeAll { it.first == key }
        return map.remove(key) ?: emptyList()
    }

    override fun remove(key: K, value: V): Boolean {
        val list = map[key] ?: return false
        val removed = list.remove(value)
        if (removed) {
            val idx = insertionOrder.indexOfFirst { it.first == key && it.second == value }
            if (idx >= 0) insertionOrder.removeAt(idx)
            if (list.isEmpty()) map.remove(key)
        }
        return removed
    }

    override fun clear() {
        map.clear()
        insertionOrder.clear()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ListMultimap<*, *>) return false
        return map == other.asMap
    }

    override fun hashCode(): Int = map.hashCode()

    override fun toString(): String = insertionOrder.toString()
}
