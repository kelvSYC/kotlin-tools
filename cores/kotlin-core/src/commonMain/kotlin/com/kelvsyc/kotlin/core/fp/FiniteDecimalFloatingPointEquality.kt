package com.kelvsyc.kotlin.core.fp

import com.kelvsyc.kotlin.core.traits.ValueEquality

/**
 * Reduces an (exponent, significand) pair to canonical form by stripping trailing decimal zeros from the significand
 * and incrementing the exponent accordingly.
 *
 * Two finite non-zero [FiniteDecimalFloatingPoint] values are numerically equal if and only if they produce the same
 * canonical pair (and have the same sign).
 */
private fun normalizeUInt(exp: Int, sig: UInt): Pair<Int, UInt> {
    var e = exp
    var s = sig
    while (s != 0u && s % 10u == 0u) { s /= 10u; e++ }
    return e to s
}

/**
 * Constructs a [ValueEquality] for [FiniteDecimalFloatingPoint] with a [UInt] significand based on cohort membership.
 *
 * Two values are considered equal if they represent the same numerical value — that is, if they are in the same
 * cohort. Differently-scaled representations of the same value (e.g. `significand=1u, exponent=0` and
 * `significand=10u, exponent=-1`) compare equal. Any two zero significands compare equal regardless of sign or
 * exponent, reflecting the unspecified sign-of-zero convention of [FiniteDecimalFloatingPoint].
 */
val FiniteDecimalFloatingPoint.Companion.uIntCohortEquality: ValueEquality<FiniteDecimalFloatingPoint<UInt>>
    get() = uIntInstance

private val uIntInstance: ValueEquality<FiniteDecimalFloatingPoint<UInt>> =
    object : ValueEquality<FiniteDecimalFloatingPoint<UInt>> {
        override fun FiniteDecimalFloatingPoint<UInt>.isEqualTo(other: FiniteDecimalFloatingPoint<UInt>): Boolean {
            val thisZero = significand == 0u
            val otherZero = other.significand == 0u
            return when {
                thisZero && otherZero -> true
                thisZero || otherZero -> false
                sign != other.sign -> false
                else -> {
                    val (ea, sa) = normalizeUInt(exponent, significand)
                    val (eb, sb) = normalizeUInt(other.exponent, other.significand)
                    ea == eb && sa == sb
                }
            }
        }
    }
