package com.kelvsyc.internal.kotlin.core.collections

import com.kelvsyc.kotlin.core.collections.MutableSortedSet

internal class TreeMapStore<K, V>(
    private val tree: TreeMap<K, V>,
) : NavigableMapStore<K, V> {

    constructor(comparator: Comparator<in K>) : this(TreeMap(comparator))

    override val comparator: Comparator<in K> get() = tree.comparator

    override val size: Int get() = tree.size
    override fun get(key: K): V? = tree[key]
    override fun put(key: K, value: V) { tree[key] = value }
    override fun remove(key: K): V? = tree.remove(key)
    override fun containsKey(key: K): Boolean = tree.containsKey(key)
    override fun clear() = tree.clear()

    override fun firstKey(): K = tree.firstKey()
    override fun lastKey(): K = tree.lastKey()
    override fun floorKey(key: K): K? = tree.floorKey(key)
    override fun ceilingKey(key: K): K? = tree.ceilingKey(key)
    override fun lowerKey(key: K): K? = tree.lowerKey(key)
    override fun higherKey(key: K): K? = tree.higherKey(key)
    override fun keys(): MutableSortedSet<K> = tree.keys

    // tree.entries creates a wrapper set each call; its iterator delegates to
    // TreeMap.entryIterator() whose remove() calls deleteNode() — correct for SyncedIterator.
    override fun entryIterator(): MutableIterator<Map.Entry<K, V>> = tree.entries.iterator()
}
