package com.kelvsyc.internal.kotlin.core.collections

import com.kelvsyc.kotlin.core.collections.MutableEnumBiMap
import kotlin.enums.EnumEntries

@PublishedApi
internal class ArrayEnumBiMap<K : Enum<K>, V> internal constructor(
    override val enumEntries: EnumEntries<K>,
    fwd: ArrayMapStore<K, V>,
    bwd: MapStore<V, K>,
) : FlexBiMap<K, V>(fwd, bwd), MutableEnumBiMap<K, V> {

    constructor(enumEntries: EnumEntries<K>) : this(
        enumEntries,
        ArrayMapStore(enumEntries),
        HashMapStore(),
    )
}
