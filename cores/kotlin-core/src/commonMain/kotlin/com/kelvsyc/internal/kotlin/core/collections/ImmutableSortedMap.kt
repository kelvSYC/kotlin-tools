package com.kelvsyc.internal.kotlin.core.collections

import com.kelvsyc.kotlin.core.collections.SortedMap
import com.kelvsyc.kotlin.core.collections.SortedSet

internal class ImmutableSortedMap<K, V>(private val backing: SortedMap<K, V>) : SortedMap<K, V> by backing {
    override val keys: SortedSet<K> get() = ImmutableSortedSet(backing.keys)
    override fun headMap(toKey: K, inclusive: Boolean): SortedMap<K, V> =
        ImmutableSortedMap(backing.headMap(toKey, inclusive))
    override fun tailMap(fromKey: K, inclusive: Boolean): SortedMap<K, V> =
        ImmutableSortedMap(backing.tailMap(fromKey, inclusive))
    override fun subMap(fromKey: K, fromInclusive: Boolean, toKey: K, toInclusive: Boolean): SortedMap<K, V> =
        ImmutableSortedMap(backing.subMap(fromKey, fromInclusive, toKey, toInclusive))
    override fun descendingMap(): SortedMap<K, V> =
        ImmutableSortedMap(backing.descendingMap())
    override fun descendingKeySet(): SortedSet<K> =
        ImmutableSortedSet(backing.descendingKeySet())
    override fun equals(other: Any?): Boolean = backing.equals(other)
    override fun hashCode(): Int = backing.hashCode()
    override fun toString(): String = backing.toString()
}
