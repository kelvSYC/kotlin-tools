package com.kelvsyc.kotlin.core.collections

import com.kelvsyc.internal.kotlin.core.collections.ImmutableListMultimap
import com.kelvsyc.internal.kotlin.core.collections.LinkedHashListMultimap

/**
 * Returns an empty read-only multimap of the specified type.
 */
fun <K, V> emptyListMultimap(): ListMultimap<K, V> = ImmutableListMultimap(emptyList())

/**
 * Returns a new read-only multimap with the specified contents, given as a list of key-value pairs where the first
 * value is the key and the second is the value. Overall insertion order is preserved.
 */
fun <K, V> listMultimapOf(vararg pairs: Pair<K, V>): ListMultimap<K, V> = ImmutableListMultimap(pairs.toList())

/**
 * Returns a new multimap containing all the key-value pairs from the given collection of [Pair]s, preserving overall
 * iteration order.
 */
fun <K, V> Iterable<Pair<K, V>>.toListMultimap(): ListMultimap<K, V> = ImmutableListMultimap(toList())

/**
 * Returns a new multimap containing all the key-value pairs from the given [Sequence] of [Pair]s, preserving overall
 * iteration order.
 */
fun <K, V> Sequence<Pair<K, V>>.toListMultimap(): ListMultimap<K, V> = ImmutableListMultimap(toList())

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
