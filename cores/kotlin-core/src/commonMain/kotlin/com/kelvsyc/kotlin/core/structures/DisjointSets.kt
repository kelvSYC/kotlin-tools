package com.kelvsyc.kotlin.core.structures

import com.kelvsyc.internal.kotlin.core.structures.ArrayEnumDisjointSet
import com.kelvsyc.internal.kotlin.core.structures.HashDisjointSet
import kotlin.enums.enumEntries

/**
 * Returns an empty [MutableDisjointSet] with no pre-registered elements. Elements are registered
 * lazily when first passed to [MutableDisjointSet.union].
 */
fun <E> mutableDisjointSetOf(): MutableDisjointSet<E> = HashDisjointSet()

/**
 * Returns a [MutableDisjointSet] with [universe] pre-registered as singleton equivalence classes.
 *
 * The [universe] is a convenience initializer, not a membership constraint: subsequent calls to
 * [MutableDisjointSet.union] with elements outside [universe] will register those elements lazily,
 * exactly as in the no-universe variant.
 */
fun <E> mutableDisjointSetOf(universe: Iterable<E>): MutableDisjointSet<E> = HashDisjointSet(universe)

/**
 * Builds a read-only [DisjointSet] by populating a [MutableDisjointSet] using the given
 * [builderAction] and returning a read-only view of the resulting partition.
 */
fun <E> buildDisjointSet(builderAction: MutableDisjointSet<E>.() -> Unit): DisjointSet<E> =
    HashDisjointSet<E>().apply(builderAction)

/**
 * Returns a [MutableDisjointSet] backed by an ordinal-indexed array, with all constants of enum
 * type [E] pre-registered as singleton equivalence classes.
 */
inline fun <reified E : Enum<E>> mutableEnumDisjointSetOf(): MutableDisjointSet<E> =
    ArrayEnumDisjointSet(enumEntries<E>())

/**
 * Builds a read-only [DisjointSet] for enum type [E] using an ordinal-indexed array backing.
 * All constants of [E] are pre-registered before [builderAction] runs.
 */
inline fun <reified E : Enum<E>> buildEnumDisjointSet(
    builderAction: MutableDisjointSet<E>.() -> Unit,
): DisjointSet<E> = ArrayEnumDisjointSet<E>(enumEntries<E>()).apply(builderAction)
