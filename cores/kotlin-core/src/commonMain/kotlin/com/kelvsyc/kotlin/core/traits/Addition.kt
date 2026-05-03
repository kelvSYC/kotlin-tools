package com.kelvsyc.kotlin.core.traits

/**
 * `Addition` is a trait for types that support addition and subtraction, with an additive identity.
 *
 * This is a granular cross-cutting trait. The "big tent" arithmetic traits ([integral.IntegerArithmetic],
 * [fp.FloatingPointArithmetic], etc.) extend it, so any instance of those traits also satisfies `Addition<T>`.
 * It is also intended to be implemented directly for types that support addition but not the full arithmetic
 * suite — for example, complex numbers (which have no total order) or rational types.
 *
 * The semantics of [add] and [subtract] are implementation-defined: IEEE 754 rounding for floating-point
 * types, wrapping or checked arithmetic for integer types, and exact arithmetic for rational types.
 */
interface Addition<T> {
    companion object

    /**
     * The additive identity: the value such that `x.add(zero) == x` for all `x`.
     */
    val zero: T

    /**
     * Returns the sum `this + other`.
     */
    fun T.add(other: T): T

    /**
     * Returns the difference `this - other`.
     */
    fun T.subtract(other: T): T
}
