package com.kelvsyc.kotlin.core.traits

import com.kelvsyc.kotlin.core.BidDouble
import com.kelvsyc.kotlin.core.bidDouble64Pack

// Powers of 10, indices 0..18. Index 18 = 10^18 (used as divisor in 16-digit quotient rounding).
private val POW10_64 = longArrayOf(
    1L, 10L, 100L, 1_000L, 10_000L, 100_000L, 1_000_000L,
    10_000_000L, 100_000_000L, 1_000_000_000L, 10_000_000_000L,
    100_000_000_000L, 1_000_000_000_000L, 10_000_000_000_000L,
    100_000_000_000_000L, 1_000_000_000_000_000L, 10_000_000_000_000_000L,
    100_000_000_000_000_000L, 1_000_000_000_000_000_000L
)

private const val POW10_16 = 10_000_000_000_000_000L  // 10^16

private fun decimalDigits64(n: Long): Int = when {
    n >= 100_000_000_000_000_000L -> 18
    n >= 10_000_000_000_000_000L  -> 17
    n >= 1_000_000_000_000_000L   -> 16
    n >= 100_000_000_000_000L     -> 15
    n >= 10_000_000_000_000L      -> 14
    n >= 1_000_000_000_000L       -> 13
    n >= 100_000_000_000L         -> 12
    n >= 10_000_000_000L          -> 11
    n >= 1_000_000_000L           -> 10
    n >= 100_000_000L             -> 9
    n >= 10_000_000L              -> 8
    n >= 1_000_000L               -> 7
    n >= 100_000L                 -> 6
    n >= 10_000L                  -> 5
    n >= 1_000L                   -> 4
    n >= 100L                     -> 3
    n >= 10L                      -> 2
    else                          -> 1
}

private fun roundHalfEven(trunc: Long, rem: Long, div: Long): Long {
    val half = div / 2L
    return when {
        rem > half -> trunc + 1L
        rem < half -> trunc
        else       -> if (trunc % 2L == 0L) trunc else trunc + 1L
    }
}

private fun roundHalfEvenSticky(trunc: Long, rem: Long, div: Long, sticky: Boolean): Long {
    val half = div / 2L
    return when {
        rem > half -> trunc + 1L
        rem < half -> trunc
        sticky     -> trunc + 1L
        else       -> if (trunc % 2L == 0L) trunc else trunc + 1L
    }
}

/**
 * Returns the product `a × b` as a split pair `(hi, lo)` satisfying `a × b = hi × 10^16 + lo`.
 *
 * Uses `a = aHi × 10^8 + aLo` decomposition. All intermediate products fit in Long:
 * - aHi, bHi < 10^8 → hiHi = aHi × bHi < 10^16 ✓
 * - cross = aHi × bLo + aLo × bHi < 2 × 10^16 ✓
 * - loLo = aLo × bLo < 10^16 ✓
 * - lo_total = crossLo × 10^8 + loLo < 2 × 10^16 ✓
 */
private fun mul64Decimal(a: Long, b: Long): Pair<Long, Long> {
    val aHi = a / 100_000_000L; val aLo = a % 100_000_000L
    val bHi = b / 100_000_000L; val bLo = b % 100_000_000L
    val hiHi = aHi * bHi
    val cross = aHi * bLo + aLo * bHi
    val loLo = aLo * bLo
    val crossHi = cross / 100_000_000L
    val crossLo = (cross % 100_000_000L) * 100_000_000L
    val loTotal = crossLo + loLo
    val carry = loTotal / POW10_16
    val loFinal = loTotal % POW10_16
    return (hiHi + crossHi + carry) to loFinal
}

private fun BidDouble.canon64(): BidDouble = with(BidDouble.encoding) { canonical() }

/**
 * Rounds and packs a significand (up to 18 digits) at [biasedExp] into a [BidDouble].
 *
 * Strips excess digits using round-half-to-even, then handles overflow (→ ±∞) and
 * underflow (→ ±0 after rounding toward zero) per IEEE 754-2008 §5.3.
 */
private fun roundToDecimal64(sign: Boolean, sig: Long, biasedExp: Int): BidDouble {
    val signBit = if (sign) Long.MIN_VALUE else 0L
    if (sig == 0L) return BidDouble(signBit)
    var s = sig; var e = biasedExp
    val digits = decimalDigits64(s)
    if (digits > 16) {
        val excess = digits - 16
        val divisor = POW10_64[excess]
        s = roundHalfEven(s / divisor, s % divisor, divisor)
        e += excess
        if (s >= POW10_16) { s /= 10L; e++ }
    }
    if (s == 0L) return BidDouble(signBit)
    if (e > 767) return BidDouble(signBit or 0x7800_0000_0000_0000L)
    if (e < 0) {
        val shift = -e
        if (shift >= 17) return BidDouble(signBit)
        val divisor = POW10_64[shift]
        s = roundHalfEven(s / divisor, s % divisor, divisor)
        e = 0
        if (s == 0L) return BidDouble(signBit)
    }
    return BidDouble(signBit or bidDouble64Pack(e, s))
}

