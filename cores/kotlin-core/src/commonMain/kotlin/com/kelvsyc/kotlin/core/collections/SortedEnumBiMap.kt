package com.kelvsyc.kotlin.core.collections

import kotlin.enums.EnumEntries

/**
 * A [SortedBiMap] whose value side is enum-backed.
 *
 * The inverse has enum-backed keys (original values) and sorted values (original keys), so
 * [inverse] is typed as [EnumSortedBiMap]. These two interfaces are mutual inverses.
 *
 * *Not yet implemented — no backing implementation or factory functions exist.*
 */
interface SortedEnumBiMap<K, V : Enum<V>> : SortedBiMap<K, V> {
    val valueEnumEntries: EnumEntries<V>

    override val inverse: EnumSortedBiMap<V, K>
}
