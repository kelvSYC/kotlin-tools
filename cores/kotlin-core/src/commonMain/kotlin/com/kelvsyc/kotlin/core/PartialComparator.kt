package com.kelvsyc.kotlin.core

/**
 * A `PartialComparator` defines a partial comparison operation over a given type [T]. Unlike a [Comparator], it allows
 * for two objects to be incomparable, returning a `null` result when the comparison cannot be made.
 */
fun interface PartialComparator<T> {
    fun compare(a: T, b: T): Int?
}

/**
 * Returns a [PartialComparator] that imposes the reverse ordering of this comparator, passing `null` through
 * unchanged.
 */
fun <T> PartialComparator<T>.reversed(): PartialComparator<T> =
    PartialComparator { a, b -> compare(a, b)?.let { -it } }

/**
 * Returns a [PartialComparator] that first compares by this comparator, then by [other] if the result is `0`.
 *
 * If this comparator returns `null`, the result is `null` — two items that are incomparable under the first
 * criterion are not made comparable by a subsequent criterion.
 */
fun <T> PartialComparator<T>.thenComparing(other: PartialComparator<T>): PartialComparator<T> =
    PartialComparator { a, b ->
        val first = compare(a, b) ?: return@PartialComparator null
        if (first != 0) first else other.compare(a, b)
    }

/**
 * Returns a [PartialComparator] that compares values of type [T] by the natural ordering of a key [K] extracted
 * by [selector]. The comparison propagates `null` if the extracted keys are incomparable under their natural
 * ordering.
 *
 * For floating-point keys where NaN must be treated as incomparable, prefer the overload that accepts an explicit
 * [PartialComparator] for the key.
 */
fun <T, K : Comparable<K>> compareByPartial(selector: (T) -> K): PartialComparator<T> =
    PartialComparator { a, b -> selector(a).compareTo(selector(b)) }

/**
 * Returns a [PartialComparator] that compares values of type [T] by a key [K] extracted by [selector], using
 * [keyComparator] to compare the keys. The comparison propagates `null` if the key comparator considers the
 * extracted keys incomparable.
 */
fun <T, K> compareByPartial(keyComparator: PartialComparator<K>, selector: (T) -> K): PartialComparator<T> =
    PartialComparator { a, b -> keyComparator.compare(selector(a), selector(b)) }

// ── Interoperability with Comparator ─────────────────────────────────────────

/**
 * Returns a [PartialComparator] that delegates to this [Comparator], never returning `null`. A total order is a
 * special case of a partial order.
 */
fun <T> Comparator<T>.asPartialComparator(): PartialComparator<T> =
    PartialComparator { a, b -> compare(a, b) }

/**
 * Returns a [PartialComparator] that first compares by this comparator, then by [other] if the result is `0`.
 *
 * If this comparator returns `null`, the result is `null` — two items that are incomparable under the first
 * criterion are not made comparable by a subsequent criterion.
 */
fun <T> PartialComparator<T>.thenComparing(other: Comparator<T>): PartialComparator<T> =
    thenComparing(other.asPartialComparator())

/**
 * Returns a [Comparator] that delegates to this [PartialComparator], using [fallback] for pairs that this
 * comparator considers incomparable.
 *
 * The caller must choose [fallback] explicitly: silently collapsing incomparable pairs into an arbitrary ordering
 * is a common source of bugs, so no default is provided.
 */
fun <T> PartialComparator<T>.asComparator(fallback: Int): Comparator<T> =
    Comparator { a, b -> compare(a, b) ?: fallback }
