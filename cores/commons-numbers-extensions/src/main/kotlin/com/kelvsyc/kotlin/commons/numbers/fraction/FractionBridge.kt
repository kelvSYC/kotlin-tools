package com.kelvsyc.kotlin.commons.numbers.fraction

import com.kelvsyc.kotlin.core.Converter
import com.kelvsyc.kotlin.core.Rational
import com.kelvsyc.kotlin.core.traits.rational.RationalArithmetic
import com.kelvsyc.kotlin.core.traits.rational.int
import org.apache.commons.numbers.fraction.Fraction

/**
 * Converts this [Fraction] to a [Rational]<[Int]> in canonical form.
 *
 * Commons Numbers guarantees that `Fraction` values are already reduced, so no additional
 * normalization is required; the [RationalArithmetic.int] instance is only used for its [of]
 * factory to ensure positive-denominator canonical form.
 */
fun Fraction.toRational(): Rational<Int> = RationalArithmetic.int.of(numerator, denominator)

/**
 * Converts this [Rational]<[Int]> to a [Fraction].
 *
 * The canonical-form invariant of [Rational] (positive denominator, fully reduced) means the
 * resulting [Fraction] is already in lowest terms.
 */
fun Rational<Int>.toFraction(): Fraction = Fraction.of(numerator, denominator)

private object FractionRationalConverter : Converter<Fraction, Rational<Int>>() {
    override fun doForward(a: Fraction): Rational<Int> = a.toRational()
    override fun doBackward(b: Rational<Int>): Fraction = b.toFraction()
}

/**
 * [Converter] between [Fraction] and [Rational]<[Int]>.
 *
 * Forward: [Fraction.toRational]. Backward: [Rational.toFraction].
 */
val Rational.Companion.fractionConverter: Converter<Fraction, Rational<Int>>
    get() = FractionRationalConverter
