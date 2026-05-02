package com.kelvsyc.kotlin.core.collections

/**
 * A mutable [ListMultiset] that supports adding and removing elements.
 *
 * [add] and [remove] from [MutableCollection] operate on single occurrences. The additional [add], [remove], and
 * [setCount] overloads operate on multiple occurrences at once.
 *
 * All removal operations follow FIFO ordering: when multiple occurrences of an element are present, the earliest
 * inserted occurrences are removed first.
 */
interface MutableListMultiset<E> : ListMultiset<E>, MutableCollection<E> {
    /**
     * Adds [count] occurrences of [element] to this multiset, appending them at the end in insertion order.
     * Has no effect if [count] is 0. Throws [IllegalArgumentException] if [count] is negative.
     */
    fun add(element: E, count: Int)

    /**
     * Removes up to [count] occurrences of [element] from this multiset, removing the earliest occurrences first
     * (FIFO). Returns the number of occurrences actually removed. Returns 0 if [count] is 0 or the element is
     * absent. Throws [IllegalArgumentException] if [count] is negative.
     */
    fun remove(element: E, count: Int): Int

    /**
     * Sets the number of occurrences of [element] to [count] and returns the previous count.
     *
     * If [count] exceeds the current count, the additional occurrences are appended at the end. If [count] is less
     * than the current count, the earliest occurrences are removed first (FIFO). A [count] of 0 removes the element
     * entirely. Throws [IllegalArgumentException] if [count] is negative.
     */
    fun setCount(element: E, count: Int): Int
}
