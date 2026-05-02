package com.kelvsyc.kotlin.core.traits.dd

import com.kelvsyc.kotlin.core.traits.fp.FloatingPointArithmetic

/**
 * `TwoSum` is a trait providing an implementation of the two-sum error-free transformations on a
 * floating-point type [T].
 *
 * Both algorithms compute the exact sum of two floating-point values `a` and `b` as a [Pair] `(s, e)`
 * where `s = fl(a + b)` (the rounded sum) and `s + e = a + b` exactly (no rounding). The pair components
 * are ordered `(high, low)` — i.e. `first` is the rounded sum and `second` is the error correction term.
 *
 * Two operations are available:
 * - [twoSum]: Knuth two-sum; correct for any relative magnitude of the operands (6 FLOP).
 * - [fastTwoSum]: fast two-sum; correct only when `|this| >= |other|` (3 FLOP). The default
 *   implementation delegates to [twoSum]; override to take advantage of the precondition.
 *
 * A factory implementation is available via [Companion.from].
 */
interface TwoSum<T> {
    companion object

    fun T.twoSum(other: T): Pair<T, T>

    fun T.fastTwoSum(other: T): Pair<T, T> = twoSum(other)
}

/**
 * Returns a [TwoSum] implementation derived from [arith].
 *
 * [TwoSum.twoSum] uses the Knuth two-sum algorithm (6 FLOP, any operand order).
 * [TwoSum.fastTwoSum] uses the fast two-sum algorithm (3 FLOP, requires `|a| >= |b|`).
 */
fun <T> TwoSum.Companion.from(arith: FloatingPointArithmetic<T>): TwoSum<T> = object : TwoSum<T> {
    override fun T.twoSum(other: T): Pair<T, T> {
        val self = this
        val s = with(arith) { self.add(other) }
        val aPrime = with(arith) { s.subtract(other) }
        val bPrime = with(arith) { s.subtract(aPrime) }
        val da = with(arith) { self.subtract(aPrime) }
        val db = with(arith) { other.subtract(bPrime) }
        val e = with(arith) { da.add(db) }
        return Pair(s, e)
    }

    override fun T.fastTwoSum(other: T): Pair<T, T> {
        val self = this
        val s = with(arith) { self.add(other) }
        val bPrime = with(arith) { s.subtract(self) }
        val e = with(arith) { other.subtract(bPrime) }
        return Pair(s, e)
    }
}
