package com.kelvsyc.kotlin.core

/**
 * A complex number `real + i·imaginary`, parameterised over the component type [T].
 *
 * Both components are stored as bare [T] values; the [Imaginary] view of the imaginary part is
 * available as an opt-in extension. No arithmetic operators are defined here; all operations are
 * provided through [com.kelvsyc.kotlin.core.traits.ComplexArithmetic] and related extension
 * functions.
 */
data class Complex<T>(val real: T, val imaginary: T)
