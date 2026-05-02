package com.kelvsyc.kotlin.core.collections

/**
 * A mutable [Multiset] that supports adding and removing elements.
 *
 * [add] and [remove] from [MutableCollection] operate on single occurrences. The additional [add], [remove], and
 * [setCount] overloads operate on multiple occurrences at once.
 */
interface MutableMultiset<E> : Multiset<E>, MutableCollection<E> {
    /**
     * Adds [count] occurrences of [element] to this multiset. Has no effect if [count] is 0. Throws
     * [IllegalArgumentException] if [count] is negative.
     */
    fun add(element: E, count: Int)

    /**
     * Removes up to [count] occurrences of [element] from this multiset. Returns the number of occurrences actually
     * removed. Returns 0 if [count] is 0 or the element is absent. Throws [IllegalArgumentException] if [count] is
     * negative.
     */
    fun remove(element: E, count: Int): Int

    /**
     * Sets the number of occurrences of [element] to [count] and returns the previous count. A [count] of 0 removes
     * the element entirely. Throws [IllegalArgumentException] if [count] is negative.
     */
    fun setCount(element: E, count: Int): Int
}
