package com.kelvsyc.internal.kotlin.core.collections

import com.kelvsyc.kotlin.core.collections.SortedFlatMultimap

internal class ImmutableSortedFlatMultimap<K, V>(private val backing: SortedFlatMultimap<K, V>) : SortedFlatMultimap<K, V> by backing {
    override fun headMultimap(toPair: Pair<K, V>, inclusive: Boolean): SortedFlatMultimap<K, V> =
        ImmutableSortedFlatMultimap(backing.headMultimap(toPair, inclusive))
    override fun tailMultimap(fromPair: Pair<K, V>, inclusive: Boolean): SortedFlatMultimap<K, V> =
        ImmutableSortedFlatMultimap(backing.tailMultimap(fromPair, inclusive))
    override fun subMultimap(fromPair: Pair<K, V>, fromInclusive: Boolean, toPair: Pair<K, V>, toInclusive: Boolean): SortedFlatMultimap<K, V> =
        ImmutableSortedFlatMultimap(backing.subMultimap(fromPair, fromInclusive, toPair, toInclusive))
    override fun descendingMultimap(): SortedFlatMultimap<K, V> =
        ImmutableSortedFlatMultimap(backing.descendingMultimap())
    override fun equals(other: Any?): Boolean = backing.equals(other)
    override fun hashCode(): Int = backing.hashCode()
    override fun toString(): String = backing.toString()
}
