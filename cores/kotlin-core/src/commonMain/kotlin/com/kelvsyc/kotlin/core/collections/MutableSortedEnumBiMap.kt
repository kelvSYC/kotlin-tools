package com.kelvsyc.kotlin.core.collections

/**
 * A mutable [SortedEnumBiMap].
 */
interface MutableSortedEnumBiMap<K, V : Enum<V>> : SortedEnumBiMap<K, V>, MutableSortedBiMap<K, V> {
    override val inverse: MutableEnumSortedBiMap<V, K>
}
