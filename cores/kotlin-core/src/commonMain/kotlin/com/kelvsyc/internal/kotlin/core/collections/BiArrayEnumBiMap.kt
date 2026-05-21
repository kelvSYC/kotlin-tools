package com.kelvsyc.internal.kotlin.core.collections

import com.kelvsyc.kotlin.core.collections.MutableBiEnumBiMap
import com.kelvsyc.kotlin.core.collections.MutableEnumBiMap
import kotlin.enums.EnumEntries

@PublishedApi
internal class BiArrayEnumBiMap<K : Enum<K>, V : Enum<V>> private constructor(
    override val enumEntries: EnumEntries<K>,
    override val valueEnumEntries: EnumEntries<V>,
    private val fwdStore: ArrayMapStore<K, V>,
    private val bwdStore: ArrayMapStore<V, K>,
) : FlexBiMap<K, V>(fwdStore, bwdStore), MutableBiEnumBiMap<K, V> {

    override val inverse: MutableEnumBiMap<V, K> by lazy {
        ArrayEnumBiMap(valueEnumEntries, bwdStore, fwdStore)
    }

    companion object {
        @PublishedApi
        internal operator fun <K : Enum<K>, V : Enum<V>> invoke(
            enumEntries: EnumEntries<K>,
            valueEnumEntries: EnumEntries<V>,
        ): BiArrayEnumBiMap<K, V> {
            val fwd = ArrayMapStore<K, V>(enumEntries)
            val bwd = ArrayMapStore<V, K>(valueEnumEntries)
            return BiArrayEnumBiMap(enumEntries, valueEnumEntries, fwd, bwd)
        }
    }
}
