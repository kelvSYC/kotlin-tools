package com.kelvsyc.kotlin.core.traits

import com.kelvsyc.kotlin.core.BidFloat

// Powers of 10 indexed 0..18; 10^18 is the largest power that fits in a signed Long.
private val FMA_POW10 = longArrayOf(
    1L, 10L, 100L, 1_000L, 10_000L, 100_000L, 1_000_000L,
    10_000_000L, 100_000_000L, 1_000_000_000L, 10_000_000_000L, 100_000_000_000L,
    1_000_000_000_000L, 10_000_000_000_000L, 100_000_000_000_000L,
    1_000_000_000_000_000L, 10_000_000_000_000_000L, 100_000_000_000_000_000L,
    1_000_000_000_000_000_000L,
)

// Decimal digit count for positive Long values up to Long.MAX_VALUE (19 digits).
private fun fmaDigits(n: Long): Int = when {
    n >= 1_000_000_000_000_000_000L -> 19
    n >= 100_000_000_000_000_000L   -> 18
    n >= 10_000_000_000_000_000L    -> 17
    n >= 1_000_000_000_000_000L     -> 16
    n >= 100_000_000_000_000L       -> 15
    n >= 10_000_000_000_000L        -> 14
    n >= 1_000_000_000_000L         -> 13
    n >= 100_000_000_000L           -> 12
    n >= 10_000_000_000L            -> 11
    n >= 1_000_000_000L             -> 10
    n >= 100_000_000L               -> 9
    n >= 10_000_000L                -> 8
    n >= 1_000_000L                 -> 7
    n >= 100_000L                   -> 6
    n >= 10_000L                    -> 5
    n >= 1_000L                     -> 4
    n >= 100L                       -> 3
    n >= 10L                        -> 2
    else                            -> 1
}

private fun fmaPack(biasedExp: Int, sig: Long): Int {
    val s = sig.toInt()
    return if (s < 0x800000) {
        ((biasedExp shl 3) or (s ushr 20)) shl 20 or (s and 0xFFFFF)
    } else {
        val combination = 0x600 or (biasedExp shl 1) or ((s ushr 20) and 1)
        (combination shl 20) or (s and 0x1FFFFF)
    }
}

private fun fmaRoundHalfEven(trunc: Long, rem: Long, div: Long): Long {
    val half = div / 2L
    return when {
        rem > half -> trunc + 1L
        rem < half -> trunc
        else       -> if (trunc % 2L == 0L) trunc else trunc + 1L
    }
}

/**
 * Rounds a wide integer significand and biased exponent to a 7-digit [BidFloat].
 *
 * [sig] must be non-negative. Excess digits are stripped with round-half-to-even; the exponent is
 * adjusted accordingly. Overflow (biased exponent > 191 after rounding) produces ±infinity;
 * underflow scales toward zero using the subnormal range, or returns ±zero.
 */
private fun fmaRound(sign: Boolean, sig: Long, biasedExp: Int): BidFloat {
    val signBit = if (sign) Int.MIN_VALUE else 0
    if (sig == 0L) return BidFloat(signBit)
    var s = sig; var e = biasedExp
    val digits = fmaDigits(s)
    if (digits > 7) {
        val excess = digits - 7
        val div = FMA_POW10[excess]
        s = fmaRoundHalfEven(s / div, s % div, div)
        e += excess
        if (s >= 10_000_000L) { s /= 10L; e++ }
    }
    if (s == 0L) return BidFloat(signBit)
    if (e > 191) return BidFloat(signBit or 0x78000000)
    if (e < 0) {
        val shift = -e
        if (shift >= 8) return BidFloat(signBit)
        val div = FMA_POW10[shift]
        s = fmaRoundHalfEven(s / div, s % div, div)
        e = 0
        if (s == 0L) return BidFloat(signBit)
    }
    return BidFloat(signBit or fmaPack(e, s))
}

