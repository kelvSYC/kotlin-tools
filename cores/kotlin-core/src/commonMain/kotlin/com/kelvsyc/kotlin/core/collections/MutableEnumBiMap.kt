package com.kelvsyc.kotlin.core.collections

/**
 * A mutable [EnumBiMap].
 *
 * *Not yet implemented — no backing implementation or factory functions exist.*
 */
interface MutableEnumBiMap<K : Enum<K>, V> : EnumBiMap<K, V>, MutableBiMap<K, V> {
    override val inverse: MutableBiMap<V, K>
}
