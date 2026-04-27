package com.kelvsyc.kotlin.core.traits

import com.kelvsyc.kotlin.core.BFloat16
import com.kelvsyc.kotlin.core.Float16

/**
 * `FloatingPointArithmetic` is a trait type that provides a uniform interface for basic floating-point arithmetic
 * over a type [T].
 *
 * This trait is intended for generic algorithms that must operate over any floating-point type without knowing the
 * concrete representation. It covers the four fundamental arithmetic operations, negation, absolute value, the three
 * standard IEEE 754 classification predicates, and a total-order comparison. It does not cover remainder or square
 * root; those capabilities are expressed as separate, optional traits.
 *
 * ## Precision and error bounds
 *
 * No precision guarantee is implied by this interface. The standard implementations for [Float] and [Double] follow
 * IEEE 754 binary32 and binary64 semantics respectively — each operation is correctly rounded to the nearest
 * representable value. An implementation for a higher-precision type such as `DoubleDouble` provides approximately
 * doubled mantissa precision, but its error model is fundamentally different and not IEEE 754. Callers that require
 * specific IEEE 754 guarantees should use [Float] or [Double] directly rather than going through this trait.
 *
 * ## Ordering
 *
 * [compareTo] implements a *total* ordering consistent with [Float.compareTo] and [Double.compareTo]. This differs
 * from the IEEE 754 partial order in two ways: NaN is ordered after all finite values and infinities rather than
 * being unordered, and negative zero compares as strictly less than positive zero. Callers that need IEEE 754
 * `compareQuietEqual` semantics should use [BinaryFloatingPoint.numericalEquality] instead.
 *
 * ## Standard implementations
 *
 * Canonical instances for [Float16], [Float], and [Double] are available as [Companion.float16], [Companion.float],
 * and [Companion.double] respectively.
 */
interface FloatingPointArithmetic<T> {
    /**
     * The additive identity: the value such that `x.add(zero) == x` for all finite `x`.
     *
     * For IEEE 754 types this is positive zero.
     */
    val zero: T

    /**
     * The multiplicative identity: the value such that `x.multiply(one) == x` for all finite `x`.
     */
    val one: T

    /**
     * Returns `true` if this value is Not-a-Number (NaN).
     *
     * NaN is unordered: it is not less than, equal to, or greater than any value including itself under IEEE 754
     * semantics. [compareTo] defines a total order that places NaN after all other values.
     */
    fun T.isNaN(): Boolean

    /**
     * Returns `true` if this value is positive or negative infinity.
     */
    fun T.isInfinite(): Boolean

    /**
     * Returns `true` if this value is finite — that is, neither infinity nor NaN.
     */
    fun T.isFinite(): Boolean

    /**
     * Returns the negation of this value.
     *
     * This is a pure sign-bit flip with no rounding: `unaryMinus(unaryMinus(x)) == x` for all values including
     * NaN, infinity, and both zeros.
     */
    fun T.unaryMinus(): T

    /**
     * Returns the absolute value of this value.
     *
     * This is a pure sign-bit clear with no rounding. `abs(NaN)` is NaN; `abs(-0)` is `+0`.
     *
     * The default implementation is correct but delegates through [compareTo] and [unaryMinus]. Override for
     * efficiency when the concrete type supports a direct bit-clear.
     */
    fun T.abs(): T = if (compareTo(zero) < 0) unaryMinus() else this

    /**
     * Returns the sum `this + other`, rounded to the nearest representable value.
     */
    fun T.add(other: T): T

    /**
     * Returns the difference `this - other`, rounded to the nearest representable value.
     */
    fun T.subtract(other: T): T

    /**
     * Returns the product `this * other`, rounded to the nearest representable value.
     */
    fun T.multiply(other: T): T

    /**
     * Returns the quotient `this / other`, rounded to the nearest representable value.
     */
    fun T.divide(other: T): T

    /**
     * Compares this value to [other] using a total ordering.
     *
     * Returns a negative integer, zero, or a positive integer when this value is less than, equal to, or greater
     * than [other] respectively. The ordering places negative zero strictly before positive zero, and NaN after
     * all finite values and infinities — consistent with [Double.compareTo] and [Float.compareTo].
     */
    fun T.compareTo(other: T): Int

    companion object
}

// ── BFloat16 ──────────────────────────────────────────────────────────────────

