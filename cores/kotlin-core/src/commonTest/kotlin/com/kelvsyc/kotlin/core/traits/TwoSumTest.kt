package com.kelvsyc.kotlin.core.traits

import com.kelvsyc.kotlin.core.traits.dd.TwoSum
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class TwoSumTest : FunSpec({

    // ── TwoSum.Companion.float ────────────────────────────────────────────────

    context("TwoSum.Companion.float") {
        val ts = TwoSum.float

        context("twoSum") {
            test("exact result: error is zero") {
                val (s, e) = with(ts) { 1.0f.twoSum(1.0f) }
                s shouldBe 2.0f
                e shouldBe 0.0f
            }

            // Distinguishing test: at 2^24 the ULP is 2.0f, so 2^24 + 1.0f rounds to 2^24.
            // twoSum must recover the lost 1.0f as the error term.
            context("rounding case: 2^24 + 1.0f") {
                val a = (1 shl 24).toFloat()   // 16777216.0f, ULP = 2.0f here
                val b = 1.0f

                test("fl(a + b) == a, confirming rounding (precondition)") {
                    (a + b) shouldBe a
                }
                test("twoSum returns (a, 1.0f): the lost 1.0f is recovered in the error term") {
                    val (s, e) = with(ts) { a.twoSum(b) }
                    s shouldBe a
                    e shouldBe 1.0f
                }
            }
        }

        context("fastTwoSum") {
            test("exact result: error is zero") {
                val (s, e) = with(ts) { 2.0f.fastTwoSum(1.0f) }
                s shouldBe 3.0f
                e shouldBe 0.0f
            }
            test("rounding case with |a| >= |b|: error term recovered") {
                val a = (1 shl 24).toFloat()
                val b = 1.0f
                val (s, e) = with(ts) { a.fastTwoSum(b) }
                s shouldBe a
                e shouldBe 1.0f
            }
        }
    }

    // ── TwoSum.Companion.double ───────────────────────────────────────────────

    context("TwoSum.Companion.double") {
        val ts = TwoSum.double

        context("twoSum") {
            test("exact result: error is zero") {
                val (s, e) = with(ts) { 1.0.twoSum(1.0) }
                s shouldBe 2.0
                e shouldBe 0.0
            }

            // Distinguishing test: at 2^53 the ULP is 2.0, so 2^53 + 1.0 rounds to 2^53
            // (round-to-even: 2^53 + 1 is halfway, 2^53 has even trailing bit).
            // twoSum must recover the lost 1.0 as the error term.
            context("rounding case: 2^53 + 1.0") {
                val a = 9007199254740992.0   // 2^53, ULP = 2.0 here
                val b = 1.0

                test("fl(a + b) == a, confirming rounding (precondition)") {
                    (a + b) shouldBe a
                }
                test("twoSum returns (a, 1.0): the lost 1.0 is recovered in the error term") {
                    val (s, e) = with(ts) { a.twoSum(b) }
                    s shouldBe a
                    e shouldBe b
                }
            }
        }

        context("fastTwoSum") {
            test("exact result: error is zero") {
                val (s, e) = with(ts) { 2.0.fastTwoSum(1.0) }
                s shouldBe 3.0
                e shouldBe 0.0
            }
            test("rounding case with |a| >= |b|: error term recovered") {
                val a = 9007199254740992.0
                val b = 1.0
                val (s, e) = with(ts) { a.fastTwoSum(b) }
                s shouldBe a
                e shouldBe b
            }
        }
    }

    // ── Singleton identity ────────────────────────────────────────────────────

    context("singleton identity") {
        test("Companion.float returns the same instance on repeated access") {
            TwoSum.float shouldBe TwoSum.float
        }
        test("Companion.double returns the same instance on repeated access") {
            TwoSum.double shouldBe TwoSum.double
        }
        test("float and double instances are distinct") {
            TwoSum.float shouldNotBe TwoSum.double
        }
    }
})
