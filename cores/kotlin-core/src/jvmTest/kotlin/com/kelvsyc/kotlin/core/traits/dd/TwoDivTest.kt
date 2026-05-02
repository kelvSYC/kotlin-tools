package com.kelvsyc.kotlin.core.traits.dd

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class TwoDivTest : FunSpec({

    // ── TwoDiv.Companion.double ───────────────────────────────────────────────

    context("TwoDiv.Companion.double") {

        context("exact division: error is zero") {
            test("6.0 / 2.0") {
                val (q, e) = with(TwoDiv.double) { 6.0.twoDiv(2.0) }
                q shouldBe 3.0
                e shouldBe 0.0
            }
            test("1.0 / 2.0") {
                val (q, e) = with(TwoDiv.double) { 1.0.twoDiv(2.0) }
                q shouldBe 0.5
                e shouldBe 0.0
            }
            test("1.0 / 1.0") {
                val (q, e) = with(TwoDiv.double) { 1.0.twoDiv(1.0) }
                q shouldBe 1.0
                e shouldBe 0.0
            }
        }

        // Distinguishing test: 1/3 is not exactly representable.
        // The error term must equal the FMA residual exactly: e = fma(q, -b, a) / b.
        // Note: (1.0/3.0) * 3.0 == 1.0 in IEEE 754 double (the product rounds back), so
        // "closer to a" comparisons are not a reliable check here.
        context("distinguishing: error term captures rounding for 1.0 / 3.0") {
            val (q, e) = with(TwoDiv.double) { 1.0.twoDiv(3.0) }

            test("q is the correctly-rounded quotient fl(1/3)") {
                q shouldBe 1.0 / 3.0
            }
            test("error term is non-zero: 1/3 is not exactly representable") {
                (e == 0.0) shouldBe false
            }
            test("e equals fma(q, -3.0, 1.0) / 3.0 — the defining identity of twoDiv") {
                val expected = Math.fma(q, -3.0, 1.0) / 3.0
                e shouldBe expected
            }
        }
    }

    // ── TwoDiv.Companion.float ────────────────────────────────────────────────

    context("TwoDiv.Companion.float") {

        context("exact division: error is zero") {
            test("6.0f / 2.0f") {
                val (q, e) = with(TwoDiv.float) { 6.0f.twoDiv(2.0f) }
                q shouldBe 3.0f
                e shouldBe 0.0f
            }
            test("1.0f / 2.0f") {
                val (q, e) = with(TwoDiv.float) { 1.0f.twoDiv(2.0f) }
                q shouldBe 0.5f
                e shouldBe 0.0f
            }
        }

        context("distinguishing: error term captures rounding for 1.0f / 3.0f") {
            val (q, e) = with(TwoDiv.float) { 1.0f.twoDiv(3.0f) }

            test("q is the correctly-rounded quotient fl(1/3)") {
                q shouldBe 1.0f / 3.0f
            }
            test("error term is non-zero: 1/3 is not exactly representable") {
                (e == 0.0f) shouldBe false
            }
            test("e equals fma(q, -3.0f, 1.0f) / 3.0f — the defining identity of twoDiv") {
                val expected = Math.fma(q, -3.0f, 1.0f) / 3.0f
                e shouldBe expected
            }
        }
    }

    // ── Singleton identity ────────────────────────────────────────────────────

    context("singleton identity") {
        test("Companion.float returns the same instance on repeated access") {
            TwoDiv.float shouldBe TwoDiv.float
        }
        test("Companion.double returns the same instance on repeated access") {
            TwoDiv.double shouldBe TwoDiv.double
        }
        test("float and double instances are distinct") {
            TwoDiv.float shouldNotBe TwoDiv.double
        }
    }
})
