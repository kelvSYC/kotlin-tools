package com.kelvsyc.kotlin.core

/**
 * A rational number `numerator/denominator`, parameterised over the component type [T].
 *
 * [T] must be a signed integer type. Unsigned types cannot represent negative numerators and
 * therefore cannot express the full set of rationals; use a signed type instead.
 *
 * Values are always in canonical form: the denominator is strictly positive, and
 * `gcd(|numerator|, denominator) == 1`. No arithmetic operators are defined here; all operations
 * are provided through [com.kelvsyc.kotlin.core.traits.RationalNumber] and
 * [com.kelvsyc.kotlin.core.traits.RationalArithmetic].
 */
interface Rational<T> {
    val numerator: T
    val denominator: T

    companion object
}

/**
 * Internal factory that constructs a [Rational] from already-canonical components.
 *
 * Callers are responsible for ensuring the invariants hold: [denominator] must be strictly
 * positive and `gcd(|numerator|, denominator)` must equal 1. Use
 * [com.kelvsyc.kotlin.core.traits.RationalArithmetic.of] for arbitrary inputs.
 */
internal fun <T> Rational(numerator: T, denominator: T): Rational<T> =
    RationalImpl(numerator, denominator)

private data class RationalImpl<T>(
    override val numerator: T,
    override val denominator: T,
) : Rational<T>
