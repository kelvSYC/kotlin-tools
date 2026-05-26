package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.BFloat16
import com.kelvsyc.kotlin.core.Float16

/**
 * `FloatingPointTrigPi` is a trait providing the seven IEEE 754-2019 / C23 π-scaled
 * trigonometric functions for a floating-point type [T]:
 *
 * **Forward functions** — compute the trig function of πx:
 * - [sinPi] — sin(πx), exact zero at integer x
 * - [cosPi] — cos(πx), exact ±1 at integer x
 * - [tanPi] — tan(πx), exact zero at integer x, ±∞ at half-integer x
 *
 * **Inverse functions** — return the result divided by π:
 * - [asinPi] — asin(x)/π; domain [−1, 1]; exact ±0.5 at x = ±1
 * - [acosPi] — acos(x)/π; domain [−1, 1]; exact 0 at x = 1, exact 1 at x = −1
 * - [atanPi] — atan(x)/π; exact ±0.5 at ±∞
 * - [atan2Pi] — atan2(y, x)/π; exact multiples of 0.25 at axis-aligned inputs
 *
 * **Why naive delegation is excluded:** `sin(x * PI)` accumulates O(|x|) ULP argument error
 * because `PI` cannot be represented exactly. More critically, `sinPi(1.0)` must return
 * exactly 0.0 — a property that `sin(1.0 * PI)` cannot guarantee because
 * `1.0 * Math.PI` is not a mathematical multiple of π.
 *
 * **IEEE 754 special values** — all seven functions preserve NaN. Additional:
 * - `sinPi(±0) = ±0`, `sinPi(integer) = ±0` (sign of x), `sinPi(±∞) = NaN`
 * - `cosPi(integer n) = (−1)^n`, `cosPi(half-integer) = 0`, `cosPi(±∞) = NaN`
 * - `tanPi(integer) = ±0` (sign of x), `tanPi(half-integer) = ±∞` (sign of x), `tanPi(±∞) = NaN`
 * - `asinPi(±1) = ±0.5`, `asinPi(outside [−1,1]) = NaN`
 * - `acosPi(1) = 0`, `acosPi(0) = 0.5`, `acosPi(−1) = 1`, `acosPi(outside [−1,1]) = NaN`
 * - `atanPi(±0) = ±0`, `atanPi(±∞) = ±0.5`
 * - `atan2Pi(±0, x>0) = ±0`, `atan2Pi(±0, x<0) = ±1`, `atan2Pi(y>0, 0) = 0.5`
 *
 * **Algorithms:**
 * - Forward functions (emulated): exact mod-1 argument reduction, hard-return at integer and
 *   half-integer inputs, then `sin`/`cos`/`tan` of `PI_HI × frac + PI_LO × frac` where
 *   `(PI_HI, PI_LO)` is a Dekker-split of π (≤ 2 ULP total).
 * - Inverse functions: hard-code the known-exact outputs (±0, ±0.25, ±0.5, ±0.75, ±1),
 *   then `asin(x) * (1/π)` etc. for all other inputs (≤ 2 ULP).
 * - macOS ARM64: delegates to platform `sinpi`/`cospi`/`tanpi`/`asinpi`/`acospi`/`atanpi`/
 *   `atan2pi` and their `f` variants via cinterop.
 *
 * Standard implementations for [Float16], [BFloat16], [Float], and [Double] are available as
 * [Companion.float16], [Companion.bfloat16], [Companion.float], and [Companion.double].
 * No `DoubleDouble` instance is provided.
 */
interface FloatingPointTrigPi<T> {
    companion object

    fun T.sinPi(): T
    fun T.cosPi(): T
    fun T.tanPi(): T

    fun T.asinPi(): T
    fun T.acosPi(): T
    fun T.atanPi(): T
    fun T.atan2Pi(x: T): T
}
