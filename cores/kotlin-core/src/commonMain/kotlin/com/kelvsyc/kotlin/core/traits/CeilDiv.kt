package com.kelvsyc.kotlin.core.traits

/**
 * `CeilDiv` is a composable trait that adds ceiling division to an arithmetic type [T].
 *
 * Ceiling division rounds toward positive infinity, as opposed to truncated division (toward zero)
 * and floor division (toward negative infinity). For a dividend `a` and divisor `b`:
 *
 * - When `a` and `b` have the same sign and the division is inexact, the result is one greater than
 *   the truncated quotient (i.e. rounded up toward +∞).
 * - When `a` and `b` have different signs and the division is inexact, the truncated quotient already
 *   rounds toward +∞, so no adjustment is needed.
 * - When the division is exact, the result equals the truncated quotient.
 *
 * This interface is typically mixed into [SignedIntegerArithmetic], which supplies the default
 * implementation via its truncated [IntegerArithmetic.divide] and [IntegerArithmetic.rem].
 */
interface CeilDiv<T> {
    fun T.ceilDiv(divisor: T): T
}
