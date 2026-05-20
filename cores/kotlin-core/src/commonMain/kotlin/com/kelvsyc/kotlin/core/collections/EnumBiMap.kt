package com.kelvsyc.kotlin.core.collections

import kotlin.enums.EnumEntries

/**
 * A [BiMap] whose key side is backed by an enum-indexed array.
 *
 * The value side carries no enum constraint; the [inverse] is therefore a plain [BiMap].
 * For a BiMap where both directions are enum-backed, see [BiEnumBiMap].
 *
 * *Not yet implemented — no backing implementation or factory functions exist.*
 */
interface EnumBiMap<K : Enum<K>, V> : BiMap<K, V> {
    val enumEntries: EnumEntries<K>

    override val inverse: BiMap<V, K>
}
