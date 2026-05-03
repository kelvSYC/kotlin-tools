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

// Arithmetic instances — fetched via explicit imports to avoid ambiguity with the extension
// properties on other companion objects in this package that share the name 'bidFloat'/'bidDouble'.
private val bidFloatArith: FloatingPointArithmetic<BidFloat> = FloatingPointArithmetic.bidFloatArithProp
private val bidDoubleArith: FloatingPointArithmetic<BidDouble> = FloatingPointArithmetic.bidDoubleArithProp

// ── BidFloat ──────────────────────────────────────────────────────────────────

private val bidFloatInstance: IntegerPower<BidFloat> = object : IntegerPower<BidFloat> {
    override fun BidFloat.pow(n: Int): BidFloat =
        binaryPow(this, n, bidFloatArith.one) { a, b -> with(bidFloatArith) { a.multiply(b) } }
}

// ── BidDouble ─────────────────────────────────────────────────────────────────

private val bidDoubleInstance: IntegerPower<BidDouble> = object : IntegerPower<BidDouble> {
    override fun BidDouble.pow(n: Int): BidDouble =
        binaryPow(this, n, bidDoubleArith.one) { a, b -> with(bidDoubleArith) { a.multiply(b) } }
}

// ── DpdFloat ──────────────────────────────────────────────────────────────────

private val dpdFloatInstance: IntegerPower<DpdFloat> = object : IntegerPower<DpdFloat> {
    override fun DpdFloat.pow(n: Int): DpdFloat =
        bidDpdFloat.wrap { a: BidFloat ->
            binaryPow(a, n, bidFloatArith.one) { x, y -> with(bidFloatArith) { x.multiply(y) } }
        }(this)
}

// ── DpdDouble ─────────────────────────────────────────────────────────────────

private val dpdDoubleInstance: IntegerPower<DpdDouble> = object : IntegerPower<DpdDouble> {
    override fun DpdDouble.pow(n: Int): DpdDouble =
        bidDpdDouble.wrap { a: BidDouble ->
            binaryPow(a, n, bidDoubleArith.one) { x, y -> with(bidDoubleArith) { x.multiply(y) } }
        }(this)
}

val IntegerPower.Companion.bidFloat: IntegerPower<BidFloat> get() = bidFloatInstance
val IntegerPower.Companion.bidDouble: IntegerPower<BidDouble> get() = bidDoubleInstance
val IntegerPower.Companion.dpdFloat: IntegerPower<DpdFloat> get() = dpdFloatInstance
val IntegerPower.Companion.dpdDouble: IntegerPower<DpdDouble> get() = dpdDoubleInstance
