package com.kelvsyc.kotlin.core.traits

/**
 * `OverflowCheckedSignedArithmetic` is a sub-interface combining [OverflowCheckedArithmetic] and
 * [SignedIntegerArithmetic], applying the overflow-checking contract to the signed-only operations as well.
 *
 * Implementations must throw [ArithmeticException] for all operations that overflow the range of [T], including
 * [unaryMinus] (overflows for `MIN_VALUE`, which has no representable negation) and [abs] (overflows for
 * `MIN_VALUE` for the same reason).
 *
 * This interface has no JavaScript implementation; see [OverflowCheckedArithmetic] for the rationale.
 *
 * ## Standard implementations
 *
 * JVM instances for [Int] and [Long] are available as `Companion.int` and `Companion.long`.
 */
interface OverflowCheckedSignedArithmetic<T> : OverflowCheckedArithmetic<T>, SignedIntegerArithmetic<T> {
    companion object
}
