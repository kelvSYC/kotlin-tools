package com.kelvsyc.kotlin.core.fp

/**
 * `FiniteBinaryFloatingPoint` is a generic representation of a finite floating-point value.
 *
 * This type is intentionally not a number: it is a raw structural view of a floating-point encoding, not a numeric
 * type in its own right. Arithmetic, value-based equality, ordering, and human-readable formatting are all left to
 * native floating-point types such as [Float] and [Double], which can perform these operations more efficiently and
 * accurately. As a consequence, [equals] and [hashCode] reflect structural identity of the fields rather than
 * numerical equality, and [toString] exposes the raw representation rather than the mathematical value. In
 * particular, this representation does not enforce a normalized form, so the same numerical value can be expressed
 * in multiple ways (e.g. `significand=1u, exponent=1` and `significand=2u, exponent=0` both represent 2.0), and
 * the sign of zero is unspecified.
 *
 * @param T The type of the significand. This is generally an unsigned integral type, but is not required to be.
 * @param exponent The exponent of the floating point number. Unlike IEEE floating point formats, this is an unbiased
 *                 value.
 * @param significand The significand of the floating point number. Unlike IEEE floating point formats, there is no
 *                    implicit leading 1 bit.
 */
data class FiniteBinaryFloatingPoint<T>(val sign: Boolean, val exponent: Int, val significand: T) {
    companion object
}
