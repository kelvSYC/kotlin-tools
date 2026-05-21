package com.kelvsyc.kotlin.core.collections

/**
 * An [EnumBiMap] whose value side is comparator-ordered.
 *
 * The inverse has sorted keys (original values) and enum-backed values (original keys), so
 * [inverse] is typed as [SortedEnumBiMap]. These two interfaces are mutual inverses.
 */
interface EnumSortedBiMap<K : Enum<K>, V> : EnumBiMap<K, V> {
    val valueComparator: Comparator<in V>

    override val inverse: SortedEnumBiMap<V, K>
}
