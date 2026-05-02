package com.kelvsyc.kotlin.core.collections

/**
 * A mutable [FlatMultimap] that supports adding and removing key-value pairs.
 *
 * Unlike [MutableMap.put], [put] does not replace existing values — it appends a new key-value pair, preserving
 * duplicates. Similarly, [remove] removes all values for a key rather than a single value.
 */
interface MutableFlatMultimap<K, V> : FlatMultimap<K, V> {
    /**
     * Adds the key-value pair to this multimap. Unlike [MutableMap.put], this does not replace any existing mapping —
     * the new value is appended to the values for [key].
     */
    fun put(key: K, value: V)

    /**
     * Adds all [values] under [key] to this multimap, in iteration order. Has no effect if [values] is empty.
     */
    fun putAll(key: K, values: Iterable<V>) {
        values.forEach { put(key, it) }
    }

    /**
     * Adds all key-value pairs from [from] to this multimap, in [FlatMultimap.entries] order.
     */
    fun putAll(from: FlatMultimap<out K, V>) {
        from.entries.forEach { (k, v) -> put(k, v) }
    }

    /**
     * Adds all key-value pairs in [pairs] to this multimap, in iteration order.
     */
    fun putAll(pairs: Iterable<Pair<K, V>>) {
        pairs.forEach { (k, v) -> put(k, v) }
    }

    /**
     * Replaces all values for [key] with [values], and returns the list of values previously associated with [key].
     * If [values] is empty, [key] is removed from the multimap.
     */
    fun replaceValues(key: K, values: Iterable<V>): List<V>

    /**
     * Removes all key-value pairs with the given [key] and returns the list of values that were associated with it,
     * or an empty list if the key was not present.
     */
    fun remove(key: K): List<V>

    /**
     * Removes one occurrence of the key-value pair ([key], [value]) from this multimap. Returns `true` if the pair
     * was present and was removed, or `false` if it was not found.
     */
    fun remove(key: K, value: V): Boolean

    /**
     * Removes all key-value pairs from this multimap.
     */
    fun clear()
}
