package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.BFloat16
import com.kelvsyc.kotlin.core.Float16

/**
 * `FloatingPointNextValue` is a trait providing the `nextUp` and `nextDown` operations for a
 * floating-point type [T].
 *
 * These are IEEE 754 §5.3.1 required operations:
 * - `nextUp(x)` returns the smallest representable value greater than `x`.
 * - `nextDown(x)` returns the largest representable value less than `x`.
 *
 * Special values: `nextUp(+∞) = +∞`, `nextDown(-∞) = -∞`, `nextUp(NaN) = NaN`.
 * Both ±0 map to `MIN_VALUE` under `nextUp`. The identity `nextDown(x) = -nextUp(-x)` holds.
 *
 * [Float16] and [BFloat16] delegate to their built-in member functions. [Float] delegates to
 * platform-specific implementations via `FloatingPointNextValuePlatform` (JVM: `Math.nextUp`;
 * macOS/Windows: POSIX `nextafterf`; JS/Linux: bit-pattern arithmetic). [Double] uses direct
 * bit-pattern arithmetic (`toRawBits`/`fromBits`) for platform portability, since
 * `Double.nextUp()` is absent from the Kotlin/JS stdlib for the same reasons as `Float.nextUp()`.
 *
 * Standard implementations for [Float16], [BFloat16], [Float], and [Double] are available as
 * [Companion.float16], [Companion.bfloat16], [Companion.float], and [Companion.double] respectively.
 */
interface FloatingPointNextValue<T> {
    companion object

    fun T.nextUp(): T
    fun T.nextDown(): T
}

private val bfloat16Instance: FloatingPointNextValue<BFloat16> = object : FloatingPointNextValue<BFloat16> {
    override fun BFloat16.nextUp(): BFloat16 = nextUp()
    override fun BFloat16.nextDown(): BFloat16 = nextDown()
}

private val float16Instance: FloatingPointNextValue<Float16> = object : FloatingPointNextValue<Float16> {
    override fun Float16.nextUp(): Float16 = nextUp()
    override fun Float16.nextDown(): Float16 = nextDown()
}

private val floatInstance: FloatingPointNextValue<Float> = object : FloatingPointNextValue<Float> {
    override fun Float.nextUp(): Float = nextUpFloat(this)
    override fun Float.nextDown(): Float = nextDownFloat(this)
}

private fun doubleNextUp(x: Double): Double {
    if (x.isNaN()) return x
    val bits = x.toRawBits()
    return when {
        bits == 0x7FF0000000000000L -> x
        bits == 0L || bits == Long.MIN_VALUE -> Double.fromBits(1L)
        bits > 0L -> Double.fromBits(bits + 1L)
        else -> Double.fromBits(bits - 1L)
    }
}

private fun doubleNextDown(x: Double): Double = -doubleNextUp(-x)

private val doubleInstance: FloatingPointNextValue<Double> = object : FloatingPointNextValue<Double> {
    override fun Double.nextUp(): Double = doubleNextUp(this)
    override fun Double.nextDown(): Double = doubleNextDown(this)
}

val FloatingPointNextValue.Companion.bfloat16: FloatingPointNextValue<BFloat16> get() = bfloat16Instance
val FloatingPointNextValue.Companion.float16: FloatingPointNextValue<Float16> get() = float16Instance
val FloatingPointNextValue.Companion.float: FloatingPointNextValue<Float> get() = floatInstance
val FloatingPointNextValue.Companion.double: FloatingPointNextValue<Double> get() = doubleInstance
