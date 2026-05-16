package com.kelvsyc.kotlin.core

import com.kelvsyc.kotlin.core.traits.integral.Int32
import com.kelvsyc.kotlin.core.traits.integral.Int64
import com.kelvsyc.kotlin.core.traits.integral.UInt32
import com.kelvsyc.kotlin.core.traits.integral.UInt64
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class BigIntConvertersTest : FunSpec({

    context("BigInt.longConverter") {
        val conv = BigInt.longConverter

        context("forward: BigInt → Long") {
            test("zero") { conv(bigIntOf(0)) shouldBe 0L }
            test("positive") { conv(bigIntOf(42)) shouldBe 42L }
            test("negative") { conv(bigIntOf(-42)) shouldBe -42L }
            test("Long.MAX_VALUE") { conv(bigIntOf(Long.MAX_VALUE.toString())) shouldBe Long.MAX_VALUE }
            test("Long.MIN_VALUE") { conv(bigIntOf(Long.MIN_VALUE.toString())) shouldBe Long.MIN_VALUE }
            test("Long.MAX_VALUE + 1 throws") {
                val tooBig = bigIntOf(Long.MAX_VALUE.toString()) + bigIntOf(1)
                shouldThrow<ArithmeticException> { conv(tooBig) }
            }
            test("Long.MIN_VALUE - 1 throws") {
                val tooSmall = bigIntOf(Long.MIN_VALUE.toString()) - bigIntOf(1)
                shouldThrow<ArithmeticException> { conv(tooSmall) }
            }
        }

        context("backward: Long → BigInt") {
            test("0L") { conv.reverse(0L).toDecimalString() shouldBe "0" }
            test("Long.MAX_VALUE") { conv.reverse(Long.MAX_VALUE).toDecimalString() shouldBe Long.MAX_VALUE.toString() }
            test("Long.MIN_VALUE") { conv.reverse(Long.MIN_VALUE).toDecimalString() shouldBe Long.MIN_VALUE.toString() }
        }

        test("round-trip Long.MAX_VALUE") {
            val v = Long.MAX_VALUE
            conv(conv.reverse(v)) shouldBe v
        }
    }

    context("BigInt.intConverter") {
        val conv = BigInt.intConverter

        context("forward: BigInt → Int") {
            test("zero") { conv(bigIntOf(0)) shouldBe 0 }
            test("Int.MAX_VALUE") { conv(bigIntOf(Int.MAX_VALUE)) shouldBe Int.MAX_VALUE }
            test("Int.MIN_VALUE") { conv(bigIntOf(Int.MIN_VALUE.toString())) shouldBe Int.MIN_VALUE }
            test("Int.MAX_VALUE + 1 throws") {
                val tooBig = bigIntOf(Int.MAX_VALUE) + bigIntOf(1)
                shouldThrow<ArithmeticException> { conv(tooBig) }
            }
        }

        context("backward: Int → BigInt") {
            test("42") { conv.reverse(42).toDecimalString() shouldBe "42" }
            test("Int.MIN_VALUE") { conv.reverse(Int.MIN_VALUE).toDecimalString() shouldBe Int.MIN_VALUE.toString() }
        }
    }

    context("SignedIntegral.toBigInt") {
        test("Int32: 42 → bigIntOf(42)") {
            Int32.toBigInt(42).toDecimalString() shouldBe "42"
        }
        test("Int64: Long.MAX_VALUE") {
            Int64.toBigInt(Long.MAX_VALUE).toDecimalString() shouldBe Long.MAX_VALUE.toString()
        }
        test("Int64: Long.MIN_VALUE") {
            Int64.toBigInt(Long.MIN_VALUE).toDecimalString() shouldBe Long.MIN_VALUE.toString()
        }
    }

    context("UnsignedIntegral.toBigInt") {
        test("UInt32: UInt.MAX_VALUE") {
            UInt32.toBigInt(UInt.MAX_VALUE).toDecimalString() shouldBe UInt.MAX_VALUE.toString()
        }
        test("UInt64: ULong.MAX_VALUE") {
            UInt64.toBigInt(ULong.MAX_VALUE).toDecimalString() shouldBe ULong.MAX_VALUE.toString()
        }
    }
})
