package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.BFloat16
import com.kelvsyc.kotlin.core.Float16

/**
 * `FloatingPointRemainder` is a trait type providing a remainder operation over floating-point type [T].
 *
 * The IEEE 754 standard defines two notions of remainder, distinguished by how the intermediate quotient
 * `n = x / y` is rounded before computing `x − n × y`:
 *
 * - **IEEE 754 remainder** ([ieee754] instances): `n` is rounded to the nearest integer (ties to even),
 *   following IEEE 754-2008 §5.3.1. The result is always in `[−|y|/2, +|y|/2]` and may have the
 *   opposite sign from `x`.
 * - **Truncating remainder** ([truncating] instances): `n` is truncated toward zero. This matches the
 *   `%` operator on [Float] and [Double] in Kotlin. The result always has the same sign as `x` (or is zero).
 *
 * Both are available as [Companion] properties. Two instances exist for the same reason as two instances of
 * [ValueEquality]: one matches IEEE 754 numerical semantics, and one matches Kotlin operator semantics.
 */
interface FloatingPointRemainder<T> {
    /**
     * Returns the remainder when `this` is divided by [other].
     */
    fun T.rem(other: T): T

    companion object
}

// ── IEEE 754 remainder helpers ────────────────────────────────────────────────
// Declared as expect in FloatingPointRemainderPlatform.kt; platform actuals
// supply hardware-backed implementations where available.

// ── BFloat16 ─────────────────────────────────────────────────────────────────

private val bfloat16Ieee754Instance: FloatingPointRemainder<BFloat16> = object : FloatingPointRemainder<BFloat16> {
    override fun BFloat16.rem(other: BFloat16): BFloat16 =
        calculate { ieee754RemFloat(it, other.toFloat()) }
}

private val bfloat16TruncatingInstance: FloatingPointRemainder<BFloat16> = object : FloatingPointRemainder<BFloat16> {
    override fun BFloat16.rem(other: BFloat16): BFloat16 = this % other
}

// ── Float16 ───────────────────────────────────────────────────────────────────

private val float16Ieee754Instance: FloatingPointRemainder<Float16> = object : FloatingPointRemainder<Float16> {
    override fun Float16.rem(other: Float16): Float16 =
        calculate { ieee754RemFloat(it, other.toFloat()) }
}

private val float16TruncatingInstance: FloatingPointRemainder<Float16> = object : FloatingPointRemainder<Float16> {
    override fun Float16.rem(other: Float16): Float16 = this % other
}

// ── Float ─────────────────────────────────────────────────────────────────────

private val floatIeee754Instance: FloatingPointRemainder<Float> = object : FloatingPointRemainder<Float> {
    override fun Float.rem(other: Float): Float = ieee754RemFloat(this, other)
}

private val floatTruncatingInstance: FloatingPointRemainder<Float> = object : FloatingPointRemainder<Float> {
    override fun Float.rem(other: Float): Float = this % other
}

// ── Double ────────────────────────────────────────────────────────────────────

private val doubleIeee754Instance: FloatingPointRemainder<Double> = object : FloatingPointRemainder<Double> {
    override fun Double.rem(other: Double): Double = ieee754RemDouble(this, other)
}

private val doubleTruncatingInstance: FloatingPointRemainder<Double> = object : FloatingPointRemainder<Double> {
    override fun Double.rem(other: Double): Double = this % other
}

val FloatingPointRemainder.Companion.bfloat16Ieee754: FloatingPointRemainder<BFloat16> get() = bfloat16Ieee754Instance
val FloatingPointRemainder.Companion.bfloat16Truncating: FloatingPointRemainder<BFloat16> get() = bfloat16TruncatingInstance
val FloatingPointRemainder.Companion.float16Ieee754: FloatingPointRemainder<Float16> get() = float16Ieee754Instance
val FloatingPointRemainder.Companion.float16Truncating: FloatingPointRemainder<Float16> get() = float16TruncatingInstance
val FloatingPointRemainder.Companion.floatIeee754: FloatingPointRemainder<Float> get() = floatIeee754Instance
val FloatingPointRemainder.Companion.floatTruncating: FloatingPointRemainder<Float> get() = floatTruncatingInstance
val FloatingPointRemainder.Companion.doubleIeee754: FloatingPointRemainder<Double> get() = doubleIeee754Instance
val FloatingPointRemainder.Companion.doubleTruncating: FloatingPointRemainder<Double> get() = doubleTruncatingInstance
