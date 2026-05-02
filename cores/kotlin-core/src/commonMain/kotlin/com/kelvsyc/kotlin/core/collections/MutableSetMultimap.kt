package com.kelvsyc.kotlin.core.collections

/**
 * A mutable [SetMultimap] that supports adding and removing key-value pairs.
 *
 * Unlike [MutableMap.put], [put] does not replace existing values — it adds a new value to the set for [key].
 * Because values per key are a [Set], adding a duplicate pair has no effect and returns `false`.
 */
interface MutableSetMultimap<K, V> : SetMultimap<K, V> {
    /**
     * Adds the key-value pair ([key], [value]) to this multimap. Returns `true` if the pair was newly added, or
     * `false` if it was already present.
     */
    fun put(key: K, value: V): Boolean

    /**
     * Adds all [values] under [key] to this multimap. Has no effect for values already associated with [key].
     */
    fun putAll(key: K, values: Iterable<V>) {
        values.forEach { put(key, it) }
    }

    /**
     * Adds all key-value pairs from [from] to this multimap.
     */
    fun putAll(from: SetMultimap<out K, V>) {
        from.entries.forEach { (k, v) -> put(k, v) }
    }

    /**
     * Adds all key-value pairs in [pairs] to this multimap.
     */
    fun putAll(pairs: Iterable<Pair<K, V>>) {
        pairs.forEach { (k, v) -> put(k, v) }
    }

    /**
     * Replaces all values for [key] with [values], and returns the set of values previously associated with [key].
     * If [values] is empty, [key] is removed from the multimap.
     */
    fun replaceValues(key: K, values: Iterable<V>): Set<V>

    /**
     * Removes all key-value pairs with the given [key] and returns the set of values that were associated with it,
     * or an empty set if the key was not present.
     */
    fun remove(key: K): Set<V>

    /**
     * Removes the key-value pair ([key], [value]) from this multimap. Returns `true` if the pair was present and
     * removed, or `false` if it was not found.
     */
    fun remove(key: K, value: V): Boolean

    /**
     * Removes all key-value pairs from this multimap.
     */
    fun clear()
}
