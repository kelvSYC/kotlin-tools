package com.kelvsyc.kotlin.core.collections

import com.kelvsyc.internal.kotlin.core.collections.ArrayEnumSetMultimap
import kotlin.enums.enumEntries

/**
 * Returns a new empty [MutableEnumSetMultimap] for enum type [K].
 */
inline fun <reified K : Enum<K>, V> mutableEnumSetMultimapOf(): MutableEnumSetMultimap<K, V> =
    ArrayEnumSetMultimap(enumEntries<K>())

/**
 * Returns a new [MutableEnumSetMultimap] with the specified contents, given as key-value pairs. Duplicate pairs are
 * silently discarded.
 */
inline fun <reified K : Enum<K>, V> mutableEnumSetMultimapOf(
    vararg pairs: Pair<K, V>,
): MutableEnumSetMultimap<K, V> =
    ArrayEnumSetMultimap<K, V>(enumEntries<K>()).apply { putAll(pairs.asIterable()) }

/**
 * Returns a new [MutableEnumSetMultimap] containing all key-value pairs from this [Iterable] of [Pair]s. Duplicate
 * pairs are silently discarded.
 */
inline fun <reified K : Enum<K>, V> Iterable<Pair<K, V>>.toMutableEnumSetMultimap(): MutableEnumSetMultimap<K, V> =
    ArrayEnumSetMultimap<K, V>(enumEntries<K>()).apply { putAll(this@toMutableEnumSetMultimap) }

/**
 * Returns a new [MutableEnumSetMultimap] containing all key-value pairs from this [Sequence] of [Pair]s. Duplicate
 * pairs are silently discarded.
 */
inline fun <reified K : Enum<K>, V> Sequence<Pair<K, V>>.toMutableEnumSetMultimap(): MutableEnumSetMultimap<K, V> =
    asIterable().toMutableEnumSetMultimap()

/**
 * Returns a new [MutableEnumSetMultimap] containing all key-value pairs from this [SetMultimap].
 */
inline fun <reified K : Enum<K>, V> SetMultimap<K, V>.toMutableEnumSetMultimap(): MutableEnumSetMultimap<K, V> =
    ArrayEnumSetMultimap<K, V>(enumEntries<K>()).apply { putAll(this@toMutableEnumSetMultimap) }
