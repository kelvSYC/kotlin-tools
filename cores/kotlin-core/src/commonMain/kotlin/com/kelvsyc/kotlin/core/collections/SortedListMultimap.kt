package com.kelvsyc.kotlin.core.collections

/**
 * A [ListMultimap] whose keys are ordered by a [Comparator]. Iteration over [keys], [entries], and [values] always
 * follows comparator key order; values per key remain in insertion order.
 *
 * ### Range views
 *
 * All range views ([headMultimap], [tailMultimap], [subMultimap], [descendingMultimap]) return **snapshots** —
 * independent copies at the time of the call.
 */
interface SortedListMultimap<K, out V> : ListMultimap<K, V> {
    /**
     * The comparator that determines the order of keys in this multimap.
     */
    val comparator: Comparator<in K>

    /**
     * Returns a read-only view of this multimap as a [SortedMap] mapping each key to its non-empty ordered [List]
     * of values, in comparator key order.
     */
    override val asMap: SortedMap<K, List<@UnsafeVariance V>>

    /**
     * Returns a read-only [SortedSet] of all distinct keys in this multimap, in comparator order.
     */
    override val keys: SortedSet<K> get() = asMap.keys

    /**
     * Returns the first (least) key in this multimap.
     * @throws NoSuchElementException if the multimap is empty.
     */
    fun firstKey(): K

    /**
     * Returns the last (greatest) key in this multimap.
     * @throws NoSuchElementException if the multimap is empty.
     */
    fun lastKey(): K

    /**
     * Returns the first (least) key, or `null` if the multimap is empty.
     */
    fun firstKeyOrNull(): K? = if (isEmpty()) null else firstKey()

    /**
     * Returns the last (greatest) key, or `null` if the multimap is empty.
     */
    fun lastKeyOrNull(): K? = if (isEmpty()) null else lastKey()

    /**
     * Returns the greatest key less than or equal to [key], or `null` if no such key exists.
     */
    fun floorKey(key: K): K?

    /**
     * Returns the least key greater than or equal to [key], or `null` if no such key exists.
     */
    fun ceilingKey(key: K): K?

    /**
     * Returns the greatest key strictly less than [key], or `null` if no such key exists.
     */
    fun lowerKey(key: K): K?

    /**
     * Returns the least key strictly greater than [key], or `null` if no such key exists.
     */
    fun higherKey(key: K): K?

    /**
     * Returns a snapshot of all entries whose keys are strictly less than (or less than or equal to, if [inclusive]
     * is `true`) [toKey].
     */
    fun headMultimap(toKey: K, inclusive: Boolean): SortedListMultimap<K, V>

    /**
     * Returns a snapshot of all entries whose keys are greater than or equal to (or strictly greater than, if
     * [inclusive] is `false`) [fromKey].
     */
    fun tailMultimap(fromKey: K, inclusive: Boolean): SortedListMultimap<K, V>

    /**
     * Returns a snapshot of all entries whose keys fall in the range from [fromKey] to [toKey].
     *
     * [fromInclusive] controls whether [fromKey] itself is included; [toInclusive] controls whether [toKey] itself
     * is included.
     *
     * @throws IllegalArgumentException if [fromKey] is greater than [toKey] by the [comparator].
     */
    fun subMultimap(fromKey: K, fromInclusive: Boolean, toKey: K, toInclusive: Boolean): SortedListMultimap<K, V>

    /**
     * Returns a snapshot of all entries in this multimap in reverse comparator key order.
     */
    fun descendingMultimap(): SortedListMultimap<K, V>
}
