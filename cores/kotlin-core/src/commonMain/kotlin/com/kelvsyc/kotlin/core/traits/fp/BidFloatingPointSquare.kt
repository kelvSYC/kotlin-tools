package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.BidDouble
import com.kelvsyc.kotlin.core.BidFloat
import com.kelvsyc.kotlin.core.DpdDouble
import com.kelvsyc.kotlin.core.DpdFloat
import com.kelvsyc.kotlin.core.fp.bidDpdDouble
import com.kelvsyc.kotlin.core.fp.bidDpdFloat
import com.kelvsyc.kotlin.core.traits.dfp.bidDouble as bidDoubleArithProp
import com.kelvsyc.kotlin.core.traits.dfp.bidFloat as bidFloatArithProp
import com.kelvsyc.kotlin.core.wrap

// Captured at file level; aliased imports avoid ambiguity with the 'bidFloat'/'bidDouble'
// extension properties on other companion objects defined in this package.
private val sqBidFloatArith: FloatingPointArithmetic<BidFloat> = FloatingPointArithmetic.bidFloatArithProp
private val sqBidDoubleArith: FloatingPointArithmetic<BidDouble> = FloatingPointArithmetic.bidDoubleArithProp

// ── BidFloat ──────────────────────────────────────────────────────────────────

private val bidFloatInstance: FloatingPointSquare<BidFloat> = object : FloatingPointSquare<BidFloat> {
    override fun BidFloat.square(): BidFloat =
        with(sqBidFloatArith) { this@square.multiply(this@square) }
}

// ── BidDouble ─────────────────────────────────────────────────────────────────

private val bidDoubleInstance: FloatingPointSquare<BidDouble> =
    object : FloatingPointSquare<BidDouble> {
        override fun BidDouble.square(): BidDouble =
            with(sqBidDoubleArith) { this@square.multiply(this@square) }
    }

// ── DpdFloat ──────────────────────────────────────────────────────────────────

private val dpdFloatInstance: FloatingPointSquare<DpdFloat> = object : FloatingPointSquare<DpdFloat> {
    override fun DpdFloat.square(): DpdFloat =
        bidDpdFloat.wrap { a: BidFloat -> with(sqBidFloatArith) { a.multiply(a) } }(this)
}

// ── DpdDouble ─────────────────────────────────────────────────────────────────

private val dpdDoubleInstance: FloatingPointSquare<DpdDouble> =
    object : FloatingPointSquare<DpdDouble> {
        override fun DpdDouble.square(): DpdDouble =
            bidDpdDouble.wrap { a: BidDouble -> with(sqBidDoubleArith) { a.multiply(a) } }(this)
    }

val FloatingPointSquare.Companion.bidFloat: FloatingPointSquare<BidFloat> get() = bidFloatInstance
val FloatingPointSquare.Companion.bidDouble: FloatingPointSquare<BidDouble> get() = bidDoubleInstance
val FloatingPointSquare.Companion.dpdFloat: FloatingPointSquare<DpdFloat> get() = dpdFloatInstance
val FloatingPointSquare.Companion.dpdDouble: FloatingPointSquare<DpdDouble> get() = dpdDoubleInstance
