package com.kelvsyc.kotlin.core.collections

/**
 * A mutable [ListMultimap] that supports adding and removing key-value pairs.
 *
 * [put] appends a value to the end of the list for [key], preserving per-key insertion order. It does not replace
 * existing values. [remove] with a key removes all values for that key; [remove] with a key and value removes the
 * first matching occurrence (FIFO).
 */
interface MutableListMultimap<K, V> : ListMultimap<K, V> {
    /**
     * Appends [value] to the list of values for [key], preserving per-key insertion order.
     */
    fun put(key: K, value: V)

    /**
     * Appends all [values] to the list for [key] in iteration order. Has no effect if [values] is empty.
     */
    fun putAll(key: K, values: Iterable<V>) {
        values.forEach { put(key, it) }
    }

    /**
     * Adds all key-value pairs from [from] to this multimap, grouped by key in [from]'s key order.
     */
    fun putAll(from: ListMultimap<out K, V>) {
        from.asMap.forEach { (k, vs) -> vs.forEach { put(k, it) } }
    }

    /**
     * Adds all key-value pairs in [pairs] to this multimap in iteration order.
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
     * Removes the first occurrence of the key-value pair ([key], [value]) from this multimap (FIFO). Returns `true`
     * if the pair was present and removed, or `false` if it was not found.
     */
    fun remove(key: K, value: V): Boolean

    /**
     * Removes all key-value pairs from this multimap.
     */
    fun clear()
}
