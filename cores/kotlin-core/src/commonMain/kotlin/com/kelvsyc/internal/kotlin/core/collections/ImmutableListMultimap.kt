package com.kelvsyc.internal.kotlin.core.collections

import com.kelvsyc.kotlin.core.collections.ListMultimap

internal class ImmutableListMultimap<K, out V>(map: Map<K, List<@UnsafeVariance V>>) : ListMultimap<K, V> {
    private val backingMap: Map<K, List<V>> = map.entries
        .filter { it.value.isNotEmpty() }
        .associate { (k, vs) -> k to vs.toList() }

    override val asMap: Map<K, List<V>> = backingMap

    override val size: Int by lazy { backingMap.values.sumOf { it.size } }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ListMultimap<*, *>) return false
        return backingMap == other.asMap
    }

    override fun hashCode(): Int = backingMap.hashCode()

    override fun toString(): String = backingMap.toString()
}
