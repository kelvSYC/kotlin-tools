package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.BidDouble
import com.kelvsyc.kotlin.core.BidFloat
import com.kelvsyc.kotlin.core.DpdDouble
import com.kelvsyc.kotlin.core.DpdFloat
import com.kelvsyc.kotlin.core.fp.bidDpdDouble
import com.kelvsyc.kotlin.core.fp.bidDpdFloat
import com.kelvsyc.kotlin.core.wrap

// ── DpdFloat ──────────────────────────────────────────────────────────────────

private val dpdFloatInstance: FloatingPointScald<DpdFloat> = object : FloatingPointScald<DpdFloat> {
    override fun DpdFloat.scald(n: Int): DpdFloat =
        bidDpdFloat.wrap { a: BidFloat -> with(FloatingPointScald.bidFloat) { a.scald(n) } }(this)
}

// ── DpdDouble ─────────────────────────────────────────────────────────────────

private val dpdDoubleInstance: FloatingPointScald<DpdDouble> = object : FloatingPointScald<DpdDouble> {
    override fun DpdDouble.scald(n: Int): DpdDouble =
        bidDpdDouble.wrap { a: BidDouble -> with(FloatingPointScald.bidDouble) { a.scald(n) } }(this)
}

val FloatingPointScald.Companion.dpdFloat: FloatingPointScald<DpdFloat> get() = dpdFloatInstance
val FloatingPointScald.Companion.dpdDouble: FloatingPointScald<DpdDouble> get() = dpdDoubleInstance
