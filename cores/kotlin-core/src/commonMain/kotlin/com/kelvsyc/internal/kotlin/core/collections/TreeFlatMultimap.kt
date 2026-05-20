package com.kelvsyc.internal.kotlin.core.collections

import com.kelvsyc.kotlin.core.collections.FlatMultimap
import com.kelvsyc.kotlin.core.collections.MutableSortedFlatMultimap
import com.kelvsyc.kotlin.core.collections.SortedFlatMultimap

/**
 * Dual-index implementation of [MutableSortedFlatMultimap].
 *
 * Two structures are kept in sync on every mutation (analogous to [LinkedHashMap]):
 *  - [sortedPairs]: an [ArrayList] of all pairs maintained in [comparator] order; this is the
 *    source of truth for pair-order traversal and navigation.
 *  - [keyIndex]: a [HashMap] mapping each key to the ordered list of its values, in the same
 *    relative order as they appear in [sortedPairs]; this powers O(1) key-based lookup.
 *
 * Duplicate pairs (including pairs that compare as equal by [comparator]) are preserved as
 * distinct entries in [sortedPairs].
 */
internal class TreeFlatMultimap<K, V>(
    override val comparator: Comparator<in Pair<K, V>>,
) : MutableSortedFlatMultimap<K, V> {

    private val sortedPairs = ArrayList<Pair<K, V>>()
    private val keyIndex = HashMap<K, MutableList<V>>()

    // ── FlatMultimap ──────────────────────────────────────────────────────────

    override val size: Int get() = sortedPairs.size

    override val entries: Collection<Pair<K, V>> get() = sortedPairs.toList()

    override val keys: Set<K> get() = keyIndex.keys.toSet()

    override val values: Collection<V> get() = sortedPairs.map { it.second }

    override val asMap: Map<K, List<V>> get() = keyIndex.mapValues { it.value.toList() }

    override fun isEmpty(): Boolean = sortedPairs.isEmpty()

    override fun containsKey(key: K): Boolean = keyIndex.containsKey(key)

    override fun containsValue(value: @UnsafeVariance V): Boolean =
        sortedPairs.any { it.second == value }

    override fun containsEntry(key: K, value: @UnsafeVariance V): Boolean =
        keyIndex[key]?.contains(value) == true

    override fun get(key: K): List<V> = keyIndex[key]?.toList() ?: emptyList()

    // ── SortedFlatMultimap navigation ─────────────────────────────────────────

    override fun firstEntry(): Pair<K, V> =
        sortedPairs.firstOrNull() ?: throw NoSuchElementException("Multimap is empty")

    override fun lastEntry(): Pair<K, V> =
        sortedPairs.lastOrNull() ?: throw NoSuchElementException("Multimap is empty")

    override fun floorEntry(pair: Pair<K, @UnsafeVariance V>): Pair<K, V>? {
        var result: Pair<K, V>? = null
        for (p in sortedPairs) {
            val c = comparator.compare(p, pair)
            if (c <= 0) result = p else break
        }
        return result
    }

    override fun ceilingEntry(pair: Pair<K, @UnsafeVariance V>): Pair<K, V>? {
        for (p in sortedPairs) {
            if (comparator.compare(p, pair) >= 0) return p
        }
        return null
    }

    override fun lowerEntry(pair: Pair<K, @UnsafeVariance V>): Pair<K, V>? {
        var result: Pair<K, V>? = null
        for (p in sortedPairs) {
            val c = comparator.compare(p, pair)
            if (c < 0) result = p else break
        }
        return result
    }

    override fun higherEntry(pair: Pair<K, @UnsafeVariance V>): Pair<K, V>? {
        for (p in sortedPairs) {
            if (comparator.compare(p, pair) > 0) return p
        }
        return null
    }

    // ── Range views (snapshots) ───────────────────────────────────────────────

    override fun headMultimap(toPair: Pair<K, V>, inclusive: Boolean): MutableSortedFlatMultimap<K, V> {
        val result = TreeFlatMultimap<K, V>(comparator)
        for (p in sortedPairs) {
            val c = comparator.compare(p, toPair)
            if (if (inclusive) c <= 0 else c < 0) result.put(p.first, p.second)
        }
        return result
    }

    override fun tailMultimap(fromPair: Pair<K, V>, inclusive: Boolean): MutableSortedFlatMultimap<K, V> {
        val result = TreeFlatMultimap<K, V>(comparator)
        for (p in sortedPairs) {
            val c = comparator.compare(p, fromPair)
            if (if (inclusive) c >= 0 else c > 0) result.put(p.first, p.second)
        }
        return result
    }

    override fun subMultimap(
        fromPair: Pair<K, V>, fromInclusive: Boolean,
        toPair: Pair<K, V>, toInclusive: Boolean,
    ): MutableSortedFlatMultimap<K, V> {
        require(comparator.compare(fromPair, toPair) <= 0) { "fromPair must be <= toPair" }
        val result = TreeFlatMultimap<K, V>(comparator)
        for (p in sortedPairs) {
            val lo = comparator.compare(p, fromPair).let { if (fromInclusive) it >= 0 else it > 0 }
            val hi = comparator.compare(p, toPair).let { if (toInclusive) it <= 0 else it < 0 }
            if (lo && hi) result.put(p.first, p.second)
        }
        return result
    }

    override fun descendingMultimap(): MutableSortedFlatMultimap<K, V> {
        val result = TreeFlatMultimap<K, V>(comparator.reversed())
        for (p in sortedPairs.asReversed()) result.put(p.first, p.second)
        return result
    }

    // ── MutableFlatMultimap ───────────────────────────────────────────────────

    override fun put(key: K, value: V) {
        val pair = Pair(key, value)
        val pos = insertionIndex(pair)
        sortedPairs.add(pos, pair)
        val bucket = keyIndex.getOrPut(key) { mutableListOf() }
        val bucketPos = bucketInsertionIndex(bucket, pair)
        bucket.add(bucketPos, value)
    }

    override fun replaceValues(key: K, values: Iterable<V>): List<V> {
        val old = removeAllForKey(key)
        values.forEach { put(key, it) }
        return old
    }

    override fun remove(key: K): List<V> = removeAllForKey(key)

    override fun remove(key: K, value: V): Boolean {
        val bucket = keyIndex[key] ?: return false
        val bucketIdx = bucket.indexOf(value)
        if (bucketIdx < 0) return false
        bucket.removeAt(bucketIdx)
        if (bucket.isEmpty()) keyIndex.remove(key)
        val pairIdx = sortedPairs.indexOfFirst { it.first == key && it.second == value }
        if (pairIdx >= 0) sortedPairs.removeAt(pairIdx)
        return true
    }

    override fun clear() {
        sortedPairs.clear()
        keyIndex.clear()
    }

    // ── equals / hashCode / toString ──────────────────────────────────────────

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is FlatMultimap<*, *>) return false
        return asMap == other.asMap
    }

    override fun hashCode(): Int = asMap.hashCode()

    override fun toString(): String = entries.toString()

    // ── Internal helpers ──────────────────────────────────────────────────────

    private fun insertionIndex(pair: Pair<K, V>): Int {
        var lo = 0; var hi = sortedPairs.size
        while (lo < hi) {
            val mid = (lo + hi) ushr 1
            if (comparator.compare(sortedPairs[mid], pair) <= 0) lo = mid + 1 else hi = mid
        }
        return lo
    }

    private fun bucketInsertionIndex(bucket: List<V>, pair: Pair<K, V>): Int {
        var lo = 0; var hi = bucket.size
        while (lo < hi) {
            val mid = (lo + hi) ushr 1
            if (comparator.compare(Pair(pair.first, bucket[mid]), pair) <= 0) lo = mid + 1 else hi = mid
        }
        return lo
    }

    private fun removeAllForKey(key: K): List<V> {
        val bucket = keyIndex.remove(key) ?: return emptyList()
        sortedPairs.removeAll { it.first == key }
        return bucket.toList()
    }
}
