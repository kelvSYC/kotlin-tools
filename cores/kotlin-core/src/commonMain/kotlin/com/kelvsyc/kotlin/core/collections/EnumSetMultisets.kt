package com.kelvsyc.kotlin.core.collections

import com.kelvsyc.internal.kotlin.core.collections.ArrayEnumSetMultiset
import kotlin.enums.enumEntries

/**
 * Returns an empty read-only [EnumSetMultiset] for enum type [K].
 */
inline fun <reified K : Enum<K>> emptyEnumSetMultiset(): EnumSetMultiset<K> =
    ArrayEnumSetMultiset(enumEntries<K>())

/**
 * Returns a new read-only [EnumSetMultiset] with the specified contents.
 */
inline fun <reified K : Enum<K>> enumSetMultisetOf(vararg elements: K): EnumSetMultiset<K> =
    ArrayEnumSetMultiset<K>(enumEntries<K>()).apply { addAll(elements.asList()) }

/**
 * Builds a read-only [EnumSetMultiset] by populating a [MutableEnumSetMultiset] using the given [builderAction] and
 * returning a read-only view of its contents.
 */
inline fun <reified K : Enum<K>> buildEnumSetMultiset(
    builderAction: MutableEnumSetMultiset<K>.() -> Unit,
): EnumSetMultiset<K> = ArrayEnumSetMultiset<K>(enumEntries<K>()).apply(builderAction)

/**
 * Returns a new read-only [EnumSetMultiset] containing all elements from this [Iterable].
 */
inline fun <reified K : Enum<K>> Iterable<K>.toEnumSetMultiset(): EnumSetMultiset<K> =
    ArrayEnumSetMultiset<K>(enumEntries<K>()).apply { addAll(this@toEnumSetMultiset) }

/**
 * Returns a new read-only [EnumSetMultiset] containing all elements from this [Sequence].
 */
inline fun <reified K : Enum<K>> Sequence<K>.toEnumSetMultiset(): EnumSetMultiset<K> =
    asIterable().toEnumSetMultiset()

/**
 * Returns a new read-only [EnumSetMultiset] containing all elements from this [SetMultiset].
 */
inline fun <reified K : Enum<K>> SetMultiset<K>.toEnumSetMultiset(): EnumSetMultiset<K> =
    ArrayEnumSetMultiset<K>(enumEntries<K>()).apply {
        this@toEnumSetMultiset.asMap.forEach { (element, count) -> add(element, count) }
    }
