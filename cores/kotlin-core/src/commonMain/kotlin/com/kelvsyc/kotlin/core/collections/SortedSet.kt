package com.kelvsyc.kotlin.core.collections

/**
 * A [Set] whose elements are ordered by a [Comparator]. Iteration always yields elements in comparator order.
 *
 * This is a Kotlin Multiplatform type with no JVM interoperability requirement; it does not extend
 * `java.util.SortedSet` or `java.util.NavigableSet`.
 *
 * ### Comparator vs. equality
 *
 * The [comparator] governs element ordering and membership: two elements that compare as equal (result 0) are
 * treated as duplicates — only one is retained. [contains] and [containsAll] use structural equality ([equals]).
 *
 * ### Range views
 *
 * All range views ([headSet], [tailSet], [subSet], [descendingSet]) return **snapshots** — independent copies at
 * the time of the call. Subsequent mutations to the source set are not reflected in the snapshot, and vice versa.
 * This follows Kotlin's collection idiom (cf. `filter`, `map`).
 *
 * ### entries ordering note
 *
 * [iterator] yields elements in comparator order. The static type is `Iterator<E>`, which carries no ordering
 * contract; the ordering guarantee is part of this interface's contract.
 */
interface SortedSet<E> : Set<E> {
    /**
     * The comparator that determines the order of elements in this set.
     */
    val comparator: Comparator<in E>

    /**
     * Returns the first (least) element in this set.
     * @throws NoSuchElementException if the set is empty.
     */
    fun first(): E

    /**
     * Returns the last (greatest) element in this set.
     * @throws NoSuchElementException if the set is empty.
     */
    fun last(): E

    /**
     * Returns the first (least) element, or `null` if the set is empty.
     */
    fun firstOrNull(): E? = if (isEmpty()) null else first()

    /**
     * Returns the last (greatest) element, or `null` if the set is empty.
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
     * Returns a snapshot of the elements strictly less than (or less than or equal to, if [inclusive] is `true`)
     * [toElement].
     */
    fun headSet(toElement: E, inclusive: Boolean): SortedSet<E>

    /**
     * Returns a snapshot of the elements greater than or equal to (or strictly greater than, if [inclusive] is
     * `false`) [fromElement].
     */
    fun tailSet(fromElement: E, inclusive: Boolean): SortedSet<E>

    /**
     * Returns a snapshot of elements in the range from [fromElement] to [toElement].
     *
     * [fromInclusive] controls whether [fromElement] itself is included; [toInclusive] controls whether [toElement]
     * itself is included.
     *
     * @throws IllegalArgumentException if [fromElement] is greater than [toElement] by the [comparator].
     */
    fun subSet(fromElement: E, fromInclusive: Boolean, toElement: E, toInclusive: Boolean): SortedSet<E>

    /**
     * Returns a snapshot of all elements in this set in reverse comparator order.
     */
    fun descendingSet(): SortedSet<E>
}
