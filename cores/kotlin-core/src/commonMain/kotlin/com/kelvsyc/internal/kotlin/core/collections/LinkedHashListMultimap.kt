package com.kelvsyc.internal.kotlin.core.collections

import com.kelvsyc.kotlin.core.collections.ListMultimap
import com.kelvsyc.kotlin.core.collections.MutableListMultimap

internal class LinkedHashListMultimap<K, V>(initialCapacity: Int = -1) : MutableListMultimap<K, V> {
    private val map: LinkedHashMap<K, MutableList<V>> =
        if (initialCapacity >= 0) LinkedHashMap(initialCapacity) else LinkedHashMap()
    private var _size: Int = 0

    override val asMap: Map<K, List<V>> get() = map

    override val size: Int get() = _size

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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ListMultimap<*, *>) return false
        return map == other.asMap
    }

    override fun hashCode(): Int = map.hashCode()

    override fun toString(): String = map.toString()
}
