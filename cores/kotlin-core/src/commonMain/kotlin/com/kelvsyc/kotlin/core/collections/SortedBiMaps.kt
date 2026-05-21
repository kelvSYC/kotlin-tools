package com.kelvsyc.kotlin.core.collections

import com.kelvsyc.internal.kotlin.core.collections.SortedBiMapImpl

/**
 * Returns a new read-only [SortedBiMap] with keys ordered by [comparator] and the specified contents.
 *
 * @throws IllegalArgumentException if any value appears more than once.
 */
fun <K, V> sortedBiMapOf(comparator: Comparator<in K>, vararg pairs: Pair<K, V>): SortedBiMap<K, V> =
    SortedBiMapImpl<K, V>(comparator).apply { pairs.forEach { (k, v) -> put(k, v) } }

/**
 * Builds a read-only [SortedBiMap] by populating a [MutableSortedBiMap] using [builderAction].
 */
fun <K, V> buildSortedBiMap(
    comparator: Comparator<in K>,
    builderAction: MutableSortedBiMap<K, V>.() -> Unit,
): SortedBiMap<K, V> = SortedBiMapImpl<K, V>(comparator).apply(builderAction)

/**
 * Returns a new read-only [SortedBiMap] containing all entries from this [Map], with keys ordered by [comparator].
 *
 * @throws IllegalArgumentException if any value appears more than once.
 */
fun <K, V> Map<K, V>.toSortedBiMap(comparator: Comparator<in K>): SortedBiMap<K, V> =
    SortedBiMapImpl<K, V>(comparator).apply { putAll(this@toSortedBiMap) }

/**
 * Returns a new empty [MutableSortedBiMap] with keys ordered by [comparator].
 */
fun <K, V> mutableSortedBiMapOf(comparator: Comparator<in K>): MutableSortedBiMap<K, V> =
    SortedBiMapImpl(comparator)

/**
 * Returns a new [MutableSortedBiMap] with keys ordered by [comparator] and the specified contents.
 *
 * @throws IllegalArgumentException if any value appears more than once.
 */
fun <K, V> mutableSortedBiMapOf(comparator: Comparator<in K>, vararg pairs: Pair<K, V>): MutableSortedBiMap<K, V> =
    SortedBiMapImpl<K, V>(comparator).apply { pairs.forEach { (k, v) -> put(k, v) } }

/**
 * Builds a [MutableSortedBiMap] by applying [builderAction] to a new empty instance.
 */
fun <K, V> buildMutableSortedBiMap(
    comparator: Comparator<in K>,
    builderAction: MutableSortedBiMap<K, V>.() -> Unit,
): MutableSortedBiMap<K, V> = SortedBiMapImpl<K, V>(comparator).apply(builderAction)

/**
 * Returns a new [MutableSortedBiMap] containing all entries from this [Map], with keys ordered by [comparator].
 *
 * @throws IllegalArgumentException if any value appears more than once.
 */
fun <K, V> Map<K, V>.toMutableSortedBiMap(comparator: Comparator<in K>): MutableSortedBiMap<K, V> =
    SortedBiMapImpl<K, V>(comparator).apply { putAll(this@toMutableSortedBiMap) }
