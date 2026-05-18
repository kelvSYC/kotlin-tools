package com.kelvsyc.kotlin.commons.collections

import org.apache.commons.collections4.ListUtils

/**
 * Longest common subsequence (LCS) of this list and another.
 *
 * Uses default equality (`==`) for element comparison.
 *
 * For example: `[1, 2, 3, 4].longestCommonSubsequence([1, 3, 4, 5])` returns `[1, 3, 4]`.
 */
fun <T> List<T>.longestCommonSubsequence(other: List<T>): List<T> =
    ListUtils.longestCommonSubsequence(this, other)

/**
 * Longest common subsequence (LCS) of this list and another, using a custom equality function.
 *
 * For example: `["A", "B", "C"].longestCommonSubsequence(["a", "c"]) { a, b -> a.equals(b, ignoreCase = true) }`
 * returns `["A", "C"]`.
 */
fun <T> List<T>.longestCommonSubsequence(
    other: List<T>,
    equator: (T, T) -> Boolean
): List<T> =
    ListUtils.longestCommonSubsequence(this, other, object : org.apache.commons.collections4.Equator<T> {
        override fun equate(o1: T, o2: T): Boolean = equator(o1, o2)
        override fun hash(o: T): Int = o?.hashCode() ?: 0
    })

/**
 * List-level union: returns a new list containing all elements from both lists,
 * with order preserved and max frequency per element.
 *
 * This uses multiset union semantics (like `multisetUnion`), not set union.
 *
 * For example: `[1, 2, 3].listUnion([3, 4, 5])` returns `[1, 2, 3, 3, 4, 5]`.
 */
fun <T> List<T>.listUnion(other: List<T>): List<T> =
    ListUtils.union(this, other)

/**
 * List-level intersection: returns a new list containing only elements that appear
 * in both lists, with order from the first list preserved and min frequency per element.
 *
 * For example: `[1, 2, 3].listIntersection([2, 3, 4])` returns `[2, 3]`.
 */
fun <T> List<T>.listIntersection(other: List<T>): List<T> =
    ListUtils.intersection(this, other)

/**
 * List-level subtract: returns a new list with the first occurrence of each element
 * in the second list removed from the first.
 *
 * For example: `[1, 2, 3, 4].listSubtract([2, 4])` returns `[1, 3]`.
 */
fun <T> List<T>.listSubtract(other: List<T>): List<T> =
    ListUtils.subtract(this, other)
