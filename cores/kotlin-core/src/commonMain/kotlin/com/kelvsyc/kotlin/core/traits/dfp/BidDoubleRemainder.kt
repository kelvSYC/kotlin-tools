package com.kelvsyc.kotlin.core.traits.dfp
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointRemainder

import com.kelvsyc.kotlin.core.BidDouble
import com.kelvsyc.kotlin.core.bidDouble64Pack

// Powers of 10, indices 0..16. Index 16 = 10^16 (max needed for the rExp < yExp diff branch).
private val REM_POW10_64 = longArrayOf(
    1L, 10L, 100L, 1_000L, 10_000L, 100_000L, 1_000_000L, 10_000_000L, 100_000_000L,
    1_000_000_000L, 10_000_000_000L, 100_000_000_000L, 1_000_000_000_000L,
    10_000_000_000_000L, 100_000_000_000_000L, 1_000_000_000_000_000L, 10_000_000_000_000_000L
)

private fun BidDouble.canonRem(): BidDouble = with(BidDouble.encoding) { canonical() }

/**
 * Shared reduction kernel for BidDouble remainder operations.
 *
 * Mirrors [bidFloatRemKernel] with two key differences for 16-digit significands:
 * - Loop step limited to `min(rExp−yExp, 2)` to prevent rSig×10^k from overflowing Long
 *   (rSig ≤ 10^16−1; rSig×10^2 < 10^18 < Long.MAX; rSig×10^3 may overflow).
 * - Early return threshold raised to diff ≥ 17 (for 7-digit BidFloat it was 8).
 * - Overflow guard in the `rExp < yExp` branch: if ySig×10^diff overflows Long, scaledY > 2×rSig.
 */
private inline fun bidDoubleRemKernel(
    cx: BidDouble,
    cy: BidDouble,
    finalize: (rSig: Long, ref: Long, lastQ: Long, xSign: Boolean) -> Pair<Boolean, Long>
): BidDouble {
    val xSign = cx.sign
    val zeroResult = if (xSign) BidDouble.negativeZero else BidDouble.positiveZero
    var rSig = cx.significand
    var rExp = cx.biasedExponent

    var ySig = cy.significand
    var yExp = cy.biasedExponent
    while (ySig % 10L == 0L) { ySig /= 10L; yExp++ }

    val remSign: Boolean
    val remSig: Long
    val remExp: Int

    if (rExp >= yExp) {
        var lastQ = 0L
        while (rExp > yExp) {
            val k = minOf(rExp - yExp, 2)
            val scaled = rSig * REM_POW10_64[k]
            lastQ = scaled / ySig
            rSig = scaled % ySig
            rExp -= k
        }
        if (rSig >= ySig) {
            lastQ = rSig / ySig
            rSig %= ySig
        }
        val (rs, rv) = finalize(rSig, ySig, lastQ, xSign)
        remSign = rs; remSig = rv; remExp = yExp
    } else {
        val diff = yExp - rExp
        // diff ≥ 17: ySig×10^diff ≥ 10^17 > 2×rSig → |x| < |y|/2 → n = 0.
        if (diff >= 17) return cx
        // Overflow guard: if ySig×10^diff would exceed Long.MAX_VALUE, scaledY > 2×rSig anyway.
        if (ySig > Long.MAX_VALUE / REM_POW10_64[diff]) return cx
        val scaledY = ySig * REM_POW10_64[diff]
        val q: Long
        if (rSig >= scaledY) {
            q = rSig / scaledY
            rSig %= scaledY
        } else {
            q = 0L
        }
        val (rs, rv) = finalize(rSig, scaledY, q, xSign)
        remSign = rs; remSig = rv; remExp = rExp
    }

    if (remSig == 0L) return zeroResult
    val remSignBit = if (remSign) Long.MIN_VALUE else 0L
    return BidDouble(remSignBit or bidDouble64Pack(remExp, remSig))
}

private fun ieee754RemBidDouble(x: BidDouble, y: BidDouble): BidDouble {
    val cx = x.canonRem(); val cy = y.canonRem()
    if (cx.isNaN() || cy.isNaN()) return BidDouble.NaN
    if (cx.isInfinite() || cy.isZero()) return BidDouble.NaN
    if (cx.isZero() || cy.isInfinite()) return cx
    return bidDoubleRemKernel(cx, cy) { rSig, ref, lastQ, xSign ->
        val twoR = 2L * rSig
        when {
            twoR < ref -> xSign to rSig
            twoR > ref -> !xSign to (ref - rSig)
            else       -> if (lastQ % 2L == 0L) xSign to rSig else !xSign to (ref - rSig)
        }
    }
}

private fun truncatingRemBidDouble(x: BidDouble, y: BidDouble): BidDouble {
    val cx = x.canonRem(); val cy = y.canonRem()
    if (cx.isNaN() || cy.isNaN()) return BidDouble.NaN
    if (cx.isInfinite() || cy.isZero()) return BidDouble.NaN
    if (cx.isZero() || cy.isInfinite()) return cx
    return bidDoubleRemKernel(cx, cy) { rSig, _, _, xSign -> xSign to rSig }
}

private val bidDoubleIeee754Instance: FloatingPointRemainder<BidDouble> =
    object : FloatingPointRemainder<BidDouble> {
        override fun BidDouble.rem(other: BidDouble): BidDouble = ieee754RemBidDouble(this, other)
    }

private val bidDoubleTruncatingInstance: FloatingPointRemainder<BidDouble> =
    object : FloatingPointRemainder<BidDouble> {
        override fun BidDouble.rem(other: BidDouble): BidDouble = truncatingRemBidDouble(this, other)
    }

/** IEEE 754-2008 §5.3.1 remainder for [BidDouble]: `x − round-half-even(x/y) × y`. */
val FloatingPointRemainder.Companion.bidDoubleIeee754: FloatingPointRemainder<BidDouble>
    get() = bidDoubleIeee754Instance

/** Truncating remainder for [BidDouble]: `x − trunc(x/y) × y`. Result has the sign of `x`. */
val FloatingPointRemainder.Companion.bidDoubleTruncating: FloatingPointRemainder<BidDouble>
    get() = bidDoubleTruncatingInstance
