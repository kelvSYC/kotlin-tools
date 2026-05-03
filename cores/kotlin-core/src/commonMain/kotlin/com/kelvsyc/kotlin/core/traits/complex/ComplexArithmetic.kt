package com.kelvsyc.kotlin.core.traits.complex

import com.kelvsyc.kotlin.core.Complex
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointArithmetic
import com.kelvsyc.kotlin.core.traits.fp.FusedMultiplyAdd
import com.kelvsyc.kotlin.core.traits.fp.double
import com.kelvsyc.kotlin.core.traits.fp.float

/**
 * `ComplexArithmetic` is a trait providing arithmetic operations over complex values of type [C]
 * whose components are of type [T].
 *
 * Two implementations are available per supported component type:
 * - **Naive**: textbook formulas, matching common C/C++ complex arithmetic behaviour.
 * - **Strict**: FMA-based multiplication and Smith's-method division, targeting C99 Annex G
 *   semantics to the extent the platform allows. Correctly handles cases where naive formulas
 *   produce spurious NaN results due to infinity × 0 cancellation.
 *
 * Default implementations are provided for [negate], [conjugate], [add], and [subtract] using
 * [componentArithmetic]. Implementations must override [multiply] and [divide] as these have
 * fundamentally different algorithms (naive vs strict) with no universal default.
 *
 * Standard instances for [Complex]<[Float]> and [Complex]<[Double]> are available as
 * [Companion.naiveFloat], [Companion.strictFloat], [Companion.naiveDouble], and
 * [Companion.strictDouble].
 */
interface ComplexArithmetic<C, T> {
    /**
     * Floating-point arithmetic for the component type [T].
     */
    val componentArithmetic: FloatingPointArithmetic<T>

    /**
     * Returns the real component of this complex value.
     */
    fun C.real(): T

    /**
     * Returns the imaginary component of this complex value.
     */
    fun C.imaginary(): T

    /**
     * Constructs a [C] value from real and imaginary components.
     */
    fun of(real: T, imaginary: T): C

    /**
     * The additive identity: `0 + i·0`.
     */
    val zero: C get() = of(componentArithmetic.zero, componentArithmetic.zero)

    /**
     * The multiplicative identity: `1 + i·0`.
     */
    val one: C get() = of(componentArithmetic.one, componentArithmetic.zero)

    /**
     * Returns the negation `-(a + i·b) = (-a) + i·(-b)`.
     */
    fun C.negate(): C {
        val r = real(); val i = imaginary()
        with(componentArithmetic) { return of(r.negate(), i.negate()) }
    }

    /**
     * Returns the complex conjugate `conj(a + i·b) = a + i·(-b)`.
     */
    fun C.conjugate(): C {
        val r = real(); val i = imaginary()
        with(componentArithmetic) { return of(r, i.negate()) }
    }

    /**
     * Returns the sum `(a + i·b) + (c + i·d) = (a+c) + i·(b+d)`.
     */
    fun C.add(other: C): C {
        val r1 = real(); val i1 = imaginary()
        val r2 = other.real(); val i2 = other.imaginary()
        with(componentArithmetic) { return of(r1.add(r2), i1.add(i2)) }
    }

    /**
     * Returns the difference `(a + i·b) - (c + i·d) = (a-c) + i·(b-d)`.
     */
    fun C.subtract(other: C): C {
        val r1 = real(); val i1 = imaginary()
        val r2 = other.real(); val i2 = other.imaginary()
        with(componentArithmetic) { return of(r1.subtract(r2), i1.subtract(i2)) }
    }

    /**
     * Returns the product `(a + i·b) × (c + i·d) = (ac-bd) + i·(ad+bc)`.
     *
     * Naive implementations use the textbook formula directly. Strict implementations use FMA for
     * the real and imaginary parts and apply Annex G fix-up to recover a finite infinity when both
     * naive results are NaN due to an ∞ × 0 cancellation.
     */
    fun C.multiply(other: C): C

    /**
     * Returns the quotient `(a + i·b) / (c + i·d)`.
     *
     * Naive implementations use the textbook formula `((ac+bd) + i·(bc-ad)) / (c²+d²)`, which
     * can overflow or underflow in the denominator. Strict implementations use Smith's method,
     * branching on `|c|` vs `|d|` to keep intermediate values in range.
     */
    fun C.divide(other: C): C

