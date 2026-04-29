package com.kelvsyc.kotlin.core.fp

import com.kelvsyc.kotlin.core.BidDouble
import com.kelvsyc.kotlin.core.DpdDouble
import com.kelvsyc.kotlin.core.bidDouble64Pack

// Powers of 10 indexed by exponent (0..16), used for digit counting and rounding.
private val POW10_ULONG = ulongArrayOf(
    1uL, 10uL, 100uL, 1_000uL, 10_000uL, 100_000uL, 1_000_000uL, 10_000_000uL, 100_000_000uL,
    1_000_000_000uL, 10_000_000_000uL, 100_000_000_000uL, 1_000_000_000_000uL,
    10_000_000_000_000uL, 100_000_000_000_000uL, 1_000_000_000_000_000uL, 10_000_000_000_000_000uL
)

private fun decimalDigits(n: ULong): Int = when {
    n >= 10_000_000_000_000_000uL -> 17
    n >= 1_000_000_000_000_000uL -> 16
    n >= 100_000_000_000_000uL -> 15
    n >= 10_000_000_000_000uL -> 14
    n >= 1_000_000_000_000uL -> 13
    n >= 100_000_000_000uL -> 12
    n >= 10_000_000_000uL -> 11
    n >= 1_000_000_000uL -> 10
    n >= 100_000_000uL -> 9
    n >= 10_000_000uL -> 8
    n >= 1_000_000uL -> 7
    n >= 100_000uL -> 6
    n >= 10_000uL -> 5
    n >= 1_000uL -> 4
    n >= 100uL -> 3
    n >= 10uL -> 2
    else -> 1
}

private fun roundHalfEven(truncated: ULong, remainder: ULong, divisor: ULong): ULong {
    val half = divisor / 2uL
    return when {
        remainder > half -> truncated + 1uL
        remainder < half -> truncated
        else -> if (truncated % 2uL == 0uL) truncated else truncated + 1uL
    }
}

/**
 * Converts this value to a [FiniteDecimalFloatingPoint], preserving the full significand and exponent.
 *
 * Special values (NaN, infinity) are not supported and will throw [IllegalArgumentException].
 */
fun BidDouble.toRegularDecimalFloatingPoint(): FiniteDecimalFloatingPoint<ULong> {
    require(!isNaN() && !isInfinite()) { "Cannot convert non-finite BidDouble (bits=$bits) to FiniteDecimalFloatingPoint" }
    return FiniteDecimalFloatingPoint(sign, biasedExponent - 398, significand.toULong())
}

/**
 * Converts this decimal floating-point representation to a [BidDouble].
 *
 * The [FiniteDecimalFloatingPoint.significand] may be any [ULong]; if it has more than 16 decimal
 * digits it is rounded to 16 using round-half-to-even before packing. Overflow (biased exponent
 * > 767 after scaling) produces ±infinity. Underflow (biased exponent < 0 after scaling) is handled
 * by scaling the significand toward zero; if the result rounds to zero, ±zero is returned.
 *
 * A zero significand always produces ±zero regardless of [FiniteDecimalFloatingPoint.exponent].
 */
fun FiniteDecimalFloatingPoint<ULong>.toBidDouble(): BidDouble {
    val signBit = if (sign) Long.MIN_VALUE else 0L
    if (significand == 0uL) return BidDouble(signBit)

    var sig = significand
    var biasedExp = exponent + 398

    val digits = decimalDigits(sig)
    if (digits > 16) {
        val excess = digits - 16
        val divisor = POW10_ULONG[excess]
        sig = roundHalfEven(sig / divisor, sig % divisor, divisor)
        biasedExp += excess
        // Rounding up can carry a 17th digit (e.g. 9_999_999_999_999_999.5 → 10_000_000_000_000_000); normalize.
        if (sig >= 10_000_000_000_000_000uL) { sig /= 10uL; biasedExp++ }
    }

    if (sig == 0uL) return BidDouble(signBit)
    if (biasedExp > 767) return BidDouble(signBit or 0x7800_0000_0000_0000L)

    if (biasedExp < 0) {
        val shift = -biasedExp
        // For shift ≥ 17, any 16-digit significand rounds to zero.
        if (shift >= 17) return BidDouble(signBit)
        val divisor = POW10_ULONG[shift]
        sig = roundHalfEven(sig / divisor, sig % divisor, divisor)
        biasedExp = 0
        if (sig == 0uL) return BidDouble(signBit)
    }

    return BidDouble(signBit or bidDouble64Pack(biasedExp, sig.toLong()))
}

