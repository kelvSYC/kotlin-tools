package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.traits.Signed

/**
 * Trait for IEEE 754-2008 §5.5.1 *copy operations* on a floating-point type [T].
 *
 * Extends [Signed] with [copySign] and narrows the semantics of [isNegative] and [negate] to
 * their IEEE 754 sign-bit interpretations. Copy operations manipulate only the sign bit and are
 * defined for every bit pattern — finite values, infinities, both zeros, and NaN — without
 * performing arithmetic. They contrast with their arithmetic lookalikes:
 *
 * - [negate] is a **sign-bit flip**, not `0 - x`.  Arithmetic subtraction raises floating-point
 *   exceptions and applies NaN propagation rules; a sign-bit flip simply inverts the sign bit
 *   unconditionally and never raises an exception.
 *
 * For compound types such as `DoubleDouble` (where the value is split across a (hi, lo) pair),
 * "the sign" is the sign of the high component, and [negate] must flip every component to
 * preserve the canonical pair invariant.
 */
interface FloatingPointSign<T> : Signed<T> {
    /**
     * Returns `true` if this value has its sign bit set.
     *
     * This is a pure sign-bit read, equivalent to IEEE 754-2008 §5.6 `isSignMinus`.  Returns
     * `true` for negative finite values, negative infinity, negative zero, and negative NaN —
     * any bit pattern whose sign bit is 1.
     *
     * Note that [isNegative] and [isPositive] are complementary: every bit pattern is either
     * negative or positive, including zeros and NaNs, with no third case.
     */
    override fun T.isNegative(): Boolean

    /**
     * Returns `true` if this value has its sign bit clear.
     *
     * Equivalent to `!isNegative()`.  Returns `true` for positive finite values, positive
     * infinity, positive zero, and positive NaN — any bit pattern whose sign bit is 0.
     */
    override fun T.isPositive(): Boolean = !isNegative()

    /**
     * Returns this value with its sign bit flipped.
     *
     * Equivalent to IEEE 754-2008 §5.5.1 `negate` — a copy operation, **not** arithmetic
     * negation.  Defined for every bit pattern with no exception raised:
     *
     * | Input       | Output                                               |
     * |-------------|------------------------------------------------------|
     * | finite `x`  | mathematically negated `x`                           |
     * | `+0`        | `-0`                                                 |
     * | `-0`        | `+0`                                                 |
     * | `+∞`        | `-∞`                                                 |
     * | NaN (any)   | NaN with sign bit inverted; payload bits preserved   |
     *
     * The operation is always involutory: `negate(negate(x)) == x` for every bit pattern.
     */
    override fun T.negate(): T

    /**
     * Returns this value with its sign bit cleared, equivalent to IEEE 754-2008 §5.5.1 `abs`.
     *
     * A pure sign-bit clear — no arithmetic, no exception raised, defined for every bit pattern.
     * `abs(-0)` is `+0`; `abs(NaN)` is NaN with the sign bit cleared.
     *
     * The default implementation delegates to [isNegative] and [negate].
     */
    override fun T.abs(): T = if (isNegative()) negate() else this

    /**
     * Returns a value with the magnitude of this value and the sign of [other], equivalent to
     * IEEE 754-2008 §5.5.1 `copySign`.
     *
     * A pure sign-bit copy — no arithmetic, no exception raised, defined for every bit pattern
     * including NaN and both zeros.
     *
     * The default implementation delegates to [isNegative] and [negate].
     */
    fun T.copySign(other: T): T = if (other.isNegative() != isNegative()) negate() else this
}
