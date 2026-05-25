package com.kelvsyc.kotlin.core.traits.integral

/**
 * `DivRem` is a trait providing combined truncating division and remainder for an integer type [T].
 *
 * [divRem] computes both the truncated quotient and the truncating remainder in a single pass.
 * On supported platforms this maps to a hardware or library primitive that produces both results
 * from one division instruction:
 * - **Kotlin/Native (`Int`)**: C stdlib `div()` → `div_t { quot; rem }`
 * - **Kotlin/Native (`Long`)**: C stdlib `lldiv()` → `lldiv_t { quot; rem }`
 * - **JVM (`BigInteger`)**: `BigInteger.divideAndRemainder()`
 *
 * Instances are intentionally absent on platforms and for types where no single-pass primitive
 * exists (JVM primitives, Kotlin/JS, `Byte`, `Short`, unsigned types). Callers on those platforms
 * should call [IntegerArithmetic.divide] and [IntegerArithmetic.rem] individually.
 *
 * @see DivRemResult
 */
interface DivRem<T> {
    companion object

    /**
     * Returns `(this / other, this % other)` computed in a single pass. Both the quotient and
     * remainder use truncating (toward-zero) semantics, matching [IntegerArithmetic.divide] and
     * [IntegerArithmetic.rem].
     */
    fun T.divRem(other: T): DivRemResult<T>
}
