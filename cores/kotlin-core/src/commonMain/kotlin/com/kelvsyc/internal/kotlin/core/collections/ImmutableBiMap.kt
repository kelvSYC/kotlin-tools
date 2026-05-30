package com.kelvsyc.internal.kotlin.core.collections

import com.kelvsyc.kotlin.core.collections.BiMap

internal class ImmutableBiMap<K, V>(private val backing: BiMap<K, V>) : BiMap<K, V> by backing {
    override val inverse: BiMap<V, K> by lazy { ImmutableBiMap(backing.inverse) }
    override fun equals(other: Any?): Boolean = backing.equals(other)
    override fun hashCode(): Int = backing.hashCode()
    override fun toString(): String = backing.toString()
}
