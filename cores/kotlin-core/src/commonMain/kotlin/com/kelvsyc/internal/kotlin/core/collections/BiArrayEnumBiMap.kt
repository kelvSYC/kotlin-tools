package com.kelvsyc.internal.kotlin.core.collections

import com.kelvsyc.kotlin.core.collections.MutableBiEnumBiMap
import com.kelvsyc.kotlin.core.collections.MutableBiMap
import com.kelvsyc.kotlin.core.collections.MutableEnumBiMap
import kotlin.enums.EnumEntries

@PublishedApi
internal class BiArrayEnumBiMap<K : Enum<K>, V : Enum<V>> private constructor(
    override val enumEntries: EnumEntries<K>,
    override val valueEnumEntries: EnumEntries<V>,
    private val inner: FlexBiMap<K, V>,
    // Kept so the inverse can share the same store objects.
    private val bwd: ArrayMapStore<V, K>,
    private val fwd: ArrayMapStore<K, V>,
) : MutableBiEnumBiMap<K, V>, MutableBiMap<K, V> by inner {

    override val inverse: MutableEnumBiMap<V, K> by lazy {
        ArrayEnumBiMap(valueEnumEntries, FlexBiMap(bwd, fwd))
    }

    override fun equals(other: Any?): Boolean = inner.equals(other)
    override fun hashCode(): Int = inner.hashCode()
    override fun toString(): String = inner.toString()

    companion object {
        @PublishedApi
        internal operator fun <K : Enum<K>, V : Enum<V>> invoke(
            enumEntries: EnumEntries<K>,
            valueEnumEntries: EnumEntries<V>,
        ): BiArrayEnumBiMap<K, V> {
            val fwd = ArrayMapStore<K, V>(enumEntries)
            val bwd = ArrayMapStore<V, K>(valueEnumEntries)
            return BiArrayEnumBiMap(enumEntries, valueEnumEntries, FlexBiMap(fwd, bwd), bwd, fwd)
        }
    }
}
