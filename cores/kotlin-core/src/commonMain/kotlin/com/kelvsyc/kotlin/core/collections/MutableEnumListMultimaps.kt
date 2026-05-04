package com.kelvsyc.kotlin.core.collections

import com.kelvsyc.internal.kotlin.core.collections.ArrayEnumListMultimap
import kotlin.enums.enumEntries

/**
 * Returns a new empty [MutableEnumListMultimap] for enum type [K].
 */
inline fun <reified K : Enum<K>, V> mutableEnumListMultimapOf(): MutableEnumListMultimap<K, V> =
    ArrayEnumListMultimap(enumEntries<K>())

/**
 * Returns a new [MutableEnumListMultimap] with the specified contents, given as key-value pairs.
 */
inline fun <reified K : Enum<K>, V> mutableEnumListMultimapOf(
    vararg pairs: Pair<K, V>,
): MutableEnumListMultimap<K, V> =
    ArrayEnumListMultimap<K, V>(enumEntries<K>()).apply { putAll(pairs.asIterable()) }

/**
 * Returns a new [MutableEnumListMultimap] containing all key-value pairs from this [Iterable] of [Pair]s.
 */
inline fun <reified K : Enum<K>, V> Iterable<Pair<K, V>>.toMutableEnumListMultimap(): MutableEnumListMultimap<K, V> =
    ArrayEnumListMultimap<K, V>(enumEntries<K>()).apply { putAll(this@toMutableEnumListMultimap) }

/**
 * Returns a new [MutableEnumListMultimap] containing all key-value pairs from this [Sequence] of [Pair]s.
 */
inline fun <reified K : Enum<K>, V> Sequence<Pair<K, V>>.toMutableEnumListMultimap(): MutableEnumListMultimap<K, V> =
    asIterable().toMutableEnumListMultimap()

/**
 * Returns a new [MutableEnumListMultimap] containing all key-value pairs from this [ListMultimap].
 */
inline fun <reified K : Enum<K>, V> ListMultimap<K, V>.toMutableEnumListMultimap(): MutableEnumListMultimap<K, V> =
    ArrayEnumListMultimap<K, V>(enumEntries<K>()).apply { putAll(this@toMutableEnumListMultimap) }
