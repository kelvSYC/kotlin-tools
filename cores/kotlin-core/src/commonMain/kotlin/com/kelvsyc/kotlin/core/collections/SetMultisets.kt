package com.kelvsyc.kotlin.core.collections

import com.kelvsyc.internal.kotlin.core.collections.ImmutableSetMultiset
import com.kelvsyc.internal.kotlin.core.collections.LinkedHashSetMultiset

/**
 * Returns an empty read-only [SetMultiset] of the specified type.
 */
fun <E> emptySetMultiset(): SetMultiset<E> = ImmutableSetMultiset(emptyMap())

/**
 * Returns a new read-only [SetMultiset] with the specified contents.
 */
fun <E> setMultisetOf(vararg elements: E): SetMultiset<E> =
    ImmutableSetMultiset(elements.groupingBy { it }.eachCount())

/**
 * Returns a new [SetMultiset] containing all elements from the given [Iterable].
 */
fun <E> Iterable<E>.toSetMultiset(): SetMultiset<E> =
    ImmutableSetMultiset(groupingBy { it }.eachCount())

/**
 * Returns a new [SetMultiset] containing all elements from the given [Sequence].
 */
fun <E> Sequence<E>.toSetMultiset(): SetMultiset<E> = asIterable().toSetMultiset()

/**
 * Builds a read-only [SetMultiset] by populating a [MutableSetMultiset] using the given [builderAction] and
 * returning a read-only snapshot of its contents.
 */
fun <E> buildSetMultiset(builderAction: MutableSetMultiset<E>.() -> Unit): SetMultiset<E> =
    LinkedHashSetMultiset<E>().apply(builderAction)

/**
 * Builds a read-only [SetMultiset] by populating a [MutableSetMultiset] with the given initial element [capacity]
 * and using the given [builderAction], returning a read-only snapshot of its contents.
 */
fun <E> buildSetMultiset(capacity: Int, builderAction: MutableSetMultiset<E>.() -> Unit): SetMultiset<E> =
    LinkedHashSetMultiset<E>(capacity).apply(builderAction)