    companion object
}

// ── Helpers ───────────────────────────────────────────────────────────────────

private fun copysign1f(x: Float): Float =
    Float.fromBits(0x3F800000 or (x.toRawBits() and Int.MIN_VALUE))

private fun copysign0f(x: Float): Float =
    Float.fromBits(x.toRawBits() and Int.MIN_VALUE)

private fun annexGFixupFloat(
    reNaive: Float, imNaive: Float,
    a: Float, b: Float, c: Float, d: Float
): Complex<Float> {
    var re = reNaive; var im = imNaive
    if (re.isNaN() && im.isNaN()) {
        var recalc = false
        var ra = a; var rb = b; var rc = c; var rd = d
        if (ra.isInfinite() || rb.isInfinite()) {
            ra = if (ra.isInfinite()) copysign1f(ra) else 0f
            rb = if (rb.isInfinite()) copysign1f(rb) else 0f
            if (rc.isNaN()) rc = copysign0f(rc)
            if (rd.isNaN()) rd = copysign0f(rd)
            recalc = true
        }
        if (rc.isInfinite() || rd.isInfinite()) {
            rc = if (rc.isInfinite()) copysign1f(rc) else 0f
            rd = if (rd.isInfinite()) copysign1f(rd) else 0f
            if (ra.isNaN()) ra = copysign0f(ra)
            if (rb.isNaN()) rb = copysign0f(rb)
            recalc = true
        }
        if (recalc) {
            re = Float.POSITIVE_INFINITY * (ra * rc - rb * rd)
            im = Float.POSITIVE_INFINITY * (ra * rd + rb * rc)
        }
    }
    return Complex(re, im)
}

private fun copysign1d(x: Double): Double =
    Double.fromBits(0x3FF0000000000000L or (x.toRawBits() and Long.MIN_VALUE))

private fun copysign0d(x: Double): Double =
    Double.fromBits(x.toRawBits() and Long.MIN_VALUE)

private fun annexGFixupDouble(
    reNaive: Double, imNaive: Double,
    a: Double, b: Double, c: Double, d: Double
): Complex<Double> {
    var re = reNaive; var im = imNaive
    if (re.isNaN() && im.isNaN()) {
        var recalc = false
        var ra = a; var rb = b; var rc = c; var rd = d
        if (ra.isInfinite() || rb.isInfinite()) {
            ra = if (ra.isInfinite()) copysign1d(ra) else 0.0
            rb = if (rb.isInfinite()) copysign1d(rb) else 0.0
            if (rc.isNaN()) rc = copysign0d(rc)
            if (rd.isNaN()) rd = copysign0d(rd)
            recalc = true
        }
        if (rc.isInfinite() || rd.isInfinite()) {
            rc = if (rc.isInfinite()) copysign1d(rc) else 0.0
            rd = if (rd.isInfinite()) copysign1d(rd) else 0.0
            if (ra.isNaN()) ra = copysign0d(ra)
            if (rb.isNaN()) rb = copysign0d(rb)
            recalc = true
        }
        if (recalc) {
            re = Double.POSITIVE_INFINITY * (ra * rc - rb * rd)
            im = Double.POSITIVE_INFINITY * (ra * rd + rb * rc)
        }
    }
    return Complex(re, im)
}

// ── Float — naive ─────────────────────────────────────────────────────────────

private val floatNaiveInstance: ComplexArithmetic<Complex<Float>, Float> =
    object : ComplexArithmetic<Complex<Float>, Float> {
        override val componentArithmetic: FloatingPointArithmetic<Float> get() = FloatingPointArithmetic.float
        override fun Complex<Float>.real(): Float = real
        override fun Complex<Float>.imaginary(): Float = imaginary
        override fun of(real: Float, imaginary: Float): Complex<Float> = Complex(real, imaginary)

        override fun Complex<Float>.multiply(other: Complex<Float>): Complex<Float> {
            val (a, b) = this; val (c, d) = other
            return Complex(a * c - b * d, a * d + b * c)
        }

        override fun Complex<Float>.divide(other: Complex<Float>): Complex<Float> {
            val (a, b) = this; val (c, d) = other
            val denom = c * c + d * d
            return Complex((a * c + b * d) / denom, (b * c - a * d) / denom)
        }
    }

