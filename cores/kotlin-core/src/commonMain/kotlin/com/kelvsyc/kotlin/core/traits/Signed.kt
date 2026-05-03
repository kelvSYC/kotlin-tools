package com.kelvsyc.kotlin.core.traits

import com.kelvsyc.kotlin.core.traits.integral.SignedIntegerArithmetic
import com.kelvsyc.kotlin.core.traits.integral.byte
import com.kelvsyc.kotlin.core.traits.integral.int
import com.kelvsyc.kotlin.core.traits.integral.long
import com.kelvsyc.kotlin.core.traits.integral.short

/**
 * `Signed` is a trait for types that have a notion of sign: negation, absolute value, and sign
 * queries.
 *
 * This trait captures the operations shared by signed floating-point types and signed integer
 * types. The precise semantics of each operation are implementation-defined:
 *
 * - For IEEE 754 floating-point types, [isNegative] is a sign-bit read (true for negative NaN,
 *   negative zero, and negative infinities), and [negate] is a copy operation that flips the sign
 *   bit unconditionally without raising exceptions.
 * - For signed integer types, [isNegative] is an arithmetic comparison (`this < 0`), and [negate]
 *   is arithmetic negation (may wrap on `MIN_VALUE`).
 *
 * Implementations must be self-consistent: after [negate], [isNegative] must return the opposite
 * of its pre-negation value (except on types where negation has fixed points, such as `MIN_VALUE`
 * for two's-complement integers).
 *
 * Standard implementations for signed primitive types delegate to [SignedIntegerArithmetic]:
 * [Companion.byte], [Companion.short], [Companion.int], [Companion.long].
 * JVM-only instances for [java.math.BigInteger] and [java.math.BigDecimal] are available as
 * [Companion.bigInteger] and [Companion.bigDecimal].
 */
interface Signed<T> {
    companion object

    /**
     * Returns `true` if this value is negative.
     *
     * The precise meaning is implementation-defined. For IEEE 754 types this is a sign-bit read
     * (true for negative NaN and negative zero); for integer types this is `this < 0`.
     */
    fun T.isNegative(): Boolean

    /**
     * Returns `true` if this value is non-negative.
     *
     * The default implementation delegates to [isNegative]. For integer types where zero is
     * neither positive nor negative, prefer [SignedIntegerArithmetic.isPositive] which returns
     * `this > 0` instead of `!isNegative()`.
     */
    fun T.isPositive(): Boolean = !isNegative()

    /**
     * Returns the negation of this value.
     *
     * The precise meaning is implementation-defined. For IEEE 754 types this is a sign-bit flip
     * (copy operation); for integer types this is arithmetic negation (may wrap on `MIN_VALUE`).
     */
    fun T.negate(): T

    /**
     * Returns the absolute value of this value.
     *
     * The default implementation returns `this` unchanged if not negative, or [negate] otherwise.
     * For IEEE 754 types this is a sign-bit clear; for integer types this is the arithmetic
     * absolute value (may wrap on `MIN_VALUE`).
     */
    fun T.abs(): T = if (isNegative()) negate() else this
}

val Signed.Companion.byte: Signed<Byte> get() = SignedIntegerArithmetic.byte
val Signed.Companion.short: Signed<Short> get() = SignedIntegerArithmetic.short
val Signed.Companion.int: Signed<Int> get() = SignedIntegerArithmetic.int
val Signed.Companion.long: Signed<Long> get() = SignedIntegerArithmetic.long
