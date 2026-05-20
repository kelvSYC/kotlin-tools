package com.kelvsyc.internal.kotlin.core.collections

internal class TreeMapStore<K, V>(
    private val tree: TreeMap<K, V>,
) : MapStore<K, V> {

    constructor(comparator: Comparator<in K>) : this(TreeMap(comparator))

    override val size: Int get() = tree.size
    override fun get(key: K): V? = tree[key]
    override fun put(key: K, value: V) { tree[key] = value }
    override fun remove(key: K): V? = tree.remove(key)
    override fun containsKey(key: K): Boolean = tree.containsKey(key)
    override fun clear() = tree.clear()

    // tree.entries creates a wrapper set each call; its iterator delegates to
    // TreeMap.entryIterator() whose remove() calls deleteNode() — correct for SyncedIterator.
    override fun entryIterator(): MutableIterator<Map.Entry<K, V>> = tree.entries.iterator()
}
