package com.kelvsyc.kotlin.core.collections

/**
 * A mutable [BiEnumBiMap].
 */
interface MutableBiEnumBiMap<K : Enum<K>, V : Enum<V>> : BiEnumBiMap<K, V>, MutableEnumBiMap<K, V> {
    override val inverse: MutableEnumBiMap<V, K>
}
