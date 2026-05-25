package com.kelvsyc.kotlin.core.traits.integral

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import java.math.BigInteger

class DivRemTest : FunSpec({

    context("DivRem.Companion.bigInteger") {
        val ops = DivRem.bigInteger
        val arith = SignedIntegerArithmetic.bigInteger

        context("positive / positive") {
            test("7 divRem 3 = (2, 1)") {
                with(ops) { 7.toBigInteger().divRem(3.toBigInteger()) } shouldBe
                    DivRemResult(2.toBigInteger(), 1.toBigInteger())
            }
            test("6 divRem 3 = (2, 0) exact") {
                with(ops) { 6.toBigInteger().divRem(3.toBigInteger()) } shouldBe
                    DivRemResult(2.toBigInteger(), BigInteger.ZERO)
            }
        }

        context("negative dividend, positive divisor") {
            test("-7 divRem 3 = (-2, -1): quotient truncates toward zero, remainder has sign of dividend") {
                with(ops) { (-7).toBigInteger().divRem(3.toBigInteger()) } shouldBe
                    DivRemResult((-2).toBigInteger(), (-1).toBigInteger())
            }
        }

        context("positive dividend, negative divisor") {
            test("7 divRem -3 = (-2, 1): quotient truncates toward zero, remainder has sign of dividend") {
                with(ops) { 7.toBigInteger().divRem((-3).toBigInteger()) } shouldBe
                    DivRemResult((-2).toBigInteger(), 1.toBigInteger())
            }
        }

        context("negative / negative") {
            test("-7 divRem -3 = (2, -1)") {
                with(ops) { (-7).toBigInteger().divRem((-3).toBigInteger()) } shouldBe
                    DivRemResult(2.toBigInteger(), (-1).toBigInteger())
            }
        }

        context("zero dividend") {
            test("0 divRem 5 = (0, 0)") {
                with(ops) { BigInteger.ZERO.divRem(5.toBigInteger()) } shouldBe
                    DivRemResult(BigInteger.ZERO, BigInteger.ZERO)
            }
        }

        context("division by zero") {
            test("throws ArithmeticException") {
                shouldThrow<ArithmeticException> {
                    with(ops) { BigInteger.ONE.divRem(BigInteger.ZERO) }
                }
            }
        }

        context("consistency with divide and rem") {
            test("quotient matches SignedIntegerArithmetic.divide") {
                val a = 123456789.toBigInteger(); val b = 97.toBigInteger()
                val (q, _) = with(ops) { a.divRem(b) }
                q shouldBe with(arith) { a.divide(b) }
            }
            test("remainder matches SignedIntegerArithmetic.rem") {
                val a = 123456789.toBigInteger(); val b = 97.toBigInteger()
                val (_, r) = with(ops) { a.divRem(b) }
                r shouldBe with(arith) { a.rem(b) }
            }
            test("invariant: quotient * divisor + remainder == dividend") {
                val a = (-1234567890123456789L).toBigInteger(); val b = 999983.toBigInteger()
                val (q, r) = with(ops) { a.divRem(b) }
                with(arith) { q.multiply(b).add(r) } shouldBe a
            }
        }

        context("destructuring") {
            test("val (q, r) = ... works") {
                val (q, r) = with(ops) { 17.toBigInteger().divRem(5.toBigInteger()) }
                q shouldBe 3.toBigInteger()
                r shouldBe 2.toBigInteger()
            }
        }

        context("singleton identity") {
            test("DivRem.bigInteger is stable") {
                DivRem.bigInteger shouldBeSameInstanceAs DivRem.bigInteger
            }
        }
    }
})
