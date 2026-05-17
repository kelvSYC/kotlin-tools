package com.kelvsyc.kotlin.decimal

import com.kelvsyc.kotlin.core.traits.Addition
import com.kelvsyc.kotlin.core.traits.Division
import com.kelvsyc.kotlin.core.traits.Multiplication
import com.kelvsyc.kotlin.core.traits.Signed
import com.kelvsyc.kotlin.core.traits.ValueEquality

// ── Addition<Decimal> ─────────────────────────────────────────────────────────
//
// Decimal is a JS external class; plus(), minus(), etc. are external member functions and
// always take dispatch priority over same-named member extensions — no recursion risk.

private val decimalAdditionInstance: Addition<Decimal> = object : Addition<Decimal> {
    override val zero: Decimal get() = Decimal("0")
    override fun Decimal.add(other: Decimal): Decimal = plus(other)
    override fun Decimal.subtract(other: Decimal): Decimal = minus(other)
}

val Addition.Companion.decimal: Addition<Decimal> get() = decimalAdditionInstance

// ── Multiplication<Decimal> ───────────────────────────────────────────────────

private val decimalMultiplicationInstance: Multiplication<Decimal> = object : Multiplication<Decimal> {
    override val one: Decimal get() = Decimal("1")
    override fun Decimal.multiply(other: Decimal): Decimal = times(other)
}

val Multiplication.Companion.decimal: Multiplication<Decimal> get() = decimalMultiplicationInstance

// ── Division<Decimal> ─────────────────────────────────────────────────────────
//
// Two variants mirroring BigDecimalArithmetic:
//
// Bare (decimal): uses Decimal.js global precision and rounding. Suitable when the caller
// has already configured Decimal.set() for the desired precision.
//
// Factory (decimal(precision, rounding)): uses Decimal.clone() to create an independent
// constructor with its own configuration, so division never disturbs global settings.
// The cloned constructor is captured at factory-creation time.

private val decimalDivisionInstance: Division<Decimal> = object : Division<Decimal> {
    override fun Decimal.divide(other: Decimal): Decimal = dividedBy(other)
}

val Division.Companion.decimal: Division<Decimal> get() = decimalDivisionInstance

fun Division.Companion.decimal(precision: Int, rounding: DecimalRounding): Division<Decimal> {
    val config: dynamic = js("{}")
    config.precision = precision
    config.rounding = rounding.code
    val clonedCtor = Decimal.clone(config)
    return object : Division<Decimal> {
        override fun Decimal.divide(other: Decimal): Decimal {
            // Decimal.js constructors accept invocation without `new` (self-applies new internally).
            val a: dynamic = clonedCtor(toString())
            val b: dynamic = clonedCtor(other.toString())
            return a.dividedBy(b).unsafeCast<Decimal>()
        }
    }
}

// ── Signed<Decimal> ───────────────────────────────────────────────────────────

private val decimalSignedInstance: Signed<Decimal> = object : Signed<Decimal> {
    override fun Decimal.isNegative(): Boolean = isNegative()
    override fun Decimal.negate(): Decimal = negated()
    override fun Decimal.abs(): Decimal = abs()
}

val Signed.Companion.decimal: Signed<Decimal> get() = decimalSignedInstance

// ── ValueEquality<Decimal> ────────────────────────────────────────────────────
//
// decimalNumerical:   delegates to Decimal.equals() — NaN ≠ NaN. Decimal.js normalises
//                     internally so 1.0 and 1.00 are equal (no cohort distinction).
// decimalEquivalence: NaN == NaN (reflexive), suitable for data structures and testing.
// There is no negative-zero in Decimal.js (sign is always 0 for zero), so no +0/-0 split.

private val decimalNumericalInstance: ValueEquality<Decimal> = object : ValueEquality<Decimal> {
    override fun Decimal.isEqualTo(other: Decimal): Boolean = equals(other)
}

private val decimalEquivalenceInstance: ValueEquality<Decimal> = object : ValueEquality<Decimal> {
    override fun Decimal.isEqualTo(other: Decimal): Boolean = (isNaN() && other.isNaN()) || equals(other)
}

val ValueEquality.Companion.decimalNumerical: ValueEquality<Decimal> get() = decimalNumericalInstance
val ValueEquality.Companion.decimalEquivalence: ValueEquality<Decimal> get() = decimalEquivalenceInstance
