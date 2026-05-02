package com.kelvsyc.kotlin.core.fp

import com.kelvsyc.kotlin.core.PartialComparator
import com.kelvsyc.kotlin.core.traits.ValueEquality
import com.kelvsyc.kotlin.core.traits.fp.DoubleBinaryFloatingPoint as Trait
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointClassification
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointSign

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
 * The same instance is available for generic algorithms that operate through the `BinaryFloatingPoint`
 * trait.
 */
private fun doubleIsInteger(x: Double): Boolean {
    val b = x.toBits() and Long.MAX_VALUE
    if (b >= 0x7FF0000000000000L) return false
    if (b == 0L) return true
    val biasedExp = (b ushr 52).toInt()
    if (biasedExp >= 1075) return true
    if (biasedExp < 1023) return false
    return (b and ((1L shl (1075 - biasedExp)) - 1L)) == 0L
}

class DoubleDouble internal constructor(override val high: Double, override val low: Double)
    : DoubleBinaryFloatingPoint<Double>, Comparable<DoubleDouble> {

    companion object : Trait<DoubleDouble> {

        // ── Special values ────────────────────────────────────────────────────

        /** Not-a-Number. Equal to itself under [equals]; ordered last under [compareTo]. */
        override val NaN: DoubleDouble = DoubleDouble(Double.NaN, 0.0)
        val POSITIVE_INFINITY: DoubleDouble = DoubleDouble(Double.POSITIVE_INFINITY, 0.0)
        val NEGATIVE_INFINITY: DoubleDouble = DoubleDouble(Double.NEGATIVE_INFINITY, 0.0)
        val ZERO: DoubleDouble = DoubleDouble(0.0, 0.0)
        val ONE: DoubleDouble = DoubleDouble(1.0, 0.0)
        override val positiveInfinity: DoubleDouble get() = POSITIVE_INFINITY
        override val negativeInfinity: DoubleDouble get() = NEGATIVE_INFINITY
        override val positiveZero: DoubleDouble get() = ZERO
        override val negativeZero: DoubleDouble = DoubleDouble(-0.0, 0.0)
        // 2^970 = ulp(Double.MAX_VALUE) / 2, the largest valid low when high = Double.MAX_VALUE.
        override val maxValue: DoubleDouble = DoubleDouble(Double.MAX_VALUE, Double.fromBits(0x7C90000000000000L))
        // Smallest positive DoubleDouble: high is the smallest positive subnormal Double, low must be 0.
        override val minValue: DoubleDouble = DoubleDouble(Double.MIN_VALUE, 0.0)
        // 2^(-105): DoubleDouble unit roundoff (~2p−2 significant bits, p = 53).
        override val epsilon: DoubleDouble = DoubleDouble(Double.fromBits(0x3960000000000000L), 0.0)

        // ── Factory ───────────────────────────────────────────────────────────

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

        // ── Equality ──────────────────────────────────────────────────────────

        /** Numerical equality: NaN is not equal to anything including itself; +0 equals −0. */
        override val numericalEquality: ValueEquality<DoubleDouble> = object : ValueEquality<DoubleDouble> {
            // isNaN() short-circuits NaN cases; Double == then handles +0 == -0 per IEEE 754.
            override fun DoubleDouble.isEqualTo(other: DoubleDouble): Boolean {
                if (isNaN() || other.isNaN()) return false
                return high == other.high && low == other.low
            }
        }

        /** Equivalence equality: NaN equals NaN; +0 does not equal −0. Consistent with [equals]. */
        override val equivalenceEquality: ValueEquality<DoubleDouble> = object : ValueEquality<DoubleDouble> {
            // DoubleDouble.equals uses Double.equals on each component: NaN == NaN, +0 != -0.
            override fun DoubleDouble.isEqualTo(other: DoubleDouble): Boolean = this == other
        }

        // ── Ordering ──────────────────────────────────────────────────────────

        override val comparator: Comparator<DoubleDouble> = Comparator { a, b -> a.compareTo(b) }

        /**
         * [PartialComparator] that returns `null` when either operand is NaN, reflecting the IEEE 754 rule
         * that NaN is unordered with respect to every value including itself. Non-NaN values are ordered by
         * [compareTo].
         */
        override val partialComparator: PartialComparator<DoubleDouble> = PartialComparator { a, b ->
            if (a.isNaN() || b.isNaN()) null else a.compareTo(b)
        }

        // ── Classification ────────────────────────────────────────────────────

        override val classification: FloatingPointClassification<DoubleDouble> =
            object : FloatingPointClassification<DoubleDouble> {
                override fun DoubleDouble.isNaN(): Boolean = high.isNaN()
                override fun DoubleDouble.isInfinite(): Boolean = high.isInfinite()
                override fun DoubleDouble.isFinite(): Boolean = high.isFinite()
                // +0 and -0 both satisfy high == 0.0 under IEEE 754 ==; by the representation
                // invariant, if high is zero then low must also be zero.
                override fun DoubleDouble.isZero(): Boolean = high == 0.0
                // hi + lo is integer iff both components are integers: for |hi| < 2^52,
                // |lo| < 0.5*ulp(hi) < 1 so lo cannot cancel frac(hi); for |hi| >= 2^52,
                // hi is always an integer and lo must also be an integer.
                override fun DoubleDouble.isInteger(): Boolean = doubleIsInteger(high) && doubleIsInteger(low)
            }

        // ── Sign ──────────────────────────────────────────────────────────────

        override val sign: FloatingPointSign<DoubleDouble> = object : FloatingPointSign<DoubleDouble> {
            // Sign is determined entirely by the high component's sign bit.
            override fun DoubleDouble.isNegative(): Boolean = high.toBits() < 0L
            // Negate both components to preserve the canonical pair invariant.
            override fun DoubleDouble.negate(): DoubleDouble = -this
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
