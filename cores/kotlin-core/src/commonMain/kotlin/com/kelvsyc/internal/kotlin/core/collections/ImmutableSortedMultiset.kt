package com.kelvsyc.internal.kotlin.core.collections

import com.kelvsyc.kotlin.core.collections.SortedMap
import com.kelvsyc.kotlin.core.collections.SortedMultiset
import com.kelvsyc.kotlin.core.collections.SortedSet

internal class ImmutableSortedMultiset<E>(private val backing: SortedMultiset<E>) : SortedMultiset<E> by backing {
    override val elements: SortedSet<E> get() = ImmutableSortedSet(backing.elements)
    override val asMap: SortedMap<E, Int> get() = ImmutableSortedMap(backing.asMap)
    override fun headMultiset(toElement: E, inclusive: Boolean): SortedMultiset<E> =
        ImmutableSortedMultiset(backing.headMultiset(toElement, inclusive))
    override fun tailMultiset(fromElement: E, inclusive: Boolean): SortedMultiset<E> =
        ImmutableSortedMultiset(backing.tailMultiset(fromElement, inclusive))
    override fun subMultiset(fromElement: E, fromInclusive: Boolean, toElement: E, toInclusive: Boolean): SortedMultiset<E> =
        ImmutableSortedMultiset(backing.subMultiset(fromElement, fromInclusive, toElement, toInclusive))
    override fun descendingMultiset(): SortedMultiset<E> =
        ImmutableSortedMultiset(backing.descendingMultiset())
    override fun equals(other: Any?): Boolean = backing.equals(other)
    override fun hashCode(): Int = backing.hashCode()
    override fun toString(): String = backing.toString()
}
