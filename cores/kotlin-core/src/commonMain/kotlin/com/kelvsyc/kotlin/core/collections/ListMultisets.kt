package com.kelvsyc.kotlin.core.collections

import com.kelvsyc.internal.kotlin.core.collections.ImmutableListMultiset
import com.kelvsyc.internal.kotlin.core.collections.LinkedHashListMultiset

/**
 * Returns an empty read-only multiset of the specified type.
 */
fun <E> emptyListMultiset(): ListMultiset<E> = ImmutableListMultiset(emptyList())

/**
 * Returns a new read-only multiset with the specified contents, preserving overall insertion order.
 */
fun <E> listMultisetOf(vararg elements: E): ListMultiset<E> = ImmutableListMultiset(elements.toList())

/**
 * Returns a new multiset containing all elements from the given [Iterable], preserving overall iteration order.
 */
fun <E> Iterable<E>.toListMultiset(): ListMultiset<E> = ImmutableListMultiset(toList())

/**
 * Returns a new multiset containing all elements from the given [Sequence], preserving overall iteration order.
 */
fun <E> Sequence<E>.toListMultiset(): ListMultiset<E> = ImmutableListMultiset(toList())

/**
 * Builds a read-only [ListMultiset] by populating a [MutableListMultiset] using the given [builderAction] and
 * returning a read-only snapshot of its contents.
 */
fun <E> buildListMultiset(builderAction: MutableListMultiset<E>.() -> Unit): ListMultiset<E> =
    LinkedHashListMultiset<E>().apply(builderAction)

/**
 * Builds a read-only [ListMultiset] by populating a [MutableListMultiset] with the given initial element [capacity]
 * and using the given [builderAction], returning a read-only snapshot of its contents.
 */
fun <E> buildListMultiset(capacity: Int, builderAction: MutableListMultiset<E>.() -> Unit): ListMultiset<E> =
    LinkedHashListMultiset<E>(capacity).apply(builderAction)
