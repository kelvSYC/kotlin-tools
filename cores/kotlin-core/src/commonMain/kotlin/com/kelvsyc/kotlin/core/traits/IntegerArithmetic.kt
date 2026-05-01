package com.kelvsyc.kotlin.core.traits

/**
 * `IntegerArithmetic` is a trait type providing a uniform interface for basic integer arithmetic over a type [T].
 *
 * This trait covers operations applicable to both signed and unsigned integer types: the four arithmetic operations,
 * remainder, a total-order comparison, and the additive and multiplicative identities. It does not include
 * signed-only operations such as negation and absolute value; those are in [SignedIntegerArithmetic].
 *
 * ## Overflow behavior
 *
 * No specific overflow behavior is guaranteed by this interface. Implementations may wrap silently on overflow
 * (two's complement for signed types, modular arithmetic for unsigned types) or throw [ArithmeticException] — see
 * [OverflowCheckedArithmetic] for a sub-interface with the stricter contract. Code that relies on a specific
 * overflow behavior should not be written generically against this interface.
 *
 * ## Standard implementations
 *
 * Canonical wrapping instances for [Byte], [Short], [Int], and [Long] are available as [Companion.byte],
 * [Companion.short], [Companion.int], and [Companion.long].
 */
interface IntegerArithmetic<T> {
    /**
     * The additive identity: the value such that `x.add(zero) == x` for all `x`.
     */
    val zero: T

    /**
     * The multiplicative identity: the value such that `x.multiply(one) == x` for all `x`.
     */
    val one: T

    /**
     * Returns the sum `this + other`. Overflow behavior is implementation-defined.
     */
    fun T.add(other: T): T

    /**
     * Returns the difference `this - other`. Overflow behavior is implementation-defined.
     */
    fun T.subtract(other: T): T

    /**
     * Returns the product `this * other`. Overflow behavior is implementation-defined.
     */
    fun T.multiply(other: T): T

    /**
     * Returns the quotient of this value divided by [other], truncated toward zero.
     *
     * Throws [ArithmeticException] if [other] is zero. Overflow behavior is implementation-defined; see
     * [OverflowCheckedArithmetic] for an implementation that also throws on overflow.
     */
    fun T.divide(other: T): T

    /**
     * Returns the remainder of dividing this value by [other], using truncated-division semantics.
     *
     * The result has the same sign as the dividend (this value), or is zero. Throws [ArithmeticException]
     * if [other] is zero. Matches Kotlin's `%` operator.
     *
     * For signed types, [SignedIntegerArithmetic.mod] provides the floor-division counterpart, which
     * produces a result with the sign of the divisor instead.
     */
    fun T.rem(other: T): T

    /**
     * Compares this value to [other], returning a negative integer, zero, or a positive integer when this value
     * is less than, equal to, or greater than [other] respectively.
     */
    fun T.compareTo(other: T): Int

    companion object
}

// Byte and Short arithmetic operations in Kotlin widen to Int; results are narrowed back with
// toByte()/toShort(). This gives the correct wrapping behavior for each type's bit width.

private val byteInstance: IntegerArithmetic<Byte> = object : IntegerArithmetic<Byte> {
    override val zero: Byte get() = 0
    override val one: Byte get() = 1
    override fun Byte.add(other: Byte): Byte = (this + other).toByte()
    override fun Byte.subtract(other: Byte): Byte = (this - other).toByte()
    override fun Byte.multiply(other: Byte): Byte = (this * other).toByte()
    override fun Byte.divide(other: Byte): Byte = (this / other).toByte()
    override fun Byte.rem(other: Byte): Byte = (this % other).toByte()
    override fun Byte.compareTo(other: Byte): Int = this.compareTo(other)
}

private val shortInstance: IntegerArithmetic<Short> = object : IntegerArithmetic<Short> {
    override val zero: Short get() = 0
    override val one: Short get() = 1
    override fun Short.add(other: Short): Short = (this + other).toShort()
    override fun Short.subtract(other: Short): Short = (this - other).toShort()
    override fun Short.multiply(other: Short): Short = (this * other).toShort()
    override fun Short.divide(other: Short): Short = (this / other).toShort()
    override fun Short.rem(other: Short): Short = (this % other).toShort()
    override fun Short.compareTo(other: Short): Int = this.compareTo(other)
}

private val intInstance: IntegerArithmetic<Int> = object : IntegerArithmetic<Int> {
    override val zero: Int get() = 0
    override val one: Int get() = 1

    override fun Int.add(other: Int): Int = this + other
    override fun Int.subtract(other: Int): Int = this - other
    override fun Int.multiply(other: Int): Int = this * other
    override fun Int.divide(other: Int): Int = this / other
    override fun Int.rem(other: Int): Int = this % other
    override fun Int.compareTo(other: Int): Int = this.compareTo(other)
}

private val longInstance: IntegerArithmetic<Long> = object : IntegerArithmetic<Long> {
    override val zero: Long get() = 0L
    override val one: Long get() = 1L

    override fun Long.add(other: Long): Long = this + other
    override fun Long.subtract(other: Long): Long = this - other
    override fun Long.multiply(other: Long): Long = this * other
    override fun Long.divide(other: Long): Long = this / other
    override fun Long.rem(other: Long): Long = this % other
    override fun Long.compareTo(other: Long): Int = this.compareTo(other)
}

val IntegerArithmetic.Companion.byte: IntegerArithmetic<Byte>
    get() = byteInstance

val IntegerArithmetic.Companion.short: IntegerArithmetic<Short>
    get() = shortInstance

val IntegerArithmetic.Companion.int: IntegerArithmetic<Int>
    get() = intInstance

val IntegerArithmetic.Companion.long: IntegerArithmetic<Long>
    get() = longInstance
