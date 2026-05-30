package com.kelvsyc.internal.kotlin.core.collections

import com.kelvsyc.kotlin.core.collections.EnumSetMultiset

@PublishedApi
internal class ImmutableEnumSetMultiset<K : Enum<K>>(private val backing: EnumSetMultiset<K>) : EnumSetMultiset<K> by backing {
    override fun equals(other: Any?): Boolean = backing.equals(other)
    override fun hashCode(): Int = backing.hashCode()
    override fun toString(): String = backing.toString()
}
