package com.kelvsyc.kotlin.core.collections

import com.kelvsyc.internal.kotlin.core.collections.ArrayEnumListMultimap
import com.kelvsyc.internal.kotlin.core.collections.ImmutableEnumListMultimap
import kotlin.enums.enumEntries

/**
 * Returns an empty read-only [EnumListMultimap] for enum type [K].
 */
inline fun <reified K : Enum<K>, V> emptyEnumListMultimap(): EnumListMultimap<K, V> =
    ImmutableEnumListMultimap(ArrayEnumListMultimap(enumEntries<K>()))

/**
 * Returns a new read-only [EnumListMultimap] with the specified contents, given as key-value pairs.
 */
inline fun <reified K : Enum<K>, V> enumListMultimapOf(vararg pairs: Pair<K, V>): EnumListMultimap<K, V> =
    ImmutableEnumListMultimap(ArrayEnumListMultimap<K, V>(enumEntries<K>()).apply { putAll(pairs.asIterable()) })

/**
 * Builds a read-only [EnumListMultimap] by populating a [MutableEnumListMultimap] using the given [builderAction] and
 * returning a read-only view of its contents.
 */
inline fun <reified K : Enum<K>, V> buildEnumListMultimap(
    builderAction: MutableEnumListMultimap<K, V>.() -> Unit,
): EnumListMultimap<K, V> = ImmutableEnumListMultimap(ArrayEnumListMultimap<K, V>(enumEntries<K>()).apply(builderAction))

/**
 * Returns a new read-only [EnumListMultimap] containing all key-value pairs from this [Iterable] of [Pair]s.
 */
inline fun <reified K : Enum<K>, V> Iterable<Pair<K, V>>.toEnumListMultimap(): EnumListMultimap<K, V> =
    ImmutableEnumListMultimap(ArrayEnumListMultimap<K, V>(enumEntries<K>()).apply { putAll(this@toEnumListMultimap) })

/**
 * Returns a new read-only [EnumListMultimap] containing all key-value pairs from this [Sequence] of [Pair]s.
 */
inline fun <reified K : Enum<K>, V> Sequence<Pair<K, V>>.toEnumListMultimap(): EnumListMultimap<K, V> =
    asIterable().toEnumListMultimap()

/**
 * Returns a new read-only [EnumListMultimap] containing all key-value pairs from this [ListMultimap].
 */
inline fun <reified K : Enum<K>, V> ListMultimap<K, V>.toEnumListMultimap(): EnumListMultimap<K, V> =
    ImmutableEnumListMultimap(ArrayEnumListMultimap<K, V>(enumEntries<K>()).apply { putAll(this@toEnumListMultimap) })
