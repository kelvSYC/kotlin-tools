package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.BidDouble
import com.kelvsyc.kotlin.core.BidFloat
import com.kelvsyc.kotlin.core.bidDouble64Pack
import com.kelvsyc.kotlin.core.bidFloat32Pack

/**
 * `FloatingPointScald` is a trait providing decimal scaling for floating-point type [T].
 *
 * `scald(x, n)` computes `x × 10^n` — the `scaleB` operation for **decimal** floating-point types
 * using base 10. Because the base is 10, this operation is only meaningful for decimal formats such
 * as [BidFloat], [BidDouble], and their DPD counterparts
 * ([com.kelvsyc.kotlin.core.DpdFloat]/[com.kelvsyc.kotlin.core.DpdDouble]). For binary
 * floating-point (where the natural base is 2), see `FloatingPointScalb` instead.
 *
 * For finite non-zero values, `scald` adjusts the biased exponent of the stored representation by
 * [n], keeping the significand unchanged. Values that overflow the format's exponent range become
 * ±infinity; values that underflow become ±zero (preserving the sign).
 *
 * Special-value behaviour: NaN, infinity, and zero are returned unchanged.
 *
 * Standard implementations are available as companion extension properties:
 * [Companion.bidFloat] and [Companion.bidDouble].
 * DPD instances ([Companion.dpdFloat] and [Companion.dpdDouble]) are in `DpdScald.kt`.
 */
interface FloatingPointScald<T> {
    companion object

    /**
     * Returns `this × 10^[n]`.
     */
    fun T.scald(n: Int): T
}

// ── BidFloat ──────────────────────────────────────────────────────────────────

private const val BID_FLOAT_MAX_BIASED_EXP = 191

private val bidFloatInstance: FloatingPointScald<BidFloat> = object : FloatingPointScald<BidFloat> {
    override fun BidFloat.scald(n: Int): BidFloat {
        if (isNaN() || isInfinite() || isZero()) return this
        val signBit = if (sign) Int.MIN_VALUE else 0
        val newBiasedExp = biasedExponent + n
        return when {
            newBiasedExp > BID_FLOAT_MAX_BIASED_EXP ->
                if (sign) BidFloat.negativeInfinity else BidFloat.positiveInfinity
            newBiasedExp < 0 -> BidFloat(signBit)
            else -> BidFloat(signBit or bidFloat32Pack(newBiasedExp, significand))
        }
    }
}

// ── BidDouble ─────────────────────────────────────────────────────────────────

private const val BID_DOUBLE_MAX_BIASED_EXP = 767

private val bidDoubleInstance: FloatingPointScald<BidDouble> = object : FloatingPointScald<BidDouble> {
    override fun BidDouble.scald(n: Int): BidDouble {
        if (isNaN() || isInfinite() || isZero()) return this
        val signBit = if (sign) Long.MIN_VALUE else 0L
        val newBiasedExp = biasedExponent + n
        return when {
            newBiasedExp > BID_DOUBLE_MAX_BIASED_EXP ->
                if (sign) BidDouble.negativeInfinity else BidDouble.positiveInfinity
            newBiasedExp < 0 -> BidDouble(signBit)
            else -> BidDouble(signBit or bidDouble64Pack(newBiasedExp, significand))
        }
    }
}

val FloatingPointScald.Companion.bidFloat: FloatingPointScald<BidFloat> get() = bidFloatInstance
val FloatingPointScald.Companion.bidDouble: FloatingPointScald<BidDouble> get() = bidDoubleInstance
