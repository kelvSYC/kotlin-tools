package com.kelvsyc.internal.kotlin.core.collections

import com.kelvsyc.kotlin.core.collections.MutableBiMap
import com.kelvsyc.kotlin.core.collections.MutableEnumSortedBiMap
import com.kelvsyc.kotlin.core.collections.MutableSortedEnumBiMap
import kotlin.enums.EnumEntries

@PublishedApi
internal class SortedEnumBiMapImpl<K, V : Enum<V>> internal constructor(
    override val comparator: Comparator<in K>,
    override val valueEnumEntries: EnumEntries<V>,
    private val inner: FlexBiMap<K, V>,
    // Kept so the typed inverse can share the same live stores.
    private val bwd: ArrayMapStore<V, K>,
    private val fwd: TreeMapStore<K, V>,
) : MutableSortedEnumBiMap<K, V>, MutableBiMap<K, V> by inner {

    override val inverse: MutableEnumSortedBiMap<V, K> by lazy {
        // bwd (ArrayMapStore<V,K>) becomes fwd in the inverse; fwd (TreeMapStore<K,V>) becomes bwd.
        EnumSortedBiMapImpl(valueEnumEntries, comparator, FlexBiMap(bwd, fwd), fwd, bwd)
    }

    override fun equals(other: Any?): Boolean = inner.equals(other)
    override fun hashCode(): Int = inner.hashCode()
    override fun toString(): String = inner.toString()

    companion object {
        @PublishedApi
        internal operator fun <K, V : Enum<V>> invoke(
            comparator: Comparator<in K>,
            valueEnumEntries: EnumEntries<V>,
        ): SortedEnumBiMapImpl<K, V> {
            val fwd = TreeMapStore<K, V>(comparator)
            val bwd = ArrayMapStore<V, K>(valueEnumEntries)
            return SortedEnumBiMapImpl(comparator, valueEnumEntries, FlexBiMap(fwd, bwd), bwd, fwd)
        }
    }
}
