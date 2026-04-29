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
 * Implementations of this trait must produce a correctly-rounded result — that is, the result must equal
 * `round(a × b + c)` with a single rounding step applied to the infinite-precision value. The banned pattern
 * is the naive `(a * b) + c`, which rounds the product before adding `c` and therefore cannot recover the
 * rounding error. A correctly-rounded software emulation — such as [from] using [TwoProduct] and [TwoSum] —
 * satisfies the contract and is a valid implementation on platforms without a hardware FMA instruction.
 *
 * Note that software emulations based on [TwoProduct] cannot handle the case where `a × b` overflows to
 * infinity but the exact value `a × b + c` is finite; in that case the emulated result will be infinite.
 * Hardware implementations (e.g. [java.lang.Math.fma]) do not have this limitation.
 */
interface FusedMultiplyAdd<T> {
    /**
     * Computes `a × b + c` with a single rounding step applied to the infinite-precision value.
     *
     * The result is the value nearest to the infinite-precision value of `a × b + c`, rounded according to
     * the prevailing rounding mode. Special values follow IEEE 754 semantics: if any operand is NaN, or if
     * the multiplication produces an infinity that is then cancelled by `c`, the result is NaN.
     */
    fun fma(a: T, b: T, c: T): T

    companion object
}
