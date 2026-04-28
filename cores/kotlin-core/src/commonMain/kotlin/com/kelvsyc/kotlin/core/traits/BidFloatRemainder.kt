package com.kelvsyc.kotlin.core.traits

import com.kelvsyc.kotlin.core.BidFloat

// Powers of 10, indices 0..7. Index 7 is needed for the rExp < yExp branch (diff up to 7).
// Max intermediate in the main loop: 9_999_999 × 10^6 ≈ 10^13 < Long.MAX_VALUE ✓
private val REM_POW10 = longArrayOf(
    1L, 10L, 100L, 1_000L, 10_000L, 100_000L, 1_000_000L, 10_000_000L
)

private fun bidRemPackLong(biasedExp: Int, sig: Long): Int {
    val s = sig.toInt()
    return if (s < 0x800000) {
        ((biasedExp shl 3) or (s ushr 20)) shl 20 or (s and 0xFFFFF)
    } else {
        val combination = 0x600 or (biasedExp shl 1) or ((s ushr 20) and 1)
        (combination shl 20) or (s and 0x1FFFFF)
    }
}

private fun BidFloat.canonRem(): BidFloat = with(BidFloat.encoding) { canonical() }

/**
 * IEEE 754-2008 §5.3.1 remainder: `x − n×y` where `n = round-half-even(x/y)`.
 *
 * The result lies in `[−|y|/2, +|y|/2]` and is always exact (no rounding occurs).
 *
 * Algorithm: iterative modular reduction using Long arithmetic. At each step rSig is scaled
 * by at most 10^6 before taking mod ySig, keeping intermediates below Long.MAX_VALUE.
 * After the loop rSig ∈ [0, ySig); a half-comparison then determines whether to keep or
 * subtract one ulp of y. The parity of the accumulated quotient (needed for tie-breaking)
 * is derived from the last reduction step's quotient, relying on the fact that all earlier
 * partial quotients are multiplied by even powers of 10.
 */
private fun ieee754RemBidFloat(x: BidFloat, y: BidFloat): BidFloat {
    val cx = x.canonRem(); val cy = y.canonRem()
    if (cx.isNaN() || cy.isNaN()) return BidFloat.NaN
    if (cx.isInfinite() || cy.isZero()) return BidFloat.NaN
    if (cx.isZero() || cy.isInfinite()) return cx

    val xSign = cx.sign
    val zeroResult = if (xSign) BidFloat.negativeZero else BidFloat.positiveZero
    var rSig = cx.significand.toLong()
    var rExp = cx.biasedExponent

    // Normalize y: strip trailing decimal zeros to minimize loop iterations.
    // yExp may exceed 191 after stripping, but that only happens when yExp > rExp (else branch),
    // so the result is never packed with an out-of-range yExp.
    var ySig = cy.significand.toLong()
    var yExp = cy.biasedExponent
    while (ySig % 10L == 0L) { ySig /= 10L; yExp++ }

    if (rExp >= yExp) {
        var lastQ = 0L
        while (rExp > yExp) {
            val k = minOf(rExp - yExp, 6)
            val scaled = rSig * REM_POW10[k]
            lastQ = scaled / ySig
            rSig = scaled % ySig
            rExp -= k
        }
        // rExp == yExp. The loop always leaves rSig < ySig; the extra step below only fires
        // when the loop was skipped entirely (rExp == yExp from the start, rSig may be ≥ ySig).
        if (rSig >= ySig) {
            lastQ = rSig / ySig
            rSig %= ySig
        }

        val twoR = 2L * rSig
        val (remSign, remSig) = when {
            twoR < ySig -> xSign to rSig
            twoR > ySig -> !xSign to (ySig - rSig)
            // Tie: round to even. All prior partial quotients were multiplied by even powers of 10,
            // so only the last partial quotient's parity determines n_total mod 2.
            else        -> if (lastQ % 2L == 0L) xSign to rSig else !xSign to (ySig - rSig)
        }
        if (remSig == 0L) return zeroResult
        val remSignBit = if (remSign) Int.MIN_VALUE else 0
        return BidFloat(remSignBit or bidRemPackLong(yExp, remSig))
    } else {
        // x has a finer quantum than y. The result exponent is rExp (x's quantum).
        val diff = yExp - rExp
        // diff ≥ 8: 2·rSig ≤ 2·9_999_999 < 10^8 ≤ ySig·10^diff → |x| < |y|/2 → n = 0.
        if (diff >= 8) return cx
        val scaledY = ySig * REM_POW10[diff]
        // If |x| ≥ |y| (can happen despite rExp < yExp when xSig is large), reduce once.
        val q: Long
        if (rSig >= scaledY) {
            q = rSig / scaledY
            rSig %= scaledY
        } else {
            q = 0L
        }
        // rSig ∈ [0, scaledY), represented at quantum 10^rExp.
        val twoR = 2L * rSig
        val (remSign, remSig) = when {
            twoR < scaledY -> xSign to rSig
            twoR > scaledY -> !xSign to (scaledY - rSig)
            else           -> if (q % 2L == 0L) xSign to rSig else !xSign to (scaledY - rSig)
        }
        if (remSig == 0L) return zeroResult
        val remSignBit = if (remSign) Int.MIN_VALUE else 0
        return BidFloat(remSignBit or bidRemPackLong(rExp, remSig))
    }
}

private val bidFloatIeee754Instance: FloatingPointRemainder<BidFloat> =
    object : FloatingPointRemainder<BidFloat> {
        override fun BidFloat.rem(other: BidFloat): BidFloat = ieee754RemBidFloat(this, other)
    }

/** IEEE 754-2008 §5.3.1 remainder for [BidFloat]. */
val FloatingPointRemainder.Companion.bidFloatIeee754: FloatingPointRemainder<BidFloat>
    get() = bidFloatIeee754Instance
