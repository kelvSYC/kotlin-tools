package com.kelvsyc.kotlin.core.collections

import com.kelvsyc.internal.kotlin.core.collections.HashBiMap

/**
 * Returns an empty read-only [BiMap].
 */
fun <K, V> emptyBiMap(): BiMap<K, V> = HashBiMap()

/**
 * Returns a new read-only [BiMap] with the specified contents.
 *
 * @throws IllegalArgumentException if any value appears more than once.
 */
fun <K, V> biMapOf(vararg pairs: Pair<K, V>): BiMap<K, V> =
    HashBiMap<K, V>().apply { pairs.forEach { (k, v) -> put(k, v) } }

/**
 * Builds a read-only [BiMap] by populating a [MutableBiMap] using [builderAction].
 */
fun <K, V> buildBiMap(builderAction: MutableBiMap<K, V>.() -> Unit): BiMap<K, V> =
    HashBiMap<K, V>().apply(builderAction)

/**
 * Returns a new read-only [BiMap] containing all entries from this [Map].
 *
 * @throws IllegalArgumentException if any value appears more than once.
 */
fun <K, V> Map<K, V>.toBiMap(): BiMap<K, V> =
    HashBiMap<K, V>().apply { putAll(this@toBiMap) }

/**
 * Returns a new empty [MutableBiMap].
 */
fun <K, V> mutableBiMapOf(): MutableBiMap<K, V> = HashBiMap()

/**
 * Returns a new [MutableBiMap] with the specified contents.
 *
 * @throws IllegalArgumentException if any value appears more than once.
 */
fun <K, V> mutableBiMapOf(vararg pairs: Pair<K, V>): MutableBiMap<K, V> =
    HashBiMap<K, V>().apply { pairs.forEach { (k, v) -> put(k, v) } }

/**
 * Builds a [MutableBiMap] by applying [builderAction] to a new empty instance.
 */
fun <K, V> buildMutableBiMap(builderAction: MutableBiMap<K, V>.() -> Unit): MutableBiMap<K, V> =
    HashBiMap<K, V>().apply(builderAction)

/**
 * Returns a new [MutableBiMap] containing all entries from this [Map].
 *
 * @throws IllegalArgumentException if any value appears more than once.
 */
fun <K, V> Map<K, V>.toMutableBiMap(): MutableBiMap<K, V> =
    HashBiMap<K, V>().apply { putAll(this@toMutableBiMap) }
