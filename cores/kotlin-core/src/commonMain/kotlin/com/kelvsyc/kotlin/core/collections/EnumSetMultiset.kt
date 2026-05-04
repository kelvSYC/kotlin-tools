package com.kelvsyc.kotlin.core.collections

import kotlin.enums.EnumEntries

/**
 * A [SetMultiset] whose elements are enum constants, backed by an [IntArray] indexed by ordinal. Iteration follows
 * natural enum declaration order.
 */
interface EnumSetMultiset<K : Enum<K>> : SetMultiset<K> {
    /**
     * The universe of enum constants for the element type.
     */
    val enumEntries: EnumEntries<K>
}
