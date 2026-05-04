package com.kelvsyc.kotlin.core.collections

import kotlin.enums.EnumEntries

/**
 * A [SetMultimap] keyed by enum constants, backed by an array indexed by ordinal. Iteration follows natural enum
 * declaration order.
 *
 * Unlike `java.util.EnumMap`, this is a Kotlin Multiplatform type with no JVM interoperability requirement.
 */
interface EnumSetMultimap<K : Enum<K>, out V> : SetMultimap<K, V> {
    /**
     * The universe of enum constants for the key type.
     */
    val enumEntries: EnumEntries<K>
}
