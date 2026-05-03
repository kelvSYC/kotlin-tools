package com.kelvsyc.kotlin.commons.numbers.fraction

import com.kelvsyc.kotlin.core.traits.integral.Gcd
import com.kelvsyc.kotlin.core.traits.integral.SignedIntegerArithmetic
import com.kelvsyc.kotlin.core.traits.integral.bigInteger
import com.kelvsyc.kotlin.core.traits.integral.from
import com.kelvsyc.kotlin.core.traits.rational.RationalArithmetic
import org.apache.commons.numbers.fraction.BigFraction
import java.math.BigInteger

// ── RationalArithmetic<BigFraction, BigInteger> ────────────────────────────────
//
// BigFraction.of(BigInteger, BigInteger) normalises to positive denominator and reduced form,
// matching the RationalArithmetic<R, T>.of() contract. BigFraction.of() throws ArithmeticException
// for a zero denominator, consistent with the trait requirement.
//
// All arithmetic defaults (add, subtract, multiply, divide, compareTo, …) are inherited from
// RationalArithmetic<BigFraction, BigInteger> without override.

private val bigFractionInstance: RationalArithmetic<BigFraction, BigInteger> =
    object : RationalArithmetic<BigFraction, BigInteger> {
        override val arithmetic: SignedIntegerArithmetic<BigInteger> = SignedIntegerArithmetic.bigInteger
        override val gcd: Gcd<BigInteger> = Gcd.from(SignedIntegerArithmetic.bigInteger)
        override fun BigFraction.numerator(): BigInteger = numerator
        override fun BigFraction.denominator(): BigInteger = denominator
        override fun of(numerator: BigInteger, denominator: BigInteger): BigFraction {
            // BigFraction.of() does not normalise negative denominators; handle sign here.
            if (denominator.signum() == 0) throw ArithmeticException("Denominator must be non-zero")
            return if (denominator.signum() < 0) {
                BigFraction.of(numerator.negate(), denominator.negate())
            } else {
                BigFraction.of(numerator, denominator)
            }
        }
    }

/**
 * A [RationalArithmetic] instance for [BigFraction] with component type [BigInteger].
 *
 * All arithmetic operations are derived from [SignedIntegerArithmetic.bigInteger] and a
 * [Gcd] instance backed by [BigInteger.gcd]. All operations are inherently overflow-free.
 * [RationalArithmetic.of] delegates to [BigFraction.of], which normalises to positive denominator
 * and lowest terms.
 */
val RationalArithmetic.Companion.bigFraction: RationalArithmetic<BigFraction, BigInteger>
    get() = bigFractionInstance
