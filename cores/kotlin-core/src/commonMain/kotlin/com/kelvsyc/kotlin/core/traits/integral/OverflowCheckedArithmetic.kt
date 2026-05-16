package com.kelvsyc.kotlin.core.traits.integral

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
 * ## JavaScript implementation note
 *
 * The JS target provides an [Int] instance (`Companion.int`) that uses Double-promotion: since
 * JavaScript's `number` type has a 53-bit mantissa, all 32-bit integer values are exactly
 * representable. Each arithmetic operation widens both operands to `Double`, performs the operation,
 * and checks whether the result lies within `Int.MIN_VALUE..Int.MAX_VALUE` before truncating.
 * [Long] has no JS implementation because 64 bits exceed the 53-bit mantissa.
 *
 * ## Standard implementations
 *
 * JVM instances for [Int] and [Long] are available as `Companion.int` and `Companion.long`.
 * JS instance for [Int] is available as `Companion.int`.
 */
interface OverflowCheckedArithmetic<T> : IntegerArithmetic<T> {
    companion object
}
