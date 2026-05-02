package com.kelvsyc.kotlin.core.traits

import com.kelvsyc.kotlin.core.Complex

/**
 * `ComplexArithmetic` is a trait providing arithmetic operations over [Complex] values of
 * component type [T].
 *
 * Two implementations are available per supported component type:
 * - **Naive**: textbook formulas, matching common C/C++ complex arithmetic behaviour.
 * - **Strict**: FMA-based multiplication and Smith's-method division, targeting C99 Annex G
 *   semantics to the extent the platform allows. Correctly handles cases where naive formulas
 *   produce spurious NaN results due to infinity × 0 cancellation.
 *
 * Standard instances for [Float] and [Double] are available as [Companion.naiveFloat],
 * [Companion.strictFloat], [Companion.naiveDouble], and [Companion.strictDouble].
 */
interface ComplexArithmetic<T> {
    /**
     * Structural metadata and sign operations for the component type [T].
     */
    val componentTraits: IeeeBinaryFloatingPoint<T>

    /**
     * The additive identity: `0 + i·0`.
     */
    val zero: Complex<T> get() = Complex(componentTraits.positiveZero, componentTraits.positiveZero)

    /**
     * The multiplicative identity: `1 + i·0`.
     */
    val one: Complex<T>

    /**
     * Returns the negation `-(a + i·b) = (-a) + i·(-b)`.
     */
    fun Complex<T>.negate(): Complex<T>

    /**
     * Returns the complex conjugate `conj(a + i·b) = a + i·(-b)`.
     */
    fun Complex<T>.conjugate(): Complex<T>

    /**
     * Returns the sum `(a + i·b) + (c + i·d) = (a+c) + i·(b+d)`.
     */
    fun Complex<T>.add(other: Complex<T>): Complex<T>

    /**
     * Returns the difference `(a + i·b) - (c + i·d) = (a-c) + i·(b-d)`.
     */
    fun Complex<T>.subtract(other: Complex<T>): Complex<T>

    /**
     * Returns the product `(a + i·b) × (c + i·d) = (ac-bd) + i·(ad+bc)`.
     *
     * Naive implementations use the textbook formula directly. Strict implementations use FMA for
     * the real and imaginary parts and apply Annex G fix-up to recover a finite infinity when both
     * naive results are NaN due to an ∞ × 0 cancellation.
     */
    fun Complex<T>.multiply(other: Complex<T>): Complex<T>

    /**
     * Returns the quotient `(a + i·b) / (c + i·d)`.
     *
     * Naive implementations use the textbook formula `((ac+bd) + i·(bc-ad)) / (c²+d²)`, which
     * can overflow or underflow in the denominator. Strict implementations use Smith's method,
     * branching on `|c|` vs `|d|` to keep intermediate values in range.
     */
    fun Complex<T>.divide(other: Complex<T>): Complex<T>

    companion object
}

// ── Helpers ───────────────────────────────────────────────────────────────────

// copysign(1.0f, x): ±1 with the sign of x (used in Annex G fix-up).
private fun copysign1f(x: Float): Float =
    Float.fromBits(0x3F800000 or (x.toRawBits() and Int.MIN_VALUE))

// copysign(0.0f, x): signed zero with the sign of x.
private fun copysign0f(x: Float): Float =
    Float.fromBits(x.toRawBits() and Int.MIN_VALUE)

// Annex G-inspired fix-up: if both parts of the naive result are NaN and at least one operand
// is infinite, recover the correctly-signed infinity result.
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

