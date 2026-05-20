package com.kelvsyc.kotlin.core.collections

import com.kelvsyc.internal.kotlin.core.collections.BiArrayEnumBiMap
import kotlin.enums.enumEntries

/**
 * Returns a new read-only [BiEnumBiMap] with the specified contents.
 *
 * @throws IllegalArgumentException if any value appears more than once.
 */
inline fun <reified K : Enum<K>, reified V : Enum<V>> biEnumBiMapOf(
    vararg pairs: Pair<K, V>,
): BiEnumBiMap<K, V> =
    BiArrayEnumBiMap(enumEntries<K>(), enumEntries<V>()).apply { pairs.forEach { (k, v) -> put(k, v) } }

/**
 * Builds a read-only [BiEnumBiMap] by populating a [MutableBiEnumBiMap] using [builderAction].
 */
inline fun <reified K : Enum<K>, reified V : Enum<V>> buildBiEnumBiMap(
    builderAction: MutableBiEnumBiMap<K, V>.() -> Unit,
): BiEnumBiMap<K, V> =
    BiArrayEnumBiMap(enumEntries<K>(), enumEntries<V>()).apply(builderAction)

/**
 * Returns a new read-only [BiEnumBiMap] containing all entries from this [Map].
 *
 * @throws IllegalArgumentException if any value appears more than once.
 */
inline fun <reified K : Enum<K>, reified V : Enum<V>> Map<K, V>.toBiEnumBiMap(): BiEnumBiMap<K, V> =
    BiArrayEnumBiMap(enumEntries<K>(), enumEntries<V>()).apply { putAll(this@toBiEnumBiMap) }

/**
 * Returns a new empty [MutableBiEnumBiMap].
 */
inline fun <reified K : Enum<K>, reified V : Enum<V>> mutableBiEnumBiMapOf(): MutableBiEnumBiMap<K, V> =
    BiArrayEnumBiMap(enumEntries<K>(), enumEntries<V>())

/**
 * Returns a new [MutableBiEnumBiMap] with the specified contents.
 *
 * @throws IllegalArgumentException if any value appears more than once.
 */
inline fun <reified K : Enum<K>, reified V : Enum<V>> mutableBiEnumBiMapOf(
    vararg pairs: Pair<K, V>,
): MutableBiEnumBiMap<K, V> =
    BiArrayEnumBiMap(enumEntries<K>(), enumEntries<V>()).apply { pairs.forEach { (k, v) -> put(k, v) } }

/**
 * Builds a [MutableBiEnumBiMap] by applying [builderAction] to a new empty instance.
 */
inline fun <reified K : Enum<K>, reified V : Enum<V>> buildMutableBiEnumBiMap(
    builderAction: MutableBiEnumBiMap<K, V>.() -> Unit,
): MutableBiEnumBiMap<K, V> =
    BiArrayEnumBiMap(enumEntries<K>(), enumEntries<V>()).apply(builderAction)

/**
 * Returns a new [MutableBiEnumBiMap] containing all entries from this [Map].
 *
 * @throws IllegalArgumentException if any value appears more than once.
 */
inline fun <reified K : Enum<K>, reified V : Enum<V>> Map<K, V>.toMutableBiEnumBiMap(): MutableBiEnumBiMap<K, V> =
    BiArrayEnumBiMap(enumEntries<K>(), enumEntries<V>()).apply { putAll(this@toMutableBiEnumBiMap) }
