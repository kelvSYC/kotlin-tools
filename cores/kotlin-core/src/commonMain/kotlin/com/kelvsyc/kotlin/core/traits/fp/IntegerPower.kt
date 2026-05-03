package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.BFloat16
import com.kelvsyc.kotlin.core.Float16
import com.kelvsyc.kotlin.core.fp.DoubleDouble
import com.kelvsyc.kotlin.core.fp.times

/**
 * `IntegerPower` is a trait providing exponentiation by a non-negative integer exponent for type [T].
 *
 * `pow(x, n)` computes `x^n` — the product of `x` with itself `n` times. The exponent [n] must be
 * non-negative; negative exponents are rejected with [IllegalArgumentException]. This restriction is
 * necessary because the trait is shared between **integer** types (where `x^-n` is generally not an
 * integer) and **floating-point** types (where it would be `1 / x^n`); a single default would be
 * wrong for at least one camp.
 *
 * `pow(x, 0)` returns the multiplicative identity (`1`) for all `x`, including zero and NaN, as is
 * conventional for exponentiation in algebraic structures. Callers that require IEEE 754 `pow(NaN, 0)
 * = NaN` semantics should use [kotlin.math.pow] or a type-specific method instead.
 *
 * The default implementation uses binary exponentiation (repeated squaring), requiring O(log n)
 * multiplications. Concrete instances may override with a more efficient or more numerically stable
 * version.
 *
 * Standard implementations are available as companion extension properties.
 * Integer types: [Companion.int], [Companion.long] (and [Companion.bigInteger] on JVM).
 * Binary floating-point: [Companion.float], [Companion.double], [Companion.bfloat16],
 * [Companion.float16], [Companion.doubleDouble].
 * Decimal floating-point: [Companion.bidFloat], [Companion.bidDouble], [Companion.dpdFloat],
 * [Companion.dpdDouble].
 */
interface IntegerPower<T> {
    companion object

    /**
     * Returns `this` raised to the power [n].
     *
     * @throws IllegalArgumentException if [n] is negative.
     */
    fun T.pow(n: Int): T
}

/**
 * Binary exponentiation (repeated squaring) over an arbitrary type.
 *
 * @param one the multiplicative identity for [T].
 * @param multiply the binary multiplication for [T].
 */
internal fun <T> binaryPow(base: T, n: Int, one: T, multiply: (T, T) -> T): T {
    require(n >= 0) { "Exponent must be non-negative, got $n" }
    if (n == 0) return one
    if (n == 1) return base
    var result = one
    var b = base
    var exp = n
    while (exp > 0) {
        if (exp and 1 != 0) result = multiply(result, b)
        b = multiply(b, b)
        exp = exp ushr 1
    }
    return result
}

// ── Int ───────────────────────────────────────────────────────────────────────

private val intInstance: IntegerPower<Int> = object : IntegerPower<Int> {
    override fun Int.pow(n: Int): Int = binaryPow(this, n, 1) { a, b -> a * b }
}

// ── Long ──────────────────────────────────────────────────────────────────────

private val longInstance: IntegerPower<Long> = object : IntegerPower<Long> {
    override fun Long.pow(n: Int): Long = binaryPow(this, n, 1L) { a, b -> a * b }
}

// ── Float ─────────────────────────────────────────────────────────────────────

private val floatInstance: IntegerPower<Float> = object : IntegerPower<Float> {
    override fun Float.pow(n: Int): Float = binaryPow(this, n, 1.0f) { a, b -> a * b }
}

// ── Double ────────────────────────────────────────────────────────────────────

private val doubleInstance: IntegerPower<Double> = object : IntegerPower<Double> {
    override fun Double.pow(n: Int): Double = binaryPow(this, n, 1.0) { a, b -> a * b }
}

// ── BFloat16 ──────────────────────────────────────────────────────────────────

private val bfloat16Instance: IntegerPower<BFloat16> = object : IntegerPower<BFloat16> {
    override fun BFloat16.pow(n: Int): BFloat16 =
        binaryPow(this, n, BFloat16(0x3F80.toShort())) { a, b -> a.calculate { it * b.toFloat() } }
}

// ── Float16 ───────────────────────────────────────────────────────────────────

private val float16Instance: IntegerPower<Float16> = object : IntegerPower<Float16> {
    override fun Float16.pow(n: Int): Float16 =
        binaryPow(this, n, Float16(0x3C00.toShort())) { a, b -> a.calculate { it * b.toFloat() } }
}

// ── DoubleDouble ──────────────────────────────────────────────────────────────

private val doubleDoubleInstance: IntegerPower<DoubleDouble> = object : IntegerPower<DoubleDouble> {
    override fun DoubleDouble.pow(n: Int): DoubleDouble =
        binaryPow(this, n, DoubleDouble.ONE) { a, b -> a * b }
}

val IntegerPower.Companion.int: IntegerPower<Int> get() = intInstance
val IntegerPower.Companion.long: IntegerPower<Long> get() = longInstance
val IntegerPower.Companion.float: IntegerPower<Float> get() = floatInstance
val IntegerPower.Companion.double: IntegerPower<Double> get() = doubleInstance
val IntegerPower.Companion.bfloat16: IntegerPower<BFloat16> get() = bfloat16Instance
val IntegerPower.Companion.float16: IntegerPower<Float16> get() = float16Instance
val IntegerPower.Companion.doubleDouble: IntegerPower<DoubleDouble> get() = doubleDoubleInstance