private val floatNaiveInstance: ComplexArithmetic<Float> = object : ComplexArithmetic<Float> {
    override val componentTraits: IeeeBinaryFloatingPoint<Float> get() = Binary32
    override val one: Complex<Float> get() = Complex(1.0f, 0.0f)

    override fun Complex<Float>.negate() = Complex(-real, -imaginary)
    override fun Complex<Float>.conjugate() = Complex(real, -imaginary)
    override fun Complex<Float>.add(other: Complex<Float>) =
        Complex(real + other.real, imaginary + other.imaginary)
    override fun Complex<Float>.subtract(other: Complex<Float>) =
        Complex(real - other.real, imaginary - other.imaginary)

    // Textbook: (ac-bd) + i(ad+bc). No special-value handling.
    override fun Complex<Float>.multiply(other: Complex<Float>): Complex<Float> {
        val (a, b) = this; val (c, d) = other
        return Complex(a * c - b * d, a * d + b * c)
    }

    // Textbook: denom = c²+d², re = (ac+bd)/denom, im = (bc-ad)/denom.
    override fun Complex<Float>.divide(other: Complex<Float>): Complex<Float> {
        val (a, b) = this; val (c, d) = other
        val denom = c * c + d * d
        return Complex((a * c + b * d) / denom, (b * c - a * d) / denom)
    }
}

// ── Float — strict (Annex G + FMA) ────────────────────────────────────────────

private val floatStrictInstance: ComplexArithmetic<Float> = object : ComplexArithmetic<Float> {
    override val componentTraits: IeeeBinaryFloatingPoint<Float> get() = Binary32
    override val one: Complex<Float> get() = Complex(1.0f, 0.0f)

    override fun Complex<Float>.negate() = Complex(-real, -imaginary)
    override fun Complex<Float>.conjugate() = Complex(real, -imaginary)
    override fun Complex<Float>.add(other: Complex<Float>) =
        Complex(real + other.real, imaginary + other.imaginary)
    override fun Complex<Float>.subtract(other: Complex<Float>) =
        Complex(real - other.real, imaginary - other.imaginary)

    // FMA reduces (ac-bd) and (ad+bc) to a single rounding error each, then the Annex G
    // fix-up recovers a finite infinity when both parts are NaN due to ∞×0 cancellation.
    override fun Complex<Float>.multiply(other: Complex<Float>): Complex<Float> {
        val (a, b) = this; val (c, d) = other
        val fma = FusedMultiplyAdd.float
        val re = fma.fma(a, c, -(b * d))
        val im = fma.fma(a, d, b * c)
        return annexGFixupFloat(re, im, a, b, c, d)
    }

    // Smith's method: branch on |c| vs |d| to keep the intermediate ratio r = d/c (or c/d)
    // in [−1, 1], preventing overflow in the denominator. No Annex G infinity fix-up is applied
    // here; special values propagate naturally through the branching arithmetic.
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

private val doubleNaiveInstance: ComplexArithmetic<Double> = object : ComplexArithmetic<Double> {
    override val componentTraits: IeeeBinaryFloatingPoint<Double> get() = Binary64
    override val one: Complex<Double> get() = Complex(1.0, 0.0)

    override fun Complex<Double>.negate() = Complex(-real, -imaginary)
    override fun Complex<Double>.conjugate() = Complex(real, -imaginary)
    override fun Complex<Double>.add(other: Complex<Double>) =
        Complex(real + other.real, imaginary + other.imaginary)
    override fun Complex<Double>.subtract(other: Complex<Double>) =
        Complex(real - other.real, imaginary - other.imaginary)

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

private val doubleStrictInstance: ComplexArithmetic<Double> = object : ComplexArithmetic<Double> {
    override val componentTraits: IeeeBinaryFloatingPoint<Double> get() = Binary64
    override val one: Complex<Double> get() = Complex(1.0, 0.0)

    override fun Complex<Double>.negate() = Complex(-real, -imaginary)
    override fun Complex<Double>.conjugate() = Complex(real, -imaginary)
    override fun Complex<Double>.add(other: Complex<Double>) =
        Complex(real + other.real, imaginary + other.imaginary)
    override fun Complex<Double>.subtract(other: Complex<Double>) =
        Complex(real - other.real, imaginary - other.imaginary)

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

val ComplexArithmetic.Companion.naiveFloat: ComplexArithmetic<Float> get() = floatNaiveInstance
val ComplexArithmetic.Companion.strictFloat: ComplexArithmetic<Float> get() = floatStrictInstance
val ComplexArithmetic.Companion.naiveDouble: ComplexArithmetic<Double> get() = doubleNaiveInstance
val ComplexArithmetic.Companion.strictDouble: ComplexArithmetic<Double> get() = doubleStrictInstance
