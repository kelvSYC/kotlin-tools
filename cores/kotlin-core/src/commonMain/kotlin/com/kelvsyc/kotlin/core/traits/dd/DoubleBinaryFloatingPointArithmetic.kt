package com.kelvsyc.kotlin.core.traits.dd

import com.kelvsyc.kotlin.core.fp.DoubleBinaryFloatingPoint
import com.kelvsyc.kotlin.core.traits.FloatingPointArithmetic

/**
 * Specialization of [FloatingPointArithmetic] for double binary floating-point types [F], with underlying
 * floating-point type [T].
 *
 * Instances are created via [Companion.from].
 */
interface DoubleBinaryFloatingPointArithmetic<F : DoubleBinaryFloatingPoint<T>, T> : FloatingPointArithmetic<F> {
    companion object
}

/**
 * Returns a [DoubleBinaryFloatingPointArithmetic] implementation for [F], backed by [arith] for scalar
 * operations, [twoProduct] and [twoSum] for error-free transformations, and [construct] for creating
 * instances of [F] from a (high, low) pair.
 *
 * When [twoDiv] is provided, division uses the TwoDiv path: `(q1, e1) = twoDiv(a.hi, b.hi)` directly
 * yields the scalar quotient and its exact rounding error, avoiding a full TwoProduct decomposition.
 * Without [twoDiv], division falls back to a TwoProduct-based residual computation.
 */
fun <F : DoubleBinaryFloatingPoint<T>, T> DoubleBinaryFloatingPointArithmetic.Companion.from(
    arith: FloatingPointArithmetic<T>,
    twoProduct: TwoProduct<T>,
    twoSum: TwoSum<T>,
    construct: (T, T) -> F,
    twoDiv: TwoDiv<T>? = null,
): DoubleBinaryFloatingPointArithmetic<F, T> = object : DoubleBinaryFloatingPointArithmetic<F, T> {

    // ── Constants ────────────────────────────────────────────────────────────
    override val zero: F get() = construct(arith.zero, arith.zero)
    override val one: F get() = construct(arith.one, arith.zero)

    // ── Classification ───────────────────────────────────────────────────────
    override fun F.isNaN(): Boolean = with(arith) { high.isNaN() }
    override fun F.isInfinite(): Boolean = with(arith) { high.isInfinite() }
    override fun F.isFinite(): Boolean = with(arith) { high.isFinite() }

    // ── Sign ─────────────────────────────────────────────────────────────────
    override fun F.unaryMinus(): F =
        construct(with(arith) { high.unaryMinus() }, with(arith) { low.unaryMinus() })

    // ── Total order ──────────────────────────────────────────────────────────
    override fun F.compareTo(other: F): Int {
        val thisNaN = with(arith) { high.isNaN() }
        val otherNaN = with(arith) { other.high.isNaN() }
        if (thisNaN || otherNaN) return when {
            thisNaN && otherNaN -> 0
            thisNaN -> 1
            else -> -1
        }
        val cmp = with(arith) { high.compareTo(other.high) }
        return if (cmp != 0) cmp else with(arith) { low.compareTo(other.low) }
    }

    // ── Arithmetic ───────────────────────────────────────────────────────────

    // Sloppy Knuth/Dekker DD addition (~2p-4 bits).
    override fun F.add(other: F): F {
        val self = this
        val (s1, s2) = with(twoSum) { self.high.twoSum(other.high) }
        val (t1, t2) = with(twoSum) { self.low.twoSum(other.low) }
        val c = with(arith) { s2.add(t1) }
        val (r1, r2a) = with(twoSum) { s1.fastTwoSum(c) }
        val r2 = with(arith) { r2a.add(t2) }
        val (hi, lo) = with(twoSum) { r1.fastTwoSum(r2) }
        return construct(hi, lo)
    }

    override fun F.subtract(other: F): F = add(other.unaryMinus())

    // Sloppy DD multiplication: full hi×hi via TwoProduct, first-order cross terms.
    override fun F.multiply(other: F): F {
        val self = this
        val (p1, p2) = with(twoProduct) { self.high.twoProduct(other.high) }
        val corr = with(arith) {
            p2.add(self.high.multiply(other.low))
              .add(self.low.multiply(other.high))
        }
        val (hi, lo) = with(twoSum) { p1.fastTwoSum(corr) }
        return construct(hi, lo)
    }

    // Sloppy DD division (~2p-4 bits).
    // When twoDiv is available: (q1, e1) = twoDiv(a.hi, b.hi) gives q1 and the exact
    // rounding error e1 = a.hi/b.hi - q1; then q2 = e1 + (a.lo - q1*b.lo)/b.hi captures
    // the first-order low corrections; result = fastTwoSum(q1, q2).
    // Fallback (no twoDiv): compute r = a - q1*b via twoProduct + DD subtraction, then
    // q2 = r.hi / b.hi; result = fastTwoSum(q1, q2).
    override fun F.divide(other: F): F {
        val self = this
        if (twoDiv != null) {
            val (q1, e1) = with(twoDiv) { self.high.twoDiv(other.high) }
            val q2 = with(arith) {
                val loCorr = self.low.subtract(q1.multiply(other.low)).divide(other.high)
                e1.add(loCorr)
            }
            val (hi, lo) = with(twoSum) { q1.fastTwoSum(q2) }
            return construct(hi, lo)
        }

        val q1 = with(arith) { self.high.divide(other.high) }
        val (pb1, pb2raw) = with(twoProduct) { q1.twoProduct(other.high) }
        val pb2 = with(arith) { pb2raw.add(q1.multiply(other.low)) }
        val (pbH, pbL) = with(twoSum) { pb1.fastTwoSum(pb2) }

        val (rH1, rL1) = with(twoSum) { self.high.twoSum(with(arith) { pbH.unaryMinus() }) }
        val rL = with(arith) { rL1.add(self.low).subtract(pbL) }
        val (rH, _) = with(twoSum) { rH1.fastTwoSum(rL) }

        val q2 = with(arith) { rH.divide(other.high) }
        val (hi, lo) = with(twoSum) { q1.fastTwoSum(q2) }
        return construct(hi, lo)
    }
}
