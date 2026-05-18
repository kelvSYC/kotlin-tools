package com.kelvsyc.kotlin.commons.collections

import org.apache.commons.collections4.MapUtils
import org.apache.commons.collections4.MultiValuedMap
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap

/**
 * Inverts a map by swapping keys and values.
 *
 * If the map has duplicate values, only one key will survive in the result
 * (determined by the underlying `MapUtils.invertMap` implementation).
 *
 * For example: `mapOf("a" to 1, "b" to 2).invertedMap()` returns `mapOf(1 to "a", 2 to "b")`.
 */
fun <K, V> Map<K, V>.invertedMap(): Map<V, K> = MapUtils.invertMap(this)

/**
 * Converts a map of collections to a MultiValuedMap.
 *
 * For example: `mapOf("k" to listOf(1, 2, 3)).toMultiValuedMap()["k"]` contains `[1, 2, 3]`.
 */
fun <K, V> Map<K, Collection<V>>.toMultiValuedMap(): MultiValuedMap<K, V> {
    val result = ArrayListValuedHashMap<K, V>()
    for ((key, values) in this) {
        result.putAll(key, values)
    }
    return result
}