// ── Float — strict (Annex G + FMA) ────────────────────────────────────────────

private val floatStrictInstance: ComplexArithmetic<Complex<Float>, Float> =
    object : ComplexArithmetic<Complex<Float>, Float> {
        override val componentArithmetic: FloatingPointArithmetic<Float> get() = FloatingPointArithmetic.float
        override fun Complex<Float>.real(): Float = real
        override fun Complex<Float>.imaginary(): Float = imaginary
        override fun of(real: Float, imaginary: Float): Complex<Float> = Complex(real, imaginary)

        override fun Complex<Float>.multiply(other: Complex<Float>): Complex<Float> {
            val (a, b) = this; val (c, d) = other
            val fma = FusedMultiplyAdd.float
            val re = fma.fma(a, c, -(b * d))
            val im = fma.fma(a, d, b * c)
            return annexGFixupFloat(re, im, a, b, c, d)
        }

        override fun Complex<Float>.divide(other: Complex<Float>): Complex<Float> {
            val (a, b) = this; val (c, d) = other
            return if (kotlin.math.abs(c) >= kotlin.math.abs(d)) {
                val r = d / c
                val denom = c + d * r
                Complex((a + b * r) / denom, (b - a * r) / denom)
            } else {
                val r = c / d
                val denom = c * r + d
                Complex((a * r + b) / denom, (b * r - a) / denom)
            }
        }
    }

// ── Double — naive ────────────────────────────────────────────────────────────

private val doubleNaiveInstance: ComplexArithmetic<Complex<Double>, Double> =
    object : ComplexArithmetic<Complex<Double>, Double> {
        override val componentArithmetic: FloatingPointArithmetic<Double> get() = FloatingPointArithmetic.double
        override fun Complex<Double>.real(): Double = real
        override fun Complex<Double>.imaginary(): Double = imaginary
        override fun of(real: Double, imaginary: Double): Complex<Double> = Complex(real, imaginary)

        override fun Complex<Double>.multiply(other: Complex<Double>): Complex<Double> {
            val (a, b) = this; val (c, d) = other
            return Complex(a * c - b * d, a * d + b * c)
        }

        override fun Complex<Double>.divide(other: Complex<Double>): Complex<Double> {
            val (a, b) = this; val (c, d) = other
            val denom = c * c + d * d
            return Complex((a * c + b * d) / denom, (b * c - a * d) / denom)
        }
    }

// ── Double — strict (Annex G + FMA) ───────────────────────────────────────────

private val doubleStrictInstance: ComplexArithmetic<Complex<Double>, Double> =
    object : ComplexArithmetic<Complex<Double>, Double> {
        override val componentArithmetic: FloatingPointArithmetic<Double> get() = FloatingPointArithmetic.double
        override fun Complex<Double>.real(): Double = real
        override fun Complex<Double>.imaginary(): Double = imaginary
        override fun of(real: Double, imaginary: Double): Complex<Double> = Complex(real, imaginary)

        override fun Complex<Double>.multiply(other: Complex<Double>): Complex<Double> {
            val (a, b) = this; val (c, d) = other
            val fma = FusedMultiplyAdd.double
            val re = fma.fma(a, c, -(b * d))
            val im = fma.fma(a, d, b * c)
            return annexGFixupDouble(re, im, a, b, c, d)
        }

        override fun Complex<Double>.divide(other: Complex<Double>): Complex<Double> {
            val (a, b) = this; val (c, d) = other
            return if (kotlin.math.abs(c) >= kotlin.math.abs(d)) {
                val r = d / c
                val denom = c + d * r
                Complex((a + b * r) / denom, (b - a * r) / denom)
            } else {
                val r = c / d
                val denom = c * r + d
                Complex((a * r + b) / denom, (b * r - a) / denom)
            }
        }
    }

val ComplexArithmetic.Companion.naiveFloat: ComplexArithmetic<Complex<Float>, Float> get() = floatNaiveInstance
val ComplexArithmetic.Companion.strictFloat: ComplexArithmetic<Complex<Float>, Float> get() = floatStrictInstance
val ComplexArithmetic.Companion.naiveDouble: ComplexArithmetic<Complex<Double>, Double> get() = doubleNaiveInstance
val ComplexArithmetic.Companion.strictDouble: ComplexArithmetic<Complex<Double>, Double> get() = doubleStrictInstance
