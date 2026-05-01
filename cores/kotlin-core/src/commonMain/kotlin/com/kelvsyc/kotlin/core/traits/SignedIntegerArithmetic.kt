package com.kelvsyc.kotlin.core.traits

/**
 * `SignedIntegerArithmetic` is a trait type extending [IntegerArithmetic] with operations that are only meaningful
 * for signed integer types: negation, absolute value, floor division, floor remainder, and ceiling division.
 *
 * Unsigned integer types do not have negative values, so these operations are not defined for them at the
 * arithmetic level. Signed types ([Int], [Long], etc.) implement this sub-interface in addition to the base
 * [IntegerArithmetic].
 *
 * ## Division and remainder semantics
 *
 * This trait exposes three division rounding modes:
 *
 * - [IntegerArithmetic.divide] and [IntegerArithmetic.rem] truncate toward zero; the remainder has the sign of
 *   the dividend. These match Kotlin's `/` and `%` operators.
 * - [floorDiv] and [mod] round toward negative infinity; the remainder has the sign of the divisor. These
 *   match Kotlin's `floorDiv` and `mod` extensions.
 * - [CeilDiv.ceilDiv] rounds toward positive infinity.
 *
 * The floor pair satisfies `a == b.multiply(a.floorDiv(b)).add(a.mod(b))` for all non-zero `b`.
 *
 * ## Overflow behavior
 *
 * Inherited from [IntegerArithmetic]: overflow behavior is implementation-defined. For wrapping implementations,
 * [negate] wraps on `MIN_VALUE` (no positive representable result), [abs] wraps on `MIN_VALUE`, and
 * [floorDiv] wraps on `MIN_VALUE.floorDiv(-1)` (same overflow case as `MIN_VALUE / -1`).
 * See [OverflowCheckedSignedArithmetic] for implementations that throw instead.
 *
 * ## Standard implementations
 *
 * Canonical wrapping instances for [Int] and [Long] are available as [Companion.int] and [Companion.long].
 */
interface SignedIntegerArithmetic<T> : IntegerArithmetic<T>, CeilDiv<T> {
    /**
     * Returns the negation of this value. Overflow behavior is implementation-defined.
     *
     * This is intentionally named `negate` rather than `unaryMinus` to avoid shadowing by the member function
     * `Int.unaryMinus()` (and equivalent members on other primitive types), which would take dispatch priority
     * over this member extension inside a `with(ops)` block and make this override unreachable.
     */
    fun T.negate(): T

    /**
     * Returns the absolute value of this value. Overflow behavior is implementation-defined.
     */
    fun T.abs(): T

    /**
     * Returns `true` if this value is strictly less than [zero].
     */
    fun T.isNegative(): Boolean = compareTo(zero) < 0

    /**
     * Returns `true` if this value is strictly greater than [zero].
     */
    fun T.isPositive(): Boolean = compareTo(zero) > 0

    /**
     * Returns `true` if this value is equal to [zero].
     */
    fun T.isZero(): Boolean = compareTo(zero) == 0

    /**
     * Returns `-1`, `0`, or `1` as this value is negative, zero, or positive respectively.
     *
     * Intentionally named `sign` rather than `signum` to avoid shadowing by `BigInteger.signum(): Int`,
     * which is a class member that would win at call sites and return the wrong type.
     *
     * Overflow behavior for [negate] is inherited from the implementation; the only value affected is the
     * negation of [one] (i.e. `-1`), which is always representable for any signed integer type.
     */
    fun T.sign(): T = when {
        isNegative() -> one.negate()
        isPositive() -> one
        else -> zero
    }

    /**
     * Returns the quotient of this value divided by [other], rounded toward negative infinity.
     *
     * Throws [ArithmeticException] if [other] is zero. Overflow behavior is implementation-defined;
     * the only overflow case is `MIN_VALUE.floorDiv(-1)` — the same case as for [IntegerArithmetic.divide].
     *
     * Contrast with [IntegerArithmetic.divide], which truncates toward zero.
     */
    fun T.floorDiv(other: T): T {
        val q = divide(other)
        val r = rem(other)
        return if (!r.isZero() && r.isNegative() != other.isNegative()) q.subtract(one) else q
    }

