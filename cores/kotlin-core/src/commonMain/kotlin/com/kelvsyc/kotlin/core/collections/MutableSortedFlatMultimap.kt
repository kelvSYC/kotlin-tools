package com.kelvsyc.kotlin.core.collections

/**
 * A mutable [SortedFlatMultimap] that supports adding and removing key-value pairs while
 * maintaining pair-comparator order.
 *
 * All range views return mutable snapshots — independent copies that are themselves mutable.
 */
interface MutableSortedFlatMultimap<K, V> : SortedFlatMultimap<K, V>, MutableFlatMultimap<K, V> {
    override fun headMultimap(toPair: Pair<K, V>, inclusive: Boolean): MutableSortedFlatMultimap<K, V>
    override fun tailMultimap(fromPair: Pair<K, V>, inclusive: Boolean): MutableSortedFlatMultimap<K, V>
    override fun subMultimap(
        fromPair: Pair<K, V>, fromInclusive: Boolean,
        toPair: Pair<K, V>, toInclusive: Boolean,
    ): MutableSortedFlatMultimap<K, V>
    override fun descendingMultimap(): MutableSortedFlatMultimap<K, V>
}
