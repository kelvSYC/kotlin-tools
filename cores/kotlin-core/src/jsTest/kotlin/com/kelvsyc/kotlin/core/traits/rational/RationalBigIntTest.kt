package com.kelvsyc.kotlin.core.traits.rational

import com.kelvsyc.kotlin.core.BigInt
import com.kelvsyc.kotlin.core.Rational
import com.kelvsyc.kotlin.core.bigIntOf
import com.kelvsyc.kotlin.core.toDecimalString
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class RationalBigIntTest : FunSpec({
    val ops = RationalArithmetic.bigInt

    fun rational(n: Int, d: Int): Rational<BigInt> = ops.of(bigIntOf(n), bigIntOf(d))
    fun Rational<BigInt>.num() = numerator.toDecimalString()
    fun Rational<BigInt>.den() = denominator.toDecimalString()

    context("of (canonical form)") {
        test("1/2 is canonical") {
            val r = rational(1, 2)
            r.num() shouldBe "1"
            r.den() shouldBe "2"
        }
        test("2/4 reduces to 1/2") {
            val r = rational(2, 4)
            r.num() shouldBe "1"
            r.den() shouldBe "2"
        }
        test("negative denominator is normalized: 1/-2 = -1/2") {
            val r = ops.of(bigIntOf(1), bigIntOf(-2))
            r.num() shouldBe "-1"
            r.den() shouldBe "2"
        }
    }

    context("add") {
        test("1/2 + 1/3 = 5/6") {
            val r = with(ops) { rational(1, 2).add(rational(1, 3)) }
            r.num() shouldBe "5"
            r.den() shouldBe "6"
        }
        test("1/6 + 1/6 = 1/3") {
            val r = with(ops) { rational(1, 6).add(rational(1, 6)) }
            r.num() shouldBe "1"
            r.den() shouldBe "3"
        }
    }

    context("subtract") {
        test("3/4 - 1/4 = 1/2") {
            val r = with(ops) { rational(3, 4).subtract(rational(1, 4)) }
            r.num() shouldBe "1"
            r.den() shouldBe "2"
        }
    }

    context("multiply") {
        test("2/3 * 3/4 = 1/2 (cross-cancellation)") {
            val r = with(ops) { rational(2, 3).multiply(rational(3, 4)) }
            r.num() shouldBe "1"
            r.den() shouldBe "2"
        }
    }

    context("divide") {
        test("1/2 / 1/4 = 2") {
            val r = with(ops) { rational(1, 2).divide(rational(1, 4)) }
            r.num() shouldBe "2"
            r.den() shouldBe "1"
        }
    }

    context("large numerator/denominator") {
        test("10^18/1 + 1/10^18") {
            val large = bigIntOf("1000000000000000000")
            val r = with(ops) {
                ops.of(large, bigIntOf(1)).add(ops.of(bigIntOf(1), large))
            }
            // Result = (10^36 + 1) / 10^18 — just verify it doesn't overflow/throw
            r.den().length shouldBe 19  // 10^18 has 19 digits
        }
    }
})
