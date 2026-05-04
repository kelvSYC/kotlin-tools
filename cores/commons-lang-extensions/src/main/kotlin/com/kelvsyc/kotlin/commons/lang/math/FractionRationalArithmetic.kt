package com.kelvsyc.kotlin.commons.lang.math

import com.kelvsyc.kotlin.core.traits.integral.Gcd
import com.kelvsyc.kotlin.core.traits.integral.SignedIntegerArithmetic
import com.kelvsyc.kotlin.core.traits.integral.int
import com.kelvsyc.kotlin.core.traits.rational.RationalArithmetic
import org.apache.commons.lang3.math.Fraction

private val fractionInstance: RationalArithmetic<Fraction, Int> =
    object : RationalArithmetic<Fraction, Int> {
        override val arithmetic: SignedIntegerArithmetic<Int> = SignedIntegerArithmetic.int
        override val gcd: Gcd<Int> = Gcd.int
        override fun Fraction.numerator(): Int = numerator
        override fun Fraction.denominator(): Int = denominator
        override fun of(numerator: Int, denominator: Int): Fraction {
            if (denominator == 0) throw ArithmeticException("Denominator must be non-zero")
            return if (denominator < 0) {
                Fraction.getReducedFraction(-numerator, -denominator)
            } else {
                Fraction.getReducedFraction(numerator, denominator)
            }
        }
    }

/**
 * A [RationalArithmetic] instance for Commons Lang [Fraction] with component type [Int].
 *
 * All arithmetic operations are derived from [SignedIntegerArithmetic.int] and [Gcd.int].
 * [RationalArithmetic.of] delegates to [Fraction.getFraction], which normalises to positive
 * denominator and lowest terms. Intermediate cross-multiplications in [RationalArithmetic.add],
 * [RationalArithmetic.subtract], and [RationalArithmetic.compareTo] may silently overflow.
 */
val RationalArithmetic.Companion.fraction: RationalArithmetic<Fraction, Int>
    get() = fractionInstance
