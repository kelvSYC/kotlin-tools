package com.kelvsyc.kotlin.core.collections

import com.kelvsyc.internal.kotlin.core.collections.ArrayEnumSetMultiset
import kotlin.enums.enumEntries

/**
 * Returns a new empty [MutableEnumSetMultiset] for enum type [K].
 */
inline fun <reified K : Enum<K>> mutableEnumSetMultisetOf(): MutableEnumSetMultiset<K> =
    ArrayEnumSetMultiset(enumEntries<K>())

/**
 * Returns a new [MutableEnumSetMultiset] with the specified contents.
 */
inline fun <reified K : Enum<K>> mutableEnumSetMultisetOf(vararg elements: K): MutableEnumSetMultiset<K> =
    ArrayEnumSetMultiset<K>(enumEntries<K>()).apply { addAll(elements.asList()) }

/**
 * Returns a new [MutableEnumSetMultiset] containing all elements from this [Iterable].
 */
inline fun <reified K : Enum<K>> Iterable<K>.toMutableEnumSetMultiset(): MutableEnumSetMultiset<K> =
    ArrayEnumSetMultiset<K>(enumEntries<K>()).apply { addAll(this@toMutableEnumSetMultiset) }

/**
 * Returns a new [MutableEnumSetMultiset] containing all elements from this [Sequence].
 */
inline fun <reified K : Enum<K>> Sequence<K>.toMutableEnumSetMultiset(): MutableEnumSetMultiset<K> =
    asIterable().toMutableEnumSetMultiset()

/**
 * Returns a new [MutableEnumSetMultiset] containing all elements from this [SetMultiset].
 */
inline fun <reified K : Enum<K>> SetMultiset<K>.toMutableEnumSetMultiset(): MutableEnumSetMultiset<K> =
    ArrayEnumSetMultiset<K>(enumEntries<K>()).apply {
        this@toMutableEnumSetMultiset.asMap.forEach { (element, count) -> add(element, count) }
    }
