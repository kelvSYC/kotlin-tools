package com.kelvsyc.internal.kotlin.core.collections

import com.kelvsyc.kotlin.core.collections.ListMultimap

internal class ImmutableListMultimap<K, out V>(pairs: List<Pair<K, @UnsafeVariance V>>) : ListMultimap<K, V> {
    private val list: List<Pair<K, V>> = pairs
    private val map: Map<K, List<V>> by lazy {
        list.groupBy({ it.first }, { it.second })
    }

    override val asMap: Map<K, List<V>> by this::map

    override val size: Int
        get() = list.size

    override val keys: Set<K> by lazy { map.keys }

    override val values: Collection<V>
        get() = list.map { it.second }

    override val entries: Collection<Pair<K, V>>
        get() = list

    override fun containsKey(key: K): Boolean = map.containsKey(key)

    override fun containsValue(value: @UnsafeVariance V): Boolean = list.any { it.second == value }

    override fun containsEntry(key: K, value: @UnsafeVariance V): Boolean = map[key]?.contains(value) == true

    override fun get(key: K): List<V> = map[key] ?: emptyList()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ListMultimap<*, *>) return false
        return map == other.asMap
    }

    override fun hashCode(): Int = map.hashCode()

    override fun toString(): String = list.toString()
}