private fun bidFloatFma(a: BidFloat, b: BidFloat, c: BidFloat): BidFloat {
    val ca = with(BidFloat.encoding) { a.canonical() }
    val cb = with(BidFloat.encoding) { b.canonical() }
    val cc = with(BidFloat.encoding) { c.canonical() }

    if (ca.isNaN() || cb.isNaN() || cc.isNaN()) return BidFloat.NaN

    val productSign = ca.sign xor cb.sign

    // 0 × ∞ → NaN (invalid operation).
    if ((ca.isZero() && cb.isInfinite()) || (ca.isInfinite() && cb.isZero())) return BidFloat.NaN

    if (ca.isInfinite() || cb.isInfinite()) {
        // ∞ + (−∞) → NaN.
        if (cc.isInfinite() && cc.sign != productSign) return BidFloat.NaN
        return if (productSign) BidFloat.negativeInfinity else BidFloat.positiveInfinity
    }

    // Finite product; infinite addend.
    if (cc.isInfinite()) return cc

    // All three operands are finite.
    val sA = ca.significand.toLong()
    val sB = cb.significand.toLong()
    val sC = cc.significand.toLong()
    val eP = ca.biasedExponent + cb.biasedExponent - 101
    val eC = cc.biasedExponent

    val sP = sA * sB  // exact; ≤ 9,999,999² ≈ 10^14 < Long.MAX_VALUE

    if (sP == 0L && sC == 0L) return if (productSign && cc.sign) BidFloat.negativeZero else BidFloat.positiveZero
    if (sP == 0L) return cc
    if (sC == 0L) return fmaRound(productSign, sP, eP)

    // delta > 0: addend quantum is coarser (larger); delta < 0: product quantum is coarser.
    val delta = eC - eP

    val sPsigned = if (productSign) -sP else sP
    val sCsigned = if (cc.sign) -sC else sC

    val sumSig: Long
    val sumExp: Int

    when {
        delta in -4..11 -> {
            // Overflow is impossible in this range (verified by bound analysis).
            if (delta >= 0) {
                sumSig = sPsigned + sCsigned * FMA_POW10[delta]; sumExp = eP
            } else {
                sumSig = sPsigned * FMA_POW10[-delta] + sCsigned; sumExp = eC
            }
        }

        delta >= 12 -> {
            // Addend quantum is much coarser. Attempt exact arithmetic; fall back to addend
            // dominance if scaling sC × 10^delta or the total sum would overflow Long.
            // When overflow occurs, sC × 10^delta > Long.MAX_VALUE >> sP (≤ 10^14), so the
            // addend dominates and the product cannot affect the 7-digit rounded result.
            if (delta <= 18) {
                val scale = FMA_POW10[delta]
                if (sC <= Long.MAX_VALUE / scale) {
                    val sCscaled = sCsigned * scale
                    val overflowPos = sPsigned > 0L && sCscaled > Long.MAX_VALUE - sPsigned
                    val overflowNeg = sPsigned < 0L && sCscaled < Long.MIN_VALUE - sPsigned
                    if (!overflowPos && !overflowNeg) {
                        sumSig = sPsigned + sCscaled; sumExp = eP
                    } else {
                        return fmaRound(cc.sign, sC, eC)
                    }
                } else {
                    return fmaRound(cc.sign, sC, eC)
                }
            } else {
                return fmaRound(cc.sign, sC, eC)
            }
        }

        else -> { // delta <= -5
            // Product quantum is much coarser. Attempt exact arithmetic; fall back to product
            // dominance if scaling sP × 10^(-delta) or the total sum would overflow Long.
            val negDelta = -delta
            if (negDelta <= 18) {
                val scale = FMA_POW10[negDelta]
                if (sP <= Long.MAX_VALUE / scale) {
                    val sPscaled = sPsigned * scale
                    val overflowPos = sPscaled > 0L && sCsigned > Long.MAX_VALUE - sPscaled
                    val overflowNeg = sPscaled < 0L && sCsigned < Long.MIN_VALUE - sPscaled
                    if (!overflowPos && !overflowNeg) {
                        sumSig = sPscaled + sCsigned; sumExp = eC
                    } else {
                        return fmaRound(productSign, sP, eP)
                    }
                } else {
                    return fmaRound(productSign, sP, eP)
                }
            } else {
                return fmaRound(productSign, sP, eP)
            }
        }
    }

    if (sumSig == 0L) return BidFloat.positiveZero
    val resultSign = sumSig < 0L
    return fmaRound(resultSign, if (resultSign) -sumSig else sumSig, sumExp)
}

private val bidFloatFmaInstance: FusedMultiplyAdd<BidFloat> = object : FusedMultiplyAdd<BidFloat> {
    override fun fma(a: BidFloat, b: BidFloat, c: BidFloat): BidFloat = bidFloatFma(a, b, c)
}

/** FMA for [BidFloat]: computes `a × b + c` with a single rounding step. */
val FusedMultiplyAdd.Companion.bidFloat: FusedMultiplyAdd<BidFloat>
    get() = bidFloatFmaInstance
