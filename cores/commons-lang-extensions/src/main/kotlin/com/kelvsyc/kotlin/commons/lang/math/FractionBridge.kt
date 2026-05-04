package com.kelvsyc.kotlin.commons.lang.math

import com.kelvsyc.kotlin.core.Converter
import com.kelvsyc.kotlin.core.Rational
import com.kelvsyc.kotlin.core.traits.rational.RationalArithmetic
import com.kelvsyc.kotlin.core.traits.rational.int
import org.apache.commons.lang3.math.Fraction

/**
 * Converts this [Fraction] to a [Rational]`<`[Int]`>`.
 *
 * Commons Lang guarantees that `Fraction` values are already reduced with positive denominator,
 * so no additional normalization is required.
 */
fun Fraction.toRational(): Rational<Int> = RationalArithmetic.int.of(numerator, denominator)

/**
 * Converts this [Rational]`<`[Int]`>` to a [Fraction].
 *
 * The canonical-form invariant of [Rational] (positive denominator, fully reduced) means the
 * resulting [Fraction] is already in lowest terms.
 */
fun Rational<Int>.toFraction(): Fraction = Fraction.getFraction(numerator, denominator)

private object FractionRationalConverterInstance : Converter<Fraction, Rational<Int>>() {
    override fun doForward(a: Fraction): Rational<Int> = a.toRational()
    override fun doBackward(b: Rational<Int>): Fraction = b.toFraction()
}

/**
 * [Converter] between [Fraction] and [Rational]`<`[Int]`>`.
 *
 * Forward: [Fraction.toRational]. Backward: [Rational.toFraction].
 */
val Rational.Companion.fractionConverter: Converter<Fraction, Rational<Int>>
    get() = FractionRationalConverterInstance
