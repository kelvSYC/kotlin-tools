package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.BFloat16
import com.kelvsyc.kotlin.core.Float16
import com.kelvsyc.kotlin.core.fp.DoubleDouble
import com.kelvsyc.kotlin.core.fp.times

/**
 * `FloatingPointSquare` is a trait providing a squaring operation for floating-point type [T].
 *
 * `square(x)` computes `x × x` — a single correctly-rounded multiplication of `x` with itself.
 * Although `square(x)` is mathematically equivalent to `pow(x, 2)` from [IntegerPower], it is a
 * distinct, cheaper primitive: it requires a single multiply rather than the O(log n) loop of
 * binary exponentiation, and concrete types may have a more numerically stable implementation
 * (for example, double-double types can compute `x^2` more accurately than a naive `x * x`).
 *
 * This trait covers **floating-point** types only. Integer squaring is not included; for integers,
 * use `n * n` directly or [IntegerPower.pow] with `n = 2`.
 *
 * Standard implementations are available as companion extension properties:
 * [Companion.float], [Companion.double], [Companion.bfloat16], [Companion.float16],
 * [Companion.doubleDouble].
 * Decimal instances ([Companion.bidFloat], [Companion.bidDouble], [Companion.dpdFloat],
 * [Companion.dpdDouble]) are in `BidFloatingPointSquare.kt`.
 */
interface FloatingPointSquare<T> {
    companion object

    /**
     * Returns `this × this`.
     */
    fun T.square(): T
}

// ── BFloat16 ──────────────────────────────────────────────────────────────────

private val bfloat16Instance: FloatingPointSquare<BFloat16> = object : FloatingPointSquare<BFloat16> {
    override fun BFloat16.square(): BFloat16 = calculate { it * it }
}

// ── Float16 ───────────────────────────────────────────────────────────────────

private val float16Instance: FloatingPointSquare<Float16> = object : FloatingPointSquare<Float16> {
    override fun Float16.square(): Float16 = calculate { it * it }
}

// ── Float ─────────────────────────────────────────────────────────────────────

private val floatInstance: FloatingPointSquare<Float> = object : FloatingPointSquare<Float> {
    override fun Float.square(): Float = this * this
}

// ── Double ────────────────────────────────────────────────────────────────────

private val doubleInstance: FloatingPointSquare<Double> = object : FloatingPointSquare<Double> {
    override fun Double.square(): Double = this * this
}

// ── DoubleDouble ──────────────────────────────────────────────────────────────

private val doubleDoubleInstance: FloatingPointSquare<DoubleDouble> =
    object : FloatingPointSquare<DoubleDouble> {
        override fun DoubleDouble.square(): DoubleDouble = this * this
    }

val FloatingPointSquare.Companion.bfloat16: FloatingPointSquare<BFloat16> get() = bfloat16Instance
val FloatingPointSquare.Companion.float16: FloatingPointSquare<Float16> get() = float16Instance
val FloatingPointSquare.Companion.float: FloatingPointSquare<Float> get() = floatInstance
val FloatingPointSquare.Companion.double: FloatingPointSquare<Double> get() = doubleInstance
val FloatingPointSquare.Companion.doubleDouble: FloatingPointSquare<DoubleDouble>
    get() = doubleDoubleInstance
