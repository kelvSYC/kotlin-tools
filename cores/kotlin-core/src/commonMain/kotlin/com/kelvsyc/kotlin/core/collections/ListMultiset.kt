package com.kelvsyc.kotlin.core.collections

/**
 * A `ListMultiset` is primarily an ordered collection of elements that also provides a multiset view. The overall
 * insertion order of all elements is preserved: [iterator] and [toList] reflect the order in which elements were
 * added. Duplicate elements are preserved as distinct occurrences.
 *
 * [equals] and [hashCode] are count-based and order-insensitive: two multisets are equal if and only if [asMap]
 * returns equal [Map]s, regardless of insertion order. The ordered representation is accessible via iteration or
 * [toList].
 */
interface ListMultiset<out E> : Collection<E> {
    /**
     * Returns a read-only [Set] of the distinct elements in this multiset, in first-occurrence order.
     */
    val elements: Set<@UnsafeVariance E>

    /**
     * Returns a read-only view of this multiset as a [Map] mapping each distinct element to its occurrence count.
     */
    val asMap: Map<@UnsafeVariance E, Int>

    /**
     * Returns the number of occurrences of [element] in this multiset, or 0 if the element is absent.
     */
    fun count(element: @UnsafeVariance E): Int

    /**
     * Returns an [Iterator] over all elements in overall insertion order, including duplicate occurrences.
     */
    override fun iterator(): Iterator<E>

    /**
     * Returns `true` if [element] appears at least once in this multiset.
     */
    override fun contains(element: @UnsafeVariance E): Boolean

    /**
     * Returns `true` if every element in [elements] appears at least once in this multiset. This check is
     * element-based, not count-based: duplicate elements in [elements] do not require multiple occurrences.
     */
    override fun containsAll(elements: Collection<@UnsafeVariance E>): Boolean

    /**
     * Compares the specified object with this multiset for equality. Two multisets are equal if and only if [asMap]
     * returns [Map]s that are also equal.
     */
    override fun equals(other: Any?): Boolean

    /**
     * Returns the hash code for this multiset. The hash code is guaranteed to be the same as
     * [asMap][asMap]`.`[hashCode]`()`.
     */
    override fun hashCode(): Int
}
