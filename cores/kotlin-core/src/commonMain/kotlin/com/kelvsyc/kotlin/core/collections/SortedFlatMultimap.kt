package com.kelvsyc.kotlin.core.collections

/**
 * A [FlatMultimap] whose entries are ordered by a [comparator] over **whole key-value pairs**.
 *
 * ### What makes this type different
 *
 * Every other sorted multimap in this library ([SortedListMultimap], [SortedSetMultimap],
 * [BiSortedSetMultimap]) sorts by **key** — the multimap is map-primary and must index by key to
 * locate value buckets. `SortedFlatMultimap` inherits from the pair-primary [FlatMultimap] family,
 * so its defining ordering is over the pair `(K, V)` as a whole. Consequences:
 *
 * - The comparator is `Comparator<Pair<K, V>>`, not `Comparator<K>`. It can sort by value first,
 *   by some combined key+value criterion, or by any total order over pairs.
 * - [entries] yields pairs in **comparator order**, not insertion order (contrast with the base
 *   [FlatMultimap] which preserves insertion order).
 * - [keys] is typed as `Set<K>`, **not** `SortedSet<K>` — a pair comparator gives no guarantee
 *   that keys are sorted. Similarly, [asMap] is `Map<K, List<V>>`, not `SortedMap`.
 * - Navigation methods ([firstEntry], [floorEntry], etc.) operate on **pairs**, not keys.
 *
 * ### Contrast with BiSortedSetMultimap
 *
 * [BiSortedSetMultimap] has two separate comparators (one for keys, one for values) because it is
 * map-primary: it must sort keys to index into buckets, and then sort values within each bucket.
 * `SortedFlatMultimap` has **one** comparator over pairs because the pair is the first-class unit.
 * If you need key-first lexicographic order without value deduplication, use the natural-order
 * factory (which composes a lex key-then-value comparator) or construct your own
 * `compareBy<Pair<K,V>> { it.first }.thenBy { it.second }` comparator.
 *
 * ### Duplicate pairs
 *
 * Duplicate pairs — including pairs that compare as equal by [comparator] — are preserved as
 * distinct entries. The comparator governs **ordering**, not membership. This differs from
 * [SortedMultiset] and tree-backed sets, where comparator equality implies the same slot.
 * Duplicates are preserved because pair contents may be mutable, so comparator equality at one
 * point in time does not imply equality later.
 *
 * ### Range views
 *
 * All range views ([headMultimap], [tailMultimap], [subMultimap], [descendingMultimap]) return
 * **snapshots** — independent copies at the time of the call. Bounds are expressed as
 * `Pair<K, V>` values interpreted by [comparator].
 */
interface SortedFlatMultimap<K, out V> : FlatMultimap<K, V> {
    /**
     * The comparator that determines the order of entries in this multimap. Operates on whole
     * key-value pairs; it is not required to sort keys in any particular order.
     */
    val comparator: Comparator<in Pair<K, @UnsafeVariance V>>

    /**
     * Returns the first (least) entry in this multimap according to [comparator].
     * @throws NoSuchElementException if the multimap is empty.
     */
    fun firstEntry(): Pair<K, V>

    /**
     * Returns the last (greatest) entry in this multimap according to [comparator].
     * @throws NoSuchElementException if the multimap is empty.
     */
    fun lastEntry(): Pair<K, V>

    /**
     * Returns the first (least) entry, or `null` if the multimap is empty.
     */
    fun firstEntryOrNull(): Pair<K, V>? = if (isEmpty()) null else firstEntry()

    /**
     * Returns the last (greatest) entry, or `null` if the multimap is empty.
     */
    fun lastEntryOrNull(): Pair<K, V>? = if (isEmpty()) null else lastEntry()

    /**
     * Returns the greatest entry less than or equal to [pair] by [comparator], or `null` if no
     * such entry exists.
     */
    fun floorEntry(pair: Pair<K, @UnsafeVariance V>): Pair<K, V>?

    /**
     * Returns the least entry greater than or equal to [pair] by [comparator], or `null` if no
     * such entry exists.
     */
    fun ceilingEntry(pair: Pair<K, @UnsafeVariance V>): Pair<K, V>?

    /**
     * Returns the greatest entry strictly less than [pair] by [comparator], or `null` if no such
     * entry exists.
     */
    fun lowerEntry(pair: Pair<K, @UnsafeVariance V>): Pair<K, V>?

    /**
     * Returns the least entry strictly greater than [pair] by [comparator], or `null` if no such
     * entry exists.
     */
    fun higherEntry(pair: Pair<K, @UnsafeVariance V>): Pair<K, V>?

    /**
     * Returns a snapshot of entries whose pairs are strictly less than (or less than or equal to,
     * if [inclusive] is `true`) [toPair] according to [comparator].
     */
    fun headMultimap(toPair: Pair<K, @UnsafeVariance V>, inclusive: Boolean): SortedFlatMultimap<K, V>

    /**
     * Returns a snapshot of entries whose pairs are greater than or equal to (or strictly greater
     * than, if [inclusive] is `false`) [fromPair] according to [comparator].
     */
    fun tailMultimap(fromPair: Pair<K, @UnsafeVariance V>, inclusive: Boolean): SortedFlatMultimap<K, V>

    /**
     * Returns a snapshot of entries whose pairs fall in the range from [fromPair] to [toPair]
     * according to [comparator].
     *
     * [fromInclusive] controls whether [fromPair] itself is included; [toInclusive] controls
     * whether [toPair] itself is included.
     *
     * @throws IllegalArgumentException if [fromPair] is greater than [toPair] by [comparator].
     */
    fun subMultimap(
        fromPair: Pair<K, @UnsafeVariance V>, fromInclusive: Boolean,
        toPair: Pair<K, @UnsafeVariance V>, toInclusive: Boolean,
    ): SortedFlatMultimap<K, V>

    /**
     * Returns a snapshot of all entries in this multimap in reverse comparator order.
     */
    fun descendingMultimap(): SortedFlatMultimap<K, V>
}
