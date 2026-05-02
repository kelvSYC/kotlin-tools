package com.kelvsyc.kotlin.core.traits.integral

import java.math.BigInteger

// BigInteger-backed Miller-Rabin, used for ULong values that exceed Long.MAX_VALUE where the
// ULong-based mulmod (which requires m < 2^63) is not applicable.
private fun millerRabinBigInt(n: BigInteger, a: BigInteger): Boolean {
    if (n == a) return true
    var d = n - BigInteger.ONE
    var r = 0
    while (!d.testBit(0)) { d = d.shiftRight(1); r++ }
    var x = a.modPow(d, n)
    val nMinus1 = n - BigInteger.ONE
    if (x == BigInteger.ONE || x == nMinus1) return true
    repeat(r - 1) {
        x = x.multiply(x).mod(n)
        if (x == nMinus1) return true
    }
    return false
}

private val WITNESSES = listOf(2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37)
    .map { BigInteger.valueOf(it.toLong()) }

private fun ulongIsPrime(n: ULong): Boolean {
    if (n < 2uL) return false
    if (n < 4uL) return true
    if (n % 2uL == 0uL || n % 3uL == 0uL) return false
    // For values within Long range, the faster ULong Miller-Rabin path suffices.
    if (n <= Long.MAX_VALUE.toULong()) return longIsPrime(n.toLong())
    // ULong -> BigInteger: split into two non-negative 32-bit halves to avoid signed Long conversion.
    val bn = BigInteger.valueOf((n shr 32).toLong()).shiftLeft(32)
        .add(BigInteger.valueOf((n and 0xFFFFFFFFuL).toLong()))
    return WITNESSES.all { millerRabinBigInt(bn, it) }
}

private val ulongInstance: Primality<ULong> by lazy {
    object : Primality<ULong> {
        override fun ULong.isPrime(): Boolean = ulongIsPrime(this)
    }
}

val Primality.Companion.ulong: Primality<ULong> get() = ulongInstance

// ── BigInteger companion ──────────────────────────────────────────────────────

/**
 * The certainty level used by [Primality.bigInteger].
 *
 * [BigInteger.isProbablePrime] guarantees a false-positive probability below 2^(-[BIGINTEGER_CERTAINTY]),
 * so at 64 the probability of incorrectly reporting a composite as prime is less than 5.4 × 10^(-20).
 */
const val BIGINTEGER_CERTAINTY: Int = 64

private val bigIntegerInstance: Primality<BigInteger> by lazy {
    object : Primality<BigInteger> {
        // isProbablePrime operates on abs(), so a negative input like -7 would return true;
        // guard with signum() to ensure non-positive values always return false.
        override fun BigInteger.isPrime(): Boolean = signum() > 0 && isProbablePrime(BIGINTEGER_CERTAINTY)
    }
}

/**
 * A [Primality] instance for [BigInteger].
 *
 * **Limitation — probabilistic result:** unlike the fixed-width companions ([Primality.int],
 * [Primality.long], etc.) this companion delegates to [BigInteger.isProbablePrime] with certainty
 * [BIGINTEGER_CERTAINTY]. The JDK implementation uses a combined Miller-Rabin / strong-Lucas
 * (BPSW) test; no counterexample to BPSW is known, and the false-positive probability is
 * below 2^(-[BIGINTEGER_CERTAINTY]) per call. For cryptographic primality proofs, use a
 * dedicated library instead.
 */
val Primality.Companion.bigInteger: Primality<BigInteger> get() = bigIntegerInstance
