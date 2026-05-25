package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.BFloat16
import com.kelvsyc.kotlin.core.Float16

/**
 * `FloatingPointExp2` is a trait providing the base-2 exponential function (2^x) for a
 * floating-point type [T].
 *
 * `exp2` computes 2^x exactly for integer arguments (result is a power of two) and with
 * bounded error for all other finite inputs. IEEE 754 semantics are observed for special
 * values: `exp2(NaN) = NaN`, `exp2(+∞) = +∞`, `exp2(-∞) = 0`, `exp2(±0) = 1`.
 *
 * **Why `exp2` is a separate trait from [FloatingPointExpLog]:** `kotlin.math` does not expose
 * `exp2`, so a naive `exp(x * LN2)` delegation is required on platforms without a direct
 * library entry point. That naive approach produces up to ~176 ULP error for [Float]
 * (|x| ≤ 127) and ~1418 ULP for [Double] (|x| ≤ 1023) because the floating-point
 * representation of `LN2` carries ½ ULP relative error that scales linearly with `|x|`.
 *
 * **Cody-Waite range reduction (emulated platforms):** Decompose `x = n + r` where
 * `n = round(x)` (Int) and `|r| ≤ 0.5`. Then `2^x = scalbn(exp(r × ln 2), n)`.
 * Since `|r × LN2| ≤ 0.347`, argument error stays bounded to ≤ 1–2 ULP for all finite
 * normal inputs. `scalbn` is exact for normal numbers.
 *
 * **Accuracy by platform:**
 * - macOS ARM64 and Windows x64: delegates to `platform.posix.exp2` / `platform.posix.exp2f` (≤ 1 ULP)
 * - JVM, JS, and Linux x64: Cody-Waite emulation (≤ 2 ULP)
 *
 * The [Float16] and [BFloat16] instances widen to [Float] for computation and narrow back;
 * precision loss beyond what those types can represent does not occur. No instance is
 * provided for `DoubleDouble`.
 */
interface FloatingPointExp2<T> {
    companion object

    fun T.exp2(): T
}
