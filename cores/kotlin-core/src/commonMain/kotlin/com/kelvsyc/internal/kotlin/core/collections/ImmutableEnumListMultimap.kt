package com.kelvsyc.internal.kotlin.core.collections

import com.kelvsyc.kotlin.core.collections.EnumListMultimap

@PublishedApi
internal class ImmutableEnumListMultimap<K : Enum<K>, V>(private val backing: EnumListMultimap<K, V>) : EnumListMultimap<K, V> by backing {
    override fun equals(other: Any?): Boolean = backing.equals(other)
    override fun hashCode(): Int = backing.hashCode()
    override fun toString(): String = backing.toString()
}
