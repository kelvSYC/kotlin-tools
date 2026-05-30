package com.kelvsyc.kotlin.core.collections

import com.kelvsyc.internal.kotlin.core.collections.BiTreeSetMultimap
import com.kelvsyc.internal.kotlin.core.collections.ImmutableBiSortedSetMultimap

/**
 * Returns an empty read-only [BiSortedSetMultimap] with keys ordered by [keyComparator] and values per key ordered
 * by [valueComparator].
 */
fun <K, V> emptyBiSortedSetMultimap(
    keyComparator: Comparator<in K>,
    valueComparator: Comparator<in V>,
): BiSortedSetMultimap<K, V> = ImmutableBiSortedSetMultimap(BiTreeSetMultimap(keyComparator, valueComparator))

/**
 * Returns a read-only [BiSortedSetMultimap] containing [pairs], with keys ordered by [keyComparator] and values
 * per key ordered by [valueComparator]. Duplicate key-value pairs are silently ignored.
 */
fun <K, V> biSortedSetMultimapOf(
    keyComparator: Comparator<in K>,
    valueComparator: Comparator<in V>,
    vararg pairs: Pair<K, V>,
): BiSortedSetMultimap<K, V> =
    ImmutableBiSortedSetMultimap(BiTreeSetMultimap<K, V>(keyComparator, valueComparator).also { pairs.forEach { (k, v) -> it.put(k, v) } })

/**
 * Returns a read-only [BiSortedSetMultimap] containing [pairs] with keys and values in their natural order.
 */
fun <K : Comparable<K>, V : Comparable<V>> biSortedSetMultimapOf(vararg pairs: Pair<K, V>): BiSortedSetMultimap<K, V> =
    biSortedSetMultimapOf(naturalOrder(), naturalOrder(), *pairs)

/**
 * Returns a [MutableBiSortedSetMultimap] containing [pairs], with keys ordered by [keyComparator] and values per
 * key ordered by [valueComparator].
 */
fun <K, V> mutableBiSortedSetMultimapOf(
    keyComparator: Comparator<in K>,
    valueComparator: Comparator<in V>,
    vararg pairs: Pair<K, V>,
): MutableBiSortedSetMultimap<K, V> =
    BiTreeSetMultimap<K, V>(keyComparator, valueComparator).also { pairs.forEach { (k, v) -> it.put(k, v) } }

/**
 * Returns a [MutableBiSortedSetMultimap] containing [pairs] with keys and values in their natural order.
 */
fun <K : Comparable<K>, V : Comparable<V>> mutableBiSortedSetMultimapOf(vararg pairs: Pair<K, V>): MutableBiSortedSetMultimap<K, V> =
    mutableBiSortedSetMultimapOf(naturalOrder(), naturalOrder(), *pairs)

/**
 * Returns a read-only [BiSortedSetMultimap] containing all pairs from this [Iterable], with keys ordered by
 * [keyComparator] and values per key ordered by [valueComparator].
 */
fun <K, V> Iterable<Pair<K, V>>.toBiSortedSetMultimap(
    keyComparator: Comparator<in K>,
    valueComparator: Comparator<in V>,
): BiSortedSetMultimap<K, V> =
    ImmutableBiSortedSetMultimap(BiTreeSetMultimap<K, V>(keyComparator, valueComparator).also { m -> forEach { (k, v) -> m.put(k, v) } })

/**
 * Returns a read-only [BiSortedSetMultimap] containing all pairs from this [Iterable] with keys and values in
 * their natural order.
 */
fun <K : Comparable<K>, V : Comparable<V>> Iterable<Pair<K, V>>.toBiSortedSetMultimap(): BiSortedSetMultimap<K, V> =
    toBiSortedSetMultimap(naturalOrder(), naturalOrder())

/**
 * Builds a read-only [BiSortedSetMultimap] with keys ordered by [keyComparator] and values per key ordered by
 * [valueComparator] by applying [builderAction] to a [MutableBiSortedSetMultimap].
 */
fun <K, V> buildBiSortedSetMultimap(
    keyComparator: Comparator<in K>,
    valueComparator: Comparator<in V>,
    builderAction: MutableBiSortedSetMultimap<K, V>.() -> Unit,
): BiSortedSetMultimap<K, V> = ImmutableBiSortedSetMultimap(BiTreeSetMultimap<K, V>(keyComparator, valueComparator).apply(builderAction))
