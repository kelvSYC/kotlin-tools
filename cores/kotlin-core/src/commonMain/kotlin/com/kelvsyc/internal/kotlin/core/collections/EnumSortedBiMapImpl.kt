package com.kelvsyc.internal.kotlin.core.collections

import com.kelvsyc.kotlin.core.collections.MutableBiMap
import com.kelvsyc.kotlin.core.collections.MutableEnumSortedBiMap
import com.kelvsyc.kotlin.core.collections.MutableSortedEnumBiMap
import kotlin.enums.EnumEntries

@PublishedApi
internal class EnumSortedBiMapImpl<K : Enum<K>, V> internal constructor(
    override val enumEntries: EnumEntries<K>,
    override val valueComparator: Comparator<in V>,
    private val inner: FlexBiMap<K, V>,
    // Kept so the typed inverse can share the same live stores.
    private val bwd: TreeMapStore<V, K>,
    private val fwd: ArrayMapStore<K, V>,
) : MutableEnumSortedBiMap<K, V>, MutableBiMap<K, V> by inner {

    override val inverse: MutableSortedEnumBiMap<V, K> by lazy {
        // bwd (TreeMapStore<V,K>) becomes sortedFwd of the inverse;
        // fwd (ArrayMapStore<K,V>) becomes enumBwd of the inverse.
        SortedEnumBiMapImpl(valueComparator, enumEntries, bwd, fwd)
    }

    override fun equals(other: Any?): Boolean = inner.equals(other)
    override fun hashCode(): Int = inner.hashCode()
    override fun toString(): String = inner.toString()

    companion object {
        @PublishedApi
        internal operator fun <K : Enum<K>, V> invoke(
            enumEntries: EnumEntries<K>,
            valueComparator: Comparator<in V>,
        ): EnumSortedBiMapImpl<K, V> {
            val fwd = ArrayMapStore<K, V>(enumEntries)
            val bwd = TreeMapStore<V, K>(valueComparator)
            return EnumSortedBiMapImpl(enumEntries, valueComparator, FlexBiMap(fwd, bwd), bwd, fwd)
        }
    }
}
