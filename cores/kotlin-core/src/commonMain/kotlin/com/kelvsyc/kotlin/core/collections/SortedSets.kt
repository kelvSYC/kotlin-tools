package com.kelvsyc.kotlin.core.collections

import com.kelvsyc.internal.kotlin.core.collections.ImmutableSortedSet
import com.kelvsyc.internal.kotlin.core.collections.TreeSet

/**
 * Returns an empty read-only [SortedSet] ordered by [comparator].
 */
fun <E> emptySortedSet(comparator: Comparator<in E>): SortedSet<E> = ImmutableSortedSet(TreeSet(comparator))

/**
 * Returns a read-only [SortedSet] containing [elements], ordered by [comparator]. Duplicate elements
 * (by comparator) are deduplicated.
 */
fun <E> sortedSetOf(comparator: Comparator<in E>, vararg elements: E): SortedSet<E> =
    ImmutableSortedSet(TreeSet(comparator).also { it.addAll(elements.asIterable()) })

/**
 * Returns a read-only [SortedSet] containing [elements] in their natural order. Duplicate elements are
 * deduplicated.
 */
fun <E : Comparable<E>> sortedSetOf(vararg elements: E): SortedSet<E> =
    sortedSetOf(naturalOrder(), *elements)

/**
 * Returns a read-only [SortedSet] containing all elements of this [Iterable], ordered by [comparator].
 */
fun <E> Iterable<E>.toSortedSet(comparator: Comparator<in E>): SortedSet<E> =
    ImmutableSortedSet(TreeSet(comparator).also { it.addAll(this) })

/**
 * Returns a read-only [SortedSet] containing all elements of this [Iterable] in their natural order.
 */
fun <E : Comparable<E>> Iterable<E>.toSortedSet(): SortedSet<E> = toSortedSet(naturalOrder())

/**
 * Returns a read-only [SortedSet] containing all elements of this [Sequence], ordered by [comparator].
 */
fun <E> Sequence<E>.toSortedSet(comparator: Comparator<in E>): SortedSet<E> =
    ImmutableSortedSet(TreeSet(comparator).also { it.addAll(this.asIterable()) })

/**
 * Builds a read-only [SortedSet] ordered by [comparator] by applying [builderAction] to a [MutableSortedSet].
 */
fun <E> buildSortedSet(
    comparator: Comparator<in E>,
    builderAction: MutableSortedSet<E>.() -> Unit,
): SortedSet<E> = ImmutableSortedSet(TreeSet(comparator).apply(builderAction))

/**
 * Returns a [MutableSortedSet] containing [elements], ordered by [comparator].
 */
fun <E> mutableSortedSetOf(comparator: Comparator<in E>, vararg elements: E): MutableSortedSet<E> =
    TreeSet(comparator).also { it.addAll(elements.asIterable()) }

/**
 * Returns a [MutableSortedSet] containing [elements] in their natural order.
 */
fun <E : Comparable<E>> mutableSortedSetOf(vararg elements: E): MutableSortedSet<E> =
    mutableSortedSetOf(naturalOrder(), *elements)
