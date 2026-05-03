package com.kelvsyc.kotlin.core.traits.rational

import com.kelvsyc.kotlin.core.Rational
import com.kelvsyc.kotlin.core.traits.integral.Gcd
import com.kelvsyc.kotlin.core.traits.integral.OverflowCheckedSignedArithmetic
import com.kelvsyc.kotlin.core.traits.integral.SignedIntegerArithmetic
import com.kelvsyc.kotlin.core.traits.integral.int
import com.kelvsyc.kotlin.core.traits.integral.long

/**
 * `RationalArithmetic` is a trait providing arithmetic operations over rational values of type [R]
 * whose components are of type [T].
 *
 * All operations have default implementations derived from [arithmetic] and [gcd]. Concrete
 * instances must supply [arithmetic], [gcd], and [of] (the normalising constructor), as well as
 * [RationalNumber.numerator] and [RationalNumber.denominator] accessors. An instance for
 * [Rational]<[T]> can be obtained via [Companion.from].
 *
 * ## Construction
 *
 * [of] is the sole normalising constructor: it reduces the fraction to canonical form
 * (positive denominator, `gcd(|numerator|, denominator) == 1`) and throws [ArithmeticException]
 * for a zero denominator. The [zero] and [one] identities are derived from [of].
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
interface RationalArithmetic<R, T> : RationalNumber<R, T> {
    /**
     * GCD and LCM operations for the component type [T].
     */
    val gcd: Gcd<T>

    /**
     * Constructs an [R] value from [numerator] and [denominator], reducing to canonical form:
     * positive denominator and `gcd(|numerator|, denominator) == 1`.
     *
     * @throws ArithmeticException if [denominator] is zero.
     */
    fun of(numerator: T, denominator: T): R

    /**
     * The additive identity: `0/1`.
     */
    val zero: R get() = of(arithmetic.zero, arithmetic.one)

    /**
     * The multiplicative identity: `1/1`.
     */
    val one: R get() = of(arithmetic.one, arithmetic.one)

    /**
     * Returns the reciprocal `denominator/numerator`.
     *
     * Since the canonical form guarantees `gcd(|numerator|, denominator) == 1` and positive
     * denominator, the reciprocal is already in canonical form after a sign correction.
     *
     * @throws ArithmeticException if this rational is zero.
     */
    fun R.reciprocal(): R {
        val n = numerator()
        val d = denominator()
        with(arithmetic) {
            if (n.isZero()) throw ArithmeticException("Reciprocal of zero")
            return if (n.isNegative()) of(d.negate(), n.negate()) else of(d, n)
        }
    }

    /**
     * Returns the negation `-(a/b) = (-a)/b`.
     */
    fun R.negate(): R {
        val n = numerator()
        val d = denominator()
        return with(arithmetic) { of(n.negate(), d) }
    }

    /**
     * Returns the sum `(a/b) + (c/d)`.
     *
     * Uses `gcd(b, d)` to find the minimal common denominator, dividing before multiplying to
     * reduce intermediate magnitude.
     */
    fun R.add(other: R): R {
        val n1 = numerator(); val d1 = denominator()
        val n2 = other.numerator(); val d2 = other.denominator()
        with(arithmetic) {
            val g = with(gcd) { d1.gcd(d2) }
            val leftScale = d2.divide(g)
            val rightScale = d1.divide(g)
            return of(n1.multiply(leftScale).add(n2.multiply(rightScale)), d1.multiply(leftScale))
        }
    }

    /**
     * Returns the difference `(a/b) - (c/d)`.
     */
    fun R.subtract(other: R): R {
        val n1 = numerator(); val d1 = denominator()
        val n2 = other.numerator(); val d2 = other.denominator()
        with(arithmetic) {
            val g = with(gcd) { d1.gcd(d2) }
            val leftScale = d2.divide(g)
            val rightScale = d1.divide(g)
            return of(n1.multiply(leftScale).subtract(n2.multiply(rightScale)), d1.multiply(leftScale))
        }
    }

    /**
     * Returns the product `(a/b) × (c/d)`.
     *
     * Applies cross-cancellation — `gcd(a, d)` and `gcd(c, b)` — before multiplying, keeping
     * intermediate values smaller. Returns [zero] immediately if either operand is zero.
     */
    fun R.multiply(other: R): R {
        if (isZero() || other.isZero()) return zero
        val n1 = numerator(); val d1 = denominator()
        val n2 = other.numerator(); val d2 = other.denominator()
        with(arithmetic) {
            val g1 = with(gcd) { n1.gcd(d2) }
            val g2 = with(gcd) { n2.gcd(d1) }
            val a = n1.divide(g1)
            val d = d2.divide(g1)
            val c = n2.divide(g2)
            val b = d1.divide(g2)
            return of(a.multiply(c), b.multiply(d))
        }
    }

    /**
     * Returns the quotient `(a/b) / (c/d) = (a/b) × (d/c)`.
     *
     * @throws ArithmeticException if [other] is zero.
     */
    fun R.divide(other: R): R {
        if (other.isZero()) throw ArithmeticException("Division by zero")
        return multiply(other.reciprocal())
    }

    /**
     * Compares `(a/b)` to `(c/d)` by cross-multiplication: sign of `a·d − b·c`.
     *
     * Both denominators are positive in canonical form, so the sign of `a·d − b·c` equals the
     * sign of `a/b − c/d`. Intermediate products may overflow for bounded types.
     */
    fun R.compareTo(other: R): Int {
        val n1 = numerator(); val d1 = denominator()
        val n2 = other.numerator(); val d2 = other.denominator()
        return with(arithmetic) { n1.multiply(d2).compareTo(d1.multiply(n2)) }
    }

    /**
     * Returns the integer part of this rational, truncated toward zero.
     *
     * Satisfies `this == of(integerPart(), arithmetic.one).add(fractionalPart())`.
     */
    fun R.integerPart(): T {
        val n = numerator()
        val d = denominator()
        return with(arithmetic) { n.divide(d) }
    }

    /**
     * Returns the fractional part `this - integerPart()`, which lies in `(-1, 1)` and has the
     * same sign as this rational (or is zero).
     */
    fun R.fractionalPart(): R {
        val n = numerator()
        val d = denominator()
        with(arithmetic) {
            val r = n.rem(d)
            return if (r.isZero()) of(zero, one) else of(r, d)
        }
    }

    /**
     * Returns the floor of this rational: the largest integer ≤ `a/b`.
     */
    fun R.floor(): T {
        val n = numerator()
        val d = denominator()
        return with(arithmetic) { n.floorDiv(d) }
    }

    /**
     * Returns the ceiling of this rational: the smallest integer ≥ `a/b`.
     */
    fun R.ceil(): T {
        val n = numerator()
        val d = denominator()
        return with(arithmetic) { n.ceilDiv(d) }
    }

    companion object
}

