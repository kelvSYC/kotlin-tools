package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.traits.ValueEquality
import java.math.BigDecimal

// ── ValueEquality<BigDecimal> — two distinct semantics ────────────────────────
//
// BigDecimal has two natural equality relations, mirroring the numericalEquality /
// equivalenceEquality split in BinaryFloatingPoint:
//
//   numericalEquality   — compareTo(other) == 0: value equality, ignores scale.
//                         1.0 == 1.00. Analogous to IEEE 754 == for finite values.
//
//   equivalenceEquality — equals(other): structural equality, requires matching scale.
//                         1.0 != 1.00. Suitable for use in collections (consistent with
//                         BigDecimal.hashCode, which is scale-sensitive).

private val numericalEqualityInstance: ValueEquality<BigDecimal> = object : ValueEquality<BigDecimal> {
    override fun BigDecimal.isEqualTo(other: BigDecimal): Boolean = compareTo(other) == 0
}

private val equivalenceEqualityInstance: ValueEquality<BigDecimal> = object : ValueEquality<BigDecimal> {
    override fun BigDecimal.isEqualTo(other: BigDecimal): Boolean = equals(other)
}

/**
 * A [ValueEquality] instance for [BigDecimal] that compares by numeric value, ignoring scale.
 *
 * `1.0` and `1.00` are considered equal under this instance. Implemented via
 * [BigDecimal.compareTo], which is consistent with numerical equality.
 */
val ValueEquality.Companion.bigDecimalNumerical: ValueEquality<BigDecimal>
    get() = numericalEqualityInstance

/**
 * A [ValueEquality] instance for [BigDecimal] that compares by structural equality, including scale.
 *
 * `1.0` and `1.00` are considered distinct under this instance. Implemented via
 * [BigDecimal.equals], which is consistent with [BigDecimal.hashCode] and suitable for
 * hash-based collections.
 */
val ValueEquality.Companion.bigDecimalEquivalence: ValueEquality<BigDecimal>
    get() = equivalenceEqualityInstance
