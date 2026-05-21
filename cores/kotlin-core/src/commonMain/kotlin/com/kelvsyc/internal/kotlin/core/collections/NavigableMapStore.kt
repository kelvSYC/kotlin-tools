package com.kelvsyc.internal.kotlin.core.collections

import com.kelvsyc.kotlin.core.collections.MutableSortedSet

/**
 * A [MapStore] whose keys are ordered by a [Comparator], with floor/ceiling/lower/higher
 * key navigation and a sorted-key snapshot.
 */
internal interface NavigableMapStore<K, V> : MapStore<K, V> {
    val comparator: Comparator<in K>

    fun firstKey(): K
    fun lastKey(): K

    fun floorKey(key: K): K?
    fun ceilingKey(key: K): K?
    fun lowerKey(key: K): K?
    fun higherKey(key: K): K?

    /** Returns a snapshot of keys in comparator order. */
    fun keys(): MutableSortedSet<K>
}