/**
 * Returns a [RationalArithmetic] instance for [Rational]<[T]> backed by [arithmetic] and [gcd].
 */
fun <T> RationalArithmetic.Companion.from(
    arithmetic: SignedIntegerArithmetic<T>,
    gcd: Gcd<T>,
): RationalArithmetic<Rational<T>, T> = object : RationalArithmetic<Rational<T>, T> {
    override val arithmetic: SignedIntegerArithmetic<T> = arithmetic
    override val gcd: Gcd<T> = gcd
    override fun Rational<T>.numerator(): T = this.numerator
    override fun Rational<T>.denominator(): T = this.denominator
    override fun of(numerator: T, denominator: T): Rational<T> {
        with(arithmetic) {
            if (denominator.isZero()) throw ArithmeticException("Denominator must be non-zero")
            val g = with(gcd) { numerator.gcd(denominator) }
            val n = numerator.divide(g)
            val d = denominator.divide(g)
            return if (d.isNegative()) Rational(n.negate(), d.negate()) else Rational(n, d)
        }
    }
}

// ── Standard instances ────────────────────────────────────────────────────────

private val intInstance: RationalArithmetic<Rational<Int>, Int> by lazy {
    RationalArithmetic.from(SignedIntegerArithmetic.int, Gcd.int)
}

private val longInstance: RationalArithmetic<Rational<Long>, Long> by lazy {
    RationalArithmetic.from(SignedIntegerArithmetic.long, Gcd.long)
}

/**
 * A [RationalArithmetic] instance for [Rational]<[Int]> using wrapping arithmetic.
 *
 * Intermediate cross-multiplications in [RationalArithmetic.add], [RationalArithmetic.subtract],
 * and [RationalArithmetic.compareTo] may silently overflow. Use [checkedInt] on JVM for
 * overflow-detecting behaviour.
 */
val RationalArithmetic.Companion.int: RationalArithmetic<Rational<Int>, Int> get() = intInstance

/**
 * A [RationalArithmetic] instance for [Rational]<[Long]> using wrapping arithmetic.
 *
 * See [int] for overflow notes. Use [checkedLong] on JVM for overflow-detecting behaviour.
 */
val RationalArithmetic.Companion.long: RationalArithmetic<Rational<Long>, Long> get() = longInstance
