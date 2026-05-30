package com.kelvsyc.kotlin.core.structures

/**
 * A double-ended priority queue that supports O(log n) extraction from both the minimum and
 * maximum ends, plus O(log n) priority updates via an inverse-position map from elements to
 * their current heap positions.
 *
 * Like [IndexedPriorityQueue], priority [P] is explicit, mutable, caller-supplied state
 * independent of the element [T]. There is no `(T) -> P` transformer.
 *
 * ### When to prefer [IndexedPriorityQueue]
 *
 * Despite the superficial analogy to the standard library's `Deque` extending a queue,
 * [IndexedPriorityDeque] is **not** strictly superior to [IndexedPriorityQueue]. The analogy
 * does not hold:
 *
 * - A `Deque` is strictly superior to a `Queue`: every queue operation runs in O(1) on a deque
 *   with no added overhead, so you can always substitute freely.
 * - An [IndexedPriorityDeque] carries a real constant-factor overhead on **all** operations —
 *   including when only one end is ever used. A min-heap sift visits the parent once per level
 *   with a single comparison. A min-max heap sift must first determine the current node's level
 *   (a bit operation on the heap index) and then apply different traversal logic depending on
 *   whether that level is a min-level or a max-level. This overhead appears on every sift step,
 *   not only when the max end is exercised.
 *
 * **Rule of thumb:** Prefer [IndexedPriorityQueue] when the algorithm only extracts from one
 * end (Dijkstra, Prim, A*, k-way merge, Huffman). Use [IndexedPriorityDeque] only when both
 * ends are genuinely needed (sliding-window median, scheduling with dual eviction, problems
 * requiring simultaneous min/max tracking).
 *
 * This is also why [IndexedPriorityDeque] does not extend [IndexedPriorityQueue]: allowing
 * substitution at the type level would silently introduce that overhead without any indication
 * at the call site.
 *
 * ### Underlying structure
 *
 * Backed by a min-max heap (Atkinson et al., 1986): a complete binary tree stored in an array
 * where even levels are min-levels (every node ≤ all descendants) and odd levels are max-levels
 * (every node ≥ all descendants). The minimum is always at the root; the maximum is at one of
 * the two root children (whichever has the higher priority).
 *
 * ### Element identity
 *
 * Elements are identified by [equals]/[hashCode]. [T] must have stable [hashCode] and [equals]
 * while present; mutating an element in a way that affects its hash or equality produces
 * undefined behavior. The enum-backed variant has no such constraint because ordinal is the key.
 *
 * ### Thread safety
 *
 * Implementations are not thread-safe.
 */
interface IndexedPriorityDeque<T, P> {
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

    /**
     * Returns the element with the maximum priority without removing it, or `null` if empty. O(1).
     */
    fun peekMax(): T?

    /** Returns `true` if [element] is currently in this queue. O(1). */
    fun contains(element: T): Boolean

    /**
     * Returns the current priority of [element], or `null` if not present. O(1).
     */
    fun getPriority(element: T): P?

    /**
     * Inserts [element] with [priority] into this queue. O(log n).
     *
     * @throws IllegalArgumentException if [element] is already present.
     */
    fun add(element: T, priority: P)

    /**
     * Removes and returns the element with the minimum priority, or `null` if empty. O(log n).
     */
    fun pollMin(): T?

    /**
     * Removes and returns the element with the maximum priority, or `null` if empty. O(log n).
     */
    fun pollMax(): T?

    /**
     * Removes [element] from this queue. Returns `true` if [element] was present. O(log n).
     */
    fun remove(element: T): Boolean

    /**
     * Updates the priority of [element] to [newPriority], which must be strictly less than the
     * current priority. O(log n).
     *
     * @throws NoSuchElementException if [element] is not present.
     * @throws IllegalArgumentException if `comparator.compare(newPriority, currentPriority) >= 0`.
     */
    fun decreaseKey(element: T, newPriority: P)

    /**
     * Updates the priority of [element] to [newPriority], which must be strictly greater than
     * the current priority. O(log n).
     *
     * @throws NoSuchElementException if [element] is not present.
     * @throws IllegalArgumentException if `comparator.compare(newPriority, currentPriority) <= 0`.
     */
    fun increaseKey(element: T, newPriority: P)

    /**
     * Updates the priority of [element] to [newPriority] in either direction. No-op if equal. O(log n).
     *
     * @throws NoSuchElementException if [element] is not present.
     */
    fun updatePriority(element: T, newPriority: P)
}
