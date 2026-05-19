package com.kelvsyc.kotlin.core.collections

/**
 * A [Map] whose keys are ordered by a [Comparator]. Iteration over [keys], [entries], and [values] always
 * follows comparator key order.
 *
 * This is a Kotlin Multiplatform type with no JVM interoperability requirement; it does not extend
 * `java.util.SortedMap` or `java.util.NavigableMap`.
 *
 * ### Collapsed SortedMap + NavigableMap
 *
 * Java splits sorted-map functionality across `SortedMap` (basic sorted contract) and `NavigableMap` (floor/ceiling,
 * inclusive/exclusive bounds). That split was a historical accident — Java 6 added `NavigableMap` to fix
 * `SortedMap`'s exclusive-only upper-bound semantics. This interface merges both into one.
 *
 * ### keys type
 *
 * [keys] is typed as `SortedSet<K>`, narrowing `Map.keys: Set<K>`. This is possible via Kotlin's covariant return
 * type overrides and eliminates the type-system hole present in Java's `SortedMap.keySet()`.
 *
 * ### Range views
 *
 * All range views ([headMap], [tailMap], [subMap], [descendingMap]) return **snapshots** — independent copies at
 * the time of the call. This follows Kotlin's collection idiom (cf. `filter`, `map`).
 *
 * ### entries ordering note
 *
 * The static type of [entries] is `Set<Map.Entry<K,V>>`, which carries no ordering contract. By this interface's
 * contract, iteration order is comparator key order — the same situation as `LinkedHashMap` in the stdlib.
 */
interface SortedMap<K, out V> : Map<K, V> {
    /**
     * The comparator that determines the order of keys in this map.
     */
    val comparator: Comparator<in K>

    /**
     * Returns a [SortedSet] view of the keys in this map, in comparator order.
     */
    override val keys: SortedSet<K>

    /**
     * Returns the first (least) key in this map.
     * @throws NoSuchElementException if the map is empty.
     */
    fun firstKey(): K

    /**
     * Returns the last (greatest) key in this map.
     * @throws NoSuchElementException if the map is empty.
     */
    fun lastKey(): K

    /**
     * Returns the first (least) key, or `null` if the map is empty.
     */
    fun firstKeyOrNull(): K? = if (isEmpty()) null else firstKey()

    /**
     * Returns the last (greatest) key, or `null` if the map is empty.
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
     * Returns a snapshot of entries whose keys are strictly less than (or less than or equal to, if [inclusive]
     * is `true`) [toKey].
     */
    fun headMap(toKey: K, inclusive: Boolean): SortedMap<K, V>

    /**
     * Returns a snapshot of entries whose keys are greater than or equal to (or strictly greater than, if
     * [inclusive] is `false`) [fromKey].
     */
    fun tailMap(fromKey: K, inclusive: Boolean): SortedMap<K, V>

    /**
     * Returns a snapshot of entries whose keys fall in the range from [fromKey] to [toKey].
     *
     * [fromInclusive] controls whether [fromKey] itself is included; [toInclusive] controls whether [toKey] itself
     * is included.
     *
     * @throws IllegalArgumentException if [fromKey] is greater than [toKey] by the [comparator].
     */
    fun subMap(fromKey: K, fromInclusive: Boolean, toKey: K, toInclusive: Boolean): SortedMap<K, V>

    /**
     * Returns a snapshot of all entries in this map in reverse comparator key order.
     */
    fun descendingMap(): SortedMap<K, V>

    /**
     * Returns a snapshot of all keys in this map in reverse comparator order.
     */
    fun descendingKeySet(): SortedSet<K>
}
