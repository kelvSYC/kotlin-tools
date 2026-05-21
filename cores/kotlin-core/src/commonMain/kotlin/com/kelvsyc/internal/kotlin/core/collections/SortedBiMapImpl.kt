package com.kelvsyc.internal.kotlin.core.collections

import com.kelvsyc.kotlin.core.collections.MutableSortedBiMap
import com.kelvsyc.kotlin.core.collections.MutableSortedSet

@PublishedApi
internal class SortedBiMapImpl<K, V> internal constructor(
    override val comparator: Comparator<in K>,
    private val sortedFwd: NavigableMapStore<K, V>,
    bwd: MapStore<V, K>,
) : FlexBiMap<K, V>(sortedFwd, bwd), MutableSortedBiMap<K, V> {

    constructor(comparator: Comparator<in K>) : this(
        comparator,
        TreeMapStore(comparator),
        HashMapStore(),
    )

    // ── SortedSet keys view ───────────────────────────────────────────────────

    override val keys: MutableSortedSet<K> get() = sortedFwd.keys()

    // ── Key navigation ────────────────────────────────────────────────────────

    override fun firstKey(): K = sortedFwd.firstKey()
    override fun lastKey(): K = sortedFwd.lastKey()
    override fun floorKey(key: K): K? = sortedFwd.floorKey(key)
    override fun ceilingKey(key: K): K? = sortedFwd.ceilingKey(key)
    override fun lowerKey(key: K): K? = sortedFwd.lowerKey(key)
    override fun higherKey(key: K): K? = sortedFwd.higherKey(key)

    override fun descendingKeySet(): MutableSortedSet<K> = sortedFwd.keys().descendingSet()

    // ── Range views (snapshots) ───────────────────────────────────────────────

    override fun headMap(toKey: K, inclusive: Boolean): MutableSortedBiMap<K, V> {
        val result = SortedBiMapImpl<K, V>(comparator)
        val iter = sortedFwd.entryIterator()
        while (iter.hasNext()) {
            val e = iter.next()
            val c = comparator.compare(e.key, toKey)
            if (if (inclusive) c <= 0 else c < 0) result.put(e.key, e.value) else break
        }
        return result
    }

    override fun tailMap(fromKey: K, inclusive: Boolean): MutableSortedBiMap<K, V> {
        val result = SortedBiMapImpl<K, V>(comparator)
        val iter = sortedFwd.entryIterator()
        while (iter.hasNext()) {
            val e = iter.next()
            val c = comparator.compare(e.key, fromKey)
            if (if (inclusive) c >= 0 else c > 0) result.put(e.key, e.value)
        }
        return result
    }

    override fun subMap(
        fromKey: K, fromInclusive: Boolean,
        toKey: K, toInclusive: Boolean,
    ): MutableSortedBiMap<K, V> {
        require(comparator.compare(fromKey, toKey) <= 0) { "fromKey must be <= toKey" }
        val result = SortedBiMapImpl<K, V>(comparator)
        val iter = sortedFwd.entryIterator()
        while (iter.hasNext()) {
            val e = iter.next()
            val lo = comparator.compare(e.key, fromKey).let { c -> if (fromInclusive) c >= 0 else c > 0 }
            val hi = comparator.compare(e.key, toKey).let { c -> if (toInclusive) c <= 0 else c < 0 }
            when {
                lo && hi -> result.put(e.key, e.value)
                comparator.compare(e.key, toKey) > 0 -> break
            }
        }
        return result
    }

    override fun descendingMap(): MutableSortedBiMap<K, V> {
        val result = SortedBiMapImpl<K, V>(comparator.reversed())
        val iter = sortedFwd.entryIterator()
        while (iter.hasNext()) {
            val e = iter.next()
            result.put(e.key, e.value)
        }
        return result
    }
}
