package com.kelvsyc.kotlin.core.traits

/**
 * `SignedIntegerArithmetic` is a trait type extending [IntegerArithmetic] with operations that are only meaningful
 * for signed integer types: negation and absolute value.
 *
 * Unsigned integer types do not have negative values, so [unaryMinus] and [abs] are not defined for them at the
 * arithmetic level. Signed types ([Int], [Long], etc.) implement this sub-interface in addition to the base
 * [IntegerArithmetic].
 *
 * ## Overflow behavior
 *
 * Inherited from [IntegerArithmetic]: overflow behavior is implementation-defined. For wrapping implementations,
 * [unaryMinus] wraps on [Int.MIN_VALUE] and [abs] wraps on [Int.MIN_VALUE] (since the mathematical absolute value
 * exceeds [Int.MAX_VALUE]). See [OverflowCheckedSignedArithmetic] for implementations that throw instead.
 *
 * ## Standard implementations
 *
 * Canonical wrapping instances for [Int] and [Long] are available as [Companion.int] and [Companion.long].
 */
interface SignedIntegerArithmetic<T> : IntegerArithmetic<T> {
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

    companion object
}

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
}

val SignedIntegerArithmetic.Companion.int: SignedIntegerArithmetic<Int>
    get() = intInstance

val SignedIntegerArithmetic.Companion.long: SignedIntegerArithmetic<Long>
    get() = longInstance
