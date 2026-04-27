package com.kelvsyc.kotlin.core.collections

import com.kelvsyc.internal.kotlin.core.collections.LinkedHashListMultimap

/**
 * Returns a new empty [MutableListMultimap].
 */
fun <K, V> mutableListMultimapOf(): MutableListMultimap<K, V> = LinkedHashListMultimap()

/**
 * Returns a new [MutableListMultimap] with the specified contents, given as a list of key-value pairs.
 */
fun <K, V> mutableListMultimapOf(vararg pairs: Pair<K, V>): MutableListMultimap<K, V> =
    LinkedHashListMultimap<K, V>().also { it.putAll(pairs.asIterable()) }

/**
 * Returns a new [MutableListMultimap] containing all the key-value pairs from the given collection of [Pair]s.
 */
fun <K, V> Iterable<Pair<K, V>>.toMutableListMultimap(): MutableListMultimap<K, V> =
    LinkedHashListMultimap<K, V>().also { it.putAll(this) }

/**
 * Returns a new [MutableListMultimap] containing all the key-value pairs from the given [Sequence] of [Pair]s.
 */
fun <K, V> Sequence<Pair<K, V>>.toMutableListMultimap(): MutableListMultimap<K, V> =
    LinkedHashListMultimap<K, V>().also { it.putAll(this.asIterable()) }
