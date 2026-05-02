package com.kelvsyc.kotlin.core.traits.integral

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
    companion object

    fun T.ceilDiv(divisor: T): T
}

/**
 * Returns a [CeilDiv] instance derived from [arithmetic] for an unsigned integral type.
 *
 * Because all values of an unsigned type are non-negative, truncated division always rounds toward
 * zero (away from +∞) when the division is inexact. The implementation therefore increments the
 * truncated quotient whenever the remainder is non-zero.
 *
 * This factory is not correct for signed types with mixed-sign operands; use
 * [SignedIntegerArithmetic]'s built-in [CeilDiv] implementation for signed arithmetic.
 */
fun <T> CeilDiv.Companion.from(arithmetic: IntegerArithmetic<T>): CeilDiv<T> = object : CeilDiv<T> {
    override fun T.ceilDiv(divisor: T): T {
        val q = with(arithmetic) { divide(divisor) }
        val r = with(arithmetic) { rem(divisor) }
        return if (with(arithmetic) { r.compareTo(zero) } != 0) with(arithmetic) { q.add(one) } else q
    }
}
