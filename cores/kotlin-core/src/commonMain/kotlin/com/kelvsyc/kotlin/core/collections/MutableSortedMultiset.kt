package com.kelvsyc.kotlin.core.collections

/**
 * A mutable [SortedMultiset] that supports adding and removing elements.
 *
 * Range views ([headMultiset], [tailMultiset], [subMultiset], [descendingMultiset]) return mutable snapshots —
 * independent [MutableSortedMultiset] copies at the time of the call.
 */
interface MutableSortedMultiset<E> : SortedMultiset<E>, MutableMultiset<E> {
    override fun headMultiset(toElement: E, inclusive: Boolean): MutableSortedMultiset<E>
    override fun tailMultiset(fromElement: E, inclusive: Boolean): MutableSortedMultiset<E>
    override fun subMultiset(fromElement: E, fromInclusive: Boolean, toElement: E, toInclusive: Boolean): MutableSortedMultiset<E>
    override fun descendingMultiset(): MutableSortedMultiset<E>
}
