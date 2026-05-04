package com.kelvsyc.kotlin.core.collections

import kotlin.enums.EnumEntries

/**
 * A [Map] keyed by enum constants, backed by an array indexed by ordinal. Iteration follows natural enum order.
 *
 * Unlike `java.util.EnumMap`, this is a Kotlin Multiplatform type with no JVM interoperability requirement.
 */
interface EnumMap<K : Enum<K>, out V> : Map<K, V> {
    /**
     * The universe of enum constants for the key type.
     */
    val enumEntries: EnumEntries<K>
}
