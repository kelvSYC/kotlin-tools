package com.kelvsyc.kotlin.core.collections

import com.kelvsyc.internal.kotlin.core.collections.ArrayEnumSetMultimap
import kotlin.enums.enumEntries

/**
 * Returns an empty read-only [EnumSetMultimap] for enum type [K].
 */
inline fun <reified K : Enum<K>, V> emptyEnumSetMultimap(): EnumSetMultimap<K, V> =
    ArrayEnumSetMultimap(enumEntries<K>())

/**
 * Returns a new read-only [EnumSetMultimap] with the specified contents, given as key-value pairs. Duplicate pairs
 * are silently discarded.
 */
inline fun <reified K : Enum<K>, V> enumSetMultimapOf(vararg pairs: Pair<K, V>): EnumSetMultimap<K, V> =
    ArrayEnumSetMultimap<K, V>(enumEntries<K>()).apply { putAll(pairs.asIterable()) }

/**
 * Builds a read-only [EnumSetMultimap] by populating a [MutableEnumSetMultimap] using the given [builderAction] and
 * returning a read-only view of its contents.
 */
inline fun <reified K : Enum<K>, V> buildEnumSetMultimap(
    builderAction: MutableEnumSetMultimap<K, V>.() -> Unit,
): EnumSetMultimap<K, V> = ArrayEnumSetMultimap<K, V>(enumEntries<K>()).apply(builderAction)

/**
 * Returns a new read-only [EnumSetMultimap] containing all key-value pairs from this [Iterable] of [Pair]s.
 * Duplicate pairs are silently discarded.
 */
inline fun <reified K : Enum<K>, V> Iterable<Pair<K, V>>.toEnumSetMultimap(): EnumSetMultimap<K, V> =
    ArrayEnumSetMultimap<K, V>(enumEntries<K>()).apply { putAll(this@toEnumSetMultimap) }

/**
 * Returns a new read-only [EnumSetMultimap] containing all key-value pairs from this [Sequence] of [Pair]s.
 * Duplicate pairs are silently discarded.
 */
inline fun <reified K : Enum<K>, V> Sequence<Pair<K, V>>.toEnumSetMultimap(): EnumSetMultimap<K, V> =
    asIterable().toEnumSetMultimap()

/**
 * Returns a new read-only [EnumSetMultimap] containing all key-value pairs from this [SetMultimap].
 */
inline fun <reified K : Enum<K>, V> SetMultimap<K, V>.toEnumSetMultimap(): EnumSetMultimap<K, V> =
    ArrayEnumSetMultimap<K, V>(enumEntries<K>()).apply { putAll(this@toEnumSetMultimap) }
