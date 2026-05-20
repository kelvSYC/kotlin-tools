package com.kelvsyc.internal.kotlin.core.collections

/**
 * One-directional storage for a [com.kelvsyc.kotlin.core.collections.BiMap].
 *
 * A BiMap is backed by two `MapStore` instances — one for each direction — sharing their
 * underlying data. The inverse view is formed by constructing a
 * [com.kelvsyc.internal.kotlin.core.collections.FlexBiMap] with the two stores swapped.
 */
internal interface MapStore<K, V> {
    val size: Int
    fun get(key: K): V?
    fun put(key: K, value: V)
    fun remove(key: K): V?
    fun containsKey(key: K): Boolean
    fun clear()

    /**
     * Returns a mutable iterator over the entries in this store. The iterator's [MutableIterator.remove]
     * must remove the entry from this store's underlying data so that [FlexBiMap]'s synced
     * iterator can keep the other direction in sync.
     */
    fun entryIterator(): MutableIterator<Map.Entry<K, V>>
}
