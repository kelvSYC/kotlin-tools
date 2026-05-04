package com.kelvsyc.kotlin.core.collections

import com.kelvsyc.internal.kotlin.core.collections.ArrayEnumMap
import kotlin.enums.enumEntries

/**
 * Returns a new empty [MutableEnumMap] for enum type [K].
 */
inline fun <reified K : Enum<K>, V> mutableEnumMapOf(): MutableEnumMap<K, V> = ArrayEnumMap(enumEntries<K>())

/**
 * Returns a new [MutableEnumMap] with the specified contents, given as key-value pairs.
 */
inline fun <reified K : Enum<K>, V> mutableEnumMapOf(vararg pairs: Pair<K, V>): MutableEnumMap<K, V> =
    ArrayEnumMap<K, V>(enumEntries<K>()).apply { putAll(pairs) }

/**
 * Returns a new [MutableEnumMap] containing all entries from this [Map].
 */
inline fun <reified K : Enum<K>, V> Map<K, V>.toMutableEnumMap(): MutableEnumMap<K, V> =
    ArrayEnumMap<K, V>(enumEntries<K>()).apply { putAll(this@toMutableEnumMap) }
