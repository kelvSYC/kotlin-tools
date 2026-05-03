package com.kelvsyc.kotlin.core.traits

import java.math.BigDecimal
import java.math.MathContext

// ── BigDecimal ────────────────────────────────────────────────────────────────
//
// BigDecimal is a Java class; add(), subtract(), multiply(), and divide() are all Java member
// functions and always take dispatch priority over the same-named member extensions from the
// trait interfaces — no recursion risk.

// ── Addition<BigDecimal> ──────────────────────────────────────────────────────

private val bigDecimalAdditionInstance: Addition<BigDecimal> = object : Addition<BigDecimal> {
    override val zero: BigDecimal get() = BigDecimal.ZERO
    override fun BigDecimal.add(other: BigDecimal): BigDecimal = add(other)
    override fun BigDecimal.subtract(other: BigDecimal): BigDecimal = subtract(other)
}

val Addition.Companion.bigDecimal: Addition<BigDecimal> get() = bigDecimalAdditionInstance

// ── Multiplication<BigDecimal> ────────────────────────────────────────────────

private val bigDecimalMultiplicationInstance: Multiplication<BigDecimal> = object : Multiplication<BigDecimal> {
    override val one: BigDecimal get() = BigDecimal.ONE
    override fun BigDecimal.multiply(other: BigDecimal): BigDecimal = multiply(other)
}

val Multiplication.Companion.bigDecimal: Multiplication<BigDecimal> get() = bigDecimalMultiplicationInstance

// ── Division<BigDecimal> ──────────────────────────────────────────────────────
//
// Two variants:
//
// Exact (bigDecimal): delegates to BigDecimal.divide(BigDecimal), which throws
// ArithmeticException for non-terminating decimal expansions (e.g. 1 / 3). Suitable for
// contexts where all divisions are known to be exact.
//
// MathContext factory (bigDecimal(MathContext)): delegates to BigDecimal.divide(BigDecimal,
// MathContext), rounding to the supplied precision and mode. Suitable for general-purpose
// decimal arithmetic where rounding is acceptable.

private val bigDecimalDivisionInstance: Division<BigDecimal> = object : Division<BigDecimal> {
    override fun BigDecimal.divide(other: BigDecimal): BigDecimal = divide(other)
}

val Division.Companion.bigDecimal: Division<BigDecimal> get() = bigDecimalDivisionInstance

fun Division.Companion.bigDecimal(mathContext: MathContext): Division<BigDecimal> =
    object : Division<BigDecimal> {
        override fun BigDecimal.divide(other: BigDecimal): BigDecimal = divide(other, mathContext)
    }
