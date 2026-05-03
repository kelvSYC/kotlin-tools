package com.kelvsyc.kotlin.commons.numbers

import com.kelvsyc.kotlin.core.traits.fp.FloatingPointScalb
import org.apache.commons.numbers.core.DD

private object DdScalb : FloatingPointScalb<DD> {
    // DD.scalb(int) is a Java member; this.scalb(n) resolves to the member, not the override.
    override fun DD.scalb(n: Int): DD = this.scalb(n)
}

/**
 * [FloatingPointScalb] instance for Commons Numbers [DD]. Delegates to [DD.scalb].
 *
 * `scalb(x, n)` computes `x × 2^n` by scaling both the high and low components independently,
 * preserving the double-double representation without rounding.
 */
val FloatingPointScalb.Companion.dd: FloatingPointScalb<DD> get() = DdScalb
