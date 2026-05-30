package com.kelvsyc.internal.kotlin.core.collections

import com.kelvsyc.kotlin.core.collections.BiSortedSetMultimap
import com.kelvsyc.kotlin.core.collections.SortedMap
import com.kelvsyc.kotlin.core.collections.SortedSet
import com.kelvsyc.kotlin.core.collections.SortedSetMultimap

internal class ImmutableBiSortedSetMultimap<K, V>(private val backing: BiSortedSetMultimap<K, V>) : BiSortedSetMultimap<K, V> by backing {
    override val asMap: SortedMap<K, SortedSet<V>> get() = ImmutableSortedMap(backing.asMap)
    override val keys: SortedSet<K> get() = ImmutableSortedSet(backing.keys)
    override fun headMultimap(toKey: K, inclusive: Boolean): BiSortedSetMultimap<K, V> =
        ImmutableBiSortedSetMultimap(backing.headMultimap(toKey, inclusive))
    override fun tailMultimap(fromKey: K, inclusive: Boolean): BiSortedSetMultimap<K, V> =
        ImmutableBiSortedSetMultimap(backing.tailMultimap(fromKey, inclusive))
    override fun subMultimap(fromKey: K, fromInclusive: Boolean, toKey: K, toInclusive: Boolean): BiSortedSetMultimap<K, V> =
        ImmutableBiSortedSetMultimap(backing.subMultimap(fromKey, fromInclusive, toKey, toInclusive))
    override fun descendingMultimap(): BiSortedSetMultimap<K, V> =
        ImmutableBiSortedSetMultimap(backing.descendingMultimap())
    override fun equals(other: Any?): Boolean = backing.equals(other)
    override fun hashCode(): Int = backing.hashCode()
    override fun toString(): String = backing.toString()
}
