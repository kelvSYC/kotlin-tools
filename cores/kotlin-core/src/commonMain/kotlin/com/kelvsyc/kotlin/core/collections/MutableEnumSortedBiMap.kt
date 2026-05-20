package com.kelvsyc.kotlin.core.collections

/**
 * A mutable [EnumSortedBiMap].
 *
 * *Not yet implemented — no backing implementation or factory functions exist.*
 */
interface MutableEnumSortedBiMap<K : Enum<K>, V> : EnumSortedBiMap<K, V>, MutableEnumBiMap<K, V> {
    override val inverse: MutableSortedEnumBiMap<V, K>
}
