package com.kelvsyc.kotlin.core

/**
 * A purely imaginary number `i·value`, represented as a distinct type from [Complex] and from the
 * underlying component type [T].
 *
 * Holding the imaginary part as its own type rather than a bare [T] enables correct routing of
 * mixed-type operations (e.g. `Imaginary × Imaginary → -T`) and supports Annex G-compliant
 * arithmetic in [com.kelvsyc.kotlin.core.traits.ComplexArithmetic].
 *
 * No arithmetic operators are defined here; all operations are provided through
 * [com.kelvsyc.kotlin.core.traits.ImaginaryArithmetic] and related extension functions.
 */
data class Imaginary<T>(val value: T)
