package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.BFloat16
import com.kelvsyc.kotlin.core.Float16

/**
 * `FloatingPointRounding` is a trait providing directed rounding operations for a floating-point type [T].
 *
 * All four operations round to an integer-valued result in the same floating-point type, preserve NaN,
 * infinities, and signed zeros, and correspond to IEEE 754 §5.9 `roundToIntegral*` operations:
 *
 * - [floor] — toward −∞ (`roundToIntegralTowardNegative`)
 * - [ceil] — toward +∞ (`roundToIntegralTowardPositive`)
 * - [trunc] — toward zero (`roundToIntegralTowardZero`); equivalent to C99 `trunc`
 * - [roundUp] — away from zero; equivalent to Java `RoundingMode.UP`
 *
 * Standard implementations for [BFloat16], [Float16], [Float], and [Double] are available as
 * [Companion.bfloat16], [Companion.float16], [Companion.float], and [Companion.double].
 */
interface FloatingPointRounding<T> {
    companion object

    fun T.floor(): T
    fun T.ceil(): T
    fun T.trunc(): T
    fun T.roundUp(): T
}

// ── BFloat16 ──────────────────────────────────────────────────────────────────

private val bfloat16Instance: FloatingPointRounding<BFloat16> = object : FloatingPointRounding<BFloat16> {
    override fun BFloat16.floor(): BFloat16 = calculate { kotlin.math.floor(it) }
    override fun BFloat16.ceil(): BFloat16 = calculate { kotlin.math.ceil(it) }
    override fun BFloat16.trunc(): BFloat16 = calculate { if (it >= 0.0f) kotlin.math.floor(it) else kotlin.math.ceil(it) }
    override fun BFloat16.roundUp(): BFloat16 = calculate { when { it > 0.0f -> kotlin.math.ceil(it); it < 0.0f -> kotlin.math.floor(it); else -> it } }
}

// ── Float16 ───────────────────────────────────────────────────────────────────

private val float16Instance: FloatingPointRounding<Float16> = object : FloatingPointRounding<Float16> {
    override fun Float16.floor(): Float16 = calculate { kotlin.math.floor(it) }
    override fun Float16.ceil(): Float16 = calculate { kotlin.math.ceil(it) }
    override fun Float16.trunc(): Float16 = calculate { if (it >= 0.0f) kotlin.math.floor(it) else kotlin.math.ceil(it) }
    override fun Float16.roundUp(): Float16 = calculate { when { it > 0.0f -> kotlin.math.ceil(it); it < 0.0f -> kotlin.math.floor(it); else -> it } }
}

// ── Float ─────────────────────────────────────────────────────────────────────

private val floatInstance: FloatingPointRounding<Float> = object : FloatingPointRounding<Float> {
    override fun Float.floor(): Float = kotlin.math.floor(this)
    override fun Float.ceil(): Float = kotlin.math.ceil(this)
    override fun Float.trunc(): Float = if (this >= 0.0f) kotlin.math.floor(this) else kotlin.math.ceil(this)
    override fun Float.roundUp(): Float = when { this > 0.0f -> kotlin.math.ceil(this); this < 0.0f -> kotlin.math.floor(this); else -> this }
}

// ── Double ────────────────────────────────────────────────────────────────────

private val doubleInstance: FloatingPointRounding<Double> = object : FloatingPointRounding<Double> {
    override fun Double.floor(): Double = kotlin.math.floor(this)
    override fun Double.ceil(): Double = kotlin.math.ceil(this)
    override fun Double.trunc(): Double = if (this >= 0.0) kotlin.math.floor(this) else kotlin.math.ceil(this)
    override fun Double.roundUp(): Double = when { this > 0.0 -> kotlin.math.ceil(this); this < 0.0 -> kotlin.math.floor(this); else -> this }
}

val FloatingPointRounding.Companion.bfloat16: FloatingPointRounding<BFloat16> get() = bfloat16Instance
val FloatingPointRounding.Companion.float16: FloatingPointRounding<Float16> get() = float16Instance
val FloatingPointRounding.Companion.float: FloatingPointRounding<Float> get() = floatInstance
val FloatingPointRounding.Companion.double: FloatingPointRounding<Double> get() = doubleInstance
