package com.kelvsyc.kotlin.core.collections

/**
 * A double-ended priority queue that supports O(1) access to both the minimum and maximum
 * elements and O(log n) extraction from either end. The head of the queue is the least element
 * with respect to the supplied [Comparator].
 *
 * Like [PriorityQueue], priority is derived implicitly from the element via the [Comparator]; there
 * is no separate priority type.
 *
 * This is a Kotlin Multiplatform type with no JVM interoperability requirement; it does not implement
 * `java.util.Queue` or any equivalent.
 *
 * ### When to prefer [PriorityQueue]
 *
 * Despite the superficial analogy to the standard library's `Deque` extending a queue,
 * [PriorityDeque] is **not** strictly superior to [PriorityQueue]. The analogy does not hold:
 *
 * - A `Deque` is strictly superior to a `Queue`: every queue operation runs in O(1) on a deque
 *   with no added overhead, so you can always substitute freely.
 * - A [PriorityDeque] carries a real constant-factor overhead on **all** operations — including
 *   when only one end is ever used. A min-heap sift visits the parent once per level with a single
 *   comparison. A min-max heap sift must first determine the current node's level (a bit operation
 *   on the heap index) and then apply different traversal logic depending on whether that level is
 *   a min-level or a max-level. This overhead appears on every sift step, not only when the max end
 *   is exercised.
 *
 * **Rule of thumb:** Prefer [PriorityQueue] when the algorithm only extracts from one end (Dijkstra,
 * Prim, A*, k-way merge, Huffman). Use [PriorityDeque] only when both ends are genuinely needed
 * (sliding-window median, scheduling with dual eviction, problems requiring simultaneous min/max
 * tracking).
 *
 * This is also why [PriorityDeque] does not extend [PriorityQueue]: allowing substitution at the
 * type level would silently introduce that overhead without any indication at the call site.
 *
 * ### Underlying structure
 *
 * Backed by a min-max heap (Atkinson et al., 1986): a complete binary tree stored in an array
 * where even levels are min-levels (every node ≤ all descendants) and odd levels are max-levels
 * (every node ≥ all descendants). The minimum is always at the root; the maximum is at one of the
 * two root children (whichever is greater by the comparator).
 *
 * ### Nullability
 *
 * The type parameter [T] is unbounded. When [T] is non-nullable, [peekMin], [peekMax], [pollMin],
 * and [pollMax] return `null` only when the deque is empty. When [T] is nullable, a `null` return
 * is ambiguous: it may indicate either an empty deque or a `null` element at that end. Callers in
 * that case should check [isEmpty] before interpreting the result. This mirrors the standard
 * library's treatment of `firstOrNull` and similar functions on collections of nullable elements.
 *
 * ### Equality vs. comparator order
 *
 * [contains] and [remove] use structural equality ([equals]), not the [Comparator]. Two elements
 * that compare equal by the comparator are not necessarily equal by [equals], and vice versa.
 *
 * ### Element mutation
 *
 * Mutating an element in a way that changes its ordering relative to other elements while it
 * resides in the deque silently corrupts the heap invariant. As with any ordered collection,
 * callers are responsible for not mutating elements in such a way.
 *
 * ### Iteration order
 *
 * [iterator] visits elements in an unspecified order — specifically, the underlying heap-array
 * order, which is not sorted. For sorted consumption, use [drainSorted] or [toSortedList].
 *
 * ### Thread safety
 *
 * Implementations are not thread-safe.
 */
interface PriorityDeque<T> : MutableCollection<T> {

    /**
     * Returns the minimum element (head) without removing it, or `null` if the deque is empty.
     * See the class-level note on nullability when [T] is itself nullable. O(1).
     */
    fun peekMin(): T?

    /**
     * Returns the maximum element (tail) without removing it, or `null` if the deque is empty.
     * See the class-level note on nullability when [T] is itself nullable. O(1).
     */
    fun peekMax(): T?

    /**
     * Removes and returns the minimum element (head), or `null` if the deque is empty.
     * See the class-level note on nullability when [T] is itself nullable. O(log n).
     */
    fun pollMin(): T?

    /**
     * Removes and returns the maximum element (tail), or `null` if the deque is empty.
     * See the class-level note on nullability when [T] is itself nullable. O(log n).
     */
    fun pollMax(): T?

    /**
     * Inserts [element] into this deque. Equivalent to [add]; provided for parity with
     * `java.util.Queue`-style usage.
     */
    fun offer(element: T): Boolean = add(element)

    /**
     * Returns a [Sequence] that drains this deque in sorted order (ascending by the comparator).
     * Each call to the sequence's iterator removes elements from this deque. The sequence is
     * single-use in the sense that consuming it empties the deque.
     */
    fun drainSorted(): Sequence<T>

    /**
     * Returns a new [List] containing this deque's elements in sorted order (ascending by the
     * comparator). This deque is not modified.
     */
    fun toSortedList(): List<T>
}
