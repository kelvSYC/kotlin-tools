package com.kelvsyc.kotlin.core.traits

import com.kelvsyc.kotlin.core.traits.dd.TwoProduct
import com.kelvsyc.kotlin.core.traits.dd.TwoSum

/**
 * Returns a [FusedMultiplyAdd] implementation using the Boldo-Melquiond emulated-FMA algorithm.
 *
 * Computes the exact product `(p, e1) = twoProduct(a, b)` and then the exact partial sum
 * `(s, e2) = twoSum(p, c)`, and returns `s + (e1 + e2)`. This is correctly rounded for all
 * normal inputs.
 *
 * The emulation cannot recover a finite result when `a × b` overflows to infinity but the
 * exact `a × b + c` is finite; in that case the result is infinity. Prefer a hardware-backed
 * implementation where available.
 */
fun <T> FusedMultiplyAdd.Companion.from(
    arith: FloatingPointArithmetic<T>,
    twoProduct: TwoProduct<T>,
    twoSum: TwoSum<T>,
): FusedMultiplyAdd<T> = object : FusedMultiplyAdd<T> {
    override fun fma(a: T, b: T, c: T): T {
        val (p, e1) = with(twoProduct) { a.twoProduct(b) }
        val (s, e2) = with(twoSum) { p.twoSum(c) }
        return with(arith) { s.add(e1.add(e2)) }
    }
}
