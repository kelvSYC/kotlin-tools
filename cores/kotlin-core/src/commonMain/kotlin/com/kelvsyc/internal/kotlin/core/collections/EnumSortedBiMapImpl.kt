package com.kelvsyc.internal.kotlin.core.collections

import com.kelvsyc.kotlin.core.collections.MutableEnumSortedBiMap
import com.kelvsyc.kotlin.core.collections.MutableSortedEnumBiMap
import kotlin.enums.EnumEntries

@PublishedApi
internal class EnumSortedBiMapImpl<K : Enum<K>, V> internal constructor(
    override val enumEntries: EnumEntries<K>,
    override val valueComparator: Comparator<in V>,
    private val enumFwd: ArrayMapStore<K, V>,
    private val sortedBwd: TreeMapStore<V, K>,
) : FlexBiMap<K, V>(enumFwd, sortedBwd), MutableEnumSortedBiMap<K, V> {

    override val inverse: MutableSortedEnumBiMap<V, K> by lazy {
        // sortedBwd (TreeMapStore<V,K>) becomes sortedFwd of the inverse;
        // enumFwd (ArrayMapStore<K,V>) becomes enumBwd of the inverse.
        SortedEnumBiMapImpl(valueComparator, enumEntries, sortedBwd, enumFwd)
    }

    companion object {
        @PublishedApi
        internal operator fun <K : Enum<K>, V> invoke(
            enumEntries: EnumEntries<K>,
            valueComparator: Comparator<in V>,
        ): EnumSortedBiMapImpl<K, V> {
            val fwd = ArrayMapStore<K, V>(enumEntries)
            val bwd = TreeMapStore<V, K>(valueComparator)
            return EnumSortedBiMapImpl(enumEntries, valueComparator, fwd, bwd)
        }
    }
}
