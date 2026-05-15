package com.kelvsyc.kotlin.core.collections

/**
 * A mutable [EnumSet] that supports adding and removing elements.
 */
interface MutableEnumSet<K : Enum<K>> : EnumSet<K>, MutableSet<K>
