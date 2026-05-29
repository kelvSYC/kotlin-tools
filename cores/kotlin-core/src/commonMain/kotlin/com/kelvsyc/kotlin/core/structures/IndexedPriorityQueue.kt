package com.kelvsyc.kotlin.core.structures

/**
 * A priority queue that supports O(log n) priority updates via an inverse-position map from
 * elements to their current heap positions.
 *
 * Unlike [com.kelvsyc.kotlin.core.collections.PriorityQueue], where priority is derived
 * implicitly from the element via a `Comparator<in T>`, an [IndexedPriorityQueue] stores
 * priority [P] as explicit, mutable caller-supplied state independent of [T]. There is no
 * `(T) -> P` transformer; [P] is always supplied explicitly at every mutation site. The
 * `Comparator<in P>` operates purely on [P] values and never touches [T].
 *
 * The minimum element (lowest priority by the comparator) is at the head.
 *
 * ### Element identity
 *
 * Elements are identified by [equals]/[hashCode]. [T] must have stable [hashCode] and [equals]
 * while present in the queue; mutating an element in a way that changes its hash or equality
 * while it is in the queue produces undefined behavior. The enum-backed variant has no such
 * constraint because ordinal is the key.
 *
 * ### Thread safety
 *
 * Implementations are not thread-safe.
 */
interface IndexedPriorityQueue<T, P> {
    /** The number of elements currently in this queue. */
    val size: Int

    /** Returns `true` if this queue contains no elements. */
    fun isEmpty(): Boolean

    /** Returns `true` if this queue contains at least one element. */
    fun isNotEmpty(): Boolean = !isEmpty()

    /**
     * Returns the element with the minimum priority without removing it, or `null` if empty. O(1).
     */
    fun peekMin(): T?

    /** Returns `true` if [element] is currently in this queue. O(1). */
    fun contains(element: T): Boolean

    /**
     * Returns the current priority of [element], or `null` if [element] is not present. O(1).
     */
    fun getPriority(element: T): P?

    /**
     * Inserts [element] with [priority] into this queue. O(log n).
     *
     * @throws IllegalArgumentException if [element] is already present in this queue.
     */
    fun add(element: T, priority: P)

    /**
     * Removes and returns the element with the minimum priority, or `null` if this queue is
     * empty. O(log n).
     */
    fun pollMin(): T?

    /**
     * Removes [element] from this queue. Returns `true` if [element] was present. O(log n).
     */
    fun remove(element: T): Boolean

    /**
     * Updates the priority of [element] to [newPriority], which must be strictly less than the
     * current priority according to the comparator. O(log n).
     *
     * @throws NoSuchElementException if [element] is not present in this queue.
     * @throws IllegalArgumentException if `comparator.compare(newPriority, currentPriority) >= 0`.
     */
    fun decreaseKey(element: T, newPriority: P)

    /**
     * Updates the priority of [element] to [newPriority], which must be strictly greater than
     * the current priority according to the comparator. O(log n).
     *
     * @throws NoSuchElementException if [element] is not present in this queue.
     * @throws IllegalArgumentException if `comparator.compare(newPriority, currentPriority) <= 0`.
     */
    fun increaseKey(element: T, newPriority: P)

    /**
     * Updates the priority of [element] to [newPriority] in either direction. No-op if
     * [newPriority] compares equal to the current priority. O(log n).
     *
     * @throws NoSuchElementException if [element] is not present in this queue.
     */
    fun updatePriority(element: T, newPriority: P)
}
