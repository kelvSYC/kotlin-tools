package com.kelvsyc.kotlin.core.collections

import kotlin.enums.EnumEntries

/**
 * An [EnumBiMap] where both the key side and the value side are enum-backed.
 *
 * Because values are an enum type, the [inverse] is itself an [EnumBiMap].
 */
interface BiEnumBiMap<K : Enum<K>, V : Enum<V>> : EnumBiMap<K, V> {
    val valueEnumEntries: EnumEntries<V>

    override val inverse: EnumBiMap<V, K>
}
