package com.kelvsyc.kotlin.core.traits

import com.kelvsyc.kotlin.core.BidFloat
import com.kelvsyc.kotlin.core.bidFloat32Pack

// Powers of 10, indices 0..12. Max needed is 10^12 (k=12, used when the significand has 1-2 digits).
// All values fit in Long (10^12 < Long.MAX_VALUE ≈ 9.2×10^18).
private val SQRT_POW10 = longArrayOf(
    1L, 10L, 100L, 1_000L, 10_000L, 100_000L, 1_000_000L,
    10_000_000L, 100_000_000L, 1_000_000_000L, 10_000_000_000L,
    100_000_000_000L, 1_000_000_000_000L
)

private fun decimalDigits(n: Long): Int = when {
    n >= 10_000_000L -> 8  // after odd-exponent adjustment, max significand is 9×10^7
    n >= 1_000_000L  -> 7
    n >= 100_000L    -> 6
    n >= 10_000L     -> 5
    n >= 1_000L      -> 4
    n >= 100L        -> 3
    n >= 10L         -> 2
    else             -> 1
}

/**
 * Returns floor(sqrt(n)) via an initial Double estimate plus at most two integer correction steps.
 *
 * For n ≤ 10^14 the Double estimate is within 1 of the true integer sqrt, so the loops run at
 * most once each. No overflow: max correction checks (x+1)^2 ≤ (10^7+1)^2 ≈ 10^14 << Long.MAX_VALUE.
 */
private fun isqrt(n: Long): Long {
    var x = kotlin.math.sqrt(n.toDouble()).toLong()
    while (x > 0L && x * x > n) x--
    while ((x + 1L) * (x + 1L) <= n) x++
    return x
}

/**
 * Correctly-rounded IEEE 754 square root for [BidFloat].
 *
 * ## Algorithm
 *
 * A BidFloat encodes the value `sig × 10^(biasedExp − 101)`.  The sqrt is reduced to an integer
 * problem by scaling the significand so the integer square root produces a 7-digit result:
 *
 * 1. **Canonicalize** the input and handle special values (NaN, ±0, +∞, negative → NaN).
 * 2. **Make the unbiased exponent even** — if it is odd, replace `(sig, e)` with `(10·sig, e−1)`
 *    (both represent the same value; sig grows to at most 99,999,990).
 * 3. **Choose scale factor k** (always even, 6–12) such that `sig × 10^k` has 13–14 digits.
 *    `isqrt(sig × 10^k)` is then exactly a 7-digit integer in [1,000,000, 9,999,999].
 * 4. **Compute `I = isqrt(sig × 10^k)`** and `rem = sig×10^k − I²`.
 * 5. **Round correctly** — since `(I + ½)²` is never an integer, there is no halfway case for
 *    square root.  Round up iff `rem > I`; otherwise keep `I`.
 * 6. **Reconstruct** the result: significand = rounded `I`, biased exponent = `eAdj/2 − k/2 + 101`.
 *
 * All intermediate arithmetic fits in `Long` (max scaled value ≈ 10^14 << Long.MAX_VALUE).
 *
 * ## BidDouble adaptation
 *
 * For decimal64 (16-digit significand, p = 16), replace: the SQRT_POW10 table (needs 10^32,
 * exceeding Long — use `BigInteger` or unsigned 128-bit arithmetic), the `decimalDigits` range
 * (1–17 digits after odd-exponent adjustment), the scale table (k ∈ {6, 8, …, 32−d}), and the
 * pack function.  The algorithm structure is otherwise identical.
 */
private fun bidFloatSqrt(x: BidFloat): BidFloat {
    val cx = with(BidFloat.encoding) { x.canonical() }
    // Negative non-zero (including -∞) → NaN; -0 passes through.
    if (cx.isNaN() || (cx.sign && !cx.isZero())) return BidFloat.NaN
    if (cx.isZero()) return cx                     // ±0 → ±0
    if (cx.isInfinite()) return BidFloat.positiveInfinity

    var s = cx.significand.toLong()
    val eOrig = cx.biasedExponent - 101            // unbiased exponent, range [−101, +90]

    // Step 2: make the unbiased exponent even.
    val eAdj: Int
    if (eOrig % 2 != 0) {                         // Kotlin % preserves sign; ≠ 0 catches odd negatives
        s *= 10L                                   // s ≤ 9,999,999 × 10 = 99,999,990
        eAdj = eOrig - 1
    } else {
        eAdj = eOrig
    }

    // Step 3: choose even k so that sig × 10^k has 13–14 digits → isqrt gives 7 digits.
    //   d=1,2 → k=12 (scaled: 13–14 digits)
    //   d=3,4 → k=10 (scaled: 13–14 digits)
    //   d=5,6 → k= 8 (scaled: 13–14 digits)
    //   d=7,8 → k= 6 (scaled: 13–14 digits)
    val d = decimalDigits(s)
    val k = when {
        d <= 2 -> 12
        d <= 4 -> 10
        d <= 6 -> 8
        else   -> 6
    }

    // Step 4: integer sqrt.
    val scaled = s * SQRT_POW10[k]
    val I = isqrt(scaled)

    // Step 5: correct rounding — no halfway case for integer inputs, so no tie-to-even needed.
    val rem = scaled - I * I
    val sig = if (rem > I) I + 1L else I

    // Step 6: reconstruct BidFloat.
    // result unbiased exp = eAdj/2 − k/2; re-bias by adding 101.
    val resultBiasedExp = eAdj / 2 - k / 2 + 101
    return BidFloat(bidFloat32Pack(resultBiasedExp, sig.toInt()))
}

private val bidFloatSqrtInstance: FloatingPointSquareRoot<BidFloat> =
    object : FloatingPointSquareRoot<BidFloat> {
        override fun BidFloat.sqrt(): BidFloat = bidFloatSqrt(this)
    }

/**
 * Correctly-rounded IEEE 754 square root for [BidFloat].
 *
 * Returns `NaN` for negative inputs (including −∞), `±0` for `±0`, `+∞` for `+∞`, and the
 * nearest [BidFloat] to the true mathematical square root for all other positive finite inputs.
 */
val FloatingPointSquareRoot.Companion.bidFloat: FloatingPointSquareRoot<BidFloat>
    get() = bidFloatSqrtInstance
