package com.kelvsyc.internal.kotlin.core.collections

import com.kelvsyc.kotlin.core.collections.EnumSortedBiMap
import com.kelvsyc.kotlin.core.collections.SortedEnumBiMap

@PublishedApi
internal class ImmutableEnumSortedBiMap<K : Enum<K>, V>(private val backing: EnumSortedBiMap<K, V>) : EnumSortedBiMap<K, V> by backing {
    override val inverse: SortedEnumBiMap<V, K> by lazy { ImmutableSortedEnumBiMap(backing.inverse) }
    override fun equals(other: Any?): Boolean = backing.equals(other)
    override fun hashCode(): Int = backing.hashCode()
    override fun toString(): String = backing.toString()
}
