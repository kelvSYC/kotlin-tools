package com.kelvsyc.kotlin.core.collections

/**
 * A mutable [EnumMap] that supports adding and removing entries.
 */
interface MutableEnumMap<K : Enum<K>, V> : EnumMap<K, V>, MutableMap<K, V>
