package com.kelvsyc.kotlin.core.traits.dfp
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointSquareRoot

import com.kelvsyc.kotlin.core.BidDouble
import com.kelvsyc.kotlin.core.bidDouble64Pack

// Powers of 10 indexed 0..14; used to compute N_hi = nSig × 10^(k−16) in nSplit.
private val SQRT_POW10 = longArrayOf(
    1L, 10L, 100L, 1_000L, 10_000L, 100_000L, 1_000_000L,
    10_000_000L, 100_000_000L, 1_000_000_000L, 10_000_000_000L,
    100_000_000_000L, 1_000_000_000_000L, 10_000_000_000_000L, 100_000_000_000_000L
)

// Powers of 10 as Double, indexed 0..30; used to form the initial Double estimate.
private val SQRT_D_POW10 = doubleArrayOf(
    1.0, 1e1, 1e2, 1e3, 1e4, 1e5, 1e6, 1e7, 1e8, 1e9, 1e10,
    1e11, 1e12, 1e13, 1e14, 1e15, 1e16, 1e17, 1e18, 1e19, 1e20,
    1e21, 1e22, 1e23, 1e24, 1e25, 1e26, 1e27, 1e28, 1e29, 1e30
)

private const val POW8 = 100_000_000L
private const val POW16 = 10_000_000_000_000_000L

private fun decimalDigits(n: Long): Int = when {
    n >= 10_000_000_000_000_000L -> 17
    n >= 1_000_000_000_000_000L -> 16
    n >= 100_000_000_000_000L -> 15
    n >= 10_000_000_000_000L -> 14
    n >= 1_000_000_000_000L -> 13
    n >= 100_000_000_000L -> 12
    n >= 10_000_000_000L -> 11
    n >= 1_000_000_000L -> 10
    n >= 100_000_000L -> 9
    n >= 10_000_000L -> 8
    n >= 1_000_000L -> 7
    n >= 100_000L -> 6
    n >= 10_000L -> 5
    n >= 1_000L -> 4
    n >= 100L -> 3
    n >= 10L -> 2
    else -> 1
}

/**
 * Returns x² as a split pair (hi, lo) satisfying x² = hi×10^16 + lo.
 *
 * Uses x = a×10^8 + b decomposition. All intermediate products fit in Long:
 * - a < 10^8, b < 10^8 → a×b < 10^16 ✓; cross = 2ab < 2×10^16 ✓
 * - cross_lo×10^8 < 10^16 ✓; b² < 10^16 ✓; their sum < 2×10^16 ✓
 */
private fun xsqSplit(x: Long): Pair<Long, Long> {
    val a = x / POW8
    val b = x % POW8
    val cross = 2L * a * b
    val crossHi = cross / POW8
    val crossLo = cross % POW8
    val loTerm = crossLo * POW8 + b * b
    val loRes = loTerm % POW16
    val carry = loTerm / POW16
    val hiRes = a * a + crossHi + carry
    return hiRes to loRes
}

/**
 * Returns N = nSig×10^k as a split pair (hi, lo) satisfying N = hi×10^16 + lo.
 *
 * For k ≥ 16: N_hi = nSig×10^(k−16), N_lo = 0. Values fit in Long because nSig is small when k is large.
 * For k = 14: nSig has 17 digits; N_hi = nSig/100, N_lo = (nSig%100)×10^14.
 */
private fun nSplit(nSig: Long, k: Int): Pair<Long, Long> =
    if (k >= 16) nSig * SQRT_POW10[k - 16] to 0L
    else nSig / 100L to (nSig % 100L) * SQRT_POW10[14]

/** Returns negative, zero, or positive as N < x², N = x², or N > x², given N = (nHi, nLo). */
private fun cmpNxsq(nHi: Long, nLo: Long, x: Long): Int {
    val (sqHi, sqLo) = xsqSplit(x)
    return if (nHi != sqHi) nHi.compareTo(sqHi) else nLo.compareTo(sqLo)
}

/** Returns N − I² as a Long. Valid only when 0 ≤ N − I² < 2×10^16 (i.e. rem < 2I). */
private fun remNxsq(nHi: Long, nLo: Long, I: Long): Long {
    val (sqHi, sqLo) = xsqSplit(I)
    val diffHi = nHi - sqHi
    return if (diffHi == 0L) nLo - sqLo else POW16 + nLo - sqLo
}

/** Returns floor(sqrt(N)) via a Double estimate corrected by integer steps. */
private fun isqrt64(nHi: Long, nLo: Long, nSig: Long, k: Int): Long {
    var x = kotlin.math.sqrt(nSig.toDouble() * SQRT_D_POW10[k]).toLong()
    while (x > 0L && cmpNxsq(nHi, nLo, x) < 0) x--
    while (cmpNxsq(nHi, nLo, x + 1L) >= 0) x++
    return x
}

/**
 * Correctly-rounded IEEE 754 square root for [BidDouble].
 *
 * ## Algorithm
 *
 * Mirrors [BidFloatSquareRoot] but operates on 16-digit significands. Scaled values N = sig×10^k
 * reach up to ~10^31, overflowing Long; they are represented as split pairs (hi×10^16 + lo) using
 * [nSplit], and x² is computed in the same split form via [xsqSplit].
 *
 * The scale factor k (even, range 14–30) is chosen so that isqrt(N) is exactly a 16-digit integer.
 * The rounding step is identical: no halfway case exists for integer inputs, so round up iff rem > I.
 */
private fun bidDoubleSqrt(x: BidDouble): BidDouble {
    val cx = with(BidDouble.encoding) { x.canonical() }
    if (cx.isNaN() || (cx.sign && !cx.isZero())) return BidDouble.NaN
    if (cx.isZero()) return cx
    if (cx.isInfinite()) return BidDouble.positiveInfinity

    var s = cx.significand
    val eOrig = cx.biasedExponent - 398

    val eAdj: Int
    if (eOrig % 2 != 0) {
        s *= 10L
        eAdj = eOrig - 1
    } else {
        eAdj = eOrig
    }

    // Choose even k so N = s×10^k has 31–32 digits → isqrt gives 16 digits.
    val d = decimalDigits(s)
    val k = when {
        d <= 2  -> 30
        d <= 4  -> 28
        d <= 6  -> 26
        d <= 8  -> 24
        d <= 10 -> 22
        d <= 12 -> 20
        d <= 14 -> 18
        d <= 16 -> 16
        else    -> 14
    }

    val (nHi, nLo) = nSplit(s, k)
    val I = isqrt64(nHi, nLo, s, k)
    val rem = remNxsq(nHi, nLo, I)
    val sig = if (rem > I) I + 1L else I

    val resultBiasedExp = eAdj / 2 - k / 2 + 398
    return BidDouble(bidDouble64Pack(resultBiasedExp, sig))
}

private val bidDoubleSqrtInstance: FloatingPointSquareRoot<BidDouble> =
    object : FloatingPointSquareRoot<BidDouble> {
        override fun BidDouble.sqrt(): BidDouble = bidDoubleSqrt(this)
    }

/**
 * Correctly-rounded IEEE 754 square root for [BidDouble].
 *
 * Returns `NaN` for negative inputs (including −∞), `±0` for `±0`, `+∞` for `+∞`, and the
 * nearest [BidDouble] to the true mathematical square root for all other positive finite inputs.
 */
val FloatingPointSquareRoot.Companion.bidDouble: FloatingPointSquareRoot<BidDouble>
    get() = bidDoubleSqrtInstance
