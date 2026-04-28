package com.kelvsyc.kotlin.core.traits

/**
 * `DecimalFloatingPointCohorts` is a trait type defining cohort operations on a decimal floating-point type [T].
 *
 * A *cohort* is the set of all finite bit patterns that represent the same mathematical value in a given decimal
 * floating-point format. Unlike binary formats, the same value may be encoded with multiple (significand, exponent)
 * pairs — for example, `1 × 10⁰`, `10 × 10⁻¹`, and `100 × 10⁻²` are all distinct representations of 1.0 in
 * decimal32, each with a different quantum exponent. The quantum exponent determines the precision available at that
 * scale and is preserved through operations such as addition and multiplication in full IEEE 754 implementations.
 *
 * Canonical instances for [com.kelvsyc.kotlin.core.BidFloat] and [com.kelvsyc.kotlin.core.DpdFloat] are available
 * as properties on their companion objects.
 */
interface DecimalFloatingPointCohorts<T> {
    /**
     * Returns the *reduced* form of this value: the cohort member with the fewest trailing zeros in the significand
     * (equivalently, the largest quantum exponent that still represents the same mathematical value exactly).
     *
     * Per IEEE 754-2008 §5.3.3:
     * - Finite non-zero: trailing decimal zeros are stripped from the significand and the exponent is incremented
     *   until no trailing zeros remain, or the biased exponent reaches its maximum.
     * - Zero: returned with the preferred exponent of 0 (biased exponent = [DecimalFloatingPoint.exponentBias]),
     *   preserving the sign.
     * - NaN or infinity: returned unchanged.
     */
    fun T.reduce(): T

    /**
     * Returns the *quantum* of this value: a value with significand 1 and the same quantum exponent as this value,
     * with the same sign.
     *
     * The quantum represents the unit of least precision (ULP) at this value's scale. For example, if this value
     * is `1234 × 10⁻²`, the quantum is `1 × 10⁻²`.
     *
     * For NaN or infinity, the value is returned unchanged.
     */
    fun T.quantum(): T

    /**
     * Returns a value numerically equal to `this`, but with its quantum exponent adjusted to match that of [quantum].
     *
     * The significand is scaled (and rounded using round-half-to-even if necessary) so that the result's exponent
     * equals the exponent of [quantum]. Per IEEE 754-2008 §5.3.3:
     * - If the result cannot be represented with `p` significant digits at the target exponent (overflow), returns
     *   NaN (invalid operation, unlike [toBidFloat][com.kelvsyc.kotlin.core.fp.toBidFloat] which returns ±infinity).
     * - If either operand is NaN, or [quantum] is infinite, the result is NaN.
     * - If this is infinite, the result is NaN.
     * - If this is zero, the result is ±zero at the target exponent.
     */
    fun T.quantize(quantum: T): T
}
