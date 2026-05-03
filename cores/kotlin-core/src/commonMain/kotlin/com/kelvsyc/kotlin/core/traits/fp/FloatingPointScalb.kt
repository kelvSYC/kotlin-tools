package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.BFloat16
import com.kelvsyc.kotlin.core.Float16

/**
 * `FloatingPointScalb` is a trait providing binary scaling for floating-point type [T].
 *
 * `scalb(x, n)` computes `x × 2^n` — the IEEE 754 `scaleB` operation for **binary** floating-point
 * types. It is sometimes called `scalbn` (C99/C++11) or `ldexp` (classic UNIX math). The base is
 * always 2, so this operation is only meaningful for binary formats such as [BFloat16], [Float16],
 * [Float], [Double], and [com.kelvsyc.kotlin.core.fp.DoubleDouble]. For decimal floating-point (where
 * the natural base is 10), see `FloatingPointScald` instead, which scales by powers of ten.
 *
 * Special-value behaviour follows IEEE 754: NaN inputs produce NaN, infinities remain infinite, and
 * signed zeros remain zero regardless of [n].
 *
 * Standard implementations are available as companion extension properties:
 * [Companion.bfloat16], [Companion.float16], [Companion.float], and [Companion.double].
 * A [com.kelvsyc.kotlin.core.fp.DoubleDouble] instance is provided by [Companion.doubleDouble] in
 * `DoubleDoubleScalb.kt`.
 */
interface FloatingPointScalb<T> {
    companion object

    /**
     * Returns `this × 2^[n]`.
     */
    fun T.scalb(n: Int): T
}

// ── BFloat16 ──────────────────────────────────────────────────────────────────

private val bfloat16Instance: FloatingPointScalb<BFloat16> = object : FloatingPointScalb<BFloat16> {
    override fun BFloat16.scalb(n: Int): BFloat16 = calculate { scalbFloat(it, n) }
}

// ── Float16 ───────────────────────────────────────────────────────────────────

private val float16Instance: FloatingPointScalb<Float16> = object : FloatingPointScalb<Float16> {
    override fun Float16.scalb(n: Int): Float16 = calculate { scalbFloat(it, n) }
}

// ── Float ─────────────────────────────────────────────────────────────────────

private val floatInstance: FloatingPointScalb<Float> = object : FloatingPointScalb<Float> {
    override fun Float.scalb(n: Int): Float = scalbFloat(this, n)
}

// ── Double ────────────────────────────────────────────────────────────────────

private val doubleInstance: FloatingPointScalb<Double> = object : FloatingPointScalb<Double> {
    override fun Double.scalb(n: Int): Double = scalbDouble(this, n)
}

val FloatingPointScalb.Companion.bfloat16: FloatingPointScalb<BFloat16> get() = bfloat16Instance
val FloatingPointScalb.Companion.float16: FloatingPointScalb<Float16> get() = float16Instance
val FloatingPointScalb.Companion.float: FloatingPointScalb<Float> get() = floatInstance
val FloatingPointScalb.Companion.double: FloatingPointScalb<Double> get() = doubleInstance
