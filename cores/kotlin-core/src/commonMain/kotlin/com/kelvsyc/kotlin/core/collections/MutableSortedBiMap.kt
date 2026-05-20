package com.kelvsyc.kotlin.core.collections

/**
 * A mutable [SortedBiMap].
 */
interface MutableSortedBiMap<K, V> : SortedBiMap<K, V>, MutableBiMap<K, V> {
    override val inverse: MutableBiMap<V, K>
}
