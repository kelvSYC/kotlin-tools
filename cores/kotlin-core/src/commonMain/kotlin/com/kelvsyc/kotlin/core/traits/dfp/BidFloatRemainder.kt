package com.kelvsyc.kotlin.core.traits.dfp
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointRemainder

import com.kelvsyc.kotlin.core.BidFloat
import com.kelvsyc.kotlin.core.bidFloat32Pack

// Powers of 10, indices 0..7. Index 7 is needed for the rExp < yExp branch (diff up to 7).
// Max intermediate in the main loop: 9_999_999 × 10^6 ≈ 10^13 < Long.MAX_VALUE ✓
private val REM_POW10 = longArrayOf(
    1L, 10L, 100L, 1_000L, 10_000L, 100_000L, 1_000_000L, 10_000_000L
)

private fun BidFloat.canonRem(): BidFloat = with(BidFloat.encoding) { canonical() }

/**
 * Shared reduction kernel for BidFloat remainder operations.
 *
 * Reduces `|x| mod |y|` exactly using Long arithmetic, then calls [finalize] with the reduced
 * significand, the reference value it was reduced against, the last partial quotient (for
 * tie-breaking), and x's sign. [finalize] returns `(resultSign, resultSignificand)`.
 *
 * [finalize] semantics:
 * - **IEEE 754**: inspect `twoR vs ref`; use `lastQ` parity for the tie.
 * - **Truncating**: ignore all three — always return `(xSign, rSig)`.
 *
 * Special-value handling is the caller's responsibility; this function assumes both operands
 * are canonical, finite, and non-zero.
 */
private inline fun bidFloatRemKernel(
    cx: BidFloat,
    cy: BidFloat,
    finalize: (rSig: Long, ref: Long, lastQ: Long, xSign: Boolean) -> Pair<Boolean, Long>
): BidFloat {
    val xSign = cx.sign
    val zeroResult = if (xSign) BidFloat.negativeZero else BidFloat.positiveZero
    var rSig = cx.significand.toLong()
    var rExp = cx.biasedExponent

    // Normalize y: strip trailing decimal zeros to minimize loop iterations.
    // yExp may exceed 191 after stripping, but only when yExp > rExp (else branch),
    // so the result is never packed with an out-of-range exponent.
    var ySig = cy.significand.toLong()
    var yExp = cy.biasedExponent
    while (ySig % 10L == 0L) { ySig /= 10L; yExp++ }

    val remSign: Boolean
    val remSig: Long
    val remExp: Int

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
        val (rs, rv) = finalize(rSig, ySig, lastQ, xSign)
        remSign = rs; remSig = rv; remExp = yExp
    } else {
        // x has a finer quantum than y. Result exponent is rExp (x's quantum).
        val diff = yExp - rExp
        // diff ≥ 8: 2·rSig ≤ 2·9_999_999 < 10^8 ≤ ySig·10^diff → |x| < |y|/2 → n = 0.
        if (diff >= 8) return cx
        val scaledY = ySig * REM_POW10[diff]
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
    val remSignBit = if (remSign) Int.MIN_VALUE else 0
    return BidFloat(remSignBit or bidFloat32Pack(remExp, remSig.toInt()))
}

/**
 * IEEE 754-2008 §5.3.1 remainder: `x − n×y` where `n = round-half-even(x/y)`.
 *
 * The result lies in `[−|y|/2, +|y|/2]` and is always exact (no rounding occurs).
 * Tie-breaking uses only the last partial quotient's parity; earlier quotients are
 * multiplied by even powers of 10 and do not affect the result's parity.
 */
private fun ieee754RemBidFloat(x: BidFloat, y: BidFloat): BidFloat {
    val cx = x.canonRem(); val cy = y.canonRem()
    if (cx.isNaN() || cy.isNaN()) return BidFloat.NaN
    if (cx.isInfinite() || cy.isZero()) return BidFloat.NaN
    if (cx.isZero() || cy.isInfinite()) return cx
    return bidFloatRemKernel(cx, cy) { rSig, ref, lastQ, xSign ->
        val twoR = 2L * rSig
        when {
            twoR < ref -> xSign to rSig
            twoR > ref -> !xSign to (ref - rSig)
            else       -> if (lastQ % 2L == 0L) xSign to rSig else !xSign to (ref - rSig)
        }
    }
}

/**
 * Truncating remainder: `x − n×y` where `n = trunc(x/y)` (truncated toward zero).
 *
 * The result always has the same sign as `x` (or is zero). This is the decimal analogue
 * of Kotlin's `%` operator on [Int]/[Long]. The operation is exact.
 */
private fun truncatingRemBidFloat(x: BidFloat, y: BidFloat): BidFloat {
    val cx = x.canonRem(); val cy = y.canonRem()
    if (cx.isNaN() || cy.isNaN()) return BidFloat.NaN
    if (cx.isInfinite() || cy.isZero()) return BidFloat.NaN
    if (cx.isZero() || cy.isInfinite()) return cx
    return bidFloatRemKernel(cx, cy) { rSig, _, _, xSign -> xSign to rSig }
}

private val bidFloatIeee754Instance: FloatingPointRemainder<BidFloat> =
    object : FloatingPointRemainder<BidFloat> {
        override fun BidFloat.rem(other: BidFloat): BidFloat = ieee754RemBidFloat(this, other)
    }

private val bidFloatTruncatingInstance: FloatingPointRemainder<BidFloat> =
    object : FloatingPointRemainder<BidFloat> {
        override fun BidFloat.rem(other: BidFloat): BidFloat = truncatingRemBidFloat(this, other)
    }

/** IEEE 754-2008 §5.3.1 remainder for [BidFloat]: `x − round-half-even(x/y) × y`. */
val FloatingPointRemainder.Companion.bidFloatIeee754: FloatingPointRemainder<BidFloat>
    get() = bidFloatIeee754Instance

/** Truncating remainder for [BidFloat]: `x − trunc(x/y) × y`. Result has the sign of `x`. */
val FloatingPointRemainder.Companion.bidFloatTruncating: FloatingPointRemainder<BidFloat>
    get() = bidFloatTruncatingInstance
