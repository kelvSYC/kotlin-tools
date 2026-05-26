package com.kelvsyc.kotlin.core.traits.complex

import com.kelvsyc.kotlin.core.Complex
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointHypot
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointSquareRoot
import com.kelvsyc.kotlin.core.traits.fp.double
import com.kelvsyc.kotlin.core.traits.fp.float

/**
 * `ComplexSquareRoot` is a trait providing the principal square root of a complex value of type [C]
 * whose components are of type [T].
 *
 * The principal square root is the root whose real part is non-negative. The formula used is:
 * ```
 * r  = hypot(a, b)
 * re = sqrt((r + a) / 2)
 * im = if (re == 0) sign(b) · sqrt((r − a) / 2) else b / (2 · re)
 * ```
 * Using [FloatingPointHypot] for `r` prevents overflow for large inputs and gives correct IEEE 754
 * semantics (∞ dominates NaN). Deriving `im` from `b / (2·re)` avoids catastrophic cancellation
 * in `r − a` when the imaginary part is small relative to the real part. The `re == 0` case
 * (negative real axis, `b = 0`) falls back to the stable `sqrt((r − a) / 2)` form with the sign
 * of `b` preserved via IEEE 754 signed-zero semantics.
 *
 * Standard instances for [Complex]<[Float]> and [Complex]<[Double]> are available as
 * [Companion.float] and [Companion.double].
 */
interface ComplexSquareRoot<C, T> {
    fun C.real(): T
    fun C.imaginary(): T
    fun of(real: T, imaginary: T): C

    /**
     * Returns the principal square root of this complex value.
     *
     * The result has non-negative real part. The imaginary part has the same sign as [imaginary].
     */
    fun C.sqrt(): C

    companion object
}

// ── Float ─────────────────────────────────────────────────────────────────────

private val floatInstance: ComplexSquareRoot<Complex<Float>, Float> =
    object : ComplexSquareRoot<Complex<Float>, Float> {
        private val sqrtOps = FloatingPointSquareRoot.float
        private val hypotOps = FloatingPointHypot.float
        override fun Complex<Float>.real(): Float = real
        override fun Complex<Float>.imaginary(): Float = imaginary
        override fun of(real: Float, imaginary: Float): Complex<Float> = Complex(real, imaginary)

        override fun Complex<Float>.sqrt(): Complex<Float> {
            val a = real; val b = imaginary
            val r = with(hypotOps) { a.hypot(b) }
            val re = with(sqrtOps) { ((r + a) / 2).sqrt() }
            val im = if (re == 0.0f) {
                // re=0 only when b=0 and a≤0 (negative real axis); use stable form
                val mag = with(sqrtOps) { ((r - a) / 2).sqrt() }
                if (b < 0.0f) -mag else mag
            } else {
                b / (2.0f * re)
            }
            return Complex(re, im)
        }
    }

// ── Double ────────────────────────────────────────────────────────────────────

private val doubleInstance: ComplexSquareRoot<Complex<Double>, Double> =
    object : ComplexSquareRoot<Complex<Double>, Double> {
        private val sqrtOps = FloatingPointSquareRoot.double
        private val hypotOps = FloatingPointHypot.double
        override fun Complex<Double>.real(): Double = real
        override fun Complex<Double>.imaginary(): Double = imaginary
        override fun of(real: Double, imaginary: Double): Complex<Double> = Complex(real, imaginary)

        override fun Complex<Double>.sqrt(): Complex<Double> {
            val a = real; val b = imaginary
            val r = with(hypotOps) { a.hypot(b) }
            val re = with(sqrtOps) { ((r + a) / 2).sqrt() }
            val im = if (re == 0.0) {
                // re=0 only when b=0 and a≤0 (negative real axis); use stable form
                val mag = with(sqrtOps) { ((r - a) / 2).sqrt() }
                if (b < 0.0) -mag else mag
            } else {
                b / (2.0 * re)
            }
            return Complex(re, im)
        }
    }

val ComplexSquareRoot.Companion.float: ComplexSquareRoot<Complex<Float>, Float> get() = floatInstance
val ComplexSquareRoot.Companion.double: ComplexSquareRoot<Complex<Double>, Double> get() = doubleInstance
