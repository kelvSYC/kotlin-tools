package com.kelvsyc.kotlin.core.collections

/**
 * A `ListMultimap` is a one-to-many association where each key maps to an ordered [List] of values. It is map-primary:
 * [asMap] is the authoritative view, and [entries], [keys], and [values] are derived from it. Keys are in
 * first-occurrence insertion order; values per key are in the order they were inserted under that key.
 *
 * Duplicate key-value pairs are permitted.
 *
 * @see FlatMultimap for the pair-list-primary counterpart that preserves overall insertion order across all keys.
 */
interface ListMultimap<K, out V> {
    /**
     * Returns the total number of key-value pairs in this multimap.
     */
    val size: Int

    /**
     * Returns a read-only view of this multimap as a [Map] mapping each key to its non-empty ordered [List] of values.
     */
    val asMap: Map<K, List<@UnsafeVariance V>>

    /**
     * Returns a read-only [Set] of all distinct keys in this multimap, in first-occurrence insertion order.
     */
    val keys: Set<K> get() = asMap.keys

    /**
     * Returns a read-only [Collection] of all values in this multimap in key-grouped order. A value may appear more
     * than once if it is associated with a key multiple times.
     */
    val values: Collection<@UnsafeVariance V> get() = asMap.values.flatten()

    /**
     * Returns a read-only [Collection] of all key-value pairs in this multimap in key-grouped order.
     */
    val entries: Collection<Pair<K, @UnsafeVariance V>>
        get() = asMap.flatMap { (k, vs) -> vs.map { k to it } }

    /**
     * Returns `true` if this multimap contains no key-value pairs.
     */
    fun isEmpty(): Boolean = size == 0

    /**
     * Returns `true` if this multimap contains the specified [key].
     */
    fun containsKey(key: K): Boolean = asMap.containsKey(key)

    /**
     * Returns `true` if this multimap maps one or more keys to the specified [value].
     */
    fun containsValue(value: @UnsafeVariance V): Boolean = asMap.values.any { value in it }

    /**
     * Returns `true` if this multimap contains at least one occurrence of the key-value pair ([key], [value]).
     */
    fun containsEntry(key: K, value: @UnsafeVariance V): Boolean = asMap[key]?.contains(value) == true

    /**
     * Returns the ordered [List] of values associated with [key], or an empty list if [key] is not present.
     */
    operator fun get(key: K): List<@UnsafeVariance V> = asMap[key] ?: emptyList()

    /**
     * Compares the specified object with this multimap for equality. Two multimaps are equal if and only if [asMap]
     * returns [Map]s that are also equal.
     */
    override fun equals(other: Any?): Boolean

    /**
     * Returns the hash code for this multimap. The hash code is guaranteed to be the same as
     * [asMap][asMap]`.`[hashCode]`()`.
     */
    override fun hashCode(): Int
}
