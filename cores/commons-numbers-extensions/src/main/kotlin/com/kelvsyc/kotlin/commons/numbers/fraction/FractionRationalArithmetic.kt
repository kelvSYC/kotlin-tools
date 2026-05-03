package com.kelvsyc.kotlin.commons.numbers.fraction

import com.kelvsyc.kotlin.core.traits.integral.Gcd
import com.kelvsyc.kotlin.core.traits.integral.SignedIntegerArithmetic
import com.kelvsyc.kotlin.core.traits.integral.int
import com.kelvsyc.kotlin.core.traits.rational.RationalArithmetic
import org.apache.commons.numbers.fraction.Fraction

// ── RationalArithmetic<Fraction, Int> ────────────────────────────────────────
//
// Fraction.of(int, int) normalises to positive denominator and reduced form, matching the
// RationalArithmetic<R, T>.of() contract. Fraction.of() throws ArithmeticException for a zero
// denominator, consistent with the trait requirement.
//
// All arithmetic defaults (add, subtract, multiply, divide, compareTo, …) are inherited from
// RationalArithmetic<Fraction, Int> without override.

private val fractionInstance: RationalArithmetic<Fraction, Int> =
    object : RationalArithmetic<Fraction, Int> {
        override val arithmetic: SignedIntegerArithmetic<Int> = SignedIntegerArithmetic.int
        override val gcd: Gcd<Int> = Gcd.int
        override fun Fraction.numerator(): Int = numerator
        override fun Fraction.denominator(): Int = denominator
        override fun of(numerator: Int, denominator: Int): Fraction {
            // Fraction.of() does not normalise negative denominators; handle sign here.
            if (denominator == 0) throw ArithmeticException("Denominator must be non-zero")
            return if (denominator < 0) Fraction.of(-numerator, -denominator) else Fraction.of(numerator, denominator)
        }
    }

/**
 * A [RationalArithmetic] instance for [Fraction] with component type [Int].
 *
 * All arithmetic operations are derived from [SignedIntegerArithmetic.int] and [Gcd.int].
 * [RationalArithmetic.of] delegates to [Fraction.of], which normalises to positive denominator
 * and lowest terms. Intermediate cross-multiplications in [RationalArithmetic.add],
 * [RationalArithmetic.subtract], and [RationalArithmetic.compareTo] may silently overflow.
 */
val RationalArithmetic.Companion.fraction: RationalArithmetic<Fraction, Int>
    get() = fractionInstance
