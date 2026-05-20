package com.kelvsyc.kotlin.core.collections

/**
 * A mutable [EnumBiMap].
 */
interface MutableEnumBiMap<K : Enum<K>, V> : EnumBiMap<K, V>, MutableBiMap<K, V> {
    override val inverse: MutableBiMap<V, K>
}
