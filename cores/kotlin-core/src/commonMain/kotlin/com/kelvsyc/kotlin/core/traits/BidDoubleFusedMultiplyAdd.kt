package com.kelvsyc.kotlin.core.traits

import com.kelvsyc.kotlin.core.BidDouble
import com.kelvsyc.kotlin.core.bidDouble64Pack

// Powers of 10, indices 0..18.
private val FMA64_POW10 = longArrayOf(
    1L, 10L, 100L, 1_000L, 10_000L, 100_000L, 1_000_000L,
    10_000_000L, 100_000_000L, 1_000_000_000L, 10_000_000_000L, 100_000_000_000L,
    1_000_000_000_000L, 10_000_000_000_000L, 100_000_000_000_000L,
    1_000_000_000_000_000L, 10_000_000_000_000_000L, 100_000_000_000_000_000L,
    1_000_000_000_000_000_000L,
)

private const val FMA64_POW16 = 10_000_000_000_000_000L  // 10^16

/** Decimal digit count for a positive Long (1..9_999_999_999_999_999_999). */
private fun fmaDigits64(n: Long): Int = when {
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

/** Round-half-to-even with sticky bit: [sticky] breaks a tie toward the larger absolute value. */
private fun fmaHalfEven(trunc: Long, rem: Long, div: Long, sticky: Boolean): Long {
    val half = div / 2L
    return when {
        rem > half -> trunc + 1L
        rem < half -> trunc
        sticky     -> trunc + 1L
        else       -> if (trunc % 2L == 0L) trunc else trunc + 1L
    }
}

/**
 * Exact decimal product `a × b = hi × 10^16 + lo`.
 * Identical to `mul64Decimal` in BidDoubleArithmetic.
 */
private fun fmaMul64(a: Long, b: Long): Pair<Long, Long> {
    val aHi = a / 100_000_000L; val aLo = a % 100_000_000L
    val bHi = b / 100_000_000L; val bLo = b % 100_000_000L
    val hiHi  = aHi * bHi
    val cross  = aHi * bLo + aLo * bHi
    val loLo   = aLo * bLo
    val crossHi = cross / 100_000_000L
    val crossLo = (cross % 100_000_000L) * 100_000_000L
    val loTotal = crossLo + loLo
    val carry   = loTotal / FMA64_POW16
    return (hiHi + crossHi + carry) to (loTotal % FMA64_POW16)
}

/**
 * Packs [sig] and [biasedExp] into a [BidDouble], handling overflow (→ ±∞) and
 * underflow (→ ±0 via subnormal truncation). [sig] must have at most 16 digits.
 */
private fun fmaPackDecimal64(sign: Boolean, sig: Long, biasedExp: Int): BidDouble {
    val signBit = if (sign) Long.MIN_VALUE else 0L
    if (sig == 0L) return BidDouble(signBit)
    var s = sig; var e = biasedExp
    val d = fmaDigits64(s)
    if (d > 16) {
        val excess = d - 16
        val div = FMA64_POW10[excess]
        s = fmaHalfEven(s / div, s % div, div, false)
        e += excess
        if (s >= FMA64_POW16) { s /= 10L; e++ }
    }
    if (s == 0L) return BidDouble(signBit)
    if (e > 767) return BidDouble(signBit or 0x7800_0000_0000_0000L)
    if (e < 0) {
        val shift = -e
        if (shift >= 17) return BidDouble(signBit)
        val div = FMA64_POW10[shift]
        s = fmaHalfEven(s / div, s % div, div, false)
        e = 0
        if (s == 0L) return BidDouble(signBit)
    }
    return BidDouble(signBit or bidDouble64Pack(e, s))
}

/**
 * Rounds `(hi × 10^16 + lo) × 10^biasedExp` to 16 significant digits.
 *
 * [hi] may have up to 19 digits; [lo] is in `[0, 10^16)`. [sticky] indicates a
 * non-zero fractional tail below [lo] in the direction of the result.
 */
private fun fmaRoundSplit(sign: Boolean, hi: Long, lo: Long, biasedExp: Int, sticky: Boolean): BidDouble {
    if (hi == 0L && lo == 0L) return if (sign) BidDouble.negativeZero else BidDouble.positiveZero
    if (hi == 0L) return fmaPackDecimal64(sign, lo, biasedExp)
    val d   = fmaDigits64(hi)
    val exp = biasedExp + d
    val sig: Long
    if (d <= 16) {
        val div = FMA64_POW10[d]
        val rem = lo % div
        val t   = hi * FMA64_POW10[16 - d] + lo / div
        sig = fmaHalfEven(t, rem, div, sticky)
    } else {
        val excess = d - 16
        val div    = FMA64_POW10[excess]
        val half   = div / 2L
        val t      = hi / div
        val rem    = hi % div
        sig = when {
            rem > half        -> t + 1L
            rem < half        -> t
            lo > 0L || sticky -> t + 1L
            else              -> if (t % 2L == 0L) t else t + 1L
        }
    }
    // fmaPackDecimal64 handles d>16 internally: strips the excess digit and increments e.
    return fmaPackDecimal64(sign, sig, exp)
}

/**
 * Rounds `(hihi × 10^32 + hi × 10^16 + lo) × 10^biasedExp` to 16 significant digits.
 *
 * [hihi] is positive and may have up to 19 digits; [hi] and [lo] are in `[0, 10^16)`.
 * [sticky] indicates a non-zero fractional tail below [lo] in the direction of the result.
 */
private fun fmaRoundSplitThree(
    sign: Boolean,
    hihi: Long, hi: Long, lo: Long,
    biasedExp: Int,
    sticky: Boolean = false,
): BidDouble {
    if (hihi == 0L) return fmaRoundSplit(sign, hi, lo, biasedExp, sticky)
    val d   = fmaDigits64(hihi)
    val exp = biasedExp + d + 16
    val sig: Long
    if (d <= 16) {
        val div  = FMA64_POW10[d]
        val half = div / 2L
        val t    = hihi * FMA64_POW10[16 - d] + hi / div
        val rem  = hi % div
        sig = when {
            rem > half            -> t + 1L
            rem < half            -> t
            lo > 0L || sticky     -> t + 1L
            else                  -> if (t % 2L == 0L) t else t + 1L
        }
    } else {
        val excess = d - 16
        val div    = FMA64_POW10[excess]
        val half   = div / 2L
        val t      = hihi / div
        val rem    = hihi % div
        sig = when {
            rem > half                   -> t + 1L
            rem < half                   -> t
            hi > 0L || lo > 0L || sticky -> t + 1L
            else                         -> if (t % 2L == 0L) t else t + 1L
        }
    }
    // fmaPackDecimal64 handles d>16 internally: strips the excess digit and increments e.
    return fmaPackDecimal64(sign, sig, exp)
}

/**
 * Signed addition of two 2-component pairs at the same quantum.
 *
 * Returns `(resultSign, hi, lo)` of `sign1 × (hi1 × 10^16 + lo1) + sign2 × (hi2 × 10^16 + lo2)`.
 * All magnitude components must be non-negative.
 */
private fun addSigned2(
    sign1: Boolean, hi1: Long, lo1: Long,
    sign2: Boolean, hi2: Long, lo2: Long,
): Triple<Boolean, Long, Long> = if (sign1 == sign2) {
    val sumLo = lo1 + lo2
    val carry = sumLo / FMA64_POW16
    Triple(sign1, hi1 + hi2 + carry, sumLo % FMA64_POW16)
} else {
    val cmp = hi1.compareTo(hi2)
    if (cmp > 0 || (cmp == 0 && lo1 >= lo2)) {
        val rawLo = lo1 - lo2
        if (rawLo >= 0L) Triple(sign1, hi1 - hi2, rawLo)
        else Triple(sign1, hi1 - hi2 - 1L, rawLo + FMA64_POW16)
    } else {
        val rawLo = lo2 - lo1
        if (rawLo >= 0L) Triple(sign2, hi2 - hi1, rawLo)
        else Triple(sign2, hi2 - hi1 - 1L, rawLo + FMA64_POW16)
    }
}

/**
 * Scales the 2-component decimal product `hiP × 10^16 + loP` up by `10^D` (D = 1..16),
 * producing a 3-component `(hihi, hi, lo)` at the finer quantum `10^(eP - D)`.
 *
 * The identity `(hiP × 10^16 + loP) × 10^D = hihi × 10^32 + hi × 10^16 + lo` holds exactly.
 * All returned components are in `[0, 10^16)`.
 *
 * Key: for k = 16 − D (0..15),
 *   hiPhi  = hiP / 10^k           (the part of hiP × 10^D that overflows into hihi)
 *   hiPlo  = (hiP % 10^k) × 10^D  (< 10^16, contributes to hi)
 *   loPhi  = loP / 10^k           (the part of loP × 10^D that carries into hi)
 *   loPlo  = (loP % 10^k) × 10^D  (< 10^16, the lo component)
 *   hi_mid = hiPlo + loPhi         (always < 10^16, so no carry into hihi)
 */
private fun scaleProductUp(hiP: Long, loP: Long, D: Int): Triple<Long, Long, Long> {
    val k = 16 - D          // 0..15
    val scale = FMA64_POW10[D]
    val hiPhi = hiP / FMA64_POW10[k]
    val hiPlo = (hiP % FMA64_POW10[k]) * scale
    val loPhi = loP / FMA64_POW10[k]
    val loPlo = (loP % FMA64_POW10[k]) * scale
    // hiPlo < 10^16, loPhi < 10^(16-k) = 10^D ≤ 10^15 (for D ≤ 15, k ≥ 1); sum < 10^16.
    // For D = 16 (k = 0): hiPlo = 0, loPhi = loP < 10^16; sum = loP < 10^16.
    val hiMid = hiPlo + loPhi   // always < FMA64_POW16
    return Triple(hiPhi, hiMid, loPlo)
}

private fun bidDoubleFma(a: BidDouble, b: BidDouble, c: BidDouble): BidDouble {
    val ca = with(BidDouble.encoding) { a.canonical() }
    val cb = with(BidDouble.encoding) { b.canonical() }
    val cc = with(BidDouble.encoding) { c.canonical() }

    // ── NaN propagation ───────────────────────────────────────────────────────
    if (ca.isNaN() || cb.isNaN() || cc.isNaN()) return BidDouble.NaN

    val productSign = ca.sign xor cb.sign

    // ── 0 × ∞ → NaN ───────────────────────────────────────────────────────────
    if ((ca.isZero() && cb.isInfinite()) || (ca.isInfinite() && cb.isZero())) return BidDouble.NaN

    // ── Infinite product ──────────────────────────────────────────────────────
    if (ca.isInfinite() || cb.isInfinite()) {
        if (cc.isInfinite() && cc.sign != productSign) return BidDouble.NaN
        return if (productSign) BidDouble.negativeInfinity else BidDouble.positiveInfinity
    }

    // ── Infinite addend ───────────────────────────────────────────────────────
    if (cc.isInfinite()) return cc

    // ── All finite ────────────────────────────────────────────────────────────
    val sA = ca.significand;  val sB = cb.significand;  val sC = cc.significand
    val eP = ca.biasedExponent + cb.biasedExponent - 398   // product biased exponent
    val eC = cc.biasedExponent

    val (hiP, loP) = fmaMul64(sA, sB)   // product = (hiP × 10^16 + loP) × 10^eP

    // ── Zero shortcuts ────────────────────────────────────────────────────────
    if (hiP == 0L && loP == 0L && sC == 0L) {
        return if (productSign && cc.sign) BidDouble.negativeZero else BidDouble.positiveZero
    }
    if (hiP == 0L && loP == 0L) return cc
    if (sC == 0L) return fmaRoundSplit(productSign, hiP, loP, eP, false)

    val delta = eC - eP   // > 0: addend quantum coarser; < 0: product quantum coarser

    return when {
        // ── Addend completely dominates (product < 0.2 ULP of addend) ─────────
        //    Proof: product ≤ 10^32 × 10^eP, 0.5 ULP(result) ≈ 0.5 × 10^(eC − 15).
        //    product / (0.5 ULP) ≤ 2 × 10^(47 − delta) ≤ 0.2 < 1 for delta ≥ 48.
        delta >= 48 -> cc

        // ── 3-component: addend quantum much coarser than product (delta 19..47) ──
        //    Align addend at eP: addend = hihi_C × 10^32 + hi_C × 10^16 + 0 at eP.
        delta in 19..47 -> {
            val hihi_C: Long
            val hi_C: Long
            if (delta <= 32) {
                // k = 32 − delta ∈ [0, 13]
                val k = 32 - delta
                hihi_C = sC / FMA64_POW10[k]
                hi_C   = (sC % FMA64_POW10[k]) * FMA64_POW10[16 - k]
            } else {
                // delta 33..47: hihi_C = sC × 10^(delta−32), hi_C = 0.
                val expS  = delta - 32      // 1..15
                val scale = FMA64_POW10[expS]
                // If overflow: product < 0.5 ULP of addend (addend ≥ hihi_C × 10^32 ≫ product).
                if (sC > Long.MAX_VALUE / scale) return cc
                hihi_C = sC * scale
                hi_C   = 0L
            }

            if (hihi_C == 0L) {
                // Addend fits in 2 components; product may dominate.
                val (sign, hi, lo) = addSigned2(productSign, hiP, loP, cc.sign, hi_C, 0L)
                if (hi == 0L && lo == 0L) return BidDouble.positiveZero
                return fmaRoundSplit(sign, hi, lo, eP, false)
            }

            // hihi_C > 0 → addend ≥ 10^32 > product: addend always dominates.
            val resultSign = cc.sign
            var hihi_sum: Long
            val hi_sum: Long
            val lo_sum: Long
            if (cc.sign == productSign) {
                // Same sign: (hihi_C, hi_C, 0) + (0, hiP, loP)
                lo_sum = loP
                val hiRaw = hi_C + hiP
                if (hiRaw >= FMA64_POW16) { hi_sum = hiRaw - FMA64_POW16; hihi_sum = hihi_C + 1L }
                else                      { hi_sum = hiRaw;               hihi_sum = hihi_C      }
            } else {
                // Different sign; subtract product from addend (addend dominates).
                val borrow1: Long
                if (loP == 0L) { lo_sum = 0L;                 borrow1 = 0L }
                else           { lo_sum = FMA64_POW16 - loP;  borrow1 = 1L }
                val hiRaw = hi_C - hiP - borrow1
                val borrow2: Long
                if (hiRaw >= 0L) { hi_sum = hiRaw;               borrow2 = 0L }
                else             { hi_sum = hiRaw + FMA64_POW16;  borrow2 = 1L }
                hihi_sum = hihi_C - borrow2
            }
            fmaRoundSplitThree(resultSign, hihi_sum, hi_sum, lo_sum, eP)
        }

        // ── 2-component: aligned addend within product range (delta 0..18) ────
        //    hi_C ≤ sC × 10^(delta−16) ≤ 10^18 < Long.MAX for delta ≤ 18.
        delta in 0..18 -> {
            val hi_C: Long
            val lo_C: Long
            if (delta <= 16) {
                val k  = 16 - delta
                hi_C   = sC / FMA64_POW10[k]
                lo_C   = (sC % FMA64_POW10[k]) * FMA64_POW10[delta]
            } else {
                // delta = 17 or 18; hi_C = sC × 10^(delta−16) ≤ sC × 100 ≤ 10^18.
                hi_C   = sC * FMA64_POW10[delta - 16]
                lo_C   = 0L
            }
            val (sign, hi, lo) = addSigned2(productSign, hiP, loP, cc.sign, hi_C, lo_C)
            if (hi == 0L && lo == 0L) return BidDouble.positiveZero
            fmaRoundSplit(sign, hi, lo, eP, false)
        }

        // ── Negative delta, exact: scale product up by 10^|delta| to eC ───────
        //    For D = |delta| ∈ [1, 16], compute product × 10^D exactly as a
        //    3-component (hihi_P, hi_P, lo_P) at eC, then add ±sC in the lo slot.
        //
        //    Key identity (D = 16 − k):
        //      (hiP × 10^16 + loP) × 10^D = hiPhi × 10^32 + hiMid × 10^16 + loPlo
        //    where hiPhi = hiP / 10^k, hiMid = (hiP % 10^k) × 10^D + loP / 10^k < 10^16,
        //    loPlo = (loP % 10^k) × 10^D < 10^16.
        delta in -16..-1 -> {
            val D = -delta   // 1..16
            val (hihi_P, hi_P, lo_P) = scaleProductUp(hiP, loP, D)

            if (productSign == cc.sign) {
                // Same sign: add sC (≤ 10^16 − 1) to lo_P.
                val loSum = lo_P + sC
                val carry2 = if (loSum >= FMA64_POW16) 1L else 0L
                val lo_sum = if (carry2 > 0L) loSum - FMA64_POW16 else loSum
                val hiSum  = hi_P + carry2
                val carry3 = if (hiSum >= FMA64_POW16) 1L else 0L
                val hi_sum = if (carry3 > 0L) hiSum - FMA64_POW16 else hiSum
                val hihi_sum = hihi_P + carry3
                if (hihi_sum == 0L) fmaRoundSplit(productSign, hi_sum, lo_sum, eC, false)
                else fmaRoundSplitThree(productSign, hihi_sum, hi_sum, lo_sum, eC)
            } else {
                // Different signs: determine which operand has larger magnitude.
                val pDominates = hihi_P > 0L || hi_P > 0L || lo_P >= sC
                if (pDominates) {
                    val loSub = lo_P - sC
                    val borrow1 = if (loSub < 0L) 1L else 0L
                    val lo_sum  = if (borrow1 > 0L) loSub + FMA64_POW16 else loSub
                    val hiSub   = hi_P - borrow1
                    val borrow2 = if (hiSub < 0L) 1L else 0L
                    val hi_sum  = if (borrow2 > 0L) hiSub + FMA64_POW16 else hiSub
                    val hihi_sum = hihi_P - borrow2
                    if (hihi_sum == 0L && hi_sum == 0L && lo_sum == 0L) return BidDouble.positiveZero
                    if (hihi_sum == 0L) fmaRoundSplit(productSign, hi_sum, lo_sum, eC, false)
                    else fmaRoundSplitThree(productSign, hihi_sum, hi_sum, lo_sum, eC)
                } else {
                    // Addend sC dominates: (0, 0, sC) − (0, 0, lo_P); hi_P must be 0.
                    fmaRoundSplit(cc.sign, 0L, sC - lo_P, eC, false)
                }
            }
        }

        // ── Product completely dominates (delta ≤ −17) ────────────────────────
        //    Addend at eP scale ≤ sC / 10^17 < 1 unit at eP; contributes only as sticky.
        else -> {
            val sticky = sC != 0L && (productSign == cc.sign)
            fmaRoundSplit(productSign, hiP, loP, eP, sticky)
        }
    }
}

private val bidDoubleFmaInstance: FusedMultiplyAdd<BidDouble> = object : FusedMultiplyAdd<BidDouble> {
    override fun fma(a: BidDouble, b: BidDouble, c: BidDouble): BidDouble = bidDoubleFma(a, b, c)
}

/** FMA for [BidDouble]: computes `a × b + c` with a single rounding step. */
val FusedMultiplyAdd.Companion.bidDouble: FusedMultiplyAdd<BidDouble>
    get() = bidDoubleFmaInstance
