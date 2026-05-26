package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.BFloat16
import com.kelvsyc.kotlin.core.Float16

/**
 * `FloatingPointExp10` is a trait providing the base-10 exponential function (10^x) for a
 * floating-point type [T].
 *
 * IEEE 754 semantics are observed for special values: `exp10(NaN) = NaN`,
 * `exp10(+∞) = +∞`, `exp10(-∞) = 0`, `exp10(±0) = 1`.
 *
 * **Why `exp10` is a separate trait from [FloatingPointExpLog]:** a naive `exp(x * ln(10))`
 * delegation accumulates O(|x|) ULP error because `ln(10)` cannot be represented exactly.
 * For |x| = 300 (near the Double overflow boundary) this exceeds 300 ULP.
 *
 * **Emulated platforms (Cody-Waite with Dekker-split constant):** decompose
 * `x = n × log10(2) + r` where `n = round(x / log10(2))` and `|r| ≤ 0.5 × log10(2) ≈ 0.15`.
 * Then `10^x = scalbn(exp(r × ln10), n)`. The Dekker split of `log10(2)` into a 26-bit high
 * part and a low correction part keeps the subtraction `x − n × log10(2)` free of
 * catastrophic cancellation, bounding total error to ≤ 2 ULP for Double.
 * For [Float], widening to Double before the Cody-Waite step is sufficient (≤ 2 ULP).
 *
 * **Accuracy by platform:**
 * - Linux x64: delegates to `exp10`/`exp10f` from glibc (GNU extension, ≤ 1 ULP)
 * - JVM, JS, macOS ARM64, Windows x64: Cody-Waite with Dekker split (≤ 2 ULP)
 *
 * The [Float16] and [BFloat16] instances widen to [Float] for computation and narrow back.
 * No instance is provided for `DoubleDouble`.
 */
interface FloatingPointExp10<T> {
    companion object

    fun T.exp10(): T
}
