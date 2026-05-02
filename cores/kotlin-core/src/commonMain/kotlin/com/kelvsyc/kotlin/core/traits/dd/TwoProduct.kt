package com.kelvsyc.kotlin.core.traits.dd

import com.kelvsyc.kotlin.core.traits.fp.FloatingPointArithmetic
import com.kelvsyc.kotlin.core.traits.fp.FusedMultiplyAdd
import com.kelvsyc.kotlin.core.traits.fp.IeeeBinaryFloatingPoint

/**
 * `TwoProduct` is a trait providing an implementation of the two-product error-free transformation on a
 * floating-point type [T].
 *
 * The two-product algorithm computes the exact product of two floating-point values `a` and `b` as a
 * [Pair] `(p, e)` where `p = fl(a * b)` (the rounded product) and `p + e = a * b` exactly (no rounding).
 * The pair components are ordered `(high, low)` — i.e. `first` is the rounded product and `second` is the
 * error correction term.
 *
 * Two factory implementations are available via [Companion.from]:
 * - FMA-based (`from(arith, fma)`): one multiply and one FMA; always correctly rounded.
 * - Veltkamp-Dekker (`from(arith, meta)`): purely from ordinary arithmetic; works on all platforms.
 */
interface TwoProduct<T> {
    companion object

    fun T.twoProduct(other: T): Pair<T, T>
}

/**
 * Returns a [TwoProduct] implementation using the FMA operation.
 *
 * Computes `p = fl(a * b)` then `e = fma(a, b, -p)` to recover the exact rounding error in one step.
 * Requires a true FMA — see [FusedMultiplyAdd].
 */
fun <T> TwoProduct.Companion.from(
    arith: FloatingPointArithmetic<T>,
    fma: FusedMultiplyAdd<T>,
): TwoProduct<T> = object : TwoProduct<T> {
    override fun T.twoProduct(other: T): Pair<T, T> {
        val self = this
        val p = with(arith) { self.multiply(other) }
        val e = fma.fma(self, other, with(arith) { p.negate() })
        return Pair(p, e)
    }
}

/**
 * Returns a [TwoProduct] implementation using Veltkamp-Dekker splitting.
 *
 * Splits each operand into two non-overlapping halves using a splitting constant derived from
 * [IeeeBinaryFloatingPoint.mantissaBits], then recovers the exact rounding error using only ordinary
 * floating-point arithmetic. Works on all platforms, including JavaScript.
 *
 * Note: intermediate values during splitting are scaled by the splitting constant
 * `2^⌈(mantissaBits+1)/2⌉ + 1`. For types with a small dynamic range (e.g. Float16) this may
 * overflow for large inputs.
 */
fun <T> TwoProduct.Companion.from(
    arith: FloatingPointArithmetic<T>,
    meta: IeeeBinaryFloatingPoint<T>,
): TwoProduct<T> {
    // Precompute the Veltkamp splitting constant: 2^⌈(mantissaBits+1)/2⌉ + 1
    val halfP = (meta.mantissaBits + 2) / 2
    var splitter = arith.one
    repeat(halfP) { splitter = with(arith) { splitter.add(splitter) } }
    splitter = with(arith) { splitter.add(arith.one) }
    val c = splitter

    return object : TwoProduct<T> {
        override fun T.twoProduct(other: T): Pair<T, T> {
            val self = this
            val p = with(arith) { self.multiply(other) }

            // Veltkamp-split self into (aHi, aLo)
            val aC = with(arith) { c.multiply(self) }
            val aHi = with(arith) { aC.subtract(aC.subtract(self)) }
            val aLo = with(arith) { self.subtract(aHi) }

            // Veltkamp-split other into (bHi, bLo)
            val bC = with(arith) { c.multiply(other) }
            val bHi = with(arith) { bC.subtract(bC.subtract(other)) }
            val bLo = with(arith) { other.subtract(bHi) }

            // Error = aLo*bLo - (p - aHi*bHi - aLo*bHi - aHi*bLo)
            val e = with(arith) {
                aLo.multiply(bLo)
                    .subtract(
                        p.subtract(aHi.multiply(bHi))
                         .subtract(aLo.multiply(bHi))
                         .subtract(aHi.multiply(bLo))
                    )
            }

            return Pair(p, e)
        }
    }
}
