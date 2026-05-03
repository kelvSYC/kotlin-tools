package com.kelvsyc.kotlin.core.traits

/**
 * `Multiplication` is a trait for types that support multiplication, with a multiplicative identity.
 *
 * This is a granular cross-cutting trait. The "big tent" arithmetic traits ([integral.IntegerArithmetic],
 * [fp.FloatingPointArithmetic], etc.) extend it, so any instance of those traits also satisfies
 * `Multiplication<T>`. It is also intended to be implemented directly for types that support multiplication
 * but not the full arithmetic suite — for example, complex numbers or rational types.
 *
 * Division is intentionally separated into [Division], because not all multiplicative types support closed
 * division (e.g. integer types where division truncates toward zero rather than yielding a field element).
 */
interface Multiplication<T> {
    companion object

    /**
     * The multiplicative identity: the value such that `x.multiply(one) == x` for all `x`.
     */
    val one: T

    /**
     * Returns the product `this * other`.
     */
    fun T.multiply(other: T): T
}
