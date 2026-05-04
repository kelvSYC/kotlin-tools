package com.kelvsyc.kotlin.core.collections

import com.kelvsyc.internal.kotlin.core.collections.ArrayEnumMap
import kotlin.enums.enumEntries

/**
 * Returns an empty read-only [EnumMap] for enum type [K].
 */
inline fun <reified K : Enum<K>, V> emptyEnumMap(): EnumMap<K, V> = ArrayEnumMap(enumEntries<K>())

/**
 * Returns a new read-only [EnumMap] with the specified contents, given as key-value pairs.
 */
inline fun <reified K : Enum<K>, V> enumMapOf(vararg pairs: Pair<K, V>): EnumMap<K, V> =
    ArrayEnumMap<K, V>(enumEntries<K>()).apply { putAll(pairs) }

/**
 * Builds a read-only [EnumMap] by populating a [MutableEnumMap] using the given [builderAction] and returning
 * a read-only view of its contents.
 */
inline fun <reified K : Enum<K>, V> buildEnumMap(builderAction: MutableEnumMap<K, V>.() -> Unit): EnumMap<K, V> =
    ArrayEnumMap<K, V>(enumEntries<K>()).apply(builderAction)

/**
 * Returns a new read-only [EnumMap] containing all entries from this [Map].
 */
inline fun <reified K : Enum<K>, V> Map<K, V>.toEnumMap(): EnumMap<K, V> =
    ArrayEnumMap<K, V>(enumEntries<K>()).apply { putAll(this@toEnumMap) }
