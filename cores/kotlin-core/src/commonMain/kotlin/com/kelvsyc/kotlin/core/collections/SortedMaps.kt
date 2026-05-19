package com.kelvsyc.kotlin.core.collections

import com.kelvsyc.internal.kotlin.core.collections.TreeMap

/**
 * Returns an empty read-only [SortedMap] with keys ordered by [comparator].
 */
fun <K, V> emptySortedMap(comparator: Comparator<in K>): SortedMap<K, V> = TreeMap(comparator)

/**
 * Returns a read-only [SortedMap] containing [pairs], with keys ordered by [comparator]. If duplicate keys are
 * present, later values overwrite earlier ones.
 */
fun <K, V> sortedMapOf(comparator: Comparator<in K>, vararg pairs: Pair<K, V>): SortedMap<K, V> =
    TreeMap<K, V>(comparator).also { pairs.forEach { (k, v) -> it[k] = v } }

/**
 * Returns a read-only [SortedMap] containing [pairs] with keys in their natural order. If duplicate keys are
 * present, later values overwrite earlier ones.
 */
fun <K : Comparable<K>, V> sortedMapOf(vararg pairs: Pair<K, V>): SortedMap<K, V> =
    sortedMapOf(naturalOrder(), *pairs)

/**
 * Returns a read-only [SortedMap] containing all pairs from this [Iterable], with keys ordered by [comparator].
 */
fun <K, V> Iterable<Pair<K, V>>.toSortedMap(comparator: Comparator<in K>): SortedMap<K, V> =
    TreeMap<K, V>(comparator).also { m -> forEach { (k, v) -> m[k] = v } }

/**
 * Returns a read-only [SortedMap] containing all pairs from this [Iterable] with keys in their natural order.
 */
fun <K : Comparable<K>, V> Iterable<Pair<K, V>>.toSortedMap(): SortedMap<K, V> = toSortedMap(naturalOrder())

/**
 * Builds a read-only [SortedMap] with keys ordered by [comparator] by applying [builderAction] to a
 * [MutableSortedMap].
 */
fun <K, V> buildSortedMap(
    comparator: Comparator<in K>,
    builderAction: MutableSortedMap<K, V>.() -> Unit,
): SortedMap<K, V> = TreeMap<K, V>(comparator).apply(builderAction)

/**
 * Returns a [MutableSortedMap] containing [pairs], with keys ordered by [comparator].
 */
fun <K, V> mutableSortedMapOf(comparator: Comparator<in K>, vararg pairs: Pair<K, V>): MutableSortedMap<K, V> =
    TreeMap<K, V>(comparator).also { pairs.forEach { (k, v) -> it[k] = v } }

/**
 * Returns a [MutableSortedMap] containing [pairs] with keys in their natural order.
 */
fun <K : Comparable<K>, V> mutableSortedMapOf(vararg pairs: Pair<K, V>): MutableSortedMap<K, V> =
    mutableSortedMapOf(naturalOrder(), *pairs)
