package com.kelvsyc.kotlin.core.traits

import com.kelvsyc.kotlin.core.Rational

/**
 * `RationalArithmetic` is a trait providing arithmetic operations over [Rational] values of
 * component type [T].
 *
 * All operations have default implementations derived from [arithmetic] and [gcd]; concrete
 * instances need only supply those two properties. An instance can be obtained via [Companion.from].
 *
 * ## Construction
 *
 * [of] is the sole normalising constructor: it reduces the fraction to canonical form
 * (positive denominator, `gcd(|numerator|, denominator) == 1`) and throws [ArithmeticException]
 * for a zero denominator. The internal [Rational] factory is used directly only when a result is
 * already known to be canonical.
 *
 * ## Overflow behaviour
 *
 * Intermediate values in [add], [subtract], [multiply], and [compareTo] involve cross-multiplying
 * components and may overflow the range of [T]. The overflow behaviour is determined entirely by
 * the [arithmetic] instance supplied at construction:
 * - A wrapping instance ([SignedIntegerArithmetic.int], [SignedIntegerArithmetic.long]) silently wraps.
 * - An overflow-checking instance (`OverflowCheckedSignedArithmetic.int`,
 *   `OverflowCheckedSignedArithmetic.long`) throws [ArithmeticException].
 * - `BigInteger` arithmetic is inherently overflow-free.
 *
 * [multiply] applies cross-cancellation before multiplying to keep intermediate values as small
 * as possible, but this does not eliminate overflow for bounded types near their range limits.
 *
 * ## Standard instances
 *
 * - [Companion.int] and [Companion.long] use wrapping arithmetic (commonMain).
 * - `bigInteger`, `checkedInt`, and `checkedLong` are available on JVM.
 */
interface RationalArithmetic<T> : RationalNumber<T> {
    /**
     * GCD and LCM operations for the component type [T].
     */
    val gcd: Gcd<T>

    /**
     * Constructs a [Rational] from [numerator] and [denominator], reducing to canonical form:
     * positive denominator and `gcd(|numerator|, denominator) == 1`.
     *
     * @throws ArithmeticException if [denominator] is zero.
     */
    fun of(numerator: T, denominator: T): Rational<T> {
        with(arithmetic) {
            if (denominator.isZero()) throw ArithmeticException("Denominator must be non-zero")
            val g = with(gcd) { numerator.gcd(denominator) }
            val n = numerator.divide(g)
            val d = denominator.divide(g)
            return if (d.isNegative()) Rational(n.negate(), d.negate()) else Rational(n, d)
        }
    }

    /**
     * Returns the negation `-(a/b) = (-a)/b`.
     */
    fun Rational<T>.negate(): Rational<T> =
        with(arithmetic) { Rational(numerator.negate(), denominator) }

    /**
     * Returns the sum `(a/b) + (c/d)`.
     *
     * Uses `gcd(b, d)` to find the minimal common denominator, dividing before multiplying to
     * reduce intermediate magnitude.
     */
    fun Rational<T>.add(other: Rational<T>): Rational<T> {
        with(arithmetic) {
            val g = with(gcd) { denominator.gcd(other.denominator) }
            val leftScale = other.denominator.divide(g)
            val rightScale = denominator.divide(g)
            val newNum = numerator.multiply(leftScale).add(other.numerator.multiply(rightScale))
            val newDen = denominator.multiply(leftScale)
            return of(newNum, newDen)
        }
    }

    /**
     * Returns the difference `(a/b) - (c/d)`.
     */
    fun Rational<T>.subtract(other: Rational<T>): Rational<T> {
        with(arithmetic) {
            val g = with(gcd) { denominator.gcd(other.denominator) }
            val leftScale = other.denominator.divide(g)
            val rightScale = denominator.divide(g)
            val newNum = numerator.multiply(leftScale).subtract(other.numerator.multiply(rightScale))
            val newDen = denominator.multiply(leftScale)
            return of(newNum, newDen)
        }
    }

