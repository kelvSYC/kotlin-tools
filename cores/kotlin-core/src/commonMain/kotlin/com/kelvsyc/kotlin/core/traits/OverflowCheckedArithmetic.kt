package com.kelvsyc.kotlin.core.traits

/**
 * `OverflowCheckedArithmetic` is a sub-interface of [IntegerArithmetic] with a stricter overflow contract:
 * implementations must throw [ArithmeticException] if any arithmetic operation overflows the range of [T], rather
 * than wrapping silently.
 *
 * The function names are identical to those in [IntegerArithmetic], enabling drop-in substitution: generic code
 * written against [IntegerArithmetic] can be given an [OverflowCheckedArithmetic] instance to gain overflow
 * detection without any changes at the call site. The caller that supplies the instance is responsible for choosing
 * the stricter contract deliberately.
 *
 * Note that [divide] also throws [ArithmeticException] for division by zero (inherited from [IntegerArithmetic]) as
 * well as for overflow (the only overflow case for integer division is `MIN_VALUE / -1`).
 *
 * This interface has no JavaScript implementation, as JavaScript provides no mechanism for integer overflow
 * detection without software emulation.
 *
 * ## Standard implementations
 *
 * JVM instances for [Int] and [Long] are available as `Companion.int` and `Companion.long`.
 */
interface OverflowCheckedArithmetic<T> : IntegerArithmetic<T> {
    companion object
}
