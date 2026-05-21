package com.kelvsyc.kotlin.core.collections

/**
 * A mutable [BiSortedBiMap].
 */
interface MutableBiSortedBiMap<K, V> : BiSortedBiMap<K, V>, MutableSortedBiMap<K, V> {
    override val inverse: MutableSortedBiMap<V, K>
}
