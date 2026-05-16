package com.kelvsyc.kotlin.core.traits.fp

private val _floatIsNaN: (Float) -> Boolean = Float::isNaN
private val _floatIsInfinite: (Float) -> Boolean = Float::isInfinite
private val _floatIsFinite: (Float) -> Boolean = Float::isFinite

// Rounds a Float (held at binary64 precision by the JS runtime) to the nearest binary32 value.
// Equivalent to Float.fromBits(x.toRawBits()), but delegates directly to the JS engine intrinsic.
@Suppress("NOTHING_TO_INLINE")
private inline fun fround(x: Float): Float = js("Math.fround(x)")

/**
 * [FloatingPointArithmetic] instance for [Float] that guarantees true `binary32` rounding on
 * Kotlin/JS.
 *
 * Each arithmetic result is passed through JavaScript's `Math.fround()`, forcing the value to the
 * nearest representable `binary32` even though JavaScript performs all arithmetic at `binary64`
 * precision.
 *
 * This instance backs the JS-side actuals of
 * [TwoSum.float][com.kelvsyc.kotlin.core.traits.dd.TwoSum.Companion.float],
 * [TwoProduct.float][com.kelvsyc.kotlin.core.traits.dd.TwoProduct.Companion.float],
 * [TwoDiv.float][com.kelvsyc.kotlin.core.traits.dd.TwoDiv.Companion.float], and
 * [FusedMultiplyAdd.float], ensuring that error-free transformations and emulated FMA
 * produce correct results despite the platform's lack of native `binary32` arithmetic.
 */
internal val strictFloatArithmetic: FloatingPointArithmetic<Float> by lazy {
    object : FloatingPointArithmetic<Float> {
        override val zero: Float get() = 0.0f
        override val one: Float get() = 1.0f

        override fun Float.isNaN(): Boolean = _floatIsNaN(this)
        override fun Float.isInfinite(): Boolean = _floatIsInfinite(this)
        override fun Float.isFinite(): Boolean = _floatIsFinite(this)
        override fun Float.isZero(): Boolean = this == 0.0f
        override fun Float.isInteger(): Boolean {
            val b = toRawBits() and Int.MAX_VALUE
            if (b >= 0x7F800000) return false
            if (b == 0) return true
            val biasedExp = b ushr 23
            if (biasedExp >= 150) return true
            if (biasedExp < 127) return false
            return (b and ((1 shl (150 - biasedExp)) - 1)) == 0
        }
        override fun Float.isNegative(): Boolean = toRawBits() < 0

        override fun Float.negate(): Float = -this
        override fun Float.abs(): Float = kotlin.math.abs(this)
        override fun Float.copySign(other: Float): Float =
            Float.fromBits((toRawBits() and Int.MAX_VALUE) or (other.toRawBits() and Int.MIN_VALUE))

        override fun Float.add(other: Float): Float = fround(this + other)
        override fun Float.subtract(other: Float): Float = fround(this - other)
        override fun Float.multiply(other: Float): Float = fround(this * other)
        override fun Float.divide(other: Float): Float = fround(this / other)

        override fun Float.compareTo(other: Float): Int = this.compareTo(other)
    }
}
