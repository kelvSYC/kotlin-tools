package com.kelvsyc.internal.kotlin.core.collections

import com.kelvsyc.kotlin.core.collections.BiMap
import com.kelvsyc.kotlin.core.collections.EnumBiMap

@PublishedApi
internal class ImmutableEnumBiMap<K : Enum<K>, V>(private val backing: EnumBiMap<K, V>) : EnumBiMap<K, V> by backing {
    override val inverse: BiMap<V, K> by lazy { ImmutableBiMap(backing.inverse) }
    override fun equals(other: Any?): Boolean = backing.equals(other)
    override fun hashCode(): Int = backing.hashCode()
    override fun toString(): String = backing.toString()
}
