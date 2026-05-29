package com.kelvsyc.kotlin.core.structures

/**
 * A read-only view of a disjoint set (union-find) structure: a partition of elements into
 * mutually exclusive equivalence classes.
 *
 * Elements not explicitly registered via [MutableDisjointSet.union] are treated as implicit
 * singletons — [find] returns the element itself, and [getPartition] returns a single-element set.
 * This is consistent with the view that every element always belongs to some class; unregistered
 * elements simply have not been merged into any other class yet.
 */
interface DisjointSet<E> {
    /**
     * All elements currently registered in this structure. An element enters this set only via
     * [MutableDisjointSet.union]; calling [find] on an unregistered element does not add it here.
     */
    val elements: Set<E>

    /**
     * A snapshot of the current equivalence classes. Each inner set is one partition; no element
     * appears in more than one partition.
     *
     * This is an O(n) operation that allocates a new set on each call. It is intended to be used
     * to read off the final result after all [MutableDisjointSet.union] calls are complete, not as
     * a live view or in performance-sensitive loops.
     */
    val partitions: Set<Set<E>>

    /**
     * Returns the canonical representative of [element]'s equivalence class. If [element] has
     * never been registered, returns [element] itself (implicit singleton semantics).
     *
     * The choice of representative is an implementation detail determined by union history and
     * internal path compression. It is not guaranteed to be any particular element — callers
     * should use the return value only to test class membership via equality (`find(a) == find(b)`),
     * not to identify a preferred element within the class.
     */
    fun find(element: E): E

    /**
     * Returns the equivalence class containing [element]: the set of all registered elements in
     * the same partition as [element], including [element] itself.
     *
     * If [element] has never been registered, returns `setOf(element)` — consistent with the
     * implicit singleton semantics of [find].
     *
     * This is an O(n) operation that allocates a new set on each call.
     */
    fun getPartition(element: E): Set<E> {
        val rep = find(element)
        val result = elements.filterTo(mutableSetOf()) { find(it) == rep }
        result.add(element)
        return result
    }

    /**
     * Returns `true` if [a] and [b] belong to the same equivalence class.
     */
    fun connected(a: E, b: E): Boolean = find(a) == find(b)
}
