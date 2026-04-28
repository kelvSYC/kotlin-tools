package com.kelvsyc.kotlin.core.fp

/**
 * `FiniteDecimalFloatingPoint` is a generic representation of a finite decimal floating-point value.
 *
 * This type is intentionally not a number: it is a raw structural view of a decimal floating-point encoding, not a
 * numeric type in its own right. Arithmetic, value-based equality, ordering, and human-readable formatting are all
 * left to native decimal floating-point types. As a consequence, [equals] and [hashCode] reflect structural identity
 * of the fields rather than numerical equality, and [toString] exposes the raw representation rather than the
 * mathematical value.
 *
 * In particular, this representation does not enforce a normalized form: trailing decimal zeros are preserved, so
 * the same numerical value can be expressed in multiple ways (e.g. `significand=1, exponent=0` and
 * `significand=10, exponent=-1` both represent 1.0). These distinct representations are called *cohorts* in IEEE
 * 754-2008. The sign of zero is also unspecified: both `(sign=false, significand=0)` and `(sign=true, significand=0)`
 * represent zero.
 *
 * @param T The type of the significand. For `decimal32` this is [UInt]; for `decimal64` it would be [ULong].
 * @param sign The sign bit. `true` for negative values (including negative zero).
 * @param exponent The unbiased quantum exponent. The mathematical value is `(-1)^sign × significand × 10^exponent`.
 * @param significand The integer coefficient. A significand of zero represents zero regardless of [sign] or [exponent].
 */
data class FiniteDecimalFloatingPoint<T>(val sign: Boolean, val exponent: Int, val significand: T) {
    companion object
}
