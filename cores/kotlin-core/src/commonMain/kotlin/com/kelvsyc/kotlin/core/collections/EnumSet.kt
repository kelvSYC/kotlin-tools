package com.kelvsyc.kotlin.core.collections

import kotlin.enums.EnumEntries

/**
 * A [Set] of enum constants backed by an array indexed by ordinal. Iteration follows natural enum order.
 *
 * Unlike `java.util.EnumSet`, this is a Kotlin Multiplatform type with no JVM interoperability requirement.
 */
interface EnumSet<K : Enum<K>> : Set<K> {
    val enumEntries: EnumEntries<K>
}
