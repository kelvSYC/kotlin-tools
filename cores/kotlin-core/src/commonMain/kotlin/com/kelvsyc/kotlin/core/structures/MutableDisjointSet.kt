package com.kelvsyc.kotlin.core.structures

/**
 * A mutable disjoint set (union-find) structure that supports merging equivalence classes.
 *
 * This structure is **growth-only**: classes can be merged but never split, and elements can be
 * added but never removed. This is intentional — the semantics of removing a member from a merged
 * class are undefined without recording full merge history, and the real-world use cases for this
 * structure (graph connectivity, type unification, entity resolution) are all monotone.
 */
interface MutableDisjointSet<E> : DisjointSet<E> {
    /**
     * Merges the equivalence classes of [a] and [b]. Both elements are registered lazily if not
     * already present — including elements that were not part of any universe supplied at
     * construction. No-op if [a] and [b] are already in the same class.
     */
    fun union(a: E, b: E)
}
