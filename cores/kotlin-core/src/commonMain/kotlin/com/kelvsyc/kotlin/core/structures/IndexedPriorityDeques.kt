package com.kelvsyc.kotlin.core.structures

import com.kelvsyc.internal.kotlin.core.structures.ArrayEnumIndexedPriorityDeque
import com.kelvsyc.internal.kotlin.core.structures.ArrayIndexedPriorityDeque
import com.kelvsyc.internal.kotlin.core.structures.HashIndexedPriorityDeque
import kotlin.enums.enumEntries

/**
 * Returns an empty [IndexedPriorityDeque] with dynamic (unknown) universe, ordered by [comparator].
 */
fun <T, P> indexedPriorityDequeOf(comparator: Comparator<in P>): IndexedPriorityDeque<T, P> =
    HashIndexedPriorityDeque(comparator)

/**
 * Returns an empty [IndexedPriorityDeque] with a pre-registered [universe], ordered by [comparator].
 * [IndexedPriorityDeque.add] for an element outside [universe] throws [IllegalArgumentException].
 */
fun <T, P> indexedPriorityDequeOf(
    comparator: Comparator<in P>,
    universe: Iterable<T>,
): IndexedPriorityDeque<T, P> = ArrayIndexedPriorityDeque(comparator, universe)

/**
 * Returns an empty [IndexedPriorityDeque] backed by ordinal-indexed arrays for enum type [E],
 * ordered by [comparator].
 */
inline fun <reified E : Enum<E>, P> enumIndexedPriorityDequeOf(
    comparator: Comparator<in P>,
): IndexedPriorityDeque<E, P> = ArrayEnumIndexedPriorityDeque(comparator, enumEntries<E>())

/**
 * Returns an empty [IndexedPriorityDeque] with dynamic universe, ordered by the natural ordering of [P].
 */
fun <T, P : Comparable<P>> minMaxIndexedPriorityDequeOf(): IndexedPriorityDeque<T, P> =
    HashIndexedPriorityDeque(naturalOrder())

/**
 * Returns an empty [IndexedPriorityDeque] with a pre-registered [universe], ordered by the natural
 * ordering of [P].
 */
fun <T, P : Comparable<P>> minMaxIndexedPriorityDequeOf(
    universe: Iterable<T>,
): IndexedPriorityDeque<T, P> = ArrayIndexedPriorityDeque(naturalOrder(), universe)

/**
 * Returns an empty [IndexedPriorityDeque] backed by ordinal-indexed arrays for enum type [E],
 * ordered by the natural ordering of [P].
 */
inline fun <reified E : Enum<E>, P : Comparable<P>> minMaxEnumIndexedPriorityDequeOf(): IndexedPriorityDeque<E, P> =
    ArrayEnumIndexedPriorityDeque(naturalOrder(), enumEntries<E>())