private val bfloat16Instance: FloatingPointArithmetic<BFloat16> = object : FloatingPointArithmetic<BFloat16> {
    override val zero: BFloat16 get() = BFloat16(0)
    override val one: BFloat16 get() = BFloat16(0x3F80.toShort())  // 1.0 in bfloat16

    override fun BFloat16.isNaN(): Boolean = this.isNaN()
    override fun BFloat16.isInfinite(): Boolean = this.isInfinite()
    override fun BFloat16.isFinite(): Boolean = this.isFinite()

    override fun BFloat16.unaryMinus(): BFloat16 = -this
    override fun BFloat16.abs(): BFloat16 = this.abs()

    override fun BFloat16.add(other: BFloat16): BFloat16 = this + other
    override fun BFloat16.subtract(other: BFloat16): BFloat16 = this - other
    override fun BFloat16.multiply(other: BFloat16): BFloat16 = this * other
    override fun BFloat16.divide(other: BFloat16): BFloat16 = this / other

    override fun BFloat16.compareTo(other: BFloat16): Int = toFloat().compareTo(other.toFloat())
}

// ── Float16 ───────────────────────────────────────────────────────────────────

private val float16Instance: FloatingPointArithmetic<Float16> = object : FloatingPointArithmetic<Float16> {
    override val zero: Float16 get() = Float16(0)
    override val one: Float16 get() = Float16(0x3C00.toShort())  // 1.0 in binary16

    override fun Float16.isNaN(): Boolean = this.isNaN()
    override fun Float16.isInfinite(): Boolean = this.isInfinite()
    override fun Float16.isFinite(): Boolean = this.isFinite()

    override fun Float16.unaryMinus(): Float16 = -this
    override fun Float16.abs(): Float16 = this.abs()

    override fun Float16.add(other: Float16): Float16 = this + other
    override fun Float16.subtract(other: Float16): Float16 = this - other
    override fun Float16.multiply(other: Float16): Float16 = this * other
    override fun Float16.divide(other: Float16): Float16 = this / other

    override fun Float16.compareTo(other: Float16): Int = toFloat().compareTo(other.toFloat())
}

// ── Float ─────────────────────────────────────────────────────────────────────

// Float.isNaN/isInfinite/isFinite are stdlib extension functions, not member functions.
// Inside an anonymous object implementing FloatingPointArithmetic<Float>, calling
// this.isNaN() within override fun Float.isNaN() would dispatch back to the override
// itself, causing infinite recursion. Capturing the references at file scope — where no
// such override is in scope — resolves them to the stdlib extensions instead.
// See UInt16.Companion for the same pattern applied to UShort arithmetic.
private val _floatIsNaN: (Float) -> Boolean = Float::isNaN
private val _floatIsInfinite: (Float) -> Boolean = Float::isInfinite
private val _floatIsFinite: (Float) -> Boolean = Float::isFinite

private val floatInstance: FloatingPointArithmetic<Float> = object : FloatingPointArithmetic<Float> {
    override val zero: Float get() = 0.0f
    override val one: Float get() = 1.0f

    override fun Float.isNaN(): Boolean = _floatIsNaN(this)
    override fun Float.isInfinite(): Boolean = _floatIsInfinite(this)
    override fun Float.isFinite(): Boolean = _floatIsFinite(this)

    override fun Float.unaryMinus(): Float = -this
    override fun Float.abs(): Float = kotlin.math.abs(this)

    override fun Float.add(other: Float): Float = this + other
    override fun Float.subtract(other: Float): Float = this - other
    override fun Float.multiply(other: Float): Float = this * other
    override fun Float.divide(other: Float): Float = this / other

    override fun Float.compareTo(other: Float): Int = this.compareTo(other)
}

// ── Double ────────────────────────────────────────────────────────────────────

// Same dispatch issue as Float above; same fix.
private val _doubleIsNaN: (Double) -> Boolean = Double::isNaN
private val _doubleIsInfinite: (Double) -> Boolean = Double::isInfinite
private val _doubleIsFinite: (Double) -> Boolean = Double::isFinite

private val doubleInstance: FloatingPointArithmetic<Double> = object : FloatingPointArithmetic<Double> {
    override val zero: Double get() = 0.0
    override val one: Double get() = 1.0

    override fun Double.isNaN(): Boolean = _doubleIsNaN(this)
    override fun Double.isInfinite(): Boolean = _doubleIsInfinite(this)
    override fun Double.isFinite(): Boolean = _doubleIsFinite(this)

    override fun Double.unaryMinus(): Double = -this
    override fun Double.abs(): Double = kotlin.math.abs(this)

    override fun Double.add(other: Double): Double = this + other
    override fun Double.subtract(other: Double): Double = this - other
    override fun Double.multiply(other: Double): Double = this * other
    override fun Double.divide(other: Double): Double = this / other

    override fun Double.compareTo(other: Double): Int = this.compareTo(other)
}

val FloatingPointArithmetic.Companion.bfloat16: FloatingPointArithmetic<BFloat16>
    get() = bfloat16Instance

val FloatingPointArithmetic.Companion.float16: FloatingPointArithmetic<Float16>
    get() = float16Instance

val FloatingPointArithmetic.Companion.float: FloatingPointArithmetic<Float>
    get() = floatInstance

val FloatingPointArithmetic.Companion.double: FloatingPointArithmetic<Double>
    get() = doubleInstance
