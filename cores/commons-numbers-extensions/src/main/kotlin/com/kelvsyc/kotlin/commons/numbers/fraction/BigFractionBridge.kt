package com.kelvsyc.kotlin.commons.numbers.fraction

import com.kelvsyc.kotlin.core.Converter
import com.kelvsyc.kotlin.core.Rational
import com.kelvsyc.kotlin.core.traits.rational.RationalArithmetic
import com.kelvsyc.kotlin.core.traits.rational.bigInteger
import org.apache.commons.numbers.fraction.BigFraction
import java.math.BigInteger

/**
 * Converts this [BigFraction] to a [Rational]<[BigInteger]> in canonical form.
 */
fun BigFraction.toRational(): Rational<BigInteger> =
    RationalArithmetic.bigInteger.of(numerator, denominator)

/**
 * Converts this [Rational]<[BigInteger]> to a [BigFraction].
 *
 * The canonical-form invariant of [Rational] (positive denominator, fully reduced) means the
 * resulting [BigFraction] is already in lowest terms.
 */
fun Rational<BigInteger>.toBigFraction(): BigFraction = BigFraction.of(numerator, denominator)

private object BigFractionRationalConverter : Converter<BigFraction, Rational<BigInteger>>() {
    override fun doForward(a: BigFraction): Rational<BigInteger> = a.toRational()
    override fun doBackward(b: Rational<BigInteger>): BigFraction = b.toBigFraction()
}

/**
 * [Converter] between [BigFraction] and [Rational]<[BigInteger]>.
 *
 * Forward: [BigFraction.toRational]. Backward: [Rational.toBigFraction].
 */
val Rational.Companion.bigFractionConverter: Converter<BigFraction, Rational<BigInteger>>
    get() = BigFractionRationalConverter
