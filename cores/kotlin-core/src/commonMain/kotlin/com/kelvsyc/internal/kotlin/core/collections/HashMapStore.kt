package com.kelvsyc.internal.kotlin.core.collections

internal class HashMapStore<K, V>(
    private val map: HashMap<K, V> = HashMap(),
) : MapStore<K, V> {

    override val size: Int get() = map.size

    override fun get(key: K): V? = map[key]

    override fun put(key: K, value: V) { map[key] = value }

    override fun remove(key: K): V? = map.remove(key)

    override fun containsKey(key: K): Boolean = map.containsKey(key)

    override fun clear() = map.clear()

    override fun entryIterator(): MutableIterator<Map.Entry<K, V>> = map.entries.iterator()
}
