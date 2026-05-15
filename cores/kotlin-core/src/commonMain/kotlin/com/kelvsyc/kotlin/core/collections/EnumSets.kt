package com.kelvsyc.kotlin.core.collections

import com.kelvsyc.internal.kotlin.core.collections.ArrayEnumSet
import kotlin.enums.enumEntries

/**
 * Returns an empty read-only [EnumSet] for enum type [K].
 */
inline fun <reified K : Enum<K>> emptyEnumSet(): EnumSet<K> = ArrayEnumSet(enumEntries<K>())

/**
 * Returns a new read-only [EnumSet] with the specified [elements].
 */
inline fun <reified K : Enum<K>> enumSetOf(vararg elements: K): EnumSet<K> =
    ArrayEnumSet<K>(enumEntries<K>()).apply { addAll(elements) }

/**
 * Builds a read-only [EnumSet] by populating a [MutableEnumSet] using the given [builderAction].
 */
inline fun <reified K : Enum<K>> buildEnumSet(builderAction: MutableEnumSet<K>.() -> Unit): EnumSet<K> =
    ArrayEnumSet<K>(enumEntries<K>()).apply(builderAction)

/**
 * Returns a new read-only [EnumSet] containing all elements from this [Iterable].
 */
inline fun <reified K : Enum<K>> Iterable<K>.toEnumSet(): EnumSet<K> =
    ArrayEnumSet<K>(enumEntries<K>()).apply { addAll(this@toEnumSet) }