    /**
     * Returns the remainder of floor division of this value by [other].
     *
     * The result has the same sign as [other] (or is zero), and satisfies
     * `this == other.multiply(this.floorDiv(other)).add(this.mod(other))`.
     * Throws [ArithmeticException] if [other] is zero.
     *
     * Contrast with [IntegerArithmetic.rem], which uses truncated-division semantics and produces a result
     * with the sign of the dividend.
     */
    fun T.mod(other: T): T {
        val r = rem(other)
        return if (!r.isZero() && r.isNegative() != other.isNegative()) r.add(other) else r
    }

    /**
     * Returns the quotient of this value divided by [other], rounded toward positive infinity.
     *
     * Throws [ArithmeticException] if [other] is zero. Overflow behavior is implementation-defined;
     * the only overflow case is `MIN_VALUE.ceilDiv(-1)` — the same case as for [IntegerArithmetic.divide].
     *
     * Contrast with [IntegerArithmetic.divide] (truncates toward zero) and [floorDiv] (rounds toward
     * negative infinity).
     */
    override fun T.ceilDiv(divisor: T): T {
        val q = divide(divisor)
        val r = rem(divisor)
        return if (!r.isZero() && r.isNegative() == divisor.isNegative()) q.add(one) else q
    }

    companion object
}

// Capture stdlib floorDiv/mod at file scope to avoid member-extension dispatch issues inside the anonymous
// objects below. Inside an object implementing SignedIntegerArithmetic<Int>, the member extensions
// `fun Int.floorDiv` and `fun Int.mod` from the interface take priority over the stdlib extension functions
// of the same signature. Capturing them here — where no such override is in scope — resolves them to the
// stdlib versions. The same pattern is used for Float::isNaN in FloatingPointArithmetic.
private val _intFloorDiv: (Int, Int) -> Int = { a, b -> a.floorDiv(b) }
private val _intMod: (Int, Int) -> Int = { a, b -> a.mod(b) }
private val _longFloorDiv: (Long, Long) -> Long = { a, b -> a.floorDiv(b) }
private val _longMod: (Long, Long) -> Long = { a, b -> a.mod(b) }

private val intInstance: SignedIntegerArithmetic<Int> = object : SignedIntegerArithmetic<Int> {
    override val zero: Int get() = 0
    override val one: Int get() = 1

    override fun Int.add(other: Int): Int = this + other
    override fun Int.subtract(other: Int): Int = this - other
    override fun Int.multiply(other: Int): Int = this * other
    override fun Int.divide(other: Int): Int = this / other
    override fun Int.rem(other: Int): Int = this % other
    override fun Int.compareTo(other: Int): Int = this.compareTo(other)

    override fun Int.negate(): Int = -this
    override fun Int.abs(): Int = kotlin.math.abs(this)

    override fun Int.floorDiv(other: Int): Int = _intFloorDiv(this, other)
    override fun Int.mod(other: Int): Int = _intMod(this, other)
}

private val longInstance: SignedIntegerArithmetic<Long> = object : SignedIntegerArithmetic<Long> {
    override val zero: Long get() = 0L
    override val one: Long get() = 1L

    override fun Long.add(other: Long): Long = this + other
    override fun Long.subtract(other: Long): Long = this - other
    override fun Long.multiply(other: Long): Long = this * other
    override fun Long.divide(other: Long): Long = this / other
    override fun Long.rem(other: Long): Long = this % other
    override fun Long.compareTo(other: Long): Int = this.compareTo(other)

    override fun Long.negate(): Long = -this
    override fun Long.abs(): Long = kotlin.math.abs(this)

    override fun Long.floorDiv(other: Long): Long = _longFloorDiv(this, other)
    override fun Long.mod(other: Long): Long = _longMod(this, other)
}

val SignedIntegerArithmetic.Companion.int: SignedIntegerArithmetic<Int>
    get() = intInstance

val SignedIntegerArithmetic.Companion.long: SignedIntegerArithmetic<Long>
    get() = longInstance
