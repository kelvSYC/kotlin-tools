package com.kelvsyc.kotlin.core.collections

/**
 * A mutable [SortedMap] that supports adding and removing key-value pairs.
 *
 * [keys] is typed as [MutableSortedSet]`<K>`, satisfying both [SortedMap.keys]`: SortedSet<K>` and
 * [MutableMap.keys]`: MutableSet<K>`.
 *
 * Range views ([headMap], [tailMap], [subMap], [descendingMap]) return mutable snapshots — independent
 * [MutableSortedMap] copies at the time of the call.
 */
interface MutableSortedMap<K, V> : SortedMap<K, V>, MutableMap<K, V> {
    override val keys: MutableSortedSet<K>

    override fun headMap(toKey: K, inclusive: Boolean): MutableSortedMap<K, V>
    override fun tailMap(fromKey: K, inclusive: Boolean): MutableSortedMap<K, V>
    override fun subMap(fromKey: K, fromInclusive: Boolean, toKey: K, toInclusive: Boolean): MutableSortedMap<K, V>
    override fun descendingMap(): MutableSortedMap<K, V>
    override fun descendingKeySet(): MutableSortedSet<K>
}
