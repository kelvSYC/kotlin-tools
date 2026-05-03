package com.kelvsyc.kotlin.core.traits

/**
 * `Division` is a trait for types that support a divide operation.
 *
 * This is a granular cross-cutting trait. The "big tent" arithmetic traits ([integral.IntegerArithmetic],
 * [fp.FloatingPointArithmetic], etc.) extend it, so any instance of those traits also satisfies `Division<T>`.
 * It is also intended to be implemented directly for types that support division but not the full arithmetic
 * suite — for example, complex numbers or rational types.
 *
 * The semantics of [divide] are implementation-defined:
 * - Integer types: truncating division toward zero (matches Kotlin's `/` operator).
 * - IEEE 754 floating-point types: correctly-rounded quotient.
 * - Rational and complex types: exact field division.
 *
 * Callers must know which instance they hold to interpret the semantics correctly. Code that requires a
 * specific division mode (e.g. floor division) should use a more specific trait such as
 * [integral.SignedIntegerArithmetic].
 */
interface Division<T> {
    companion object

    /**
     * Returns the quotient of this value divided by [other].
     *
     * The precise semantics are implementation-defined; see the class documentation for details.
     * Throws [ArithmeticException] if [other] is the zero element and the implementation does not
     * support division by zero (e.g. integer types).
     */
    fun T.divide(other: T): T
}
