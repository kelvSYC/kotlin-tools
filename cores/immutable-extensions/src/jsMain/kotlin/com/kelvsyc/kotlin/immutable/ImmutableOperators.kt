package com.kelvsyc.kotlin.immutable

// ── ImmutableList operators ───────────────────────────────────────────────────

operator fun <T> ImmutableList<T>.plus(element: T): ImmutableList<T> = push(element)
operator fun <T> ImmutableList<T>.plus(other: ImmutableList<T>): ImmutableList<T> = concat(other)
operator fun <T> ImmutableList<T>.minus(index: Int): ImmutableList<T> = delete(index)
operator fun <T> ImmutableList<T>.get(index: Int): T? = get(index)

// ── ImmutableMap operators ────────────────────────────────────────────────────

operator fun <K, V> ImmutableMap<K, V>.plus(pair: Pair<K, V>): ImmutableMap<K, V> = set(pair.first, pair.second)
operator fun <K, V> ImmutableMap<K, V>.plus(other: ImmutableMap<K, V>): ImmutableMap<K, V> = merge(other)
operator fun <K, V> ImmutableMap<K, V>.minus(key: K): ImmutableMap<K, V> = delete(key)
operator fun <K, V> ImmutableMap<K, V>.get(key: K): V? = get(key)
operator fun <K, V> ImmutableMap<K, V>.contains(key: K): Boolean = has(key)

// ── ImmutableSet operators ────────────────────────────────────────────────────

operator fun <T> ImmutableSet<T>.plus(value: T): ImmutableSet<T> = add(value)
operator fun <T> ImmutableSet<T>.plus(other: ImmutableSet<T>): ImmutableSet<T> = union(other)
operator fun <T> ImmutableSet<T>.minus(value: T): ImmutableSet<T> = delete(value)
operator fun <T> ImmutableSet<T>.minus(other: ImmutableSet<T>): ImmutableSet<T> = subtract(other)
operator fun <T> ImmutableSet<T>.contains(value: T): Boolean = has(value)

// ── sortedBy extensions ───────────────────────────────────────────────────────
//
// Wraps the dynamic sortBy() binding with typed key extractor and Comparator<R>.
// The Comparator is wrapped in a lambda so it passes as a bare JS function — in Kotlin/JS IR,
// SAM types compile to objects, not functions, and would not be callable as (a,b)=>number.

fun <T, R : Comparable<R>> ImmutableList<T>.sortedBy(keyExtractor: (T) -> R): ImmutableList<T> =
    sortBy(keyExtractor)

fun <T, R> ImmutableList<T>.sortedBy(keyExtractor: (T) -> R, comparator: Comparator<R>): ImmutableList<T> =
    sortBy(keyExtractor) { a: R, b: R -> comparator.compare(a, b) }

fun <K, V, R : Comparable<R>> ImmutableMap<K, V>.sortedBy(keyExtractor: (V) -> R): ImmutableMap<K, V> =
    sortBy(keyExtractor)

fun <K, V, R> ImmutableMap<K, V>.sortedBy(keyExtractor: (V) -> R, comparator: Comparator<R>): ImmutableMap<K, V> =
    sortBy(keyExtractor) { a: R, b: R -> comparator.compare(a, b) }

fun <T, R : Comparable<R>> ImmutableSet<T>.sortedBy(keyExtractor: (T) -> R): ImmutableSet<T> =
    sortBy(keyExtractor)

fun <T, R> ImmutableSet<T>.sortedBy(keyExtractor: (T) -> R, comparator: Comparator<R>): ImmutableSet<T> =
    sortBy(keyExtractor) { a: R, b: R -> comparator.compare(a, b) }
