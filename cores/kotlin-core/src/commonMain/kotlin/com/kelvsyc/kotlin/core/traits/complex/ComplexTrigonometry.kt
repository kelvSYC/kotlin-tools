package com.kelvsyc.kotlin.core.traits.complex

import com.kelvsyc.kotlin.core.Complex
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointSinhCosh
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointTrigonometry
import com.kelvsyc.kotlin.core.traits.fp.double
import com.kelvsyc.kotlin.core.traits.fp.float

/**
 * `ComplexTrigonometry` is a trait providing trigonometric and inverse trigonometric operations
 * over complex values of type [C] whose components are of type [T].
 *
 * **Forward functions** ([sin], [cos], [tan], [sinh], [cosh], [tanh]) are backed by
 * [FloatingPointTrigonometry] and [FloatingPointSinhCosh], exploiting the combined hyperbolic
 * computation to halve the number of `exp` evaluations.
 *
 * **Inverse functions** ([asin], [acos], [atan], [asinh], [acosh], [atanh]) are implemented via
 * [ComplexExpLog.ln] and [ComplexSquareRoot.sqrt] using the standard logarithmic representations:
 * - `asin(z)  = −i · ln(iz + sqrt(1 − z²))`
 * - `acos(z)  = −i · ln(z + i·sqrt(1 − z²))`
 * - `atan(z)  = (i/2) · ln((i+z) / (i−z))`
 * - `asinh(z) = ln(z + sqrt(z²+1))`
 * - `acosh(z) = ln(z + sqrt(z²−1))`
 * - `atanh(z) = (1/2) · ln((1+z) / (1−z))`
 *
 * Standard instances for [Complex]<[Float]> and [Complex]<[Double]> are available as
 * [Companion.float] and [Companion.double].
 */
interface ComplexTrigonometry<C, T> {
    fun C.real(): T
    fun C.imaginary(): T
    fun of(real: T, imaginary: T): C

    fun C.sin(): C
    fun C.cos(): C
    fun C.tan(): C
    fun C.sinh(): C
    fun C.cosh(): C
    fun C.tanh(): C
    fun C.asin(): C
    fun C.acos(): C
    fun C.atan(): C
    fun C.asinh(): C
    fun C.acosh(): C
    fun C.atanh(): C

    companion object
}

// ── Float ─────────────────────────────────────────────────────────────────────

