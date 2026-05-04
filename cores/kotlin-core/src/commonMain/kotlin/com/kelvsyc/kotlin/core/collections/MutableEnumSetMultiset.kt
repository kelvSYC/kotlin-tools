package com.kelvsyc.kotlin.core.collections

/**
 * A mutable [EnumSetMultiset] that supports adding and removing elements.
 */
interface MutableEnumSetMultiset<K : Enum<K>> : EnumSetMultiset<K>, MutableSetMultiset<K>
