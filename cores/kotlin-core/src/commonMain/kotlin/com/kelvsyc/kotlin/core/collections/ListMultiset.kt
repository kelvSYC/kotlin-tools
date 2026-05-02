package com.kelvsyc.kotlin.core.collections

/**
 * A [Multiset] that preserves the overall insertion order of all elements: [iterator] and [toList] reflect the order
 * in which elements were added. Duplicate elements are preserved as distinct occurrences.
 *
 * [equals] and [hashCode] are count-based and order-insensitive: two multisets are equal if and only if [asMap]
 * returns equal [Map]s, regardless of insertion order. The ordered representation is accessible via iteration or
 * [toList].
 */
interface ListMultiset<out E> : Multiset<E> {
    /**
     * Returns an [Iterator] over all elements in overall insertion order, including duplicate occurrences.
     */
    override fun iterator(): Iterator<E>
}
