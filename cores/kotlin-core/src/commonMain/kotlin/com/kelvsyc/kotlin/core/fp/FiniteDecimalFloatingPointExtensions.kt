package com.kelvsyc.kotlin.core.fp

import com.kelvsyc.kotlin.core.BidFloat

// Powers of 10 indexed by exponent (0..9), used for digit counting and rounding.
private val POW10 = intArrayOf(1, 10, 100, 1_000, 10_000, 100_000, 1_000_000, 10_000_000, 100_000_000, 1_000_000_000)

private fun decimalDigits(n: UInt): Int = when {
    n >= 1_000_000_000u -> 10
    n >= 100_000_000u -> 9
    n >= 10_000_000u -> 8
    n >= 1_000_000u -> 7
    n >= 100_000u -> 6
    n >= 10_000u -> 5
    n >= 1_000u -> 4
    n >= 100u -> 3
    n >= 10u -> 2
    else -> 1
}

private fun roundHalfEven(truncated: UInt, remainder: UInt, divisor: UInt): UInt {
    val half = divisor / 2u
    return when {
        remainder > half -> truncated + 1u
        remainder < half -> truncated
        else -> if (truncated % 2u == 0u) truncated else truncated + 1u
    }
}

/**
 * Packs a biased exponent and a 7-digit significand into the BID combination + continuation fields (sign excluded).
 *
 * IEEE 754-2008 §3.5.2 defines two encoding cases for the combination field:
 * - Normal: significand < 2²³. The biased exponent occupies combination[10:3]; the top 3 significand bits occupy
 *   combination[2:0]; the low 20 bits are in the continuation field.
 * - Large-significand: significand ≥ 2²³ (decimal digits 8–9 in the leading position). combination[10:9] = 11,
 *   combination[8:1] = biased exponent, combination[0] = bit 20 of significand; the low 20 bits are in continuation.
 */
private fun packBid(biasedExp: Int, sig: UInt): Int {
    val s = sig.toInt()
    return if (s < 0x800000) {
        val combination = (biasedExp shl 3) or (s ushr 20)
        val continuation = s and 0xFFFFF
        (combination shl 20) or continuation
    } else {
        // Large-significand: combination[10:9]=11, combination[8:1]=biasedExp, combination[0]=sig bit 20.
        val combination = 0x600 or (biasedExp shl 1) or ((s ushr 20) and 1)
        val low21 = s and 0x1FFFFF
        (combination shl 20) or low21
    }
}

/**
 * Converts this value to a [FiniteDecimalFloatingPoint], preserving the full significand and exponent.
 *
 * The returned representation is the structural view of this value: [FiniteDecimalFloatingPoint.sign] reflects
 * the sign bit, [FiniteDecimalFloatingPoint.significand] is the integer coefficient (0–9,999,999) as a [UInt], and
 * [FiniteDecimalFloatingPoint.exponent] is the unbiased quantum exponent (biased exponent − 101).
 *
 * The representation is not normalized: trailing decimal zeros in the significand are preserved, so cohort-distinct
 * `BidFloat` values (e.g. `1 × 10⁰` and `10 × 10⁻¹`) yield distinct `FiniteDecimalFloatingPoint` values.
 *
 * Special values (NaN, infinity) are not supported and will throw [IllegalArgumentException].
 */
fun BidFloat.toRegularDecimalFloatingPoint(): FiniteDecimalFloatingPoint<UInt> {
    require(!isNaN() && !isInfinite()) { "Cannot convert non-finite BidFloat (bits=$bits) to FiniteDecimalFloatingPoint" }
    return FiniteDecimalFloatingPoint(sign, biasedExponent - 101, significand.toUInt())
}

/**
 * Converts this decimal floating-point representation to a [BidFloat].
 *
 * The [FiniteDecimalFloatingPoint.significand] may be any [UInt]; if it has more than 7 decimal digits it is rounded
 * to 7 using round-half-to-even before packing. Overflow (biased exponent > 191 after scaling) produces ±infinity.
 * Underflow (biased exponent < 0 after scaling) is handled by scaling the significand toward zero; if the result
 * rounds to zero, ±zero is returned.
 *
 * A zero significand always produces ±zero regardless of [FiniteDecimalFloatingPoint.exponent].
 */
fun FiniteDecimalFloatingPoint<UInt>.toBidFloat(): BidFloat {
    val signBit = if (sign) Int.MIN_VALUE else 0
    if (significand == 0u) return BidFloat(signBit)

    var sig = significand
    var biasedExp = exponent + 101

    // Reduce if significand has more than 7 decimal digits (UInt can be up to 10 digits).
    val digits = decimalDigits(sig)
    if (digits > 7) {
        val excess = digits - 7
        val divisor = POW10[excess].toUInt()
        sig = roundHalfEven(sig / divisor, sig % divisor, divisor)
        biasedExp += excess
        // Rounding up can carry an 8th digit (e.g. 9_999_999.5 → 10_000_000); normalize.
        if (sig >= 10_000_000u) { sig /= 10u; biasedExp++ }
    }

    if (sig == 0u) return BidFloat(signBit)

    // Overflow: biased exponent exceeds the maximum of 191.
    if (biasedExp > 191) return BidFloat(signBit or 0x78000000)

    // Underflow: scale the significand right until biasedExp reaches 0.
    if (biasedExp < 0) {
        val shift = -biasedExp
        // For shift ≥ 8, any 7-digit significand rounds to zero (max 9,999,999 / 10^8 < 0.5).
        if (shift >= 8) return BidFloat(signBit)
        val divisor = POW10[shift].toUInt()
        sig = roundHalfEven(sig / divisor, sig % divisor, divisor)
        biasedExp = 0
        if (sig == 0u) return BidFloat(signBit)
    }

    return BidFloat(signBit or packBid(biasedExp, sig))
}
