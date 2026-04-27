package com.kelvsyc.kotlin.core.fp

import com.kelvsyc.kotlin.core.PartialComparator
import com.kelvsyc.kotlin.core.traits.ValueEquality

/**
 * `DoubleDouble` is a non-standard floating-point type that represents a value as the unevaluated sum
 * of two non-overlapping [Double] instances, providing approximately twice the precision of a standard
 * [Double] (~31 decimal digits vs ~15).
 *
 * The representation maintains the invariant `|low| <= ulp(high) / 2`. Instances produced by arithmetic
 * operators preserve this invariant; use [create] when constructing from an external (high, low) pair.
 *
 * [equals] and [hashCode] reflect structural identity of the component fields rather than numerical
 * equality (consistent with [Double.equals]). NaN compares equal to NaN.
 *
 * Arithmetic operators (`+`, `-`, `*`, `/`) are available on JVM and delegate to
 * `DoubleBinaryFloatingPointArithmetic.doubleDouble` (FMA-backed, sloppy Dekker, ~2p-4 bits of precision).
 * The same instance is available for generic algorithms that operate through the [DoubleBinaryFloatingPoint]
 * typeclass.
 */
class DoubleDouble internal constructor(override val high: Double, override val low: Double)
    : DoubleBinaryFloatingPoint<Double>, Comparable<DoubleDouble> {

    companion object {
        /** Not-a-Number. Equal to itself under [equals]; ordered last under [compareTo]. */
        val NaN: DoubleDouble = DoubleDouble(Double.NaN, 0.0)
        val POSITIVE_INFINITY: DoubleDouble = DoubleDouble(Double.POSITIVE_INFINITY, 0.0)
        val NEGATIVE_INFINITY: DoubleDouble = DoubleDouble(Double.NEGATIVE_INFINITY, 0.0)
        val ZERO: DoubleDouble = DoubleDouble(0.0, 0.0)
        val ONE: DoubleDouble = DoubleDouble(1.0, 0.0)

        /**
         * Creates a [DoubleDouble] from an explicit (high, low) pair.
         *
         * Requires `|high| >= |low|`; throws [IllegalArgumentException] otherwise.
         * Internal code that already guarantees the invariant should use the primary constructor directly.
         */
        fun create(high: Double, low: Double): DoubleDouble {
            require(kotlin.math.abs(high) >= kotlin.math.abs(low)) {
                "high must have magnitude >= low: high=$high, low=$low"
            }
            return DoubleDouble(high, low)
        }

        /** Numerical equality: NaN is not equal to anything including itself; +0 equals −0. */
        val numericalEquality: ValueEquality<DoubleDouble> = object : ValueEquality<DoubleDouble> {
            // isNaN() short-circuits NaN cases; Double == then handles +0 == -0 per IEEE 754.
            override fun DoubleDouble.isEqualTo(other: DoubleDouble): Boolean {
                if (isNaN() || other.isNaN()) return false
                return high == other.high && low == other.low
            }
        }

        /** Equivalence equality: NaN equals NaN; +0 does not equal −0. Consistent with [equals]. */
        val equivalenceEquality: ValueEquality<DoubleDouble> = object : ValueEquality<DoubleDouble> {
            // DoubleDouble.equals uses Double.equals on each component: NaN == NaN, +0 != -0.
            override fun DoubleDouble.isEqualTo(other: DoubleDouble): Boolean = this == other
        }

        /**
         * [PartialComparator] that returns `null` when either operand is NaN, reflecting the IEEE 754 rule
         * that NaN is unordered with respect to every value including itself. Non-NaN values are ordered by
         * [compareTo].
         */
        val partialComparator: PartialComparator<DoubleDouble> = PartialComparator { a, b ->
            if (a.isNaN() || b.isNaN()) null else a.compareTo(b)
        }
    }

    // ── Classification ───────────────────────────────────────────────────────

    /** Returns `true` if this value is Not-a-Number. */
    fun isNaN(): Boolean = high.isNaN()

    /** Returns `true` if this value is positive or negative infinity. */
    fun isInfinite(): Boolean = high.isInfinite()

    /** Returns `true` if this value is finite (neither infinity nor NaN). */
    fun isFinite(): Boolean = high.isFinite()

    // ── Sign ─────────────────────────────────────────────────────────────────

    operator fun unaryMinus(): DoubleDouble = DoubleDouble(-high, -low)

    // ── Ordering ─────────────────────────────────────────────────────────────

    /**
     * Compares this value to [other] using a total ordering: NaN is ordered last; otherwise high
     * components are compared first, then low components as a tiebreaker.
     */
    override fun compareTo(other: DoubleDouble): Int {
        val thisNaN = high.isNaN()
        val otherNaN = other.high.isNaN()
        if (thisNaN || otherNaN) return when {
            thisNaN && otherNaN -> 0
            thisNaN -> 1
            else -> -1
        }
        val cmp = high.compareTo(other.high)
        return if (cmp != 0) cmp else low.compareTo(other.low)
    }

    // ── Identity ─────────────────────────────────────────────────────────────

    /**
     * Returns `true` if [other] is a [DoubleDouble] with bitwise-identical components.
     *
     * NaN equals NaN (unlike IEEE 754). Two values that are numerically equal but have different
     * (high, low) decompositions are not considered equal.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DoubleDouble) return false
        if (high.isNaN() && other.high.isNaN()) return true
        return high.equals(other.high) && low.equals(other.low)
    }

    override fun hashCode(): Int {
        if (high.isNaN()) return Double.NaN.hashCode()
        return 31 * high.hashCode() + low.hashCode()
    }

    override fun toString(): String = "($high + $low)"
}
