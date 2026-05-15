package com.kelvsyc.kotlin.core.collections

import com.kelvsyc.internal.kotlin.core.collections.ArrayEnumSet
import kotlin.enums.enumEntries

/**
 * Returns a new empty [MutableEnumSet] for enum type [K].
 */
inline fun <reified K : Enum<K>> mutableEnumSetOf(): MutableEnumSet<K> = ArrayEnumSet(enumEntries<K>())

/**
 * Returns a new [MutableEnumSet] with the specified [elements].
 */
inline fun <reified K : Enum<K>> mutableEnumSetOf(vararg elements: K): MutableEnumSet<K> =
    ArrayEnumSet<K>(enumEntries<K>()).apply { addAll(elements) }

/**
 * Returns a new [MutableEnumSet] containing all elements from this [Iterable].
 */
inline fun <reified K : Enum<K>> Iterable<K>.toMutableEnumSet(): MutableEnumSet<K> =
    ArrayEnumSet<K>(enumEntries<K>()).apply { addAll(this@toMutableEnumSet) }
