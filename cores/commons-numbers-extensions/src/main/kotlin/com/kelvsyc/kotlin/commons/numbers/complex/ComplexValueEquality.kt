package com.kelvsyc.kotlin.commons.numbers.complex

import com.kelvsyc.kotlin.core.traits.ValueEquality
import org.apache.commons.numbers.complex.Complex as CommonsComplex

// True iff d is ±0 (excludes NaN and infinity, which have non-zero exponent/mantissa bits).
private fun isDoubleZero(d: Double) = d.toBits() and Long.MAX_VALUE == 0L

// IEEE 754 component equality: NaN != NaN, +0 == -0.
private fun ieee754Eq(a: Double, b: Double): Boolean {
    if (a.isNaN() || b.isNaN()) return false
    return a.toBits() == b.toBits() || (isDoubleZero(a) && isDoubleZero(b))
}

private object NumericalEquality : ValueEquality<CommonsComplex> {
    override fun CommonsComplex.isEqualTo(other: CommonsComplex): Boolean =
        ieee754Eq(real, other.real) && ieee754Eq(imaginary, other.imaginary)
}

private object EquivalenceEquality : ValueEquality<CommonsComplex> {
    // Complex.equals() uses Double.doubleToLongBits: NaN == NaN, +0 != -0.
    override fun CommonsComplex.isEqualTo(other: CommonsComplex): Boolean = this == other
}

/**
 * Numerical equality for Commons Numbers [CommonsComplex]: NaN is not equal to anything including
 * itself; +0 equals −0. Follows IEEE 754 semantics for each component.
 */
val commonsComplexNumericalEquality: ValueEquality<CommonsComplex> get() = NumericalEquality

/**
 * Equivalence equality for Commons Numbers [CommonsComplex]: NaN equals NaN; +0 does not equal −0.
 * Delegates to [CommonsComplex.equals], which uses [Double.doubleToLongBits] on each component —
 * consistent with [hashCode] and hash-based collections.
 */
val commonsComplexEquivalenceEquality: ValueEquality<CommonsComplex> get() = EquivalenceEquality
