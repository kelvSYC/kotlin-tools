package com.kelvsyc.internal.kotlin.core.collections

import com.kelvsyc.kotlin.core.collections.SetMultimap

internal class ImmutableSetMultimap<K, out V>(map: Map<K, Set<@UnsafeVariance V>>) : SetMultimap<K, V> {
    private val backingMap: Map<K, Set<V>> = map.entries
        .filter { it.value.isNotEmpty() }
        .associate { (k, vs) -> k to vs.toSet() }

    override val asMap: Map<K, Set<V>> = backingMap

    override val size: Int by lazy { backingMap.values.sumOf { it.size } }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SetMultimap<*, *>) return false
        return backingMap == other.asMap
    }

    override fun hashCode(): Int = backingMap.hashCode()

    override fun toString(): String = backingMap.toString()
}
