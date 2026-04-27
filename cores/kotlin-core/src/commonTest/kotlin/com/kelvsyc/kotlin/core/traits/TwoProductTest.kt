package com.kelvsyc.kotlin.core.traits

import com.kelvsyc.kotlin.core.traits.dd.TwoProduct
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class TwoProductTest : FunSpec({

    // ── TwoProduct.Companion.float ────────────────────────────────────────────

    context("TwoProduct.Companion.float") {

        // Distinguishing test: same construction as FusedMultiplyAddTest.
        // a = 2^13+1, b = 2^13-1; exact product = 2^26-1 = 67108863; ULP at 2^26 is 8.0f
        // so 67108863 rounds to 2^26 = 67108864.0f; error term must be -1.0f.
        context("distinguishing: exact product error is recovered") {
            val a = (1 shl 13).toFloat() + 1.0f   // 8193.0f
            val b = (1 shl 13).toFloat() - 1.0f   // 8191.0f
            val rounded = a * b                    // 2^26 = 67108864.0f

            test("a * b is rounded (precondition)") {
                rounded shouldBe 67108864.0f
            }
            test("twoProduct returns the rounded product") {
                val (p, _) = with(TwoProduct.float) { a.twoProduct(b) }
                p shouldBe rounded
            }
            test("error term is -1.0f: the exact product was 2^26 - 1") {
                val (_, e) = with(TwoProduct.float) { a.twoProduct(b) }
                e shouldBe -1.0f
            }
        }

        context("exact cases: error is zero") {
            test("2.0f * 3.0f") {
                val (p, e) = with(TwoProduct.float) { 2.0f.twoProduct(3.0f) }
                p shouldBe 6.0f
                e shouldBe 0.0f
            }
            test("1.0f * 1.0f") {
                val (p, e) = with(TwoProduct.float) { 1.0f.twoProduct(1.0f) }
                p shouldBe 1.0f
                e shouldBe 0.0f
            }
            test("0.5f * 0.5f") {
                val (p, e) = with(TwoProduct.float) { 0.5f.twoProduct(0.5f) }
                p shouldBe 0.25f
                e shouldBe 0.0f
            }
        }
    }

    // ── TwoProduct.Companion.double ───────────────────────────────────────────

    context("TwoProduct.Companion.double") {

        // Distinguishing test: a = 2^27+1, b = 2^27-1; exact product = 2^54-1; ULP at 2^54 is 4.0
        // so 2^54-1 rounds to 2^54 = 18014398509481984.0; error term must be -1.0.
        context("distinguishing: exact product error is recovered") {
            val a = (1L shl 27).toDouble() + 1.0   // 134217729.0
            val b = (1L shl 27).toDouble() - 1.0   // 134217727.0
            val rounded = a * b                     // 2^54 = 18014398509481984.0

            test("a * b is rounded (precondition)") {
                rounded shouldBe 18014398509481984.0
            }
            test("twoProduct returns the rounded product") {
                val (p, _) = with(TwoProduct.double) { a.twoProduct(b) }
                p shouldBe rounded
            }
            test("error term is -1.0: the exact product was 2^54 - 1") {
                val (_, e) = with(TwoProduct.double) { a.twoProduct(b) }
                e shouldBe -1.0
            }
        }

        context("exact cases: error is zero") {
            test("2.0 * 3.0") {
                val (p, e) = with(TwoProduct.double) { 2.0.twoProduct(3.0) }
                p shouldBe 6.0
                e shouldBe 0.0
            }
            test("1.0 * 1.0") {
                val (p, e) = with(TwoProduct.double) { 1.0.twoProduct(1.0) }
                p shouldBe 1.0
                e shouldBe 0.0
            }
            test("0.5 * 0.5") {
                val (p, e) = with(TwoProduct.double) { 0.5.twoProduct(0.5) }
                p shouldBe 0.25
                e shouldBe 0.0
            }
        }
    }

    // ── Singleton identity ────────────────────────────────────────────────────

    context("singleton identity") {
        test("Companion.float returns the same instance on repeated access") {
            TwoProduct.float shouldBe TwoProduct.float
        }
        test("Companion.double returns the same instance on repeated access") {
            TwoProduct.double shouldBe TwoProduct.double
        }
        test("float and double instances are distinct") {
            TwoProduct.float shouldNotBe TwoProduct.double
        }
    }
})
