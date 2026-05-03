package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.BFloat16
import com.kelvsyc.kotlin.core.BidDouble
import com.kelvsyc.kotlin.core.BidFloat
import com.kelvsyc.kotlin.core.Float16
import com.kelvsyc.kotlin.core.bidDouble64Pack
import com.kelvsyc.kotlin.core.bidFloat32Pack
import com.kelvsyc.kotlin.core.fp.DoubleDouble
import com.kelvsyc.kotlin.core.fp.bidDpdDouble
import com.kelvsyc.kotlin.core.fp.bidDpdFloat
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class IntegerPowerTest : FunSpec({

    // ── Int ───────────────────────────────────────────────────────────────────

    context("IntegerPower.Companion.int") {
        val ops = IntegerPower.int

        test("pow(2, 0) = 1") { with(ops) { 2.pow(0) } shouldBe 1 }
        test("pow(2, 1) = 2") { with(ops) { 2.pow(1) } shouldBe 2 }
        test("pow(2, 10) = 1024") { with(ops) { 2.pow(10) } shouldBe 1024 }
        test("pow(3, 4) = 81") { with(ops) { 3.pow(4) } shouldBe 81 }
        test("pow(0, 5) = 0") { with(ops) { 0.pow(5) } shouldBe 0 }
        test("pow(0, 0) = 1") { with(ops) { 0.pow(0) } shouldBe 1 }
        test("negative exponent throws") { shouldThrow<IllegalArgumentException> { with(ops) { 2.pow(-1) } } }
    }

    // ── Long ──────────────────────────────────────────────────────────────────

    context("IntegerPower.Companion.long") {
        val ops = IntegerPower.long

        test("pow(2L, 10) = 1024L") { with(ops) { 2L.pow(10) } shouldBe 1024L }
        test("pow(2L, 0) = 1L") { with(ops) { 2L.pow(0) } shouldBe 1L }
        test("negative exponent throws") { shouldThrow<IllegalArgumentException> { with(ops) { 2L.pow(-1) } } }
    }

    // ── Float ─────────────────────────────────────────────────────────────────

    context("IntegerPower.Companion.float") {
        val ops = IntegerPower.float

        test("pow(2.0f, 0) = 1.0f") { with(ops) { 2.0f.pow(0) } shouldBe 1.0f }
        test("pow(2.0f, 10) = 1024.0f") { with(ops) { 2.0f.pow(10) } shouldBe 1024.0f }
        test("pow(3.0f, 3) = 27.0f") { with(ops) { 3.0f.pow(3) } shouldBe 27.0f }
        test("negative exponent throws") { shouldThrow<IllegalArgumentException> { with(ops) { 2.0f.pow(-1) } } }
    }

    // ── Double ────────────────────────────────────────────────────────────────

    context("IntegerPower.Companion.double") {
        val ops = IntegerPower.double

        test("pow(2.0, 0) = 1.0") { with(ops) { 2.0.pow(0) } shouldBe 1.0 }
        test("pow(2.0, 10) = 1024.0") { with(ops) { 2.0.pow(10) } shouldBe 1024.0 }
        test("pow(3.0, 3) = 27.0") { with(ops) { 3.0.pow(3) } shouldBe 27.0 }
        test("negative exponent throws") { shouldThrow<IllegalArgumentException> { with(ops) { 2.0.pow(-1) } } }
    }

    // ── BFloat16 ──────────────────────────────────────────────────────────────

    context("IntegerPower.Companion.bfloat16") {
        val ops = IntegerPower.bfloat16

        test("pow(2, 0) = 1") { with(ops) { BFloat16(2.0f).pow(0) } shouldBe BFloat16(1.0f) }
        test("pow(2, 8) = 256") { with(ops) { BFloat16(2.0f).pow(8) } shouldBe BFloat16(256.0f) }
        test("negative exponent throws") {
            shouldThrow<IllegalArgumentException> { with(ops) { BFloat16(2.0f).pow(-1) } }
        }
    }

    // ── Float16 ───────────────────────────────────────────────────────────────

    context("IntegerPower.Companion.float16") {
        val ops = IntegerPower.float16

        test("pow(2, 0) = 1") { with(ops) { Float16(2.0f).pow(0) } shouldBe Float16(1.0f) }
        test("pow(2, 8) = 256") { with(ops) { Float16(2.0f).pow(8) } shouldBe Float16(256.0f) }
        test("negative exponent throws") {
            shouldThrow<IllegalArgumentException> { with(ops) { Float16(2.0f).pow(-1) } }
        }
    }

    // ── DoubleDouble ──────────────────────────────────────────────────────────

    context("IntegerPower.Companion.doubleDouble") {
        val ops = IntegerPower.doubleDouble

        test("pow(2, 0) = 1") {
            val r = with(ops) { DoubleDouble.create(2.0, 0.0).pow(0) }
            r.high shouldBe 1.0
            r.low shouldBe 0.0
        }
        test("pow(2, 3) = 8") {
            val r = with(ops) { DoubleDouble.create(2.0, 0.0).pow(3) }
            r.high shouldBe 8.0
            r.low shouldBe 0.0
        }
        test("negative exponent throws") {
            shouldThrow<IllegalArgumentException> {
                with(ops) { DoubleDouble.create(2.0, 0.0).pow(-1) }
            }
        }
    }

    // ── BidFloat ──────────────────────────────────────────────────────────────

    context("IntegerPower.Companion.bidFloat") {
        val ops = IntegerPower.bidFloat

        test("pow(2, 0) = 1: result is 1 × 10^0") {
            // binaryPow returns the 'one' element: 1 × 10^0 = biasedExp=101, sig=1
            val x = BidFloat(bidFloat32Pack(101, 2))
            val r = with(ops) { x.pow(0) }
            r shouldBe BidFloat(bidFloat32Pack(101, 1))
        }
        test("pow(3, 3) = 27: non-NaN, non-zero") {
            val x = BidFloat(bidFloat32Pack(101, 3))
            val r = with(ops) { x.pow(3) }
            r.isNaN() shouldBe false
            r.isZero() shouldBe false
        }
        test("negative exponent throws") {
            shouldThrow<IllegalArgumentException> {
                with(ops) { BidFloat(bidFloat32Pack(101, 2)).pow(-1) }
            }
        }
    }

    // ── BidDouble ─────────────────────────────────────────────────────────────

    context("IntegerPower.Companion.bidDouble") {
        val ops = IntegerPower.bidDouble

        test("pow(2, 0) = 1: result is 1 × 10^0") {
            // binaryPow returns the 'one' element: 1 × 10^0 = biasedExp=398, sig=1
            val x = BidDouble(bidDouble64Pack(398, 2L))
            val r = with(ops) { x.pow(0) }
            r shouldBe BidDouble(bidDouble64Pack(398, 1L))
        }
        test("negative exponent throws") {
            shouldThrow<IllegalArgumentException> {
                with(ops) { BidDouble(bidDouble64Pack(398, 2L)).pow(-1) }
            }
        }
    }

    // ── DpdFloat ──────────────────────────────────────────────────────────────

    context("IntegerPower.Companion.dpdFloat") {
        val ops = IntegerPower.dpdFloat

        test("pow(2, 0) = 1: round-trips through BID as 1 × 10^0") {
            val x = bidDpdFloat(BidFloat(bidFloat32Pack(101, 2)))
            val r = with(ops) { x.pow(0) }
            r shouldBe bidDpdFloat(BidFloat(bidFloat32Pack(101, 1)))
        }
        test("negative exponent throws") {
            shouldThrow<IllegalArgumentException> {
                val x = bidDpdFloat(BidFloat(bidFloat32Pack(101, 2)))
                with(ops) { x.pow(-1) }
            }
        }
    }

    // ── DpdDouble ─────────────────────────────────────────────────────────────

    context("IntegerPower.Companion.dpdDouble") {
        val ops = IntegerPower.dpdDouble

        test("pow(2, 0) = 1: round-trips through BID as 1 × 10^0") {
            val x = bidDpdDouble(BidDouble(bidDouble64Pack(398, 2L)))
            val r = with(ops) { x.pow(0) }
            r shouldBe bidDpdDouble(BidDouble(bidDouble64Pack(398, 1L)))
        }
        test("negative exponent throws") {
            shouldThrow<IllegalArgumentException> {
                val x = bidDpdDouble(BidDouble(bidDouble64Pack(398, 2L)))
                with(ops) { x.pow(-1) }
            }
        }
    }
})