/**
 * Rounds the value `hi × 10^16 + lo` at [biasedExp] to 16 significant digits.
 *
 * [hi] and [lo] must each be < 10^16. [biasedExp] is the quantum exponent for the `lo` part.
 * The high part [hi] contributes digits at exponent `biasedExp + 16`, so the combined value has
 * up to 32 decimal digits; this function extracts and correctly rounds the top 16.
 */
private fun roundSplit(sign: Boolean, hi: Long, lo: Long, biasedExp: Int): BidDouble {
    if (hi == 0L && lo == 0L) return if (sign) BidDouble.negativeZero else BidDouble.positiveZero
    if (hi == 0L) return roundToDecimal64(sign, lo, biasedExp)
    val d = decimalDigits64(hi)
    val trunc = hi * POW10_64[16 - d] + lo / POW10_64[d]
    val rem = lo % POW10_64[d]
    var sig = roundHalfEven(trunc, rem, POW10_64[d])
    var exp = biasedExp + d
    if (sig >= POW10_16) { sig /= 10L; exp++ }
    return roundToDecimal64(sign, sig, exp)
}

private val bidDoubleArithmeticInstance: FloatingPointArithmetic<BidDouble> =
    object : FloatingPointArithmetic<BidDouble> {
        override val zero: BidDouble get() = BidDouble.positiveZero
        // 1 × 10^0: biasedExp=398, sig=1 → bidDouble64Pack(398, 1L) = 0x31C0_0000_0000_0001L
        override val one: BidDouble get() = BidDouble(0x31C0_0000_0000_0001L)

        override fun BidDouble.isNaN(): Boolean = this.isNaN()
        override fun BidDouble.isInfinite(): Boolean = this.isInfinite()
        override fun BidDouble.isFinite(): Boolean = !this.isNaN() && !this.isInfinite()
        override fun BidDouble.isZero(): Boolean = this.isZero()
        override fun BidDouble.isInteger(): Boolean = this.isInteger()
        override fun BidDouble.isNegative(): Boolean = bits < 0L
        override fun BidDouble.negate(): BidDouble = BidDouble(bits xor Long.MIN_VALUE)
        override fun BidDouble.abs(): BidDouble = BidDouble(bits and Long.MAX_VALUE)
        override fun BidDouble.copySign(other: BidDouble): BidDouble =
            BidDouble((bits and Long.MAX_VALUE) or (other.bits and Long.MIN_VALUE))

        override fun BidDouble.add(other: BidDouble): BidDouble {
            val a = canon64(); val b = other.canon64()
            if (a.isNaN() || b.isNaN()) return BidDouble.NaN
            if (a.isInfinite() && b.isInfinite()) return if (a.sign == b.sign) a else BidDouble.NaN
            if (a.isInfinite()) return a
            if (b.isInfinite()) return b
            if (a.isZero() && b.isZero()) return if (a.sign && b.sign) BidDouble.negativeZero else BidDouble.positiveZero
            if (a.isZero()) return b
            if (b.isZero()) return a

            val (larger, smaller) = if (a.biasedExponent >= b.biasedExponent) a to b else b to a
            val diff = larger.biasedExponent - smaller.biasedExponent
            // diff ≥ 17: |smaller| < 0.5 ULP of |larger| at 16-digit precision; result is larger unchanged.
            if (diff >= 17) return larger

            val sigL = larger.significand
            val sigS = smaller.significand
            // Represent sigL × 10^diff as (s1Hi × 10^16 + s1Lo).
            val k = 16 - diff
            val s1Hi = sigL / POW10_64[k]
            val s1Lo = (sigL % POW10_64[k]) * POW10_64[diff]

            val absHi: Long
            val absLo: Long
            val resultSign: Boolean

            if (larger.sign == smaller.sign) {
                resultSign = larger.sign
                val sumLo = s1Lo + sigS
                val carry = sumLo / POW10_16
                absHi = s1Hi + carry
                absLo = sumLo % POW10_16
            } else {
                val rawLo = s1Lo - sigS
                when {
                    rawLo >= 0L  -> { absHi = s1Hi;       absLo = rawLo;           resultSign = larger.sign  }
                    s1Hi > 0L    -> { absHi = s1Hi - 1L;  absLo = rawLo + POW10_16; resultSign = larger.sign  }
                    else         -> { absHi = 0L;          absLo = -rawLo;           resultSign = !larger.sign }
                }
            }

            if (absHi == 0L && absLo == 0L) return BidDouble.positiveZero
            return roundSplit(resultSign, absHi, absLo, smaller.biasedExponent)
        }

        override fun BidDouble.subtract(other: BidDouble): BidDouble =
            add(BidDouble(other.bits xor Long.MIN_VALUE))

        override fun BidDouble.multiply(other: BidDouble): BidDouble {
            val a = canon64(); val b = other.canon64()
            if (a.isNaN() || b.isNaN()) return BidDouble.NaN
            val resultSign = a.sign xor b.sign
            val signBit = if (resultSign) Long.MIN_VALUE else 0L
            if ((a.isInfinite() && b.isZero()) || (a.isZero() && b.isInfinite())) return BidDouble.NaN
            if (a.isInfinite() || b.isInfinite()) return BidDouble(signBit or 0x7800_0000_0000_0000L)
            if (a.isZero() || b.isZero()) return BidDouble(signBit)

            val (hi, lo) = mul64Decimal(a.significand, b.significand)
            // Unbiased product exponent = (expA − 398) + (expB − 398); re-bias: add 398.
            val biasedExpProduct = a.biasedExponent + b.biasedExponent - 398
            return roundSplit(resultSign, hi, lo, biasedExpProduct)
        }

        override fun BidDouble.divide(other: BidDouble): BidDouble {
            val a = canon64(); val b = other.canon64()
            if (a.isNaN() || b.isNaN()) return BidDouble.NaN
            val resultSign = a.sign xor b.sign
            val signBit = if (resultSign) Long.MIN_VALUE else 0L

            if (b.isZero()) return if (a.isZero()) BidDouble.NaN else BidDouble(signBit or 0x7800_0000_0000_0000L)
            if (a.isInfinite() && b.isInfinite()) return BidDouble.NaN
            if (a.isInfinite()) return BidDouble(signBit or 0x7800_0000_0000_0000L)
            if (b.isInfinite() || a.isZero()) return BidDouble(signBit)

            val sigA = a.significand
            val sigB = b.significand
            val sigBuL = sigB.toULong()

            // Compute q0 (integer quotient), then extend 18 fractional digits via 6 × ×1000 steps.
            // ULong avoids overflow: rem < sigB ≤ 10^16, rem × 1000 ≤ ~10^19 > Long.MAX.
            var q0 = sigA / sigB
            var rem = sigA % sigB
            var fracQ = 0L
            repeat(6) {
                val scaled = rem.toULong() * 1000UL
                val qi = (scaled / sigBuL).toLong()
                rem = (scaled % sigBuL).toLong()
                fracQ = fracQ * 1000L + qi
            }
            val divRem = rem

            // biasedExpBase is the biased exponent for fracQ's unit (10^−18 relative to q0's position).
            val biasedExpBase = a.biasedExponent - b.biasedExponent + 380

            var s: Long
            var e: Int

            if (q0 == 0L) {
                if (fracQ == 0L) return BidDouble(signBit)
                val dFrac = decimalDigits64(fracQ)
                if (dFrac <= 16) {
                    s = fracQ; e = biasedExpBase
                } else {
                    val excess = dFrac - 16
                    val div = POW10_64[excess]
                    s = roundHalfEvenSticky(fracQ / div, fracQ % div, div, divRem != 0L)
                    e = biasedExpBase + excess
                    if (s >= POW10_16) { s /= 10L; e++ }
                }
            } else {
                // d digits in q0; combined q0 × 10^18 + fracQ has d+18 digits; discard d+2.
                val d = decimalDigits64(q0)
                val excess = d + 2
                val pow10excess = POW10_64[excess]
                val trunc = q0 * POW10_64[16 - d] + fracQ / pow10excess
                val rem2 = fracQ % pow10excess
                s = roundHalfEvenSticky(trunc, rem2, pow10excess, divRem != 0L)
                e = biasedExpBase + excess
                if (s >= POW10_16) { s /= 10L; e++ }
            }

            // Strip trailing decimal zeros introduced by the integer-quotient representation.
            while (s > 0L && s % 10L == 0L && e < 767) { s /= 10L; e++ }

            if (s == 0L) return BidDouble(signBit)
            if (e > 767) return BidDouble(signBit or 0x7800_0000_0000_0000L)
            if (e < 0) {
                val shift = -e
                if (shift >= 17) return BidDouble(signBit)
                val div = POW10_64[shift]
                s = roundHalfEven(s / div, s % div, div)
                e = 0
                if (s == 0L) return BidDouble(signBit)
            }

            return BidDouble(signBit or bidDouble64Pack(e, s))
        }

        override fun BidDouble.compareTo(other: BidDouble): Int =
            BidDouble.comparator.compare(this, other)
    }

/** IEEE 754-2008 arithmetic for [BidDouble]: correctly-rounded add, subtract, multiply, and divide. */
val FloatingPointArithmetic.Companion.bidDouble: FloatingPointArithmetic<BidDouble>
    get() = bidDoubleArithmeticInstance