private val floatInstance: ComplexTrigonometry<Complex<Float>, Float> =
    object : ComplexTrigonometry<Complex<Float>, Float> {
        private val trigOps = FloatingPointTrigonometry.float
        private val sinhCoshOps = FloatingPointSinhCosh.float
        private val sqrtOps = ComplexSquareRoot.float
        private val expLogOps = ComplexExpLog.float
        private val arithmeticOps = ComplexArithmetic.strictFloat

        override fun Complex<Float>.real(): Float = real
        override fun Complex<Float>.imaginary(): Float = imaginary
        override fun of(real: Float, imaginary: Float): Complex<Float> = Complex(real, imaginary)

        override fun Complex<Float>.sin(): Complex<Float> {
            val a = real; val b = imaginary
            val sa = with(trigOps) { a.sin() }; val ca = with(trigOps) { a.cos() }
            val shch = with(sinhCoshOps) { b.sinhcosh() }
            return Complex(sa * shch.cosh, ca * shch.sinh)
        }

        override fun Complex<Float>.cos(): Complex<Float> {
            val a = real; val b = imaginary
            val sa = with(trigOps) { a.sin() }; val ca = with(trigOps) { a.cos() }
            val shch = with(sinhCoshOps) { b.sinhcosh() }
            return Complex(ca * shch.cosh, -(sa * shch.sinh))
        }

        override fun Complex<Float>.tan(): Complex<Float> {
            val a = real; val b = imaginary
            val s2a = with(trigOps) { (2.0f * a).sin() }
            val c2a = with(trigOps) { (2.0f * a).cos() }
            val shch2 = with(sinhCoshOps) { (2.0f * b).sinhcosh() }
            val denom = c2a + shch2.cosh
            return Complex(s2a / denom, shch2.sinh / denom)
        }

        override fun Complex<Float>.sinh(): Complex<Float> {
            val a = real; val b = imaginary
            val shch = with(sinhCoshOps) { a.sinhcosh() }
            val sb = with(trigOps) { b.sin() }; val cb = with(trigOps) { b.cos() }
            return Complex(shch.sinh * cb, shch.cosh * sb)
        }

        override fun Complex<Float>.cosh(): Complex<Float> {
            val a = real; val b = imaginary
            val shch = with(sinhCoshOps) { a.sinhcosh() }
            val sb = with(trigOps) { b.sin() }; val cb = with(trigOps) { b.cos() }
            return Complex(shch.cosh * cb, shch.sinh * sb)
        }

        override fun Complex<Float>.tanh(): Complex<Float> {
            val a = real; val b = imaginary
            val shch2 = with(sinhCoshOps) { (2.0f * a).sinhcosh() }
            val s2b = with(trigOps) { (2.0f * b).sin() }
            val c2b = with(trigOps) { (2.0f * b).cos() }
            val denom = shch2.cosh + c2b
            return Complex(shch2.sinh / denom, s2b / denom)
        }

        override fun Complex<Float>.asin(): Complex<Float> {
            val a = real; val b = imaginary
            val iz = Complex(-b, a)
            val z2re = a * a - b * b; val z2im = 2.0f * a * b
            val sqrtVal = with(sqrtOps) { Complex(1.0f - z2re, -z2im).sqrt() }
            val lnArg = Complex(iz.real + sqrtVal.real, iz.imaginary + sqrtVal.imaginary)
            val lnVal = with(expLogOps) { lnArg.ln() }
            return Complex(lnVal.imaginary, -lnVal.real)
        }

        override fun Complex<Float>.acos(): Complex<Float> {
            val a = real; val b = imaginary
            val z2re = a * a - b * b; val z2im = 2.0f * a * b
            val sqrtVal = with(sqrtOps) { Complex(1.0f - z2re, -z2im).sqrt() }
            val lnArg = Complex(a - sqrtVal.imaginary, b + sqrtVal.real)
            val lnVal = with(expLogOps) { lnArg.ln() }
            return Complex(lnVal.imaginary, -lnVal.real)
        }

        override fun Complex<Float>.atan(): Complex<Float> {
            val a = real; val b = imaginary
            val ratio = with(arithmeticOps) {
                Complex(a, 1.0f + b).divide(Complex(-a, 1.0f - b))
            }
            val lnVal = with(expLogOps) { ratio.ln() }
            return Complex(-lnVal.imaginary / 2.0f, lnVal.real / 2.0f)
        }

        override fun Complex<Float>.asinh(): Complex<Float> {
            val a = real; val b = imaginary
            val z2re = a * a - b * b; val z2im = 2.0f * a * b
            val sqrtVal = with(sqrtOps) { Complex(z2re + 1.0f, z2im).sqrt() }
            val lnArg = Complex(a + sqrtVal.real, b + sqrtVal.imaginary)
            return with(expLogOps) { lnArg.ln() }
        }

        override fun Complex<Float>.acosh(): Complex<Float> {
            val a = real; val b = imaginary
            val z2re = a * a - b * b; val z2im = 2.0f * a * b
            val sqrtVal = with(sqrtOps) { Complex(z2re - 1.0f, z2im).sqrt() }
            val lnArg = Complex(a + sqrtVal.real, b + sqrtVal.imaginary)
            return with(expLogOps) { lnArg.ln() }
        }

        override fun Complex<Float>.atanh(): Complex<Float> {
            val a = real; val b = imaginary
            val ratio = with(arithmeticOps) {
                Complex(1.0f + a, b).divide(Complex(1.0f - a, -b))
            }
            val lnVal = with(expLogOps) { ratio.ln() }
            return Complex(lnVal.real / 2.0f, lnVal.imaginary / 2.0f)
        }
    }

// ── Double ────────────────────────────────────────────────────────────────────

