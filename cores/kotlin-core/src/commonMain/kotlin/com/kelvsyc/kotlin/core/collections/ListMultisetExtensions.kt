package com.kelvsyc.kotlin.core.collections

/**
 * Returns a new [ListMultiset] containing only the elements matching the given [predicate], preserving overall
 * insertion order.
 */
fun <E> ListMultiset<E>.filter(predicate: (E) -> Boolean): ListMultiset<E> =
    toList().filter(predicate).toListMultiset()

/**
 * Returns a new [ListMultiset] containing all elements of the original multiset plus [element] appended at the end.
 */
operator fun <E> ListMultiset<E>.plus(element: E): ListMultiset<E> =
    (toList() + element).toListMultiset()

/**
 * Returns a new [ListMultiset] containing all elements of the original multiset plus all elements of [other],
 * appended in [other]'s insertion order.
 */
operator fun <E> ListMultiset<E>.plus(other: ListMultiset<E>): ListMultiset<E> =
    (toList() + other.toList()).toListMultiset()

/**
 * Returns a new [ListMultiset] containing all elements of the original multiset plus all elements of [elements],
 * appended in iteration order.
 */
operator fun <E> ListMultiset<E>.plus(elements: Iterable<E>): ListMultiset<E> =
    (toList() + elements).toListMultiset()

/**
 * Returns a new [ListMultiset] with the first occurrence of [element] removed (FIFO), preserving insertion order.
 * If [element] is absent, returns an equivalent multiset.
 */
operator fun <E> ListMultiset<E>.minus(element: E): ListMultiset<E> {
    val result = toMutableList()
    result.remove(element)
    return result.toListMultiset()
}

/**
 * Returns a new [ListMultiset] with one occurrence of each element in [elements] removed (FIFO), preserving
 * insertion order. If an element in [elements] appears multiple times, that many occurrences are removed.
 * Absent elements are ignored.
 */
operator fun <E> ListMultiset<E>.minus(elements: Iterable<E>): ListMultiset<E> {
    val result = toMutableList()
    elements.forEach { result.remove(it) }
    return result.toListMultiset()
}

/**
 * Returns this multiset if not `null`, or an empty [ListMultiset] otherwise.
 */
fun <E> ListMultiset<E>?.orEmpty(): ListMultiset<E> = this ?: emptyListMultiset()
