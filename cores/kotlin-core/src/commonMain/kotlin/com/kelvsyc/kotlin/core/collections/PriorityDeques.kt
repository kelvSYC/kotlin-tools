package com.kelvsyc.kotlin.core.collections

import com.kelvsyc.internal.kotlin.core.collections.MinMaxPriorityDeque

/**
 * Returns an empty [PriorityDeque] ordered by the given [comparator].
 */
fun <T> priorityDequeOf(comparator: Comparator<in T>): PriorityDeque<T> =
    MinMaxPriorityDeque(comparator)

/**
 * Returns a [PriorityDeque] containing the given [elements], ordered by the given [comparator].
 * Constructed in O(n) via bottom-up heapification.
 */
fun <T> priorityDequeOf(comparator: Comparator<in T>, vararg elements: T): PriorityDeque<T> =
    MinMaxPriorityDeque(comparator, elements.asList())

/**
 * Returns an empty [PriorityDeque] ordered by the natural ordering of [T].
 */
fun <T : Comparable<T>> minMaxPriorityDequeOf(): PriorityDeque<T> =
    MinMaxPriorityDeque(naturalOrder())

/**
 * Returns a [PriorityDeque] containing the given [elements], ordered by the natural ordering of
 * [T]. Constructed in O(n) via bottom-up heapification.
 */
fun <T : Comparable<T>> minMaxPriorityDequeOf(vararg elements: T): PriorityDeque<T> =
    MinMaxPriorityDeque(naturalOrder(), elements.asList())

/**
 * Returns a new [PriorityDeque] containing all elements of this [Iterable], ordered by the given
 * [comparator]. Constructed in O(n) via bottom-up heapification when the source is a [Collection].
 */
fun <T> Iterable<T>.toPriorityDeque(comparator: Comparator<in T>): PriorityDeque<T> {
    val collection = if (this is Collection<T>) this else toList()
    return MinMaxPriorityDeque(comparator, collection)
}

/**
 * Returns a new [PriorityDeque] containing all elements of this [Sequence], ordered by the given
 * [comparator].
 */
fun <T> Sequence<T>.toPriorityDeque(comparator: Comparator<in T>): PriorityDeque<T> =
    MinMaxPriorityDeque(comparator, toList())
