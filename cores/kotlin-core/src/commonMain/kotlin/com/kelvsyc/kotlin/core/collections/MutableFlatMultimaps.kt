package com.kelvsyc.kotlin.core.collections

import com.kelvsyc.internal.kotlin.core.collections.LinkedHashFlatMultimap

/**
 * Returns a new empty [MutableFlatMultimap].
 */
fun <K, V> mutableFlatMultimapOf(): MutableFlatMultimap<K, V> = LinkedHashFlatMultimap()

/**
 * Returns a new [MutableFlatMultimap] with the specified contents, given as a list of key-value pairs.
 */
fun <K, V> mutableFlatMultimapOf(vararg pairs: Pair<K, V>): MutableFlatMultimap<K, V> =
    LinkedHashFlatMultimap<K, V>().also { it.putAll(pairs.asIterable()) }

/**
 * Returns a new [MutableFlatMultimap] containing all the key-value pairs from the given collection of [Pair]s.
 */
fun <K, V> Iterable<Pair<K, V>>.toMutableFlatMultimap(): MutableFlatMultimap<K, V> =
    LinkedHashFlatMultimap<K, V>().also { it.putAll(this) }

/**
 * Returns a new [MutableFlatMultimap] containing all the key-value pairs from the given [Sequence] of [Pair]s.
 */
fun <K, V> Sequence<Pair<K, V>>.toMutableFlatMultimap(): MutableFlatMultimap<K, V> =
    LinkedHashFlatMultimap<K, V>().also { it.putAll(this.asIterable()) }
