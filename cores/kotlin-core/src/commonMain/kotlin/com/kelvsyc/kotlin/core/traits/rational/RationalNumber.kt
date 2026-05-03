package com.kelvsyc.kotlin.core.traits.rational

import com.kelvsyc.kotlin.core.Rational
import com.kelvsyc.kotlin.core.traits.integral.SignedIntegerArithmetic

/**
 * `RationalNumber` is a trait providing structural properties over rational values of type [R]
 * whose components are of type [T].
 *
 * Structural properties are those derivable from the canonical representation
 * (`numerator/denominator` with positive denominator and `gcd(|numerator|, denominator) == 1`)
 * without performing full arithmetic on rationals: zero/unity/sign detection.
 *
 * For full arithmetic operations, see [RationalArithmetic].
 *
 * An instance can be obtained via [Companion.from].
 */
interface RationalNumber<R, T> {
    /**
     * Signed integer arithmetic for the component type [T].
     */
    val arithmetic: SignedIntegerArithmetic<T>

    /**
     * Returns the numerator of this rational.
     */
    fun R.numerator(): T

    /**
     * Returns the denominator of this rational. Always strictly positive in canonical form.
     */
    fun R.denominator(): T

    /**
     * Returns `true` if this rational is zero (numerator is zero).
     */
    fun R.isZero(): Boolean {
        val n = numerator()
        return with(arithmetic) { n.isZero() }
    }

    /**
     * Returns `true` if this rational is a whole number (denominator is one).
     */
    fun R.isWhole(): Boolean {
        val d = denominator()
        return with(arithmetic) { d.compareTo(one) == 0 }
    }

    /**
     * Returns `true` if this rational is strictly positive.
     */
    fun R.isPositive(): Boolean {
        val n = numerator()
        return with(arithmetic) { n.isPositive() }
    }

    /**
     * Returns `true` if this rational is strictly negative.
     */
    fun R.isNegative(): Boolean {
        val n = numerator()
        return with(arithmetic) { n.isNegative() }
    }

    /**
     * Returns `-1`, `0`, or `1` as this rational is negative, zero, or positive respectively.
     */
    fun R.sign(): T {
        val n = numerator()
        return with(arithmetic) { n.sign() }
    }

    companion object
}

/**
 * Returns a [RationalNumber] instance for [Rational]<[T]> backed by [arithmetic].
 */
fun <T> RationalNumber.Companion.from(arithmetic: SignedIntegerArithmetic<T>): RationalNumber<Rational<T>, T> =
    object : RationalNumber<Rational<T>, T> {
        override val arithmetic: SignedIntegerArithmetic<T> = arithmetic
        override fun Rational<T>.numerator(): T = this.numerator
        override fun Rational<T>.denominator(): T = this.denominator
    }
