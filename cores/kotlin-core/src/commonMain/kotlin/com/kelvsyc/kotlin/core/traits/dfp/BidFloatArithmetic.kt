package com.kelvsyc.kotlin.core.traits.dfp
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointArithmetic

import com.kelvsyc.kotlin.core.BidFloat
import com.kelvsyc.kotlin.core.bidFloat32Pack

// Powers of 10 indexed 0..10, used for scaling and rounding. Max needed: 10^10 (division scale).
private val POW10 = longArrayOf(
    1L, 10L, 100L, 1_000L, 10_000L, 100_000L, 1_000_000L,
    10_000_000L, 100_000_000L, 1_000_000_000L, 10_000_000_000L
)

private fun decimalDigitsLong(n: Long): Int = when {
    n >= 100_000_000_000_000L -> 15
    n >= 10_000_000_000_000L  -> 14
    n >= 1_000_000_000_000L   -> 13
    n >= 100_000_000_000L     -> 12
    n >= 10_000_000_000L      -> 11
    n >= 1_000_000_000L       -> 10
    n >= 100_000_000L         -> 9
    n >= 10_000_000L          -> 8
    n >= 1_000_000L           -> 7
    n >= 100_000L             -> 6
    n >= 10_000L              -> 5
    n >= 1_000L               -> 4
    n >= 100L                 -> 3
    n >= 10L                  -> 2
    else                      -> 1
}

private fun roundHalfEven(trunc: Long, rem: Long, div: Long): Long {
    val half = div / 2L
    return when {
        rem > half -> trunc + 1L
        rem < half -> trunc
        else       -> if (trunc % 2L == 0L) trunc else trunc + 1L
    }
}

// Like roundHalfEven but with a sticky bit from a prior inexact operation (e.g. integer division).
private fun roundHalfEvenSticky(trunc: Long, rem: Long, div: Long, sticky: Boolean): Long {
    val half = div / 2L
    return when {
        rem > half -> trunc + 1L
        rem < half -> trunc
        sticky     -> trunc + 1L  // exact value is above the halfway point
        else       -> if (trunc % 2L == 0L) trunc else trunc + 1L
    }
}

/**
 * Rounds a wide integer significand at a given biased quantum exponent to a valid 7-digit [BidFloat].
 *
 * The input [biasedExp] is the quantum exponent of [sig] before rounding. Excess digits are stripped
 * using round-half-to-even, and the exponent is adjusted accordingly. Overflow produces ±infinity;
 * underflow scales toward zero or returns ±zero.
 */
private fun roundToDecimal32(sign: Boolean, sig: Long, biasedExp: Int): BidFloat {
    val signBit = if (sign) Int.MIN_VALUE else 0
    if (sig == 0L) return BidFloat(signBit)

    var s = sig
    var e = biasedExp

    val digits = decimalDigitsLong(s)
    if (digits > 7) {
        val excess = digits - 7
        val divisor = POW10[excess]
        s = roundHalfEven(s / divisor, s % divisor, divisor)
        e += excess
        // Rounding carry can produce an 8th digit (e.g. 9,999,999.5 → 10,000,000).
        if (s >= 10_000_000L) { s /= 10L; e++ }
    }

    if (s == 0L) return BidFloat(signBit)
    if (e > 191) return BidFloat(signBit or 0x78000000)
    if (e < 0) {
        val shift = -e
        if (shift >= 8) return BidFloat(signBit)
        val divisor = POW10[shift]
        s = roundHalfEven(s / divisor, s % divisor, divisor)
        e = 0
        if (s == 0L) return BidFloat(signBit)
    }

    return BidFloat(signBit or bidFloat32Pack(e, s.toInt()))
}

