package com.kelvsyc.kotlin.decimal

import com.kelvsyc.kotlin.core.PartialComparator

operator fun Decimal.plus(other: Decimal): Decimal = plus(other)
operator fun Decimal.minus(other: Decimal): Decimal = minus(other)
operator fun Decimal.times(other: Decimal): Decimal = times(other)
operator fun Decimal.div(other: Decimal): Decimal = dividedBy(other)
operator fun Decimal.rem(other: Decimal): Decimal = modulo(other)
operator fun Decimal.unaryMinus(): Decimal = negated()

/**
 * A [PartialComparator] for [Decimal] values that returns `null` when either operand is NaN,
 * reflecting Decimal.js's rule that NaN is unordered with respect to every value including
 * itself. Non-NaN values (including infinities) are compared by [Decimal.comparedTo].
 *
 * Consumers needing a total-order [Comparator] can use
 * `Decimal.partialComparator.asComparator(fallback)`, where a positive fallback places NaN
 * last and a negative fallback places it first.
 */
val Decimal.Companion.partialComparator: PartialComparator<Decimal>
    get() = PartialComparator { a, b ->
        val r = a.comparedTo(b)
        if (r.isNaN()) null else r.toInt()
    }
