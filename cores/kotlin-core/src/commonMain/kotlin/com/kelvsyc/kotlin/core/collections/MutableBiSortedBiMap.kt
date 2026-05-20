package com.kelvsyc.kotlin.core.collections

/**
 * A mutable [BiSortedBiMap].
 *
 * *Not yet implemented — no backing implementation or factory functions exist.*
 */
interface MutableBiSortedBiMap<K, V> : BiSortedBiMap<K, V>, MutableSortedBiMap<K, V> {
    override val inverse: MutableSortedBiMap<V, K>
}
