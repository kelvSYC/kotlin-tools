package com.kelvsyc.kotlin.core.collections

import com.kelvsyc.internal.kotlin.core.collections.ImmutableSetMultiset

/**
 * Returns a new [SetMultiset] containing only the elements matching the given [predicate].
 */
fun <E> SetMultiset<E>.filter(predicate: (E) -> Boolean): SetMultiset<E> =
    asMap.entries
        .filter { (e, _) -> predicate(e) }
        .associate { (e, count) -> e to count }
        .toSetMultisetFromCounts()

/**
 * Returns a new [SetMultiset] containing all elements of the original multiset plus [element] appended at the end.
 */
operator fun <E> SetMultiset<E>.plus(element: E): SetMultiset<E> =
    (asMap + mapOf(element to (count(element) + 1))).toSetMultisetFromCounts()

/**
 * Returns a new [SetMultiset] containing all elements of the original multiset plus all elements of [other].
 */
operator fun <E> SetMultiset<E>.plus(other: SetMultiset<E>): SetMultiset<E> {
    val result = asMap.toMutableMap()
    other.asMap.forEach { (e, count) -> result[e] = (result[e] ?: 0) + count }
    return result.toSetMultisetFromCounts()
}

/**
 * Returns a new [SetMultiset] containing all elements of the original multiset plus all elements of [elements].
 */
operator fun <E> SetMultiset<E>.plus(elements: Iterable<E>): SetMultiset<E> {
    val result = asMap.toMutableMap()
    elements.forEach { e -> result[e] = (result[e] ?: 0) + 1 }
    return result.toSetMultisetFromCounts()
}

/**
 * Returns a new [SetMultiset] with the count of [element] decremented by one. If the element has only one occurrence,
 * it is removed entirely. If [element] is absent, returns an equivalent multiset.
 */
operator fun <E> SetMultiset<E>.minus(element: E): SetMultiset<E> {
    val current = count(element)
    if (current == 0) return asMap.toSetMultisetFromCounts()
    val result = asMap.toMutableMap()
    if (current == 1) result.remove(element) else result[element] = current - 1
    return result.toSetMultisetFromCounts()
}

/**
 * Returns a new [SetMultiset] with the count of each element in [elements] decremented by one. Elements whose count
 * reaches zero are removed. Absent elements are ignored.
 */
operator fun <E> SetMultiset<E>.minus(elements: Iterable<E>): SetMultiset<E> {
    val result = asMap.toMutableMap()
    elements.forEach { e ->
        val current = result[e] ?: return@forEach
        if (current <= 1) result.remove(e) else result[e] = current - 1
    }
    return result.toSetMultisetFromCounts()
}

/**
 * Returns this multiset if not `null`, or an empty [SetMultiset] otherwise.
 */
fun <E> SetMultiset<E>?.orEmpty(): SetMultiset<E> = this ?: emptySetMultiset()

private fun <E> Map<E, Int>.toSetMultisetFromCounts(): SetMultiset<E> = ImmutableSetMultiset(this)
