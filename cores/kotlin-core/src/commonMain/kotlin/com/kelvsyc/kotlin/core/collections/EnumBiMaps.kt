package com.kelvsyc.kotlin.core.collections

import com.kelvsyc.internal.kotlin.core.collections.ArrayEnumBiMap
import com.kelvsyc.internal.kotlin.core.collections.ImmutableEnumBiMap
import kotlin.enums.enumEntries

/**
 * Returns an empty read-only [EnumBiMap] for enum type [K].
 */
inline fun <reified K : Enum<K>, V> emptyEnumBiMap(): EnumBiMap<K, V> = ImmutableEnumBiMap(ArrayEnumBiMap(enumEntries<K>()))

/**
 * Returns a new read-only [EnumBiMap] with the specified contents.
 *
 * @throws IllegalArgumentException if any value appears more than once.
 */
inline fun <reified K : Enum<K>, V> enumBiMapOf(vararg pairs: Pair<K, V>): EnumBiMap<K, V> =
    ImmutableEnumBiMap(ArrayEnumBiMap<K, V>(enumEntries<K>()).apply { pairs.forEach { (k, v) -> put(k, v) } })

/**
 * Builds a read-only [EnumBiMap] by populating a [MutableEnumBiMap] using [builderAction].
 */
inline fun <reified K : Enum<K>, V> buildEnumBiMap(builderAction: MutableEnumBiMap<K, V>.() -> Unit): EnumBiMap<K, V> =
    ImmutableEnumBiMap(ArrayEnumBiMap<K, V>(enumEntries<K>()).apply(builderAction))

/**
 * Returns a new read-only [EnumBiMap] containing all entries from this [Map].
 *
 * @throws IllegalArgumentException if any value appears more than once.
 */
inline fun <reified K : Enum<K>, V> Map<K, V>.toEnumBiMap(): EnumBiMap<K, V> =
    ImmutableEnumBiMap(ArrayEnumBiMap<K, V>(enumEntries<K>()).apply { putAll(this@toEnumBiMap) })

/**
 * Returns a new empty [MutableEnumBiMap] for enum type [K].
 */
inline fun <reified K : Enum<K>, V> mutableEnumBiMapOf(): MutableEnumBiMap<K, V> = ArrayEnumBiMap(enumEntries<K>())

/**
 * Returns a new [MutableEnumBiMap] with the specified contents.
 *
 * @throws IllegalArgumentException if any value appears more than once.
 */
inline fun <reified K : Enum<K>, V> mutableEnumBiMapOf(vararg pairs: Pair<K, V>): MutableEnumBiMap<K, V> =
    ArrayEnumBiMap<K, V>(enumEntries<K>()).apply { pairs.forEach { (k, v) -> put(k, v) } }

/**
 * Builds a [MutableEnumBiMap] by applying [builderAction] to a new empty instance.
 */
inline fun <reified K : Enum<K>, V> buildMutableEnumBiMap(builderAction: MutableEnumBiMap<K, V>.() -> Unit): MutableEnumBiMap<K, V> =
    ArrayEnumBiMap<K, V>(enumEntries<K>()).apply(builderAction)

/**
 * Returns a new [MutableEnumBiMap] containing all entries from this [Map].
 *
 * @throws IllegalArgumentException if any value appears more than once.
 */
inline fun <reified K : Enum<K>, V> Map<K, V>.toMutableEnumBiMap(): MutableEnumBiMap<K, V> =
    ArrayEnumBiMap<K, V>(enumEntries<K>()).apply { putAll(this@toMutableEnumBiMap) }
