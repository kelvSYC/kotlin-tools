@file:JvmName("RationalArithmeticJvmInstances")

package com.kelvsyc.kotlin.core.traits.rational

import com.kelvsyc.kotlin.core.traits.integral.Gcd
import com.kelvsyc.kotlin.core.traits.integral.OverflowCheckedSignedArithmetic
import com.kelvsyc.kotlin.core.traits.integral.SignedIntegerArithmetic
import com.kelvsyc.kotlin.core.traits.integral.bigInteger
import com.kelvsyc.kotlin.core.traits.integral.from
import com.kelvsyc.kotlin.core.traits.integral.int
import com.kelvsyc.kotlin.core.traits.integral.long
import java.math.BigInteger

// ── BigInteger ────────────────────────────────────────────────────────────────

private val bigIntegerInstance: RationalArithmetic<BigInteger> by lazy {
    RationalArithmetic.from(
        arithmetic = SignedIntegerArithmetic.bigInteger,
        gcd = Gcd.from(SignedIntegerArithmetic.bigInteger),
    )
}

/**
 * A [RationalArithmetic] instance for [BigInteger].
 *
 * All operations are inherently overflow-free.
 */
val RationalArithmetic.Companion.bigInteger: RationalArithmetic<BigInteger>
    get() = bigIntegerInstance

// ── Overflow-checked Int / Long ───────────────────────────────────────────────

private val checkedIntInstance: RationalArithmetic<Int> by lazy {
    RationalArithmetic.from(
        arithmetic = OverflowCheckedSignedArithmetic.int,
        gcd = Gcd.from(OverflowCheckedSignedArithmetic.int),
    )
}

private val checkedLongInstance: RationalArithmetic<Long> by lazy {
    RationalArithmetic.from(
        arithmetic = OverflowCheckedSignedArithmetic.long,
        gcd = Gcd.from(OverflowCheckedSignedArithmetic.long),
    )
}

/**
 * A [RationalArithmetic] instance for [Int] that throws [ArithmeticException] on overflow.
 *
 * Drop-in replacement for [RationalArithmetic.int] with overflow detection. No JavaScript
 * implementation is available; see [RationalArithmetic.int] for cross-platform use.
 */
val RationalArithmetic.Companion.checkedInt: RationalArithmetic<Int>
    get() = checkedIntInstance

/**
 * A [RationalArithmetic] instance for [Long] that throws [ArithmeticException] on overflow.
 *
 * Drop-in replacement for [RationalArithmetic.long] with overflow detection. No JavaScript
 * implementation is available; see [RationalArithmetic.long] for cross-platform use.
 */
val RationalArithmetic.Companion.checkedLong: RationalArithmetic<Long>
    get() = checkedLongInstance
