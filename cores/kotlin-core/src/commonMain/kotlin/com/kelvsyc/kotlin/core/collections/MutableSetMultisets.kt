package com.kelvsyc.kotlin.core.collections

import com.kelvsyc.internal.kotlin.core.collections.LinkedHashSetMultiset

/**
 * Returns a new empty [MutableSetMultiset].
 */
fun <E> mutableSetMultisetOf(): MutableSetMultiset<E> = LinkedHashSetMultiset()

/**
 * Returns a new [MutableSetMultiset] with the specified contents.
 */
fun <E> mutableSetMultisetOf(vararg elements: E): MutableSetMultiset<E> =
    LinkedHashSetMultiset<E>().also { it.addAll(elements.asList()) }

/**
 * Returns a new [MutableSetMultiset] containing all elements from the given [Iterable].
 */
fun <E> Iterable<E>.toMutableSetMultiset(): MutableSetMultiset<E> =
    LinkedHashSetMultiset<E>().also { it.addAll(this) }

/**
 * Returns a new [MutableSetMultiset] containing all elements from the given [Sequence].
 */
fun <E> Sequence<E>.toMutableSetMultiset(): MutableSetMultiset<E> =
    LinkedHashSetMultiset<E>().also { it.addAll(this.asIterable()) }
