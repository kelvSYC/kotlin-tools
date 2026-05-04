package com.kelvsyc.kotlin.core.collections

/**
 * A mutable [EnumListMultimap] that supports adding and removing key-value pairs.
 */
interface MutableEnumListMultimap<K : Enum<K>, V> : EnumListMultimap<K, V>, MutableListMultimap<K, V>
