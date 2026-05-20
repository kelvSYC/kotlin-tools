package com.kelvsyc.kotlin.core.collections

/**
 * A [SortedSetMultimap] where values per key are also ordered by a [valueComparator]. Both keys (ordered by
 * [comparator]) and per-key value sets (ordered by [valueComparator]) are sorted.
 *
 * [asMap] is typed as `SortedMap<K, SortedSet<V>>`, narrowing [SortedSetMultimap.asMap]. Per-key value navigation
 * is available directly through the [SortedSet] values in [asMap].
 *
 * ### Range views
 *
 * All range views ([headMultimap], [tailMultimap], [subMultimap], [descendingMultimap]) return **snapshots** —
 * independent copies at the time of the call. Snapshots preserve both comparators.
 */
interface BiSortedSetMultimap<K, out V> : SortedSetMultimap<K, V> {
    /**
     * The comparator that determines the order of values within each key's [SortedSet].
     */
    val valueComparator: Comparator<in @UnsafeVariance V>

    /**
     * Returns a read-only view of this multimap as a [SortedMap] mapping each key to its non-empty [SortedSet] of
     * values, with keys in [comparator] order and values within each set in [valueComparator] order.
     */
    override val asMap: SortedMap<K, SortedSet<@UnsafeVariance V>>

    override fun headMultimap(toKey: K, inclusive: Boolean): BiSortedSetMultimap<K, V>
    override fun tailMultimap(fromKey: K, inclusive: Boolean): BiSortedSetMultimap<K, V>
    override fun subMultimap(fromKey: K, fromInclusive: Boolean, toKey: K, toInclusive: Boolean): BiSortedSetMultimap<K, V>
    override fun descendingMultimap(): BiSortedSetMultimap<K, V>
}
