package com.kelvsyc.kotlin.core.collections

import com.kelvsyc.internal.kotlin.core.collections.EnumSortedBiMapImpl
import com.kelvsyc.internal.kotlin.core.collections.SortedEnumBiMapImpl
import kotlin.enums.enumEntries

// ── EnumSortedBiMap (enum keys, sorted values) ────────────────────────────────

/**
 * Returns a new read-only [EnumSortedBiMap] with the specified contents.
 *
 * @throws IllegalArgumentException if any value appears more than once.
 */
inline fun <reified K : Enum<K>, V> enumSortedBiMapOf(
    valueComparator: Comparator<in V>,
    vararg pairs: Pair<K, V>,
): EnumSortedBiMap<K, V> =
    EnumSortedBiMapImpl(enumEntries<K>(), valueComparator).apply { pairs.forEach { (k, v) -> put(k, v) } }

/**
 * Builds a read-only [EnumSortedBiMap] by populating a [MutableEnumSortedBiMap] using [builderAction].
 */
inline fun <reified K : Enum<K>, V> buildEnumSortedBiMap(
    valueComparator: Comparator<in V>,
    builderAction: MutableEnumSortedBiMap<K, V>.() -> Unit,
): EnumSortedBiMap<K, V> = EnumSortedBiMapImpl(enumEntries<K>(), valueComparator).apply(builderAction)

/**
 * Returns a new read-only [EnumSortedBiMap] containing all entries from this [Map].
 *
 * @throws IllegalArgumentException if any value appears more than once.
 */
inline fun <reified K : Enum<K>, V> Map<K, V>.toEnumSortedBiMap(
    valueComparator: Comparator<in V>,
): EnumSortedBiMap<K, V> =
    EnumSortedBiMapImpl(enumEntries<K>(), valueComparator).apply { putAll(this@toEnumSortedBiMap) }

/**
 * Returns a new empty [MutableEnumSortedBiMap].
 */
inline fun <reified K : Enum<K>, V> mutableEnumSortedBiMapOf(
    valueComparator: Comparator<in V>,
): MutableEnumSortedBiMap<K, V> = EnumSortedBiMapImpl(enumEntries<K>(), valueComparator)

/**
 * Returns a new [MutableEnumSortedBiMap] with the specified contents.
 *
 * @throws IllegalArgumentException if any value appears more than once.
 */
inline fun <reified K : Enum<K>, V> mutableEnumSortedBiMapOf(
    valueComparator: Comparator<in V>,
    vararg pairs: Pair<K, V>,
): MutableEnumSortedBiMap<K, V> =
    EnumSortedBiMapImpl(enumEntries<K>(), valueComparator).apply { pairs.forEach { (k, v) -> put(k, v) } }

/**
 * Builds a [MutableEnumSortedBiMap] by applying [builderAction] to a new empty instance.
 */
inline fun <reified K : Enum<K>, V> buildMutableEnumSortedBiMap(
    valueComparator: Comparator<in V>,
    builderAction: MutableEnumSortedBiMap<K, V>.() -> Unit,
): MutableEnumSortedBiMap<K, V> = EnumSortedBiMapImpl(enumEntries<K>(), valueComparator).apply(builderAction)

/**
 * Returns a new [MutableEnumSortedBiMap] containing all entries from this [Map].
 *
 * @throws IllegalArgumentException if any value appears more than once.
 */
inline fun <reified K : Enum<K>, V> Map<K, V>.toMutableEnumSortedBiMap(
    valueComparator: Comparator<in V>,
): MutableEnumSortedBiMap<K, V> =
    EnumSortedBiMapImpl(enumEntries<K>(), valueComparator).apply { putAll(this@toMutableEnumSortedBiMap) }

// ── SortedEnumBiMap (sorted keys, enum values) ────────────────────────────────

/**
 * Returns a new read-only [SortedEnumBiMap] with the specified contents.
 *
 * @throws IllegalArgumentException if any value appears more than once.
 */
inline fun <K, reified V : Enum<V>> sortedEnumBiMapOf(
    comparator: Comparator<in K>,
    vararg pairs: Pair<K, V>,
): SortedEnumBiMap<K, V> =
    SortedEnumBiMapImpl(comparator, enumEntries<V>()).apply { pairs.forEach { (k, v) -> put(k, v) } }

/**
 * Builds a read-only [SortedEnumBiMap] by populating a [MutableSortedEnumBiMap] using [builderAction].
 */
inline fun <K, reified V : Enum<V>> buildSortedEnumBiMap(
    comparator: Comparator<in K>,
    builderAction: MutableSortedEnumBiMap<K, V>.() -> Unit,
): SortedEnumBiMap<K, V> = SortedEnumBiMapImpl(comparator, enumEntries<V>()).apply(builderAction)

/**
 * Returns a new read-only [SortedEnumBiMap] containing all entries from this [Map].
 *
 * @throws IllegalArgumentException if any value appears more than once.
 */
inline fun <K, reified V : Enum<V>> Map<K, V>.toSortedEnumBiMap(
    comparator: Comparator<in K>,
): SortedEnumBiMap<K, V> =
    SortedEnumBiMapImpl(comparator, enumEntries<V>()).apply { putAll(this@toSortedEnumBiMap) }

/**
 * Returns a new empty [MutableSortedEnumBiMap].
 */
inline fun <K, reified V : Enum<V>> mutableSortedEnumBiMapOf(
    comparator: Comparator<in K>,
): MutableSortedEnumBiMap<K, V> = SortedEnumBiMapImpl(comparator, enumEntries<V>())

/**
 * Returns a new [MutableSortedEnumBiMap] with the specified contents.
 *
 * @throws IllegalArgumentException if any value appears more than once.
 */
inline fun <K, reified V : Enum<V>> mutableSortedEnumBiMapOf(
    comparator: Comparator<in K>,
    vararg pairs: Pair<K, V>,
): MutableSortedEnumBiMap<K, V> =
    SortedEnumBiMapImpl(comparator, enumEntries<V>()).apply { pairs.forEach { (k, v) -> put(k, v) } }

/**
 * Builds a [MutableSortedEnumBiMap] by applying [builderAction] to a new empty instance.
 */
inline fun <K, reified V : Enum<V>> buildMutableSortedEnumBiMap(
    comparator: Comparator<in K>,
    builderAction: MutableSortedEnumBiMap<K, V>.() -> Unit,
): MutableSortedEnumBiMap<K, V> = SortedEnumBiMapImpl(comparator, enumEntries<V>()).apply(builderAction)

/**
 * Returns a new [MutableSortedEnumBiMap] containing all entries from this [Map].
 *
 * @throws IllegalArgumentException if any value appears more than once.
 */
inline fun <K, reified V : Enum<V>> Map<K, V>.toMutableSortedEnumBiMap(
    comparator: Comparator<in K>,
): MutableSortedEnumBiMap<K, V> =
    SortedEnumBiMapImpl(comparator, enumEntries<V>()).apply { putAll(this@toMutableSortedEnumBiMap) }
