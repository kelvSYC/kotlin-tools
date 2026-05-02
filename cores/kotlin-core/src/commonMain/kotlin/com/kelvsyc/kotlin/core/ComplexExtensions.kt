package com.kelvsyc.kotlin.core

import com.kelvsyc.kotlin.core.traits.complex.ImaginaryArithmetic

// ── Imaginary<T> view of Complex<T> ──────────────────────────────────────────

/**
 * Returns the imaginary component as an [Imaginary] value.
 */
fun <T> Complex<T>.imaginaryPart(): Imaginary<T> = Imaginary(imaginary)

// ── Construct Complex<T> from a real T and an Imaginary<T> ───────────────────

/**
 * Constructs a [Complex] from a real component and a typed [Imaginary] component.
 */
fun <T> Complex(real: T, imaginary: Imaginary<T>): Complex<T> = Complex(real, imaginary.value)

// ── Lift Imaginary<T> to Complex<T> ──────────────────────────────────────────

/**
 * Returns a [Complex] with the given purely imaginary value and a positive-zero real part.
 */
fun <T> ImaginaryArithmetic<T>.toComplex(im: Imaginary<T>): Complex<T> =
    Complex(componentTraits.positiveZero, im.value)

// ── Cross-type products ───────────────────────────────────────────────────────

/**
 * Returns `(i·a) × (i·b) = -(a·b)` using the supplied [arithmetic] for the component
 * multiplication and negation.
 */
fun <T> timesImaginary(
    a: Imaginary<T>,
    b: Imaginary<T>,
    arithmetic: com.kelvsyc.kotlin.core.traits.fp.FloatingPointArithmetic<T>
): T = with(arithmetic) { a.value.multiply(b.value).negate() }

/**
 * Returns `(i·a) × b = i·(a·b)`: the product of a purely imaginary and a real number.
 */
fun <T> timesReal(
    im: Imaginary<T>,
    real: T,
    arithmetic: com.kelvsyc.kotlin.core.traits.fp.FloatingPointArithmetic<T>
): Imaginary<T> = Imaginary(with(arithmetic) { im.value.multiply(real) })