private val bidFloatArithmeticInstance: FloatingPointArithmetic<BidFloat> = object : FloatingPointArithmetic<BidFloat> {
    override val zero: BidFloat get() = BidFloat.positiveZero
    // 1 × 10^0: biasedExp=101, sig=1 → combination=(101 shl 3)=0x328, continuation=1 → 0x32800001.
    override val one: BidFloat get() = BidFloat(0x32800001)

    override fun BidFloat.isNaN(): Boolean = this.isNaN()
    override fun BidFloat.isInfinite(): Boolean = this.isInfinite()
    override fun BidFloat.isFinite(): Boolean = !this.isNaN() && !this.isInfinite()
    override fun BidFloat.isZero(): Boolean = this.isZero()
    override fun BidFloat.isInteger(): Boolean = this.isInteger()
    override fun BidFloat.isNegative(): Boolean = bits < 0
    override fun BidFloat.negate(): BidFloat = BidFloat(bits xor Int.MIN_VALUE)
    override fun BidFloat.abs(): BidFloat = BidFloat(bits and Int.MAX_VALUE)
    override fun BidFloat.copySign(other: BidFloat): BidFloat =
        BidFloat((bits and Int.MAX_VALUE) or (other.bits and Int.MIN_VALUE))

    private fun BidFloat.canon(): BidFloat = with(BidFloat.encoding) { canonical() }

    override fun BidFloat.add(other: BidFloat): BidFloat {
        val a = canon(); val b = other.canon()
        if (a.isNaN() || b.isNaN()) return BidFloat.NaN
        if (a.isInfinite() && b.isInfinite()) return if (a.sign == b.sign) a else BidFloat.NaN
        if (a.isInfinite()) return a
        if (b.isInfinite()) return b
        // −0 + −0 = −0; everything else involving a zero gives +0 or the other operand.
        if (a.isZero() && b.isZero()) return if (a.sign && b.sign) BidFloat.negativeZero else BidFloat.positiveZero
        if (a.isZero()) return b
        if (b.isZero()) return a

        val (larger, smaller) = if (a.biasedExponent >= b.biasedExponent) a to b else b to a
        val diff = larger.biasedExponent - smaller.biasedExponent

        // For diff ≥ 8: |smaller| < 0.5 ULP of |larger| in the result precision; result is larger unchanged.
        if (diff >= 8) return larger

        val sLarger = larger.significand.toLong() * (if (larger.sign) -1L else 1L)
        val sSmaller = smaller.significand.toLong() * (if (smaller.sign) -1L else 1L)
        val total = sLarger * POW10[diff] + sSmaller

        if (total == 0L) return BidFloat.positiveZero
        val resultSign = total < 0
        return roundToDecimal32(resultSign, if (total < 0L) -total else total, smaller.biasedExponent)
    }

    override fun BidFloat.subtract(other: BidFloat): BidFloat =
        add(BidFloat(other.bits xor Int.MIN_VALUE))

    override fun BidFloat.multiply(other: BidFloat): BidFloat {
        val a = canon(); val b = other.canon()
        if (a.isNaN() || b.isNaN()) return BidFloat.NaN
        val resultSign = a.sign xor b.sign
        val signBit = if (resultSign) Int.MIN_VALUE else 0
        if ((a.isInfinite() && b.isZero()) || (a.isZero() && b.isInfinite())) return BidFloat.NaN
        if (a.isInfinite() || b.isInfinite()) return BidFloat(signBit or 0x78000000)
        if (a.isZero() || b.isZero()) return BidFloat(signBit)

        val sigProduct = a.significand.toLong() * b.significand.toLong()
        // Unbiased product exponent = (expA - 101) + (expB - 101); re-bias: add 101.
        val biasedExpProduct = a.biasedExponent + b.biasedExponent - 101
        return roundToDecimal32(resultSign, sigProduct, biasedExpProduct)
    }

    override fun BidFloat.divide(other: BidFloat): BidFloat {
        val a = canon(); val b = other.canon()
        if (a.isNaN() || b.isNaN()) return BidFloat.NaN
        val resultSign = a.sign xor b.sign
        val signBit = if (resultSign) Int.MIN_VALUE else 0

        if (b.isZero()) return if (a.isZero()) BidFloat.NaN else BidFloat(signBit or 0x78000000)
        if (a.isInfinite() && b.isInfinite()) return BidFloat.NaN
        if (a.isInfinite()) return BidFloat(signBit or 0x78000000)
        if (b.isInfinite() || a.isZero()) return BidFloat(signBit)

        // Scale numerator by 10^10 to obtain a quotient with at least p+3=10 significant digits.
        // sig_a ≤ 9,999,999 → scaledNumer ≤ ~10^17 < Long.MAX_VALUE ✓
        val scaledNumer = a.significand.toLong() * POW10[10]
        val sigB = b.significand.toLong()
        val quotientLong = scaledNumer / sigB
        val divRemainder = scaledNumer % sigB

        // Unbiased quotient exponent: (expA - expB - 10); re-bias.
        val biasedExpQ = a.biasedExponent - b.biasedExponent - 10 + 101

        val digits = decimalDigitsLong(quotientLong)
        val excess = digits - 7
        var s: Long; var e: Int
        if (excess <= 0) {
            // quotientLong already fits in 7 digits; no rounding needed.
            s = quotientLong; e = biasedExpQ
        } else {
            val divisor = POW10[excess]
            val trunc = quotientLong / divisor
            val rem = quotientLong % divisor
            val rounded = roundHalfEvenSticky(trunc, rem, divisor, divRemainder != 0L)
            s = rounded; e = biasedExpQ + excess
            if (s >= 10_000_000L) { s /= 10L; e++ }
        }

        // Normalize: strip trailing decimal zeros introduced by the 10^10 scale factor.
        while (s > 0L && s % 10L == 0L && e < 191) { s /= 10L; e++ }

        if (s == 0L) return BidFloat(signBit)
        if (e > 191) return BidFloat(signBit or 0x78000000)
        if (e < 0) {
            val shift = -e
            if (shift >= 8) return BidFloat(signBit)
            val d = POW10[shift]
            val t = roundHalfEven(s / d, s % d, d)
            if (t == 0L) return BidFloat(signBit)
            return BidFloat(signBit or bidFloat32Pack(0, t.toInt()))
        }
        return BidFloat(signBit or bidFloat32Pack(e, s.toInt()))
    }

    override fun BidFloat.compareTo(other: BidFloat): Int =
        BidFloat.comparator.compare(this, other)
}

val FloatingPointArithmetic.Companion.bidFloat: FloatingPointArithmetic<BidFloat>
    get() = bidFloatArithmeticInstance
