package com.kelvsyc.internal.kotlin.core.collections

import com.kelvsyc.kotlin.core.collections.SortedListMultimap
import com.kelvsyc.kotlin.core.collections.SortedMap
import com.kelvsyc.kotlin.core.collections.SortedSet

internal class ImmutableSortedListMultimap<K, V>(private val backing: SortedListMultimap<K, V>) : SortedListMultimap<K, V> by backing {
    override val asMap: SortedMap<K, List<V>> get() = ImmutableSortedMap(backing.asMap)
    override val keys: SortedSet<K> get() = ImmutableSortedSet(backing.keys)
    override fun headMultimap(toKey: K, inclusive: Boolean): SortedListMultimap<K, V> =
        ImmutableSortedListMultimap(backing.headMultimap(toKey, inclusive))
    override fun tailMultimap(fromKey: K, inclusive: Boolean): SortedListMultimap<K, V> =
        ImmutableSortedListMultimap(backing.tailMultimap(fromKey, inclusive))
    override fun subMultimap(fromKey: K, fromInclusive: Boolean, toKey: K, toInclusive: Boolean): SortedListMultimap<K, V> =
        ImmutableSortedListMultimap(backing.subMultimap(fromKey, fromInclusive, toKey, toInclusive))
    override fun descendingMultimap(): SortedListMultimap<K, V> =
        ImmutableSortedListMultimap(backing.descendingMultimap())
    override fun equals(other: Any?): Boolean = backing.equals(other)
    override fun hashCode(): Int = backing.hashCode()
    override fun toString(): String = backing.toString()
}
