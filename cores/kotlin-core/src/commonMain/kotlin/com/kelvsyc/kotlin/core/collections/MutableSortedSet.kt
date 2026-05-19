package com.kelvsyc.kotlin.core.collections

/**
 * A mutable [SortedSet] that supports adding and removing elements.
 *
 * Range views ([headSet], [tailSet], [subSet], [descendingSet]) return mutable snapshots — independent
 * [MutableSortedSet] copies at the time of the call.
 */
interface MutableSortedSet<E> : SortedSet<E>, MutableSet<E> {
    override fun headSet(toElement: E, inclusive: Boolean): MutableSortedSet<E>
    override fun tailSet(fromElement: E, inclusive: Boolean): MutableSortedSet<E>
    override fun subSet(fromElement: E, fromInclusive: Boolean, toElement: E, toInclusive: Boolean): MutableSortedSet<E>
    override fun descendingSet(): MutableSortedSet<E>
}
