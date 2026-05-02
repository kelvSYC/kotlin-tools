package com.kelvsyc.kotlin.core.traits.integral

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import java.math.BigInteger

class BigIntegerArithmeticTest : FunSpec({

    // ── SignedIntegerArithmetic.Companion.bigInteger ──────────────────────────

    context("SignedIntegerArithmetic.Companion.bigInteger") {
        val ops = SignedIntegerArithmetic.bigInteger

        context("constants") {
            test("zero is BigInteger.ZERO") { ops.zero shouldBe BigInteger.ZERO }
            test("one is BigInteger.ONE") { ops.one shouldBe BigInteger.ONE }
        }

        context("add") {
            test("2 + 3 = 5") { with(ops) { 2.toBigInteger().add(3.toBigInteger()) } shouldBe 5.toBigInteger() }
            test("zero + x = x") { with(ops) { BigInteger.ZERO.add(BigInteger.TEN) } shouldBe BigInteger.TEN }
            test("negative addend") { with(ops) { 5.toBigInteger().add((-3).toBigInteger()) } shouldBe 2.toBigInteger() }
            test("no overflow: large values") {
                val large = BigInteger.TWO.pow(200)
                with(ops) { large.add(BigInteger.ONE) } shouldBe large + BigInteger.ONE
            }
        }

        context("subtract") {
            test("5 - 3 = 2") { with(ops) { 5.toBigInteger().subtract(3.toBigInteger()) } shouldBe 2.toBigInteger() }
            test("x - x = 0") { with(ops) { 7.toBigInteger().subtract(7.toBigInteger()) } shouldBe BigInteger.ZERO }
            test("result can be negative") { with(ops) { 3.toBigInteger().subtract(5.toBigInteger()) } shouldBe (-2).toBigInteger() }
        }

        context("multiply") {
            test("2 * 3 = 6") { with(ops) { 2.toBigInteger().multiply(3.toBigInteger()) } shouldBe 6.toBigInteger() }
            test("x * zero = zero") { with(ops) { 7.toBigInteger().multiply(BigInteger.ZERO) } shouldBe BigInteger.ZERO }
            test("negative factor") { with(ops) { 4.toBigInteger().multiply((-3).toBigInteger()) } shouldBe (-12).toBigInteger() }
        }

        context("divide") {
            test("6 / 2 = 3") { with(ops) { 6.toBigInteger().divide(2.toBigInteger()) } shouldBe 3.toBigInteger() }
            test("truncates toward zero: 7 / 2 = 3") { with(ops) { 7.toBigInteger().divide(2.toBigInteger()) } shouldBe 3.toBigInteger() }
            test("truncates toward zero: -7 / 2 = -3") { with(ops) { (-7).toBigInteger().divide(2.toBigInteger()) } shouldBe (-3).toBigInteger() }
            test("truncates toward zero: 7 / -2 = -3") { with(ops) { 7.toBigInteger().divide((-2).toBigInteger()) } shouldBe (-3).toBigInteger() }
            test("divide by zero throws ArithmeticException") {
                shouldThrow<ArithmeticException> { with(ops) { BigInteger.ONE.divide(BigInteger.ZERO) } }
            }
        }

        context("rem") {
            test("7 % 3 = 1") { with(ops) { 7.toBigInteger().rem(3.toBigInteger()) } shouldBe BigInteger.ONE }
            test("6 % 3 = 0") { with(ops) { 6.toBigInteger().rem(3.toBigInteger()) } shouldBe BigInteger.ZERO }
            test("sign of dividend: -7 % 3 = -1") {
                with(ops) { (-7).toBigInteger().rem(3.toBigInteger()) } shouldBe (-1).toBigInteger()
            }
            test("sign of dividend: 7 % -3 = 1") {
                with(ops) { 7.toBigInteger().rem((-3).toBigInteger()) } shouldBe BigInteger.ONE
            }
            test("rem by zero throws ArithmeticException") {
                shouldThrow<ArithmeticException> { with(ops) { BigInteger.ONE.rem(BigInteger.ZERO) } }
            }
        }

        context("compareTo") {
            test("1 < 2") { (with(ops) { 1.toBigInteger().compareTo(2.toBigInteger()) } < 0) shouldBe true }
            test("2 > 1") { (with(ops) { 2.toBigInteger().compareTo(1.toBigInteger()) } > 0) shouldBe true }
            test("1 == 1") { with(ops) { 1.toBigInteger().compareTo(1.toBigInteger()) } shouldBe 0 }
            test("negative < positive") {
                (with(ops) { (-1).toBigInteger().compareTo(BigInteger.ONE) } < 0) shouldBe true
            }
            test("large values compare correctly") {
                val big = BigInteger.TWO.pow(300)
                (with(ops) { big.compareTo(BigInteger.ONE) } > 0) shouldBe true
            }
        }

        context("negate") {
            test("negate positive") { with(ops) { 5.toBigInteger().negate() } shouldBe (-5).toBigInteger() }
            test("negate negative") { with(ops) { (-5).toBigInteger().negate() } shouldBe 5.toBigInteger() }
            test("negate zero") { with(ops) { BigInteger.ZERO.negate() } shouldBe BigInteger.ZERO }
            test("double negation is identity") { with(ops) { 7.toBigInteger().negate().negate() } shouldBe 7.toBigInteger() }
        }

        context("abs") {
            test("abs of positive") { with(ops) { 5.toBigInteger().abs() } shouldBe 5.toBigInteger() }
            test("abs of negative") { with(ops) { (-5).toBigInteger().abs() } shouldBe 5.toBigInteger() }
            test("abs of zero") { with(ops) { BigInteger.ZERO.abs() } shouldBe BigInteger.ZERO }
        }

        context("isNegative / isPositive / isZero") {
            test("negative value isNegative") { with(ops) { (-5).toBigInteger().isNegative() } shouldBe true }
            test("positive is not negative") { with(ops) { 5.toBigInteger().isNegative() } shouldBe false }
            test("zero is not negative") { with(ops) { BigInteger.ZERO.isNegative() } shouldBe false }
            test("positive value isPositive") { with(ops) { 5.toBigInteger().isPositive() } shouldBe true }
            test("negative is not positive") { with(ops) { (-5).toBigInteger().isPositive() } shouldBe false }
            test("zero is not positive") { with(ops) { BigInteger.ZERO.isPositive() } shouldBe false }
            test("zero isZero") { with(ops) { BigInteger.ZERO.isZero() } shouldBe true }
            test("positive is not zero") { with(ops) { BigInteger.ONE.isZero() } shouldBe false }
        }

        // sign() delegates to isNegative/isPositive/isZero, which are verified above.
        context("sign") {
            test("positive gives ONE") { with(ops) { 42.toBigInteger().sign() } shouldBe BigInteger.ONE }
            test("negative gives -ONE") { with(ops) { (-42).toBigInteger().sign() } shouldBe BigInteger.ONE.negate() }
            test("zero gives ZERO") { with(ops) { BigInteger.ZERO.sign() } shouldBe BigInteger.ZERO }
            test("very large positive gives ONE") {
                with(ops) { BigInteger.TWO.pow(500).sign() } shouldBe BigInteger.ONE
            }
        }

        context("floorDiv") {
            test("7 floorDiv 2 = 3 (same signs, same as truncated)") {
                with(ops) { 7.toBigInteger().floorDiv(2.toBigInteger()) } shouldBe 3.toBigInteger()
            }
            test("-7 floorDiv 2 = -4 (rounds toward -inf)") {
                with(ops) { (-7).toBigInteger().floorDiv(2.toBigInteger()) } shouldBe (-4).toBigInteger()
            }
            test("7 floorDiv -2 = -4 (rounds toward -inf)") {
                with(ops) { 7.toBigInteger().floorDiv((-2).toBigInteger()) } shouldBe (-4).toBigInteger()
            }
            test("-7 floorDiv -2 = 3 (same signs)") {
                with(ops) { (-7).toBigInteger().floorDiv((-2).toBigInteger()) } shouldBe 3.toBigInteger()
            }
            test("floorDiv by zero throws ArithmeticException") {
                shouldThrow<ArithmeticException> { with(ops) { BigInteger.ONE.floorDiv(BigInteger.ZERO) } }
            }
            test("works for arbitrary precision") {
                val large = BigInteger.TWO.pow(200)
                with(ops) { large.negate().floorDiv(3.toBigInteger()) } shouldBe
                    large.negate().subtract(2.toBigInteger()).divide(3.toBigInteger())
            }
        }

        context("mod") {
            // Java's BigInteger.mod(BigInteger) is a class member that shadows the trait's default
            // mod() at call sites. Java's version only accepts a positive modulus; negative-divisor
            // cases (7 mod -3, -7 mod -3) therefore throw rather than produce a floor-mod result.
            // Only positive-modulus cases are testable here via with(ops).
            test("-7 mod 3 = 2 (positive divisor, sign of divisor)") {
                with(ops) { (-7).toBigInteger().mod(3.toBigInteger()) } shouldBe 2.toBigInteger()
            }
            test("exact: 6 mod 3 = 0") {
                with(ops) { 6.toBigInteger().mod(3.toBigInteger()) } shouldBe BigInteger.ZERO
            }
            test("mod by zero throws ArithmeticException") {
                shouldThrow<ArithmeticException> { with(ops) { BigInteger.ONE.mod(BigInteger.ZERO) } }
            }
            test("invariant: a == b * floorDiv(a, b) + mod(a, b) (positive b)") {
                val a = (-7).toBigInteger(); val b = 3.toBigInteger()
                with(ops) { a.floorDiv(b).multiply(b).add(a.mod(b)) } shouldBe a
            }
        }
    }

    // ── ArithmeticRightShift.Companion.bigInteger ─────────────────────────────

    context("ArithmeticRightShift.Companion.bigInteger") {
        val ops = ArithmeticRightShift.bigInteger

        test("positive value: fills with 0") {
            with(ops) { 8.toBigInteger().arithmeticRightShift(3) } shouldBe BigInteger.ONE
        }
        test("shift by 0 is identity") {
            with(ops) { 42.toBigInteger().arithmeticRightShift(0) } shouldBe 42.toBigInteger()
        }
        test("-1 shr 1 = -1 (sign-extends)") {
            with(ops) { (-1).toBigInteger().arithmeticRightShift(1) } shouldBe (-1).toBigInteger()
        }
        test("-8 shr 3 = -1") {
            with(ops) { (-8).toBigInteger().arithmeticRightShift(3) } shouldBe (-1).toBigInteger()
        }
        test("-7 shr 3 = -1") {
            with(ops) { (-7).toBigInteger().arithmeticRightShift(3) } shouldBe (-1).toBigInteger()
        }
        test("sign bit propagates: -1 shr 100 = -1") {
            with(ops) { (-1).toBigInteger().arithmeticRightShift(100) } shouldBe (-1).toBigInteger()
        }
        test("large positive value shr") {
            val large = BigInteger.TWO.pow(64)
            with(ops) { large.arithmeticRightShift(32) } shouldBe BigInteger.TWO.pow(32)
        }
    }

    // ── Singleton identity ────────────────────────────────────────────────────

    context("singleton identity") {
        test("SignedIntegerArithmetic.bigInteger is stable") {
            SignedIntegerArithmetic.bigInteger shouldBeSameInstanceAs SignedIntegerArithmetic.bigInteger
        }
        test("ArithmeticRightShift.bigInteger is stable") {
            ArithmeticRightShift.bigInteger shouldBeSameInstanceAs ArithmeticRightShift.bigInteger
        }
        test("bigInteger and long instances are distinct") {
            SignedIntegerArithmetic.bigInteger shouldNotBe SignedIntegerArithmetic.long
        }
        test("ArithmeticRightShift bigInteger and int are distinct") {
            ArithmeticRightShift.bigInteger shouldNotBe ArithmeticRightShift.int
        }
    }
})
