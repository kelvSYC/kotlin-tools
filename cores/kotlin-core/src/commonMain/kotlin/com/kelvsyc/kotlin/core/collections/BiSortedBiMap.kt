package com.kelvsyc.kotlin.core.collections

/**
 * A [SortedBiMap] where both the key side and the value side are comparator-ordered.
 *
 * Because values have a comparator, the [inverse] is itself a [SortedBiMap] (with key and value
 * comparators swapped). The name follows the convention established by [BiSortedSetMultimap].
 *
 * *Not yet implemented — no backing implementation or factory functions exist.*
 */
interface BiSortedBiMap<K, V> : SortedBiMap<K, V> {
    val valueComparator: Comparator<in V>

    override val inverse: SortedBiMap<V, K>
}
