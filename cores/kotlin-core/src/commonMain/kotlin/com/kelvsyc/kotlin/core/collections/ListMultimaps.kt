package com.kelvsyc.kotlin.core.collections

import com.kelvsyc.internal.kotlin.core.collections.ImmutableListMultimap
import com.kelvsyc.internal.kotlin.core.collections.LinkedHashListMultimap

/**
 * Returns an empty read-only [ListMultimap] of the specified type.
 */
fun <K, V> emptyListMultimap(): ListMultimap<K, V> = ImmutableListMultimap(emptyMap())

/**
 * Returns a new read-only [ListMultimap] with the specified contents, given as key-value pairs. Keys appear in
 * first-occurrence order; values per key appear in the order given.
 */
fun <K, V> listMultimapOf(vararg pairs: Pair<K, V>): ListMultimap<K, V> =
    ImmutableListMultimap(pairs.groupBy({ it.first }, { it.second }))

/**
 * Returns a new [ListMultimap] containing all the key-value pairs from the given collection of [Pair]s. Keys appear
 * in first-occurrence order; values per key appear in iteration order.
 */
fun <K, V> Iterable<Pair<K, V>>.toListMultimap(): ListMultimap<K, V> =
    ImmutableListMultimap(groupBy({ it.first }, { it.second }))

/**
 * Returns a new [ListMultimap] containing all the key-value pairs from the given [Sequence] of [Pair]s.
 */
fun <K, V> Sequence<Pair<K, V>>.toListMultimap(): ListMultimap<K, V> = asIterable().toListMultimap()

/**
 * Builds a read-only [ListMultimap] by populating a [MutableListMultimap] using the given [builderAction] and
 * returning a read-only snapshot of its contents.
 */
fun <K, V> buildListMultimap(builderAction: MutableListMultimap<K, V>.() -> Unit): ListMultimap<K, V> =
    LinkedHashListMultimap<K, V>().apply(builderAction)

/**
 * Builds a read-only [ListMultimap] by populating a [MutableListMultimap] with the given initial key [capacity] and
 * using the given [builderAction], returning a read-only snapshot of its contents.
 */
fun <K, V> buildListMultimap(capacity: Int, builderAction: MutableListMultimap<K, V>.() -> Unit): ListMultimap<K, V> =
    LinkedHashListMultimap<K, V>(capacity).apply(builderAction)
