package com.kelvsyc.kotlin.core.collections

import com.kelvsyc.internal.kotlin.core.collections.ImmutableSetMultimap
import com.kelvsyc.internal.kotlin.core.collections.LinkedHashSetMultimap

/**
 * Returns an empty read-only [SetMultimap] of the specified type.
 */
fun <K, V> emptySetMultimap(): SetMultimap<K, V> = ImmutableSetMultimap(emptyMap())

/**
 * Returns a new read-only [SetMultimap] with the specified contents, given as key-value pairs. Duplicate pairs are
 * silently discarded.
 */
fun <K, V> setMultimapOf(vararg pairs: Pair<K, V>): SetMultimap<K, V> =
    ImmutableSetMultimap(pairs.groupBy({ it.first }, { it.second }).mapValues { it.value.toSet() })

/**
 * Returns a new [SetMultimap] containing all the key-value pairs from the given collection of [Pair]s. Duplicate
 * pairs are silently discarded.
 */
fun <K, V> Iterable<Pair<K, V>>.toSetMultimap(): SetMultimap<K, V> =
    ImmutableSetMultimap(groupBy({ it.first }, { it.second }).mapValues { it.value.toSet() })

/**
 * Returns a new [SetMultimap] containing all the key-value pairs from the given [Sequence] of [Pair]s. Duplicate
 * pairs are silently discarded.
 */
fun <K, V> Sequence<Pair<K, V>>.toSetMultimap(): SetMultimap<K, V> = asIterable().toSetMultimap()

/**
 * Builds a read-only [SetMultimap] by populating a [MutableSetMultimap] using the given [builderAction] and
 * returning a read-only snapshot of its contents.
 */
fun <K, V> buildSetMultimap(builderAction: MutableSetMultimap<K, V>.() -> Unit): SetMultimap<K, V> =
    LinkedHashSetMultimap<K, V>().apply(builderAction)

/**
 * Builds a read-only [SetMultimap] by populating a [MutableSetMultimap] with the given initial key [capacity] and
 * using the given [builderAction], returning a read-only snapshot of its contents.
 */
fun <K, V> buildSetMultimap(capacity: Int, builderAction: MutableSetMultimap<K, V>.() -> Unit): SetMultimap<K, V> =
    LinkedHashSetMultimap<K, V>(capacity).apply(builderAction)
