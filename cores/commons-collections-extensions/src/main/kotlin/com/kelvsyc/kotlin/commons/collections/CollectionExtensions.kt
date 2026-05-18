package com.kelvsyc.kotlin.commons.collections

import org.apache.commons.collections4.CollectionUtils

/**
 * Multiset union: returns collection with max frequency of each element.
 *
 * For example: `[a, a, b].multisetUnion([a, b, b])` returns `[a, a, b, b]`.
 */
fun <E> Collection<E>.multisetUnion(other: Collection<E>): Collection<E> =
    CollectionUtils.union(this, other)

/**
 * Multiset intersection: returns collection with min frequency of each element.
 *
 * For example: `[a, a, b].multisetIntersection([a, b, b])` returns `[a, b]`.
 */
fun <E> Collection<E>.multisetIntersection(other: Collection<E>): Collection<E> =
    CollectionUtils.intersection(this, other)

/**
 * Multiset subtract: returns collection with frequency difference (non-negative).
 *
 * For example: `[a, a, b].multisetSubtract([a, b])` returns `[a]`.
 */
fun <E> Collection<E>.multisetSubtract(other: Collection<E>): Collection<E> =
    CollectionUtils.subtract(this, other)

/**
 * Symmetric difference by frequency: returns collection with elements that appear
 * in exactly one of the two collections, counted by frequency.
 *
 * For example: `[a, a, b].multisetDisjunction([a, b, b])` returns `[a, b]`.
 */
fun <E> Collection<E>.multisetDisjunction(other: Collection<E>): Collection<E> =
    CollectionUtils.disjunction(this, other)

/**
 * Multiset subset check: returns true if this collection's element frequencies
 * are a subset of the superset's frequencies (accounts for duplicates).
 *
 * For example:
 * - `[a, b].isSubCollectionOf([a, a, b])` returns `true`
 * - `[a, a].isSubCollectionOf([a, b])` returns `false`
 */
fun <E> Collection<E>.isSubCollectionOf(superset: Collection<E>): Boolean =
    CollectionUtils.isSubCollection(this, superset)

/**
 * Multiset equality: returns true if both collections have the same element frequencies
 * (order-insensitive).
 *
 * For example:
 * - `[a, a, b].isEqualCollection([b, a, a])` returns `true`
 * - `[a, a, b].isEqualCollection([a, b])` returns `false`
 */
fun <E> Collection<E>.isEqualCollection(other: Collection<E>): Boolean =
    CollectionUtils.isEqualCollection(this, other)

/**
 * Frequency map: returns a map from each element to its count in the collection.
 *
 * For example: `[a, a, b].cardinalityMap()` returns `{a: 2, b: 1}`.
 */
fun <E> Collection<E>.cardinalityMap(): Map<E, Int> =
    CollectionUtils.getCardinalityMap(this)

/**
 * Permutations: returns all permutations of the collection as a collection of lists.
 *
 * For example: `[1, 2, 3].permutations()` returns a collection of 6 permutations.
 */
fun <E> Collection<E>.permutations(): Collection<List<E>> =
    CollectionUtils.permutations(this)
