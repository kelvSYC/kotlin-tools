package com.kelvsyc.kotlin.core.collections

import com.kelvsyc.internal.kotlin.core.collections.TreeSetMultimap

/**
 * Returns an empty read-only [SortedSetMultimap] with keys ordered by [comparator].
 */
fun <K, V> emptySortedSetMultimap(comparator: Comparator<in K>): SortedSetMultimap<K, V> =
    TreeSetMultimap(comparator)

/**
 * Returns a read-only [SortedSetMultimap] containing [pairs], with keys ordered by [comparator]. Duplicate
 * key-value pairs are silently ignored.
 */
fun <K, V> sortedSetMultimapOf(comparator: Comparator<in K>, vararg pairs: Pair<K, V>): SortedSetMultimap<K, V> =
    TreeSetMultimap<K, V>(comparator).also { pairs.forEach { (k, v) -> it.put(k, v) } }

/**
 * Returns a read-only [SortedSetMultimap] containing [pairs] with keys in their natural order.
 */
fun <K : Comparable<K>, V> sortedSetMultimapOf(vararg pairs: Pair<K, V>): SortedSetMultimap<K, V> =
    sortedSetMultimapOf(naturalOrder(), *pairs)

/**
 * Returns a [MutableSortedSetMultimap] containing [pairs], with keys ordered by [comparator].
 */
fun <K, V> mutableSortedSetMultimapOf(comparator: Comparator<in K>, vararg pairs: Pair<K, V>): MutableSortedSetMultimap<K, V> =
    TreeSetMultimap<K, V>(comparator).also { pairs.forEach { (k, v) -> it.put(k, v) } }

/**
 * Returns a [MutableSortedSetMultimap] containing [pairs] with keys in their natural order.
 */
fun <K : Comparable<K>, V> mutableSortedSetMultimapOf(vararg pairs: Pair<K, V>): MutableSortedSetMultimap<K, V> =
    mutableSortedSetMultimapOf(naturalOrder(), *pairs)

/**
 * Returns a read-only [SortedSetMultimap] containing all pairs from this [Iterable], with keys ordered by
 * [comparator]. Duplicate key-value pairs are silently ignored.
 */
fun <K, V> Iterable<Pair<K, V>>.toSortedSetMultimap(comparator: Comparator<in K>): SortedSetMultimap<K, V> =
    TreeSetMultimap<K, V>(comparator).also { m -> forEach { (k, v) -> m.put(k, v) } }

/**
 * Returns a read-only [SortedSetMultimap] containing all pairs from this [Iterable] with keys in their natural
 * order.
 */
fun <K : Comparable<K>, V> Iterable<Pair<K, V>>.toSortedSetMultimap(): SortedSetMultimap<K, V> =
    toSortedSetMultimap(naturalOrder())

/**
 * Builds a read-only [SortedSetMultimap] with keys ordered by [comparator] by applying [builderAction] to a
 * [MutableSortedSetMultimap].
 */
fun <K, V> buildSortedSetMultimap(
    comparator: Comparator<in K>,
    builderAction: MutableSortedSetMultimap<K, V>.() -> Unit,
): SortedSetMultimap<K, V> = TreeSetMultimap<K, V>(comparator).apply(builderAction)
