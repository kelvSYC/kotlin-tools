package com.kelvsyc.kotlin.core.traits

/**
 * `FusedMultiplyAdd` is a trait that denotes that a type [T] supports the "fused multiply add" (FMA) operation.
 *
 * FMA computes `a × b + c` as a single floating-point operation. The key property is that the infinite-precision
 * product `a × b` is computed exactly and only rounded once — together with the addition of `c` — to produce the
 * final result. This contrasts with the naive sequence `(a * b) + c`, which rounds twice: once after the
 * multiplication and again after the addition. The two extra rounding errors can compound, particularly when `c`
 * is close in magnitude and opposite in sign to `a × b` (catastrophic cancellation).
 *
 * The single-rounding guarantee makes FMA essential for algorithms that require exact error representation, such
 * as the `2Prod` operation used in double-double arithmetic: `fma(a, b, -(a * b))` yields the exact rounding
 * error of the product `a * b`. With a naive implementation, this expression would evaluate to zero whenever
 * `-(a * b)` is taken from the already-rounded product.
 *
 * Implementations of this trait must perform a true fused multiply-add — that is, the multiplication and addition
 * are performed as a single operation with a single rounding step, with no intermediate rounding of the product.
 * Emulating FMA by performing separate multiply and add operations is explicitly prohibited, as this produces
 * results that are not guaranteed to be correctly rounded and defeats the purpose of the trait.
 *
 * As a consequence, this trait has no JavaScript implementation: JavaScript provides no mechanism to perform a
 * true FMA, and a software emulation would violate the contract above.
 */
interface FusedMultiplyAdd<T> {
    /**
     * Computes `a × b + c` as a single floating-point operation with a single rounding step.
     *
     * The result is the value nearest to the infinite-precision value of `a × b + c`, rounded according to
     * the prevailing rounding mode. Special values follow IEEE 754 semantics: if any operand is NaN, or if
     * the multiplication produces an infinity that is then cancelled by `c`, the result is NaN.
     */
    fun fma(a: T, b: T, c: T): T

    companion object
}
