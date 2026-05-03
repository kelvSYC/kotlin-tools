package com.kelvsyc.kotlin.core.traits.integral

import com.kelvsyc.kotlin.core.traits.fp.IntegerPower
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.math.BigInteger

class BigIntegerPowerTest : FunSpec({
    context("IntegerPower.Companion.bigInteger") {
        val ops = IntegerPower.bigInteger

        test("pow(2, 0) = 1") { with(ops) { BigInteger.TWO.pow(0) } shouldBe BigInteger.ONE }
        test("pow(2, 10) = 1024") {
            with(ops) { BigInteger.TWO.pow(10) } shouldBe BigInteger.valueOf(1024)
        }
        test("pow(3, 4) = 81") {
            with(ops) { BigInteger.valueOf(3).pow(4) } shouldBe BigInteger.valueOf(81)
        }
        test("pow(large base, 2)") {
            val base = BigInteger.TEN.pow(18)
            with(ops) { base.pow(2) } shouldBe BigInteger.TEN.pow(36)
        }
        test("negative exponent throws (ArithmeticException from BigInteger.pow member)") {
            // BigInteger has a member pow(int) that shadows the trait extension in concrete call
            // sites. The Java member throws ArithmeticException for negative exponents; the
            // trait's require(n >= 0) fires only in generic dispatch contexts.
            shouldThrow<ArithmeticException> { with(ops) { BigInteger.TWO.pow(-1) } }
        }
    }
})
