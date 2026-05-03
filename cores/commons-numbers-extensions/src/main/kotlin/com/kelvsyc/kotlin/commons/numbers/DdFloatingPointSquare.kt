package com.kelvsyc.kotlin.commons.numbers

import com.kelvsyc.kotlin.core.traits.fp.FloatingPointSquare
import org.apache.commons.numbers.core.DD

private object DdSquare : FloatingPointSquare<DD> {
    override fun DD.square(): DD = this.multiply(this)
}

/**
 * [FloatingPointSquare] instance for Commons Numbers [DD]. Delegates to [DD.multiply].
 */
val FloatingPointSquare.Companion.dd: FloatingPointSquare<DD> get() = DdSquare
