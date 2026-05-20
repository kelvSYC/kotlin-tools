package com.kelvsyc.kotlin.core.collections

import com.kelvsyc.internal.kotlin.core.collections.TreeFlatMultimap

/**
 * Returns an empty read-only [SortedFlatMultimap] with entries ordered by [comparator] over pairs.
 */
fun <K, V> emptySortedFlatMultimap(comparator: Comparator<in Pair<K, V>>): SortedFlatMultimap<K, V> =
    TreeFlatMultimap(comparator)

/**
 * Returns a read-only [SortedFlatMultimap] containing [pairs], ordered by [comparator] over whole
 * key-value pairs. Duplicate pairs are permitted and preserved.
 */
fun <K, V> sortedFlatMultimapOf(
    comparator: Comparator<in Pair<K, V>>,
    vararg pairs: Pair<K, V>,
): SortedFlatMultimap<K, V> =
    TreeFlatMultimap<K, V>(comparator).also { m -> pairs.forEach { (k, v) -> m.put(k, v) } }

/**
 * Returns a read-only [SortedFlatMultimap] containing [pairs] with entries ordered
 * lexicographically by key then value (natural order). Requires both [K] and [V] to be
 * [Comparable].
 *
 * To use a different pair ordering — such as value-first — provide an explicit [comparator] via
 * the other overload.
 */
fun <K : Comparable<K>, V : Comparable<V>> sortedFlatMultimapOf(
    vararg pairs: Pair<K, V>,
): SortedFlatMultimap<K, V> =
    sortedFlatMultimapOf(compareBy<Pair<K, V>> { it.first }.thenBy { it.second }, *pairs)

/**
 * Returns a [MutableSortedFlatMultimap] containing [pairs], ordered by [comparator] over whole
 * key-value pairs.
 */
fun <K, V> mutableSortedFlatMultimapOf(
    comparator: Comparator<in Pair<K, V>>,
    vararg pairs: Pair<K, V>,
): MutableSortedFlatMultimap<K, V> =
    TreeFlatMultimap<K, V>(comparator).also { m -> pairs.forEach { (k, v) -> m.put(k, v) } }

/**
 * Returns a [MutableSortedFlatMultimap] containing [pairs] with entries ordered lexicographically
 * by key then value (natural order).
 */
fun <K : Comparable<K>, V : Comparable<V>> mutableSortedFlatMultimapOf(
    vararg pairs: Pair<K, V>,
): MutableSortedFlatMultimap<K, V> =
    mutableSortedFlatMultimapOf(compareBy<Pair<K, V>> { it.first }.thenBy { it.second }, *pairs)

/**
 * Returns a read-only [SortedFlatMultimap] containing all pairs from this [Iterable], ordered by
 * [comparator] over whole key-value pairs.
 */
fun <K, V> Iterable<Pair<K, V>>.toSortedFlatMultimap(
    comparator: Comparator<in Pair<K, V>>,
): SortedFlatMultimap<K, V> =
    TreeFlatMultimap<K, V>(comparator).also { m -> forEach { (k, v) -> m.put(k, v) } }

/**
 * Returns a read-only [SortedFlatMultimap] containing all pairs from this [Iterable] with entries
 * ordered lexicographically by key then value (natural order).
 */
fun <K : Comparable<K>, V : Comparable<V>> Iterable<Pair<K, V>>.toSortedFlatMultimap(): SortedFlatMultimap<K, V> =
    toSortedFlatMultimap(compareBy<Pair<K, V>> { it.first }.thenBy { it.second })

/**
 * Builds a read-only [SortedFlatMultimap] ordered by [comparator] over pairs by applying
 * [builderAction] to a [MutableSortedFlatMultimap].
 */
fun <K, V> buildSortedFlatMultimap(
    comparator: Comparator<in Pair<K, V>>,
    builderAction: MutableSortedFlatMultimap<K, V>.() -> Unit,
): SortedFlatMultimap<K, V> = TreeFlatMultimap<K, V>(comparator).apply(builderAction)
