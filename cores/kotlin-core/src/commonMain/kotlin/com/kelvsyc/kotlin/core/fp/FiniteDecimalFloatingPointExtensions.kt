package com.kelvsyc.kotlin.core.fp

import com.kelvsyc.kotlin.core.BidFloat
import com.kelvsyc.kotlin.core.DpdFloat

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

/**
 * Encodes a 3-digit decimal value (0–999) into a 10-bit DPD declet.
 *
 * Implements the five encoding cases from IEEE 754-2008 Table 3.4, the inverse of the decode
 * used in [DpdFloat.declet1] / [DpdFloat.declet2]. The result is always a canonical (non-redundant)
 * declet: the don't-care bits in the all-large-digit sub-case are set to zero.
 */
private fun encodeDeclet(v: Int): Int {
    val d3 = v % 10; val d2 = (v / 10) % 10; val d1 = v / 100
    val p = d1 >= 8; val q = d2 >= 8; val r = d3 >= 8
    return when {
        !p && !q && !r ->
            // Case 0: all small — straightforward 3+3+3 packing, b[3]=0
            (d1 shl 7) or (d2 shl 4) or d3
        !p && !q &&  r ->
            // Case 1: b[3:1]=100, d3 large
            (d1 shl 7) or (d2 shl 4) or 0x8 or (d3 - 8)
        !p &&  q && !r -> {
            // Case 2: b[3:1]=101, d2 large; d3[2:1] occupy b[6:5], d2[0] occupies b[4]
            val d3hi = (d3 and 0x6) shl 4
            (d1 shl 7) or d3hi or ((d2 - 8) shl 4) or 0xA or (d3 and 0x1)
        }
         p && !q && !r -> {
            // Case 3: b[3:1]=110, d1 large; d3[2:1] occupy b[9:8], d1[0] occupies b[7]
            val d3hi = (d3 and 0x6) shl 7
            d3hi or ((d1 - 8) shl 7) or (d2 shl 4) or 0xC or (d3 and 0x1)
        }
         p &&  q && !r -> {
            // Case 4, b[9:8]=00: d1,d2 large; d3[2:1] in b[6:5], d1[0] in b[7], d2[0] in b[4]
            val d3hi = (d3 and 0x6) shl 4
            ((d1 - 8) shl 7) or d3hi or ((d2 - 8) shl 4) or 0xE or (d3 and 0x1)
        }
         p && !q &&  r ->
            // Case 4, b[9:8]=01: d1,d3 large; d2 in b[6:4], d1[0] in b[7], d3[0] in b[0]
            0x100 or ((d1 - 8) shl 7) or (d2 shl 4) or 0xE or (d3 - 8)
        !p &&  q &&  r ->
            // Case 4, b[9:8]=10: d2,d3 large; d1 in b[7:5], d2[0] in b[4], d3[0] in b[0]
            0x200 or (d1 shl 5) or ((d2 - 8) shl 4) or 0xE or (d3 - 8)
        else ->
            // Case 4, b[9:8]=11: all large; b[6:5] set to 0 (canonical)
            0x300 or ((d1 - 8) shl 7) or ((d2 - 8) shl 4) or 0xE or (d3 - 8)
    }
}

/**
 * Packs a biased exponent and a leading digit into the DPD combination field (sign excluded),
 * then combines with the two pre-encoded declets to form the lower 31 bits of a [DpdFloat].
 */
private fun packDpd(biasedExp: Int, leadingDigit: Int, declet1: Int, declet2: Int): Int {
    val combination = if (leadingDigit < 8) {
        (biasedExp shl 3) or leadingDigit
    } else {
        0x600 or (biasedExp shl 1) or (leadingDigit - 8)
    }
    return (combination shl 20) or (declet1 shl 10) or declet2
}

/**
 * Converts this value to a [FiniteDecimalFloatingPoint], preserving the full significand and exponent.
 *
 * The returned representation is the structural view of this value: [FiniteDecimalFloatingPoint.sign]
 * reflects the sign bit, [FiniteDecimalFloatingPoint.significand] is the integer coefficient
 * (0–9,999,999) as a [UInt], and [FiniteDecimalFloatingPoint.exponent] is the unbiased quantum
 * exponent (biased exponent − 101).
 *
 * The representation is not normalized: cohort-distinct `DpdFloat` values yield distinct
 * `FiniteDecimalFloatingPoint` values.
 *
 * Special values (NaN, infinity) are not supported and will throw [IllegalArgumentException].
 */
fun DpdFloat.toRegularDecimalFloatingPoint(): FiniteDecimalFloatingPoint<UInt> {
    require(!isNaN() && !isInfinite()) { "Cannot convert non-finite DpdFloat (bits=$bits) to FiniteDecimalFloatingPoint" }
    return FiniteDecimalFloatingPoint(sign, biasedExponent - 101, significand.toUInt())
}

/**
 * Converts this decimal floating-point representation to a [DpdFloat].
 *
 * The [FiniteDecimalFloatingPoint.significand] may be any [UInt]; if it has more than 7 decimal
 * digits it is rounded to 7 using round-half-to-even before packing. Overflow (biased exponent
 * > 191 after scaling) produces ±infinity. Underflow (biased exponent < 0 after scaling) is
 * handled by scaling the significand toward zero; if the result rounds to zero, ±zero is returned.
 *
 * A zero significand always produces ±zero regardless of [FiniteDecimalFloatingPoint.exponent].
 */
fun FiniteDecimalFloatingPoint<UInt>.toDpdFloat(): DpdFloat {
    val signBit = if (sign) Int.MIN_VALUE else 0
    if (significand == 0u) return DpdFloat(signBit)

    var sig = significand.toInt()
    var biasedExp = exponent + 101

    val digits = decimalDigits(significand)
    if (digits > 7) {
        val excess = digits - 7
        val divisor = POW10[excess].toUInt()
        val uSig = significand
        sig = roundHalfEven(uSig / divisor, uSig % divisor, divisor).toInt()
        biasedExp += excess
        if (sig >= 10_000_000) { sig /= 10; biasedExp++ }
    }

    if (sig == 0) return DpdFloat(signBit)
    if (biasedExp > 191) return DpdFloat(signBit or 0x78000000)

    if (biasedExp < 0) {
        val shift = -biasedExp
        if (shift >= 8) return DpdFloat(signBit)
        val divisor = POW10[shift].toUInt()
        val uSig = sig.toUInt()
        sig = roundHalfEven(uSig / divisor, uSig % divisor, divisor).toInt()
        biasedExp = 0
        if (sig == 0) return DpdFloat(signBit)
    }

    val leadingDigit = sig / 1_000_000
    val remainder = sig % 1_000_000
    val d1 = encodeDeclet(remainder / 1_000)
    val d2 = encodeDeclet(remainder % 1_000)

    return DpdFloat(signBit or packDpd(biasedExp, leadingDigit, d1, d2))
}
