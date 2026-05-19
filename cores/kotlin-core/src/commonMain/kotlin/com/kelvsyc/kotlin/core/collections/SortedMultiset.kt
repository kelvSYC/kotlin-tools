package com.kelvsyc.kotlin.core.collections

/**
 * A [Multiset] whose elements are ordered by a [Comparator]. Iteration always yields elements in comparator order,
 * with each distinct element repeated [count] times.
 *
 * This is a Kotlin Multiplatform type with no JVM interoperability requirement.
 *
 * ### Comparator vs. equality
 *
 * The [comparator] governs element ordering and identity: two elements that compare as equal (result 0) are
 * placed in the same count bucket. [contains] uses structural equality ([equals]).
 *
 * ### Range views
 *
 * All range views ([headMultiset], [tailMultiset], [subMultiset], [descendingMultiset]) return **snapshots** —
 * independent copies at the time of the call. Subsequent mutations to the source are not reflected in the
 * snapshot, and vice versa.
 *
 * ### Equality
 *
 * Equality and [hashCode] are count-based and order-insensitive, consistent with [Multiset]: two
 * [SortedMultiset]s (or any [Multiset]s) are equal if and only if their [asMap] views are equal.
 */
interface SortedMultiset<E> : Multiset<E> {
    /**
     * The comparator that determines the order of elements in this multiset.
     */
    val comparator: Comparator<in E>

    /**
     * Returns a read-only [SortedSet] of the distinct elements in this multiset, in comparator order.
     */
    override val elements: SortedSet<E>

    /**
     * Returns a read-only view of this multiset as a [SortedMap] mapping each distinct element to its occurrence
     * count, in comparator order.
     */
    override val asMap: SortedMap<E, Int>

    /**
     * Returns the first (least) element in this multiset.
     * @throws NoSuchElementException if the multiset is empty.
     */
    fun first(): E

    /**
     * Returns the last (greatest) element in this multiset.
     * @throws NoSuchElementException if the multiset is empty.
     */
    fun last(): E

    /**
     * Returns the first (least) element, or `null` if the multiset is empty.
     */
    fun firstOrNull(): E? = if (isEmpty()) null else first()

    /**
     * Returns the last (greatest) element, or `null` if the multiset is empty.
     */
    fun lastOrNull(): E? = if (isEmpty()) null else last()

    /**
     * Returns the greatest element less than or equal to [element], or `null` if no such element exists.
     */
    fun floor(element: E): E?

    /**
     * Returns the least element greater than or equal to [element], or `null` if no such element exists.
     */
    fun ceiling(element: E): E?

    /**
     * Returns the greatest element strictly less than [element], or `null` if no such element exists.
     */
    fun lower(element: E): E?

    /**
     * Returns the least element strictly greater than [element], or `null` if no such element exists.
     */
    fun higher(element: E): E?

    /**
     * Returns a snapshot of all elements (with counts) strictly less than (or less than or equal to, if
     * [inclusive] is `true`) [toElement].
     */
    fun headMultiset(toElement: E, inclusive: Boolean): SortedMultiset<E>

    /**
     * Returns a snapshot of all elements (with counts) greater than or equal to (or strictly greater than, if
     * [inclusive] is `false`) [fromElement].
     */
    fun tailMultiset(fromElement: E, inclusive: Boolean): SortedMultiset<E>

    /**
     * Returns a snapshot of all elements (with counts) in the range from [fromElement] to [toElement].
     *
     * [fromInclusive] controls whether [fromElement] itself is included; [toInclusive] controls whether [toElement]
     * itself is included.
     *
     * @throws IllegalArgumentException if [fromElement] is greater than [toElement] by the [comparator].
     */
    fun subMultiset(fromElement: E, fromInclusive: Boolean, toElement: E, toInclusive: Boolean): SortedMultiset<E>

    /**
     * Returns a snapshot of all elements (with counts) in this multiset in reverse comparator order.
     */
    fun descendingMultiset(): SortedMultiset<E>
}
