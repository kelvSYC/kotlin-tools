package com.kelvsyc.kotlin.commons.numbers

import com.kelvsyc.kotlin.core.traits.fp.FloatingPointArithmetic
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointSquareRoot
import org.apache.commons.numbers.core.DD

// Mirrors the private doubleIsInteger in DoubleDouble.kt — true iff x is a mathematical integer.
private fun doubleIsInteger(x: Double): Boolean {
    val b = x.toBits() and Long.MAX_VALUE
    if (b >= 0x7FF0000000000000L) return false
    if (b == 0L) return true
    val biasedExp = (b ushr 52).toInt()
    if (biasedExp >= 1075) return true
    if (biasedExp < 1023) return false
    return (b and ((1L shl (1075 - biasedExp)) - 1L)) == 0L
}

private object DdArithmetic : FloatingPointArithmetic<DD> {

    // ── Constants ─────────────────────────────────────────────────────────────

    override val zero: DD get() = DD.ZERO
    override val one: DD get() = DD.ONE

    // ── Classification ────────────────────────────────────────────────────────

    // DD.isFinite() and DD.isZero() are Java members; this.foo() resolves to the member, not the override.
    override fun DD.isFinite(): Boolean = this.isFinite()
    override fun DD.isInfinite(): Boolean = hi().isInfinite()
    override fun DD.isNaN(): Boolean = hi().isNaN()
    override fun DD.isZero(): Boolean = this.isZero()
    // hi + lo is integer iff both components are integers; same reasoning as DoubleDouble.
    override fun DD.isInteger(): Boolean = doubleIsInteger(hi()) && doubleIsInteger(lo())

    // ── Sign ──────────────────────────────────────────────────────────────────

    // Sign is determined by hi's sign bit — same convention as DoubleDouble.
    override fun DD.isNegative(): Boolean = hi().toRawBits() < 0L
    // DD.negate() and DD.abs() are Java members; resolve to the member, not the override.
    override fun DD.negate(): DD = this.negate()
    override fun DD.abs(): DD = this.abs()
    // copySign uses the FloatingPointSign default: conditional negate based on isNegative().

    // ── Arithmetic ────────────────────────────────────────────────────────────

    // Each delegates to the corresponding Java member on DD.
    override fun DD.add(other: DD): DD = this.add(other)
    override fun DD.subtract(other: DD): DD = this.subtract(other)
    override fun DD.multiply(other: DD): DD = this.multiply(other)
    override fun DD.divide(other: DD): DD = this.divide(other)

    // ── Total order ───────────────────────────────────────────────────────────

    // DD has no compareTo; use the same NaN-last, hi-then-lo ordering as DoubleDouble.
    override fun DD.compareTo(other: DD): Int {
        val thisNaN = hi().isNaN()
        val otherNaN = other.hi().isNaN()
        if (thisNaN || otherNaN) return when {
            thisNaN && otherNaN -> 0
            thisNaN -> 1
            else -> -1
        }
        val cmp = hi().compareTo(other.hi())
        return if (cmp != 0) cmp else lo().compareTo(other.lo())
    }
}

private object DdSquareRoot : FloatingPointSquareRoot<DD> {
    override fun DD.sqrt(): DD = this.sqrt()
}

/**
 * [FloatingPointArithmetic] instance for Commons Numbers [DD].
 *
 * Delegates `add`, `subtract`, `multiply`, `divide`, `negate`, `abs`, `isFinite`, and `isZero`
 * directly to [DD]'s Java methods. Derives `isNaN` and `isInfinite` from `hi()`, `isNegative`
 * from `hi()`'s sign bit, `isInteger` from a bit-pattern check on both components, and `compareTo`
 * from a NaN-last, hi-then-lo total ordering — matching [com.kelvsyc.kotlin.core.fp.DoubleDouble]'s
 * behaviour throughout.
 */
val FloatingPointArithmetic.Companion.dd: FloatingPointArithmetic<DD> get() = DdArithmetic

/**
 * [FloatingPointSquareRoot] instance for Commons Numbers [DD]. Delegates to [DD.sqrt].
 */
val FloatingPointSquareRoot.Companion.dd: FloatingPointSquareRoot<DD> get() = DdSquareRoot
