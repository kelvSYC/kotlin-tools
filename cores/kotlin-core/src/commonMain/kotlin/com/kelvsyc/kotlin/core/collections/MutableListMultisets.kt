package com.kelvsyc.kotlin.core.collections

import com.kelvsyc.internal.kotlin.core.collections.LinkedHashListMultiset

/**
 * Returns a new empty [MutableListMultiset].
 */
fun <E> mutableListMultisetOf(): MutableListMultiset<E> = LinkedHashListMultiset()

/**
 * Returns a new [MutableListMultiset] with the specified contents, preserving overall insertion order.
 */
fun <E> mutableListMultisetOf(vararg elements: E): MutableListMultiset<E> =
    LinkedHashListMultiset<E>().also { it.addAll(elements.asList()) }

/**
 * Returns a new [MutableListMultiset] containing all elements from the given [Iterable], preserving overall iteration
 * order.
 */
fun <E> Iterable<E>.toMutableListMultiset(): MutableListMultiset<E> =
    LinkedHashListMultiset<E>().also { it.addAll(this) }

/**
 * Returns a new [MutableListMultiset] containing all elements from the given [Sequence], preserving overall iteration
 * order.
 */
fun <E> Sequence<E>.toMutableListMultiset(): MutableListMultiset<E> =
    LinkedHashListMultiset<E>().also { it.addAll(this.asIterable()) }
