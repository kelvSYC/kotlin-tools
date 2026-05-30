package com.kelvsyc.internal.kotlin.core.collections

import com.kelvsyc.kotlin.core.collections.EnumMap

@PublishedApi
internal class ImmutableEnumMap<K : Enum<K>, V>(private val backing: EnumMap<K, V>) : EnumMap<K, V> by backing {
    override fun equals(other: Any?): Boolean = backing.equals(other)
    override fun hashCode(): Int = backing.hashCode()
    override fun toString(): String = backing.toString()
}
