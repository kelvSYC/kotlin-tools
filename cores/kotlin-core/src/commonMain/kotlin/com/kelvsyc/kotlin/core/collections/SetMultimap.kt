package com.kelvsyc.kotlin.core.collections

/**
 * A `SetMultimap` is a one-to-many association where each key maps to a [Set] of values. Duplicate key-value pairs
 * are not permitted: adding a pair that already exists has no effect.
 *
 * Unlike [ListMultimap], which is primarily an ordered list of pairs with a map-like view, `SetMultimap` is
 * map-primary: [asMap] is the authoritative view, and [entries], [keys], and [values] are derived from it.
 *
 * [equals] and [hashCode] delegate to [asMap].
 */
interface SetMultimap<K, out V> {
    /**
     * Returns the total number of key-value pairs in this multimap.
     */
    val size: Int

    /**
     * Returns a read-only view of this multimap as a [Map] mapping each key to its non-empty [Set] of values.
     */
    val asMap: Map<K, Set<@UnsafeVariance V>>

    /**
     * Returns a read-only [Set] of all distinct keys in this multimap.
     */
    val keys: Set<K> get() = asMap.keys

    /**
     * Returns a read-only [Collection] of all values in this multimap. A value may appear more than once if it is
     * associated with multiple keys.
     */
    val values: Collection<@UnsafeVariance V> get() = asMap.values.flatten()

    /**
     * Returns a read-only [Collection] of all key-value pairs in this multimap.
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
     * Returns `true` if this multimap contains the key-value pair ([key], [value]).
     */
    fun containsEntry(key: K, value: @UnsafeVariance V): Boolean = asMap[key]?.contains(value) == true

    /**
     * Returns the [Set] of values associated with [key], or an empty set if [key] is not present.
     */
    operator fun get(key: K): Set<@UnsafeVariance V> = asMap[key] ?: emptySet()

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