private val doubleInstance: ComplexTrigonometry<Complex<Double>, Double> =
    object : ComplexTrigonometry<Complex<Double>, Double> {
        private val trigOps = FloatingPointTrigonometry.double
        private val sinhCoshOps = FloatingPointSinhCosh.double
        private val sqrtOps = ComplexSquareRoot.double
        private val expLogOps = ComplexExpLog.double
        private val arithmeticOps = ComplexArithmetic.strictDouble

        override fun Complex<Double>.real(): Double = real
        override fun Complex<Double>.imaginary(): Double = imaginary
        override fun of(real: Double, imaginary: Double): Complex<Double> = Complex(real, imaginary)

        override fun Complex<Double>.sin(): Complex<Double> {
            val a = real; val b = imaginary
            val sa = with(trigOps) { a.sin() }; val ca = with(trigOps) { a.cos() }
            val shch = with(sinhCoshOps) { b.sinhcosh() }
            return Complex(sa * shch.cosh, ca * shch.sinh)
        }

        override fun Complex<Double>.cos(): Complex<Double> {
            val a = real; val b = imaginary
            val sa = with(trigOps) { a.sin() }; val ca = with(trigOps) { a.cos() }
            val shch = with(sinhCoshOps) { b.sinhcosh() }
            return Complex(ca * shch.cosh, -(sa * shch.sinh))
        }

        override fun Complex<Double>.tan(): Complex<Double> {
            val a = real; val b = imaginary
            val s2a = with(trigOps) { (2.0 * a).sin() }
            val c2a = with(trigOps) { (2.0 * a).cos() }
            val shch2 = with(sinhCoshOps) { (2.0 * b).sinhcosh() }
            val denom = c2a + shch2.cosh
            return Complex(s2a / denom, shch2.sinh / denom)
        }

        override fun Complex<Double>.sinh(): Complex<Double> {
            val a = real; val b = imaginary
            val shch = with(sinhCoshOps) { a.sinhcosh() }
            val sb = with(trigOps) { b.sin() }; val cb = with(trigOps) { b.cos() }
            return Complex(shch.sinh * cb, shch.cosh * sb)
        }

        override fun Complex<Double>.cosh(): Complex<Double> {
            val a = real; val b = imaginary
            val shch = with(sinhCoshOps) { a.sinhcosh() }
            val sb = with(trigOps) { b.sin() }; val cb = with(trigOps) { b.cos() }
            return Complex(shch.cosh * cb, shch.sinh * sb)
        }

        override fun Complex<Double>.tanh(): Complex<Double> {
            val a = real; val b = imaginary
            val shch2 = with(sinhCoshOps) { (2.0 * a).sinhcosh() }
            val s2b = with(trigOps) { (2.0 * b).sin() }
            val c2b = with(trigOps) { (2.0 * b).cos() }
            val denom = shch2.cosh + c2b
            return Complex(shch2.sinh / denom, s2b / denom)
        }

        override fun Complex<Double>.asin(): Complex<Double> {
            val a = real; val b = imaginary
            val iz = Complex(-b, a)
            val z2re = a * a - b * b; val z2im = 2.0 * a * b
            val sqrtVal = with(sqrtOps) { Complex(1.0 - z2re, -z2im).sqrt() }
            val lnArg = Complex(iz.real + sqrtVal.real, iz.imaginary + sqrtVal.imaginary)
            val lnVal = with(expLogOps) { lnArg.ln() }
            return Complex(lnVal.imaginary, -lnVal.real)
        }

        override fun Complex<Double>.acos(): Complex<Double> {
            val a = real; val b = imaginary
            val z2re = a * a - b * b; val z2im = 2.0 * a * b
            val sqrtVal = with(sqrtOps) { Complex(1.0 - z2re, -z2im).sqrt() }
            val lnArg = Complex(a - sqrtVal.imaginary, b + sqrtVal.real)
            val lnVal = with(expLogOps) { lnArg.ln() }
            return Complex(lnVal.imaginary, -lnVal.real)
        }

        override fun Complex<Double>.atan(): Complex<Double> {
            val a = real; val b = imaginary
            val ratio = with(arithmeticOps) {
                Complex(a, 1.0 + b).divide(Complex(-a, 1.0 - b))
            }
            val lnVal = with(expLogOps) { ratio.ln() }
            return Complex(-lnVal.imaginary / 2.0, lnVal.real / 2.0)
        }

        override fun Complex<Double>.asinh(): Complex<Double> {
            val a = real; val b = imaginary
            val z2re = a * a - b * b; val z2im = 2.0 * a * b
            val sqrtVal = with(sqrtOps) { Complex(z2re + 1.0, z2im).sqrt() }
            val lnArg = Complex(a + sqrtVal.real, b + sqrtVal.imaginary)
            return with(expLogOps) { lnArg.ln() }
        }

        override fun Complex<Double>.acosh(): Complex<Double> {
            val a = real; val b = imaginary
            val z2re = a * a - b * b; val z2im = 2.0 * a * b
            val sqrtVal = with(sqrtOps) { Complex(z2re - 1.0, z2im).sqrt() }
            val lnArg = Complex(a + sqrtVal.real, b + sqrtVal.imaginary)
            return with(expLogOps) { lnArg.ln() }
        }

        override fun Complex<Double>.atanh(): Complex<Double> {
            val a = real; val b = imaginary
            val ratio = with(arithmeticOps) {
                Complex(1.0 + a, b).divide(Complex(1.0 - a, -b))
            }
            val lnVal = with(expLogOps) { ratio.ln() }
            return Complex(lnVal.real / 2.0, lnVal.imaginary / 2.0)
        }
    }

val ComplexTrigonometry.Companion.float: ComplexTrigonometry<Complex<Float>, Float> get() = floatInstance
val ComplexTrigonometry.Companion.double: ComplexTrigonometry<Complex<Double>, Double> get() = doubleInstance
