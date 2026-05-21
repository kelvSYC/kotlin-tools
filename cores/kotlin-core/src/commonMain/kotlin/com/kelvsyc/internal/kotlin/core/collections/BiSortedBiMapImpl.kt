package com.kelvsyc.internal.kotlin.core.collections

import com.kelvsyc.kotlin.core.collections.MutableBiMap
import com.kelvsyc.kotlin.core.collections.MutableBiSortedBiMap
import com.kelvsyc.kotlin.core.collections.MutableSortedBiMap

@PublishedApi
internal class BiSortedBiMapImpl<K, V> private constructor(
    override val comparator: Comparator<in K>,
    override val valueComparator: Comparator<in V>,
    private val inner: FlexBiMap<K, V>,
    private val bwd: TreeMapStore<V, K>,
    private val fwd: TreeMapStore<K, V>,
) : MutableBiSortedBiMap<K, V>, MutableBiMap<K, V> by inner {

    override val inverse: MutableSortedBiMap<V, K> by lazy {
        SortedBiMapImpl(valueComparator, FlexBiMap(bwd, fwd))
    }

    override fun equals(other: Any?): Boolean = inner.equals(other)
    override fun hashCode(): Int = inner.hashCode()
    override fun toString(): String = inner.toString()

    companion object {
        internal operator fun <K, V> invoke(
            comparator: Comparator<in K>,
            valueComparator: Comparator<in V>,
        ): BiSortedBiMapImpl<K, V> {
            val fwd = TreeMapStore<K, V>(comparator)
            val bwd = TreeMapStore<V, K>(valueComparator)
            return BiSortedBiMapImpl(comparator, valueComparator, FlexBiMap(fwd, bwd), bwd, fwd)
        }
    }
}
