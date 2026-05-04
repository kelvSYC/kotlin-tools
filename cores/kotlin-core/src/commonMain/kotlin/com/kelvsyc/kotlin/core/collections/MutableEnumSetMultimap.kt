package com.kelvsyc.kotlin.core.collections

/**
 * A mutable [EnumSetMultimap] that supports adding and removing key-value pairs.
 */
interface MutableEnumSetMultimap<K : Enum<K>, V> : EnumSetMultimap<K, V>, MutableSetMultimap<K, V>
