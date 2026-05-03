package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.BFloat16
import com.kelvsyc.kotlin.core.Float16

/**
 * `FloatingPointRounding` is a trait providing floor and ceiling operations for a floating-point type [T].
 *
 * - [floor] — rounds toward negative infinity; equivalent to IEEE 754 `roundToIntegralTowardNegative`.
 * - [ceil] — rounds toward positive infinity; equivalent to IEEE 754 `roundToIntegralTowardPositive`.
 *
 * Both operations preserve NaN (returning NaN), infinities, and signed zeros.
 *
 * Standard implementations for [BFloat16], [Float16], [Float], and [Double] are available as
 * [Companion.bfloat16], [Companion.float16], [Companion.float], and [Companion.double].
 */
interface FloatingPointRounding<T> {
    companion object

    fun T.floor(): T
    fun T.ceil(): T
}

// ── BFloat16 ──────────────────────────────────────────────────────────────────

private val bfloat16Instance: FloatingPointRounding<BFloat16> = object : FloatingPointRounding<BFloat16> {
    override fun BFloat16.floor(): BFloat16 = calculate { kotlin.math.floor(it) }
    override fun BFloat16.ceil(): BFloat16 = calculate { kotlin.math.ceil(it) }
}

// ── Float16 ───────────────────────────────────────────────────────────────────

private val float16Instance: FloatingPointRounding<Float16> = object : FloatingPointRounding<Float16> {
    override fun Float16.floor(): Float16 = calculate { kotlin.math.floor(it) }
    override fun Float16.ceil(): Float16 = calculate { kotlin.math.ceil(it) }
}

// ── Float ─────────────────────────────────────────────────────────────────────

private val floatInstance: FloatingPointRounding<Float> = object : FloatingPointRounding<Float> {
    override fun Float.floor(): Float = kotlin.math.floor(this)
    override fun Float.ceil(): Float = kotlin.math.ceil(this)
}

// ── Double ────────────────────────────────────────────────────────────────────

private val doubleInstance: FloatingPointRounding<Double> = object : FloatingPointRounding<Double> {
    override fun Double.floor(): Double = kotlin.math.floor(this)
    override fun Double.ceil(): Double = kotlin.math.ceil(this)
}

val FloatingPointRounding.Companion.bfloat16: FloatingPointRounding<BFloat16> get() = bfloat16Instance
val FloatingPointRounding.Companion.float16: FloatingPointRounding<Float16> get() = float16Instance
val FloatingPointRounding.Companion.float: FloatingPointRounding<Float> get() = floatInstance
val FloatingPointRounding.Companion.double: FloatingPointRounding<Double> get() = doubleInstance
