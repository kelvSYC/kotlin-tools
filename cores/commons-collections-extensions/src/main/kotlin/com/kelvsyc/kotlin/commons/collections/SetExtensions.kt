package com.kelvsyc.kotlin.commons.collections

import org.apache.commons.collections4.SetUtils

/**
 * Symmetric difference (disjunction) between two sets.
 *
 * Returns a set containing all elements that are in either this set or the other set,
 * but not in both.
 *
 * For example: `setOf(1, 2, 3).disjunction(setOf(2, 3, 4))` returns `setOf(1, 4)`.
 */
fun <T> Set<T>.disjunction(other: Set<T>): Set<T> = SetUtils.disjunction(this, other)
