package com.kelvsyc.kotlin.core.collections

import com.kelvsyc.internal.kotlin.core.collections.TreeMultiset

/**
 * Returns an empty read-only [SortedMultiset] ordered by [comparator].
 */
fun <E> emptySortedMultiset(comparator: Comparator<in E>): SortedMultiset<E> = TreeMultiset(comparator)

/**
 * Returns a read-only [SortedMultiset] containing [elements], ordered by [comparator]. Elements that compare as
 * equal by the comparator are placed in the same count bucket.
 */
fun <E> sortedMultisetOf(comparator: Comparator<in E>, vararg elements: E): SortedMultiset<E> =
    TreeMultiset<E>(comparator).also { it.addAll(elements.asIterable()) }

/**
 * Returns a read-only [SortedMultiset] containing [elements] in their natural order.
 */
fun <E : Comparable<E>> sortedMultisetOf(vararg elements: E): SortedMultiset<E> =
    sortedMultisetOf(naturalOrder(), *elements)

/**
 * Returns a read-only [SortedMultiset] containing all elements of this [Iterable], ordered by [comparator].
 */
fun <E> Iterable<E>.toSortedMultiset(comparator: Comparator<in E>): SortedMultiset<E> =
    TreeMultiset<E>(comparator).also { it.addAll(this) }

/**
 * Returns a read-only [SortedMultiset] containing all elements of this [Sequence], ordered by [comparator].
 */
fun <E> Sequence<E>.toSortedMultiset(comparator: Comparator<in E>): SortedMultiset<E> =
    asIterable().toSortedMultiset(comparator)

/**
 * Builds a read-only [SortedMultiset] ordered by [comparator] by applying [builderAction] to a
 * [MutableSortedMultiset].
 */
fun <E> buildSortedMultiset(
    comparator: Comparator<in E>,
    builderAction: MutableSortedMultiset<E>.() -> Unit,
): SortedMultiset<E> = TreeMultiset<E>(comparator).apply(builderAction)

/**
 * Returns a [MutableSortedMultiset] containing [elements], ordered by [comparator].
 */
fun <E> mutableSortedMultisetOf(comparator: Comparator<in E>, vararg elements: E): MutableSortedMultiset<E> =
    TreeMultiset<E>(comparator).also { it.addAll(elements.asIterable()) }

/**
 * Returns a [MutableSortedMultiset] containing [elements] in their natural order.
 */
fun <E : Comparable<E>> mutableSortedMultisetOf(vararg elements: E): MutableSortedMultiset<E> =
    mutableSortedMultisetOf(naturalOrder(), *elements)
