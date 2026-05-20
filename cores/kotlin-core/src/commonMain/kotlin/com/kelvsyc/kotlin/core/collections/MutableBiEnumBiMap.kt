package com.kelvsyc.kotlin.core.collections

/**
 * A mutable [BiEnumBiMap].
 *
 * *Not yet implemented — no backing implementation or factory functions exist.*
 */
interface MutableBiEnumBiMap<K : Enum<K>, V : Enum<V>> : BiEnumBiMap<K, V>, MutableEnumBiMap<K, V> {
    override val inverse: MutableEnumBiMap<V, K>
}
