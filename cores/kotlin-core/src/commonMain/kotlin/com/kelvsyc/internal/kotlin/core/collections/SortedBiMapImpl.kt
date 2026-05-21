package com.kelvsyc.internal.kotlin.core.collections

import com.kelvsyc.kotlin.core.collections.MutableBiMap
import com.kelvsyc.kotlin.core.collections.MutableSortedBiMap

@PublishedApi
internal class SortedBiMapImpl<K, V> internal constructor(
    override val comparator: Comparator<in K>,
    private val inner: FlexBiMap<K, V>,
) : MutableSortedBiMap<K, V>, MutableBiMap<K, V> by inner {

    constructor(comparator: Comparator<in K>) : this(
        comparator,
        FlexBiMap(TreeMapStore(comparator), HashMapStore()),
    )

    override fun equals(other: Any?): Boolean = inner.equals(other)
    override fun hashCode(): Int = inner.hashCode()
    override fun toString(): String = inner.toString()
}
