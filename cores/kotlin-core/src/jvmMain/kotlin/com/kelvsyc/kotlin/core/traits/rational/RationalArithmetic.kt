@file:JvmName("RationalArithmeticJvmInstances")

package com.kelvsyc.kotlin.core.traits.rational

import com.kelvsyc.kotlin.core.Rational
import com.kelvsyc.kotlin.core.traits.integral.Gcd
import com.kelvsyc.kotlin.core.traits.integral.OverflowCheckedSignedArithmetic
import com.kelvsyc.kotlin.core.traits.integral.SignedIntegerArithmetic
import com.kelvsyc.kotlin.core.traits.integral.bigInteger
import com.kelvsyc.kotlin.core.traits.integral.from
import com.kelvsyc.kotlin.core.traits.integral.int
import com.kelvsyc.kotlin.core.traits.integral.long
import java.math.BigInteger

// ── BigInteger ────────────────────────────────────────────────────────────────

private val bigIntegerInstance: RationalArithmetic<Rational<BigInteger>, BigInteger> by lazy {
    RationalArithmetic.from(
        arithmetic = SignedIntegerArithmetic.bigInteger,
        gcd = Gcd.from(SignedIntegerArithmetic.bigInteger),
    )
}

/**
 * A [RationalArithmetic] instance for [Rational]<[BigInteger]>.
 *
 * All operations are inherently overflow-free.
 */
val RationalArithmetic.Companion.bigInteger: RationalArithmetic<Rational<BigInteger>, BigInteger>
    get() = bigIntegerInstance

// ── Overflow-checked Int / Long ───────────────────────────────────────────────

private val checkedIntInstance: RationalArithmetic<Rational<Int>, Int> by lazy {
    RationalArithmetic.from(
        arithmetic = OverflowCheckedSignedArithmetic.int,
        gcd = Gcd.from(OverflowCheckedSignedArithmetic.int),
    )
}

private val checkedLongInstance: RationalArithmetic<Rational<Long>, Long> by lazy {
    RationalArithmetic.from(
        arithmetic = OverflowCheckedSignedArithmetic.long,
        gcd = Gcd.from(OverflowCheckedSignedArithmetic.long),
    )
}

/**
 * A [RationalArithmetic] instance for [Rational]<[Int]> that throws [ArithmeticException] on overflow.
 *
 * Drop-in replacement for [RationalArithmetic.int] with overflow detection. No JavaScript
 * implementation is available; see [RationalArithmetic.int] for cross-platform use.
 */
val RationalArithmetic.Companion.checkedInt: RationalArithmetic<Rational<Int>, Int>
    get() = checkedIntInstance

/**
 * A [RationalArithmetic] instance for [Rational]<[Long]> that throws [ArithmeticException] on overflow.
 *
 * Drop-in replacement for [RationalArithmetic.long] with overflow detection. No JavaScript
 * implementation is available; see [RationalArithmetic.long] for cross-platform use.
 */
val RationalArithmetic.Companion.checkedLong: RationalArithmetic<Rational<Long>, Long>
    get() = checkedLongInstance
