package com.kelvsyc.kotlin.core.traits.complex

import com.kelvsyc.kotlin.core.Complex
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointCubeRoot
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointHypot
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointTrigPi
import com.kelvsyc.kotlin.core.traits.fp.double
import com.kelvsyc.kotlin.core.traits.fp.float

/**
 * `ComplexCubeRoot` is a trait providing the principal cube root of a complex value of type [C]
 * whose components are of type [T].
 *
 * The principal cube root uses the polar form via π-scaled trigonometry:
 * ```
 * r       = hypot(a, b)
 * thetaPi = atan2Pi(b, a)        // argument in units of π, in (−1, 1]
 * cbrtR   = cbrt(r)
 * re      = cbrtR · cosPi(thetaPi / 3)
 * im      = cbrtR · sinPi(thetaPi / 3)
 * ```
 *
 * Using [FloatingPointTrigPi] for `atan2Pi`/`cosPi`/`sinPi` gives exact results when the angle
 * is a rational fraction of π — for example, `cbrt(−1+0i)` has `thetaPi = 1`, so
 * `cosPi(1/3) = 0.5` is computed without the extra rounding that `cos(π/3)` would introduce.
 *
 * Standard instances for [Complex]<[Float]> and [Complex]<[Double]> are available as
 * [Companion.float] and [Companion.double].
 */
interface ComplexCubeRoot<C, T> {
    fun C.real(): T
    fun C.imaginary(): T
    fun of(real: T, imaginary: T): C

    /** Returns the principal cube root of this complex value. */
    fun C.cbrt(): C

    companion object
}

// ── Float ─────────────────────────────────────────────────────────────────────

private val floatInstance: ComplexCubeRoot<Complex<Float>, Float> =
    object : ComplexCubeRoot<Complex<Float>, Float> {
        private val cbrtOps = FloatingPointCubeRoot.float
        private val hypotOps = FloatingPointHypot.float
        private val trigPiOps = FloatingPointTrigPi.float

        override fun Complex<Float>.real(): Float = real
        override fun Complex<Float>.imaginary(): Float = imaginary
        override fun of(real: Float, imaginary: Float): Complex<Float> = Complex(real, imaginary)

        override fun Complex<Float>.cbrt(): Complex<Float> {
            val a = real; val b = imaginary
            val r = with(hypotOps) { a.hypot(b) }
            val thetaPi = with(trigPiOps) { b.atan2Pi(a) }
            val cbrtR = with(cbrtOps) { r.cbrt() }
            val phi = thetaPi / 3.0f
            return Complex(
                cbrtR * with(trigPiOps) { phi.cosPi() },
                cbrtR * with(trigPiOps) { phi.sinPi() },
            )
        }
    }

// ── Double ────────────────────────────────────────────────────────────────────

private val doubleInstance: ComplexCubeRoot<Complex<Double>, Double> =
    object : ComplexCubeRoot<Complex<Double>, Double> {
        private val cbrtOps = FloatingPointCubeRoot.double
        private val hypotOps = FloatingPointHypot.double
        private val trigPiOps = FloatingPointTrigPi.double

        override fun Complex<Double>.real(): Double = real
        override fun Complex<Double>.imaginary(): Double = imaginary
        override fun of(real: Double, imaginary: Double): Complex<Double> = Complex(real, imaginary)

        override fun Complex<Double>.cbrt(): Complex<Double> {
            val a = real; val b = imaginary
            val r = with(hypotOps) { a.hypot(b) }
            val thetaPi = with(trigPiOps) { b.atan2Pi(a) }
            val cbrtR = with(cbrtOps) { r.cbrt() }
            val phi = thetaPi / 3.0
            return Complex(
                cbrtR * with(trigPiOps) { phi.cosPi() },
                cbrtR * with(trigPiOps) { phi.sinPi() },
            )
        }
    }

val ComplexCubeRoot.Companion.float: ComplexCubeRoot<Complex<Float>, Float> get() = floatInstance
val ComplexCubeRoot.Companion.double: ComplexCubeRoot<Complex<Double>, Double> get() = doubleInstance
