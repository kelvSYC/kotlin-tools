package com.kelvsyc.internal.kotlin.core.collections

import com.kelvsyc.kotlin.core.collections.EnumSet

@PublishedApi
internal class ImmutableEnumSet<K : Enum<K>>(private val backing: EnumSet<K>) : EnumSet<K> by backing {
    override fun equals(other: Any?): Boolean = backing.equals(other)
    override fun hashCode(): Int = backing.hashCode()
    override fun toString(): String = backing.toString()
}
