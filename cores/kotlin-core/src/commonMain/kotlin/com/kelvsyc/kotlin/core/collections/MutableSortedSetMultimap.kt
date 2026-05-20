package com.kelvsyc.kotlin.core.collections

/**
 * A mutable [SortedSetMultimap] that supports adding and removing key-value pairs.
 *
 * Range views return mutable snapshots — independent [MutableSortedSetMultimap] copies at the time of the call.
 */
interface MutableSortedSetMultimap<K, V> : SortedSetMultimap<K, V>, MutableSetMultimap<K, V> {
    override fun headMultimap(toKey: K, inclusive: Boolean): MutableSortedSetMultimap<K, V>
    override fun tailMultimap(fromKey: K, inclusive: Boolean): MutableSortedSetMultimap<K, V>
    override fun subMultimap(fromKey: K, fromInclusive: Boolean, toKey: K, toInclusive: Boolean): MutableSortedSetMultimap<K, V>
    override fun descendingMultimap(): MutableSortedSetMultimap<K, V>
}
