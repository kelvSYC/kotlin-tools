package com.kelvsyc.kotlin.core.collections

import com.kelvsyc.internal.kotlin.core.collections.ImmutableFlatMultimap
import com.kelvsyc.internal.kotlin.core.collections.LinkedHashFlatMultimap

/**
 * Returns an empty read-only [FlatMultimap] of the specified type.
 */
fun <K, V> emptyFlatMultimap(): FlatMultimap<K, V> = ImmutableFlatMultimap(emptyList())

/**
 * Returns a new read-only [FlatMultimap] with the specified contents, given as a list of key-value pairs where the
 * first value is the key and the second is the value. Overall insertion order is preserved.
 */
fun <K, V> flatMultimapOf(vararg pairs: Pair<K, V>): FlatMultimap<K, V> = ImmutableFlatMultimap(pairs.toList())

/**
 * Returns a new [FlatMultimap] containing all the key-value pairs from the given collection of [Pair]s, preserving
 * overall iteration order.
 */
fun <K, V> Iterable<Pair<K, V>>.toFlatMultimap(): FlatMultimap<K, V> = ImmutableFlatMultimap(toList())

/**
 * Returns a new [FlatMultimap] containing all the key-value pairs from the given [Sequence] of [Pair]s, preserving
 * overall iteration order.
 */
fun <K, V> Sequence<Pair<K, V>>.toFlatMultimap(): FlatMultimap<K, V> = ImmutableFlatMultimap(toList())

/**
 * Builds a read-only [FlatMultimap] by populating a [MutableFlatMultimap] using the given [builderAction] and
 * returning a read-only snapshot of its contents.
 */
fun <K, V> buildFlatMultimap(builderAction: MutableFlatMultimap<K, V>.() -> Unit): FlatMultimap<K, V> =
    LinkedHashFlatMultimap<K, V>().apply(builderAction)

/**
 * Builds a read-only [FlatMultimap] by populating a [MutableFlatMultimap] with the given initial key [capacity] and
 * using the given [builderAction], returning a read-only snapshot of its contents.
 */
fun <K, V> buildFlatMultimap(capacity: Int, builderAction: MutableFlatMultimap<K, V>.() -> Unit): FlatMultimap<K, V> =
    LinkedHashFlatMultimap<K, V>(capacity).apply(builderAction)
