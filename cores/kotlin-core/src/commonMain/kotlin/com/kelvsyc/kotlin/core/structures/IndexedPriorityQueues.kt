package com.kelvsyc.kotlin.core.structures

import com.kelvsyc.internal.kotlin.core.structures.ArrayEnumIndexedPriorityQueue
import com.kelvsyc.internal.kotlin.core.structures.ArrayIndexedPriorityQueue
import com.kelvsyc.internal.kotlin.core.structures.HashIndexedPriorityQueue
import kotlin.enums.enumEntries

/**
 * Returns an empty [IndexedPriorityQueue] with dynamic (unknown) universe. Elements are
 * registered lazily on [IndexedPriorityQueue.add]. Priorities are ordered by [comparator].
 */
fun <T, P> indexedPriorityQueueOf(comparator: Comparator<in P>): IndexedPriorityQueue<T, P> =
    HashIndexedPriorityQueue(comparator)

/**
 * Returns an empty [IndexedPriorityQueue] with a pre-registered [universe]. Slots are
 * pre-allocated for all elements in [universe]; [IndexedPriorityQueue.add] for an element
 * outside [universe] throws [IllegalArgumentException]. Duplicate elements in [universe] are
 * silently deduplicated. Priorities are ordered by [comparator].
 */
fun <T, P> indexedPriorityQueueOf(
    comparator: Comparator<in P>,
    universe: Iterable<T>,
): IndexedPriorityQueue<T, P> = ArrayIndexedPriorityQueue(comparator, universe)

/**
 * Returns an empty [IndexedPriorityQueue] backed by ordinal-indexed arrays for enum type [E].
 * All enum constants are pre-registered. No [T]-to-[Int] map is needed; the ordinal is the
 * slot index. Priorities are ordered by [comparator].
 */
inline fun <reified E : Enum<E>, P> enumIndexedPriorityQueueOf(
    comparator: Comparator<in P>,
): IndexedPriorityQueue<E, P> = ArrayEnumIndexedPriorityQueue(comparator, enumEntries<E>())

/**
 * Returns an empty [IndexedPriorityQueue] with dynamic universe, where priorities are ordered
 * by the natural ordering of [P].
 */
fun <T, P : Comparable<P>> minIndexedPriorityQueueOf(): IndexedPriorityQueue<T, P> =
    HashIndexedPriorityQueue(naturalOrder())

/**
 * Returns an empty [IndexedPriorityQueue] with a pre-registered [universe], where priorities
 * are ordered by the natural ordering of [P].
 */
fun <T, P : Comparable<P>> minIndexedPriorityQueueOf(
    universe: Iterable<T>,
): IndexedPriorityQueue<T, P> = ArrayIndexedPriorityQueue(naturalOrder(), universe)

/**
 * Returns an empty [IndexedPriorityQueue] backed by ordinal-indexed arrays for enum type [E],
 * where priorities are ordered by the natural ordering of [P].
 */
inline fun <reified E : Enum<E>, P : Comparable<P>> minEnumIndexedPriorityQueueOf(): IndexedPriorityQueue<E, P> =
    ArrayEnumIndexedPriorityQueue(naturalOrder(), enumEntries<E>())
