package com.kelvsyc.kotlin.commons.lang.math

import com.kelvsyc.kotlin.core.Converter
import com.kelvsyc.kotlin.core.Rational
import com.kelvsyc.kotlin.core.traits.rational.RationalArithmetic
import com.kelvsyc.kotlin.core.traits.rational.int
import org.apache.commons.lang3.math.Fraction

/**
 * A [Converter] between [Fraction] and [Rational]`<`[Int]`>`.
 *
 * Both types carry the same canonical form guarantee (positive denominator,
 * `gcd(|numerator|, denominator) == 1`), so the conversion is lossless in both directions.
 *
 * The forward direction produces a [Rational]`<`[Int]`>` usable with [RationalArithmetic.int];
 * the reverse direction produces a [Fraction] with the full Commons API surface.
 */
val FractionRationalConverter: Converter<Fraction, Rational<Int>> = Converter.of(
    forward = { RationalArithmetic.int.run { of(it.numerator, it.denominator) } },
    backward = { Fraction.getFraction(it.numerator, it.denominator) },
)

/**
 * Converts this [Fraction] to a [Rational]`<`[Int]`>`.
 *
 * @see FractionRationalConverter
 */
fun Fraction.toRational(): Rational<Int> = FractionRationalConverter(this)

/**
 * Converts this [Rational]`<`[Int]`>` to a [Fraction].
 *
 * @throws ArithmeticException if [Fraction.getFraction] rejects the components (edge case for
 * values near [Int.MIN_VALUE]).
 *
 * @see FractionRationalConverter
 */
fun Rational<Int>.toFraction(): Fraction = FractionRationalConverter.reverse(this)
