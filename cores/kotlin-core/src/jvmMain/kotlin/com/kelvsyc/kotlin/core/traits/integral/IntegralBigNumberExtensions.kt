package com.kelvsyc.kotlin.core.traits.integral

import java.math.BigDecimal
import java.math.BigInteger

// Correction term added to a sign-extended Long when the true ULong value has its high bit set.
private val ULONG_CORRECTION = BigInteger.ONE.shiftLeft(64)

/**
 * Returns [value] as a [BigInteger].
 *
 * Delegates to [SignedIntegral.toLong] then [BigInteger.valueOf]; exact for all signed integral types
 * since every representable value fits in a [Long].
 */
fun <T> SignedIntegral<T>.toBigInteger(value: T): BigInteger =
    BigInteger.valueOf(with(this) { value.toLong() })

/**
 * Returns [value] as a [BigDecimal].
 *
 * Delegates to [SignedIntegral.toLong] then [BigDecimal.valueOf].
 */
fun <T> SignedIntegral<T>.toBigDecimal(value: T): BigDecimal =
    BigDecimal.valueOf(with(this) { value.toLong() })

/**
 * Returns [value] as a [BigInteger].
 *
 * Uses [UnsignedIntegral.toULong] to obtain the unsigned bit pattern. For values whose high bit is
 * clear (all of [UByte], [UShort], [UInt], and [ULong] values ≤ [Long.MAX_VALUE]) this is a direct
 * [BigInteger.valueOf] call. For [ULong] values with the high bit set the signed reinterpretation is
 * negative, so 2⁶⁴ is added to recover the true unsigned value.
 */
fun <T> UnsignedIntegral<T>.toBigInteger(value: T): BigInteger {
    val longBits = with(this) { value.toULong().toLong() }
    return if (longBits >= 0) BigInteger.valueOf(longBits)
    else BigInteger.valueOf(longBits).add(ULONG_CORRECTION)
}

/**
 * Returns [value] as a [BigDecimal], via [UnsignedIntegral.toBigInteger].
 */
fun <T> UnsignedIntegral<T>.toBigDecimal(value: T): BigDecimal =
    toBigInteger(value).toBigDecimal()
