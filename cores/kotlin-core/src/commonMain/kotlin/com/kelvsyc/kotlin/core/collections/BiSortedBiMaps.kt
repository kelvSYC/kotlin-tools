package com.kelvsyc.kotlin.core.collections

import com.kelvsyc.internal.kotlin.core.collections.BiSortedBiMapImpl
import com.kelvsyc.internal.kotlin.core.collections.ImmutableBiSortedBiMap

/**
 * Returns a new read-only [BiSortedBiMap] with the specified contents.
 *
 * @throws IllegalArgumentException if any value appears more than once.
 */
fun <K, V> biSortedBiMapOf(
    comparator: Comparator<in K>,
    valueComparator: Comparator<in V>,
    vararg pairs: Pair<K, V>,
): BiSortedBiMap<K, V> =
    ImmutableBiSortedBiMap(BiSortedBiMapImpl(comparator, valueComparator).apply { pairs.forEach { (k, v) -> put(k, v) } })

/**
 * Builds a read-only [BiSortedBiMap] by populating a [MutableBiSortedBiMap] using [builderAction].
 */
fun <K, V> buildBiSortedBiMap(
    comparator: Comparator<in K>,
    valueComparator: Comparator<in V>,
    builderAction: MutableBiSortedBiMap<K, V>.() -> Unit,
): BiSortedBiMap<K, V> = ImmutableBiSortedBiMap(BiSortedBiMapImpl(comparator, valueComparator).apply(builderAction))

/**
 * Returns a new read-only [BiSortedBiMap] containing all entries from this [Map].
 *
 * @throws IllegalArgumentException if any value appears more than once.
 */
fun <K, V> Map<K, V>.toBiSortedBiMap(
    comparator: Comparator<in K>,
    valueComparator: Comparator<in V>,
): BiSortedBiMap<K, V> =
    ImmutableBiSortedBiMap(BiSortedBiMapImpl(comparator, valueComparator).apply { putAll(this@toBiSortedBiMap) })

/**
 * Returns a new empty [MutableBiSortedBiMap].
 */
fun <K, V> mutableBiSortedBiMapOf(
    comparator: Comparator<in K>,
    valueComparator: Comparator<in V>,
): MutableBiSortedBiMap<K, V> = BiSortedBiMapImpl(comparator, valueComparator)

/**
 * Returns a new [MutableBiSortedBiMap] with the specified contents.
 *
 * @throws IllegalArgumentException if any value appears more than once.
 */
fun <K, V> mutableBiSortedBiMapOf(
    comparator: Comparator<in K>,
    valueComparator: Comparator<in V>,
    vararg pairs: Pair<K, V>,
): MutableBiSortedBiMap<K, V> =
    BiSortedBiMapImpl(comparator, valueComparator).apply { pairs.forEach { (k, v) -> put(k, v) } }

/**
 * Builds a [MutableBiSortedBiMap] by applying [builderAction] to a new empty instance.
 */
fun <K, V> buildMutableBiSortedBiMap(
    comparator: Comparator<in K>,
    valueComparator: Comparator<in V>,
    builderAction: MutableBiSortedBiMap<K, V>.() -> Unit,
): MutableBiSortedBiMap<K, V> = BiSortedBiMapImpl(comparator, valueComparator).apply(builderAction)

/**
 * Returns a new [MutableBiSortedBiMap] containing all entries from this [Map].
 *
 * @throws IllegalArgumentException if any value appears more than once.
 */
fun <K, V> Map<K, V>.toMutableBiSortedBiMap(
    comparator: Comparator<in K>,
    valueComparator: Comparator<in V>,
): MutableBiSortedBiMap<K, V> =
    BiSortedBiMapImpl(comparator, valueComparator).apply { putAll(this@toMutableBiSortedBiMap) }
