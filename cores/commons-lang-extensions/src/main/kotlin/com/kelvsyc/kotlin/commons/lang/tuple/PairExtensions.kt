package com.kelvsyc.kotlin.commons.lang.tuple

import org.apache.commons.lang3.tuple.ImmutablePair
import org.apache.commons.lang3.tuple.ImmutableTriple
import org.apache.commons.lang3.tuple.MutablePair
import org.apache.commons.lang3.tuple.MutableTriple
import org.apache.commons.lang3.tuple.Pair as CommonsPair
import org.apache.commons.lang3.tuple.Triple as CommonsTriple

// ── Pair conversions ──────────────────────────────────────────────────────────

/**
 * Returns this [Pair] as an [ImmutablePair].
 */
fun <A, B> Pair<A, B>.toImmutablePair(): ImmutablePair<A, B> = ImmutablePair.of(first, second)

/**
 * Returns this [Pair] as a [MutablePair].
 */
fun <A, B> Pair<A, B>.toMutablePair(): MutablePair<A, B> = MutablePair.of(first, second)

/**
 * Returns this Commons [CommonsPair] as a Kotlin [Pair].
 *
 * Works for both [ImmutablePair] and [MutablePair].
 */
fun <L, R> CommonsPair<L, R>.toKotlinPair(): Pair<L, R> = left to right

// ── Triple conversions ────────────────────────────────────────────────────────

/**
 * Returns this [Triple] as an [ImmutableTriple].
 */
fun <A, B, C> Triple<A, B, C>.toImmutableTriple(): ImmutableTriple<A, B, C> =
    ImmutableTriple.of(first, second, third)

/**
 * Returns this [Triple] as a [MutableTriple].
 */
fun <A, B, C> Triple<A, B, C>.toMutableTriple(): MutableTriple<A, B, C> =
    MutableTriple.of(first, second, third)

/**
 * Returns this Commons [CommonsTriple] as a Kotlin [Triple].
 *
 * Works for both [ImmutableTriple] and [MutableTriple].
 */
fun <L, M, R> CommonsTriple<L, M, R>.toKotlinTriple(): Triple<L, M, R> = Triple(left, middle, right)

// ── Destructuring operators ───────────────────────────────────────────────────

/**
 * Returns the left element, enabling destructuring of a [CommonsPair].
 */
operator fun <L, R> CommonsPair<L, R>.component1(): L = left

/**
 * Returns the right element, enabling destructuring of a [CommonsPair].
 */
operator fun <L, R> CommonsPair<L, R>.component2(): R = right

/**
 * Returns the left element, enabling destructuring of a [CommonsTriple].
 */
operator fun <L, M, R> CommonsTriple<L, M, R>.component1(): L = left

/**
 * Returns the middle element, enabling destructuring of a [CommonsTriple].
 */
operator fun <L, M, R> CommonsTriple<L, M, R>.component2(): M = middle

/**
 * Returns the right element, enabling destructuring of a [CommonsTriple].
 */
operator fun <L, M, R> CommonsTriple<L, M, R>.component3(): R = right