    /**
     * Returns the product `(a/b) × (c/d)`.
     *
     * Applies cross-cancellation — `gcd(a, d)` and `gcd(c, b)` — before multiplying, keeping
     * intermediate values smaller and guaranteeing the result is already in canonical form.
     * Returns [zero] immediately if either operand is zero.
     */
    fun Rational<T>.multiply(other: Rational<T>): Rational<T> {
        if (isZero() || other.isZero()) return zero
        with(arithmetic) {
            // Cross-cancel: divide out gcd(a, d) and gcd(c, b) before multiplying.
            val g1 = with(gcd) { numerator.gcd(other.denominator) }
            val g2 = with(gcd) { other.numerator.gcd(denominator) }
            val a = numerator.divide(g1)
            val d = other.denominator.divide(g1)
            val c = other.numerator.divide(g2)
            val b = denominator.divide(g2)
            // After cross-cancellation the result is guaranteed canonical.
            return Rational(a.multiply(c), b.multiply(d))
        }
    }

    /**
     * Returns the quotient `(a/b) / (c/d) = (a/b) × (d/c)`.
     *
     * @throws ArithmeticException if [other] is zero.
     */
    fun Rational<T>.divide(other: Rational<T>): Rational<T> {
        if (other.isZero()) throw ArithmeticException("Division by zero")
        return multiply(other.reciprocal())
    }

    /**
     * Compares `(a/b)` to `(c/d)` by cross-multiplication: sign of `a·d − b·c`.
     *
     * Both denominators are positive in canonical form, so the sign of `a·d − b·c` equals the
     * sign of `a/b − c/d`. Intermediate products may overflow for bounded types.
     */
    fun Rational<T>.compareTo(other: Rational<T>): Int =
        with(arithmetic) {
            numerator.multiply(other.denominator).compareTo(denominator.multiply(other.numerator))
        }

    /**
     * Returns the integer part of this rational, truncated toward zero.
     *
     * Satisfies `this == of(integerPart(), 1).add(fractionalPart())`.
     */
    fun Rational<T>.integerPart(): T = with(arithmetic) { numerator.divide(denominator) }

    /**
     * Returns the fractional part `this - integerPart()`, which lies in `(-1, 1)` and has the
     * same sign as this rational (or is zero).
     */
    fun Rational<T>.fractionalPart(): Rational<T> {
        with(arithmetic) {
            val r = numerator.rem(denominator)
            // `zero` here is arithmetic.zero: T, not the rational zero, so construct explicitly.
            return if (r.isZero()) Rational(zero, one) else Rational(r, denominator)
        }
    }

    /**
     * Returns the floor of this rational: the largest integer ≤ `a/b`.
     */
    fun Rational<T>.floor(): T = with(arithmetic) { numerator.floorDiv(denominator) }

    /**
     * Returns the ceiling of this rational: the smallest integer ≥ `a/b`.
     */
    fun Rational<T>.ceil(): T = with(arithmetic) { numerator.ceilDiv(denominator) }

    companion object
}

/**
 * Returns a [RationalArithmetic] instance backed by [arithmetic] and [gcd].
 */
fun <T> RationalArithmetic.Companion.from(
    arithmetic: SignedIntegerArithmetic<T>,
    gcd: Gcd<T>,
): RationalArithmetic<T> = object : RationalArithmetic<T> {
    override val arithmetic: SignedIntegerArithmetic<T> = arithmetic
    override val gcd: Gcd<T> = gcd
}

// ── Standard instances ────────────────────────────────────────────────────────

private val intInstance: RationalArithmetic<Int> by lazy {
    RationalArithmetic.from(SignedIntegerArithmetic.int, Gcd.int)
}

private val longInstance: RationalArithmetic<Long> by lazy {
    RationalArithmetic.from(SignedIntegerArithmetic.long, Gcd.long)
}

/**
 * A [RationalArithmetic] instance for [Int] using wrapping arithmetic.
 *
 * Intermediate cross-multiplications in [RationalArithmetic.add], [RationalArithmetic.subtract],
 * and [RationalArithmetic.compareTo] may silently overflow. Use [checkedInt] on JVM for
 * overflow-detecting behaviour.
 */
val RationalArithmetic.Companion.int: RationalArithmetic<Int> get() = intInstance

/**
 * A [RationalArithmetic] instance for [Long] using wrapping arithmetic.
 *
 * See [int] for overflow notes. Use [checkedLong] on JVM for overflow-detecting behaviour.
 */
val RationalArithmetic.Companion.long: RationalArithmetic<Long> get() = longInstance
