package com.kelvsyc.kotlin.commons.numbers

import com.kelvsyc.kotlin.core.traits.fp.FloatingPointRounding
import org.apache.commons.numbers.core.DD

private object DdRounding : FloatingPointRounding<DD> {
    // DD.floor() and DD.ceil() are Java members; this.floor()/ceil() resolves to the member.
    override fun DD.floor(): DD = this.floor()
    override fun DD.ceil(): DD = this.ceil()
    // For a valid DD number the sign of hi() matches the sign of the value, so we
    // dispatch on hi() to choose the correct directed rounding direction.
    override fun DD.trunc(): DD = if (hi() >= 0.0) this.floor() else this.ceil()
    override fun DD.roundUp(): DD = when {
        hi() > 0.0 -> this.ceil()
        hi() < 0.0 -> this.floor()
        else -> this
    }
}

/**
 * [FloatingPointRounding] instance for Commons Numbers [DD]. Delegates to [DD.floor] and [DD.ceil].
 */
val FloatingPointRounding.Companion.dd: FloatingPointRounding<DD> get() = DdRounding
