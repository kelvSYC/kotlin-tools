package com.kelvsyc.kotlin.core.collections

/**
 * A mutable [SortedBiMap].
 */
interface MutableSortedBiMap<K, V> : SortedBiMap<K, V>, MutableBiMap<K, V> {
    override val keys: MutableSortedSet<K>
    override val inverse: MutableBiMap<V, K>

    override fun headMap(toKey: K, inclusive: Boolean): MutableSortedBiMap<K, V>
    override fun tailMap(fromKey: K, inclusive: Boolean): MutableSortedBiMap<K, V>
    override fun subMap(fromKey: K, fromInclusive: Boolean, toKey: K, toInclusive: Boolean): MutableSortedBiMap<K, V>
    override fun descendingMap(): MutableSortedBiMap<K, V>
    override fun descendingKeySet(): MutableSortedSet<K>
}
