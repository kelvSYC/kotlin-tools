package com.kelvsyc.kotlin.core.traits

import com.kelvsyc.kotlin.core.Rational

/**
 * `RationalNumber` is a trait providing structural properties over [Rational] values of
 * component type [T].
 *
 * Structural properties are those derivable from the canonical representation
 * (`numerator/denominator` with positive denominator and `gcd(|numerator|, denominator) == 1`)
 * without performing full arithmetic on rationals: zero/unity/sign detection and reciprocal.
 *
 * For full arithmetic operations, see [RationalArithmetic].
 *
 * An instance can be obtained via [Companion.from].
 */
interface RationalNumber<T> {
    /**
     * Signed integer arithmetic for the component type [T].
     */
    val arithmetic: SignedIntegerArithmetic<T>

    /**
     * The additive identity: `0/1`.
     */
    val zero: Rational<T> get() = Rational(arithmetic.zero, arithmetic.one)

    /**
     * The multiplicative identity: `1/1`.
     */
    val one: Rational<T> get() = Rational(arithmetic.one, arithmetic.one)

    /**
     * Returns `true` if this rational is zero (numerator is zero).
     */
    fun Rational<T>.isZero(): Boolean = with(arithmetic) { numerator.isZero() }

    /**
     * Returns `true` if this rational is a whole number (denominator is one).
     */
    fun Rational<T>.isWhole(): Boolean = with(arithmetic) { denominator.compareTo(one) == 0 }

    /**
     * Returns `true` if this rational is strictly positive.
     */
    fun Rational<T>.isPositive(): Boolean = with(arithmetic) { numerator.isPositive() }

    /**
     * Returns `true` if this rational is strictly negative.
     */
    fun Rational<T>.isNegative(): Boolean = with(arithmetic) { numerator.isNegative() }

    /**
     * Returns `-1`, `0`, or `1` as this rational is negative, zero, or positive respectively.
     */
    fun Rational<T>.sign(): T = with(arithmetic) { numerator.sign() }

    /**
     * Returns the reciprocal `denominator/numerator`.
     *
     * Since the canonical form guarantees `gcd(|numerator|, denominator) == 1` and positive
     * denominator, the reciprocal is already in canonical form after a sign correction.
     *
     * @throws ArithmeticException if this rational is zero.
     */
    fun Rational<T>.reciprocal(): Rational<T> {
        with(arithmetic) {
            if (numerator.isZero()) throw ArithmeticException("Reciprocal of zero")
            // Canonical form requires positive denominator. If numerator < 0, negate both.
            return if (numerator.isNegative()) {
                Rational(denominator.negate(), numerator.negate())
            } else {
                Rational(denominator, numerator)
            }
        }
    }

    companion object
}

/**
 * Returns a [RationalNumber] instance backed by [arithmetic].
 */
fun <T> RationalNumber.Companion.from(arithmetic: SignedIntegerArithmetic<T>): RationalNumber<T> =
    object : RationalNumber<T> {
        override val arithmetic: SignedIntegerArithmetic<T> = arithmetic
    }
