package com.kelvsyc.kotlin.commons.numbers

import com.kelvsyc.kotlin.core.traits.fp.FloatingPointRounding
import org.apache.commons.numbers.core.DD

private object DdRounding : FloatingPointRounding<DD> {
    // DD.floor() and DD.ceil() are Java members; this.floor()/ceil() resolves to the member.
    override fun DD.floor(): DD = this.floor()
    override fun DD.ceil(): DD = this.ceil()
}

/**
 * [FloatingPointRounding] instance for Commons Numbers [DD]. Delegates to [DD.floor] and [DD.ceil].
 */
val FloatingPointRounding.Companion.dd: FloatingPointRounding<DD> get() = DdRounding
