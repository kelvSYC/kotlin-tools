package com.kelvsyc.kotlin.core.collections

/**
 * A mutable [BiSortedSetMultimap] that supports adding and removing key-value pairs.
 *
 * Range views return mutable snapshots — independent [MutableBiSortedSetMultimap] copies at the time of the call.
 */
interface MutableBiSortedSetMultimap<K, V> : BiSortedSetMultimap<K, V>, MutableSortedSetMultimap<K, V> {
    override fun headMultimap(toKey: K, inclusive: Boolean): MutableBiSortedSetMultimap<K, V>
    override fun tailMultimap(fromKey: K, inclusive: Boolean): MutableBiSortedSetMultimap<K, V>
    override fun subMultimap(fromKey: K, fromInclusive: Boolean, toKey: K, toInclusive: Boolean): MutableBiSortedSetMultimap<K, V>
    override fun descendingMultimap(): MutableBiSortedSetMultimap<K, V>
}
