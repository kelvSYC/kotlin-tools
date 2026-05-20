package com.kelvsyc.kotlin.core.collections

import com.kelvsyc.internal.kotlin.core.collections.TreeListMultimap

/**
 * Returns an empty read-only [SortedListMultimap] with keys ordered by [comparator].
 */
fun <K, V> emptySortedListMultimap(comparator: Comparator<in K>): SortedListMultimap<K, V> =
    TreeListMultimap(comparator)

/**
 * Returns a read-only [SortedListMultimap] containing [pairs], with keys ordered by [comparator]. Duplicate
 * key-value pairs are permitted and preserved.
 */
fun <K, V> sortedListMultimapOf(comparator: Comparator<in K>, vararg pairs: Pair<K, V>): SortedListMultimap<K, V> =
    TreeListMultimap<K, V>(comparator).also { pairs.forEach { (k, v) -> it.put(k, v) } }

/**
 * Returns a read-only [SortedListMultimap] containing [pairs] with keys in their natural order.
 */
fun <K : Comparable<K>, V> sortedListMultimapOf(vararg pairs: Pair<K, V>): SortedListMultimap<K, V> =
    sortedListMultimapOf(naturalOrder(), *pairs)

/**
 * Returns a [MutableSortedListMultimap] containing [pairs], with keys ordered by [comparator].
 */
fun <K, V> mutableSortedListMultimapOf(comparator: Comparator<in K>, vararg pairs: Pair<K, V>): MutableSortedListMultimap<K, V> =
    TreeListMultimap<K, V>(comparator).also { pairs.forEach { (k, v) -> it.put(k, v) } }

/**
 * Returns a [MutableSortedListMultimap] containing [pairs] with keys in their natural order.
 */
fun <K : Comparable<K>, V> mutableSortedListMultimapOf(vararg pairs: Pair<K, V>): MutableSortedListMultimap<K, V> =
    mutableSortedListMultimapOf(naturalOrder(), *pairs)

/**
 * Returns a read-only [SortedListMultimap] containing all pairs from this [Iterable], with keys ordered by
 * [comparator].
 */
fun <K, V> Iterable<Pair<K, V>>.toSortedListMultimap(comparator: Comparator<in K>): SortedListMultimap<K, V> =
    TreeListMultimap<K, V>(comparator).also { m -> forEach { (k, v) -> m.put(k, v) } }

/**
 * Returns a read-only [SortedListMultimap] containing all pairs from this [Iterable] with keys in their natural
 * order.
 */
fun <K : Comparable<K>, V> Iterable<Pair<K, V>>.toSortedListMultimap(): SortedListMultimap<K, V> =
    toSortedListMultimap(naturalOrder())

/**
 * Builds a read-only [SortedListMultimap] with keys ordered by [comparator] by applying [builderAction] to a
 * [MutableSortedListMultimap].
 */
fun <K, V> buildSortedListMultimap(
    comparator: Comparator<in K>,
    builderAction: MutableSortedListMultimap<K, V>.() -> Unit,
): SortedListMultimap<K, V> = TreeListMultimap<K, V>(comparator).apply(builderAction)
