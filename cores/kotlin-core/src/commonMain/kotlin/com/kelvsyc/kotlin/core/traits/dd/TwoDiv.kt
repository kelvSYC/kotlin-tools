package com.kelvsyc.kotlin.core.traits.dd

import com.kelvsyc.kotlin.core.traits.fp.FloatingPointArithmetic
import com.kelvsyc.kotlin.core.traits.fp.FusedMultiplyAdd

/**
 * `TwoDiv` is a trait providing an implementation of the two-div error-free transformation on a
 * floating-point type [T].
 *
 * The two-div algorithm computes the exact quotient of two floating-point values `a` and `b` as a
 * [Pair] `(q, e)` where `q = fl(a / b)` (the rounded quotient) and `q + e = a / b` exactly (no rounding).
 * The pair components are ordered `(high, low)` — i.e. `first` is the rounded quotient and `second` is
 * the error correction term.
 *
 * A correctly-rounded FMA is required; without it there is no error-free way to recover `a - q * b` exactly.
 * A factory implementation is available via [Companion.from]; see [FusedMultiplyAdd.Companion.from] for a
 * software emulation that satisfies the contract on all platforms.
 */
interface TwoDiv<T> {
    companion object

    fun T.twoDiv(other: T): Pair<T, T>
}

/**
 * Returns a [TwoDiv] implementation using the FMA operation.
 *
 * Computes `q = fl(a / b)` then `e = fma(q, -b, a) / b` to recover the exact rounding error.
 * Requires a true FMA — see [FusedMultiplyAdd].
 */
fun <T> TwoDiv.Companion.from(
    arith: FloatingPointArithmetic<T>,
    fma: FusedMultiplyAdd<T>,
): TwoDiv<T> = object : TwoDiv<T> {
    override fun T.twoDiv(other: T): Pair<T, T> {
        val self = this
        val q = with(arith) { self.divide(other) }
        val e = with(arith) { fma.fma(q, other.negate(), self).divide(other) }
        return Pair(q, e)
    }
}
