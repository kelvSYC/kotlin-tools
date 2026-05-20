package com.kelvsyc.kotlin.core.collections

/**
 * A mutable [SortedBiMap].
 *
 * *Not yet implemented — no backing implementation or factory functions exist.*
 */
interface MutableSortedBiMap<K, V> : SortedBiMap<K, V>, MutableBiMap<K, V> {
    override val inverse: MutableBiMap<V, K>
}
