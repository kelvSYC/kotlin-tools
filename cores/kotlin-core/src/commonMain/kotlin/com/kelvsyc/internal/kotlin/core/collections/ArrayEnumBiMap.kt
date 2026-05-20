package com.kelvsyc.internal.kotlin.core.collections

import com.kelvsyc.kotlin.core.collections.MutableBiMap
import com.kelvsyc.kotlin.core.collections.MutableEnumBiMap
import kotlin.enums.EnumEntries

@PublishedApi
internal class ArrayEnumBiMap<K : Enum<K>, V> private constructor(
    override val enumEntries: EnumEntries<K>,
    private val inner: FlexBiMap<K, V>,
) : MutableEnumBiMap<K, V>, MutableBiMap<K, V> by inner {

    constructor(enumEntries: EnumEntries<K>) : this(
        enumEntries,
        FlexBiMap(ArrayMapStore(enumEntries), HashMapStore()),
    )

    override fun equals(other: Any?): Boolean = inner.equals(other)
    override fun hashCode(): Int = inner.hashCode()
    override fun toString(): String = inner.toString()
}
