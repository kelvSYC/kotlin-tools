package com.kelvsyc.kotlin.core.collections

/**
 * A mutable [SortedEnumBiMap].
 *
 * *Not yet implemented — no backing implementation or factory functions exist.*
 */
interface MutableSortedEnumBiMap<K, V : Enum<V>> : SortedEnumBiMap<K, V>, MutableSortedBiMap<K, V> {
    override val inverse: MutableEnumSortedBiMap<V, K>
}
