package com.kelvsyc.kotlin.core.collections

import com.kelvsyc.internal.kotlin.core.collections.LinkedHashSetMultimap

/**
 * Returns a new empty [MutableSetMultimap].
 */
fun <K, V> mutableSetMultimapOf(): MutableSetMultimap<K, V> = LinkedHashSetMultimap()

/**
 * Returns a new [MutableSetMultimap] with the specified contents, given as key-value pairs. Duplicate pairs are
 * silently discarded.
 */
fun <K, V> mutableSetMultimapOf(vararg pairs: Pair<K, V>): MutableSetMultimap<K, V> =
    LinkedHashSetMultimap<K, V>().also { it.putAll(pairs.asIterable()) }

/**
 * Returns a new [MutableSetMultimap] containing all the key-value pairs from the given collection of [Pair]s.
 * Duplicate pairs are silently discarded.
 */
fun <K, V> Iterable<Pair<K, V>>.toMutableSetMultimap(): MutableSetMultimap<K, V> =
    LinkedHashSetMultimap<K, V>().also { it.putAll(this) }

/**
 * Returns a new [MutableSetMultimap] containing all the key-value pairs from the given [Sequence] of [Pair]s.
 * Duplicate pairs are silently discarded.
 */
fun <K, V> Sequence<Pair<K, V>>.toMutableSetMultimap(): MutableSetMultimap<K, V> =
    LinkedHashSetMultimap<K, V>().also { it.putAll(this.asIterable()) }