/**
 * Converts this value to a [FiniteDecimalFloatingPoint], preserving the full significand and exponent.
 *
 * Special values (NaN, infinity) are not supported and will throw [IllegalArgumentException].
 */
fun DpdDouble.toRegularDecimalFloatingPoint(): FiniteDecimalFloatingPoint<ULong> {
    require(!isNaN() && !isInfinite()) { "Cannot convert non-finite DpdDouble (bits=$bits) to FiniteDecimalFloatingPoint" }
    return FiniteDecimalFloatingPoint(sign, biasedExponent - 398, significand.toULong())
}

/**
 * Converts this decimal floating-point representation to a [DpdDouble].
 *
 * The [FiniteDecimalFloatingPoint.significand] may be any [ULong]; if it has more than 16 decimal
 * digits it is rounded to 16 using round-half-to-even before packing. Overflow (biased exponent
 * > 767 after scaling) produces ±infinity. Underflow (biased exponent < 0 after scaling) is handled
 * by scaling the significand toward zero; if the result rounds to zero, ±zero is returned.
 *
 * A zero significand always produces ±zero regardless of [FiniteDecimalFloatingPoint.exponent].
 */
fun FiniteDecimalFloatingPoint<ULong>.toDpdDouble(): DpdDouble {
    val signBit = if (sign) Long.MIN_VALUE else 0L
    if (significand == 0uL) return DpdDouble(signBit)

    var sig = significand
    var biasedExp = exponent + 398

    val digits = decimalDigits(sig)
    if (digits > 16) {
        val excess = digits - 16
        val divisor = POW10_ULONG[excess]
        sig = roundHalfEven(sig / divisor, sig % divisor, divisor)
        biasedExp += excess
        if (sig >= 10_000_000_000_000_000uL) { sig /= 10uL; biasedExp++ }
    }

    if (sig == 0uL) return DpdDouble(signBit)
    if (biasedExp > 767) return DpdDouble(signBit or 0x7800_0000_0000_0000L)

    if (biasedExp < 0) {
        val shift = -biasedExp
        if (shift >= 17) return DpdDouble(signBit)
        val divisor = POW10_ULONG[shift]
        sig = roundHalfEven(sig / divisor, sig % divisor, divisor)
        biasedExp = 0
        if (sig == 0uL) return DpdDouble(signBit)
    }

    val sigL = sig.toLong()
    val leadingDigit = (sigL / 1_000_000_000_000_000L).toInt()
    val rem5 = sigL % 1_000_000_000_000_000L
    val d1 = encodeDeclet((rem5 / 1_000_000_000_000L).toInt())
    val rem4 = rem5 % 1_000_000_000_000L
    val d2 = encodeDeclet((rem4 / 1_000_000_000L).toInt())
    val rem3 = rem4 % 1_000_000_000L
    val d3 = encodeDeclet((rem3 / 1_000_000L).toInt())
    val rem2 = rem3 % 1_000_000L
    val d4 = encodeDeclet((rem2 / 1_000L).toInt())
    val d5 = encodeDeclet((rem2 % 1_000L).toInt())

    return DpdDouble(signBit or packDpd64(biasedExp, leadingDigit, d1, d2, d3, d4, d5))
}
