package com.kelvsyc.internal.kotlin.core.collections

import com.kelvsyc.kotlin.core.collections.BiSortedBiMap
import com.kelvsyc.kotlin.core.collections.SortedBiMap
import com.kelvsyc.kotlin.core.collections.SortedSet

internal class ImmutableBiSortedBiMap<K, V>(private val backing: BiSortedBiMap<K, V>) : BiSortedBiMap<K, V> by backing {
    override val keys: SortedSet<K> get() = ImmutableSortedSet(backing.keys)
    override val inverse: SortedBiMap<V, K> by lazy { ImmutableSortedBiMap(backing.inverse) }
    override fun headMap(toKey: K, inclusive: Boolean): SortedBiMap<K, V> =
        ImmutableSortedBiMap(backing.headMap(toKey, inclusive))
    override fun tailMap(fromKey: K, inclusive: Boolean): SortedBiMap<K, V> =
        ImmutableSortedBiMap(backing.tailMap(fromKey, inclusive))
    override fun subMap(fromKey: K, fromInclusive: Boolean, toKey: K, toInclusive: Boolean): SortedBiMap<K, V> =
        ImmutableSortedBiMap(backing.subMap(fromKey, fromInclusive, toKey, toInclusive))
    override fun descendingMap(): SortedBiMap<K, V> =
        ImmutableSortedBiMap(backing.descendingMap())
    override fun descendingKeySet(): SortedSet<K> =
        ImmutableSortedSet(backing.descendingKeySet())
    override fun equals(other: Any?): Boolean = backing.equals(other)
    override fun hashCode(): Int = backing.hashCode()
    override fun toString(): String = backing.toString()
}
