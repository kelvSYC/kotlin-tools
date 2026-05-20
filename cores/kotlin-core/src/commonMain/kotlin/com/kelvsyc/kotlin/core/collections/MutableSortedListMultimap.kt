package com.kelvsyc.kotlin.core.collections

/**
 * A mutable [SortedListMultimap] that supports adding and removing key-value pairs.
 *
 * Range views return mutable snapshots — independent [MutableSortedListMultimap] copies at the time of the call.
 */
interface MutableSortedListMultimap<K, V> : SortedListMultimap<K, V>, MutableListMultimap<K, V> {
    override fun headMultimap(toKey: K, inclusive: Boolean): MutableSortedListMultimap<K, V>
    override fun tailMultimap(fromKey: K, inclusive: Boolean): MutableSortedListMultimap<K, V>
    override fun subMultimap(fromKey: K, fromInclusive: Boolean, toKey: K, toInclusive: Boolean): MutableSortedListMultimap<K, V>
    override fun descendingMultimap(): MutableSortedListMultimap<K, V>
}
