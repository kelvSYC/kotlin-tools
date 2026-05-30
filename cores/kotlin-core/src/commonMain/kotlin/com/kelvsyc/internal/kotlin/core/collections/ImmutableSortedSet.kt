package com.kelvsyc.internal.kotlin.core.collections

import com.kelvsyc.kotlin.core.collections.SortedSet

internal class ImmutableSortedSet<E>(private val backing: SortedSet<E>) : SortedSet<E> by backing {
    override fun headSet(toElement: E, inclusive: Boolean): SortedSet<E> =
        ImmutableSortedSet(backing.headSet(toElement, inclusive))
    override fun tailSet(fromElement: E, inclusive: Boolean): SortedSet<E> =
        ImmutableSortedSet(backing.tailSet(fromElement, inclusive))
    override fun subSet(fromElement: E, fromInclusive: Boolean, toElement: E, toInclusive: Boolean): SortedSet<E> =
        ImmutableSortedSet(backing.subSet(fromElement, fromInclusive, toElement, toInclusive))
    override fun descendingSet(): SortedSet<E> =
        ImmutableSortedSet(backing.descendingSet())
    override fun equals(other: Any?): Boolean = backing.equals(other)
    override fun hashCode(): Int = backing.hashCode()
    override fun toString(): String = backing.toString()
}
