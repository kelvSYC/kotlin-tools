package com.kelvsyc.internal.kotlin.core.collections

import com.kelvsyc.kotlin.core.collections.EnumSetMultimap

@PublishedApi
internal class ImmutableEnumSetMultimap<K : Enum<K>, V>(private val backing: EnumSetMultimap<K, V>) : EnumSetMultimap<K, V> by backing {
    override fun equals(other: Any?): Boolean = backing.equals(other)
    override fun hashCode(): Int = backing.hashCode()
    override fun toString(): String = backing.toString()
}
