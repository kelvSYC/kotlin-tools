package com.kelvsyc.internal.kotlin.core.collections

import com.kelvsyc.kotlin.core.collections.SortedMap
import com.kelvsyc.kotlin.core.collections.SortedSet
import com.kelvsyc.kotlin.core.collections.SortedSetMultimap

internal class ImmutableSortedSetMultimap<K, V>(private val backing: SortedSetMultimap<K, V>) : SortedSetMultimap<K, V> by backing {
    override val asMap: SortedMap<K, Set<V>> get() = ImmutableSortedMap(backing.asMap)
    override val keys: SortedSet<K> get() = ImmutableSortedSet(backing.keys)
    override fun headMultimap(toKey: K, inclusive: Boolean): SortedSetMultimap<K, V> =
        ImmutableSortedSetMultimap(backing.headMultimap(toKey, inclusive))
    override fun tailMultimap(fromKey: K, inclusive: Boolean): SortedSetMultimap<K, V> =
        ImmutableSortedSetMultimap(backing.tailMultimap(fromKey, inclusive))
    override fun subMultimap(fromKey: K, fromInclusive: Boolean, toKey: K, toInclusive: Boolean): SortedSetMultimap<K, V> =
        ImmutableSortedSetMultimap(backing.subMultimap(fromKey, fromInclusive, toKey, toInclusive))
    override fun descendingMultimap(): SortedSetMultimap<K, V> =
        ImmutableSortedSetMultimap(backing.descendingMultimap())
    override fun equals(other: Any?): Boolean = backing.equals(other)
    override fun hashCode(): Int = backing.hashCode()
    override fun toString(): String = backing.toString()
}
