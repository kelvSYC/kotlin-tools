package com.kelvsyc.kotlin.commons.numbers

import com.kelvsyc.kotlin.core.traits.fp.IntegerPower
import org.apache.commons.numbers.core.DD

private object DdPow : IntegerPower<DD> {
    override fun DD.pow(n: Int): DD {
        require(n >= 0) { "Exponent must be non-negative, got $n" }
        if (n == 0) return DD.of(1.0)
        if (n == 1) return this
        var result = DD.of(1.0)
        var b = this
        var exp = n
        while (exp > 0) {
            if (exp and 1 != 0) result = result.multiply(b)
            b = b.multiply(b)
            exp = exp ushr 1
        }
        return result
    }
}

/**
 * [IntegerPower] instance for Commons Numbers [DD]. Uses binary exponentiation via [DD.multiply].
 *
 * `pow(x, 0)` returns `DD.of(1.0)` for all `x`, including NaN.
 * Negative exponents throw [IllegalArgumentException].
 */
val IntegerPower.Companion.dd: IntegerPower<DD> get() = DdPow
