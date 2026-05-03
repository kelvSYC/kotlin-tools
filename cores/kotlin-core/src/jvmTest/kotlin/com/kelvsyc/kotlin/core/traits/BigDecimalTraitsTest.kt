package com.kelvsyc.kotlin.core.traits

import com.kelvsyc.kotlin.core.traits.fp.FloatingPointRounding
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointScald
import com.kelvsyc.kotlin.core.traits.fp.IntegerPower
import com.kelvsyc.kotlin.core.traits.fp.bigDecimal
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

// Generic dispatch helpers — call the trait extension through a type parameter so that the
// BigDecimal Java member cannot shadow it. Required for testing the MathContext factory and
// the IntegerPower negative-exponent guard, both of which are shadowed at concrete call sites.
private fun <T> divideWith(ops: Division<T>, a: T, b: T): T = with(ops) { a.divide(b) }
private fun <T> powWith(ops: IntegerPower<T>, base: T, n: Int): T = with(ops) { base.pow(n) }

class BigDecimalTraitsTest : FunSpec({

    // ── Addition ──────────────────────────────────────────────────────────────

    context("Addition.bigDecimal") {
        val ops = Addition.bigDecimal

        test("zero") { ops.zero shouldBe BigDecimal.ZERO }
        test("add") { with(ops) { BigDecimal("1.5").add(BigDecimal("2.5")) } shouldBe BigDecimal("4.0") }
        test("subtract") { with(ops) { BigDecimal("5.0").subtract(BigDecimal("3.0")) } shouldBe BigDecimal("2.0") }
        test("subtract to negative") { with(ops) { BigDecimal("1").subtract(BigDecimal("3")) } shouldBe BigDecimal("-2") }
    }

    // ── Multiplication ────────────────────────────────────────────────────────

    context("Multiplication.bigDecimal") {
        val ops = Multiplication.bigDecimal

        test("one") { ops.one shouldBe BigDecimal.ONE }
        test("multiply") { with(ops) { BigDecimal("2.5").multiply(BigDecimal("4")) } shouldBe BigDecimal("10.0") }
        test("multiply by zero") { with(ops) { BigDecimal("99").multiply(BigDecimal.ZERO) } shouldBe BigDecimal("0") }
    }

    // ── Division (exact) ──────────────────────────────────────────────────────

    context("Division.bigDecimal (exact)") {
        val ops = Division.bigDecimal

        test("exact division") { with(ops) { BigDecimal("10").divide(BigDecimal("4")) } shouldBe BigDecimal("2.5") }
        test("non-terminating throws") {
            shouldThrow<ArithmeticException> {
                with(ops) { BigDecimal("1").divide(BigDecimal("3")) }
            }
        }
    }

    // ── Division (MathContext factory) ────────────────────────────────────────
    //
    // BigDecimal.divide(BigDecimal) is a Java member that shadows the trait extension at concrete
    // call sites inside a with(ops) block. The MathContext factory instance is only effective in
    // generic dispatch contexts (where T is erased and dispatch goes through the interface).
    // Tests here use divideWith() to force generic dispatch.

    context("Division.bigDecimal(MathContext) — generic dispatch") {
        val ops = Division.bigDecimal(MathContext(4, RoundingMode.HALF_UP))

        test("exact division") {
            // divide(BigDecimal, MathContext) strips trailing zeros for exact results
            divideWith(ops, BigDecimal("10"), BigDecimal("4")).compareTo(BigDecimal("2.5")) shouldBe 0
        }
        test("non-terminating rounds") {
            divideWith(ops, BigDecimal("1"), BigDecimal("3")) shouldBe BigDecimal("0.3333")
        }
        test("rounds up at half") {
            divideWith(ops, BigDecimal("2"), BigDecimal("3")) shouldBe BigDecimal("0.6667")
        }
    }

    // ── FloatingPointRounding ─────────────────────────────────────────────────

    context("FloatingPointRounding.bigDecimal") {
        val ops = FloatingPointRounding.bigDecimal

        test("floor positive") { with(ops) { BigDecimal("3.7").floor() } shouldBe BigDecimal("3") }
        test("floor negative") { with(ops) { BigDecimal("-1.5").floor() } shouldBe BigDecimal("-2") }
        test("floor integer") { with(ops) { BigDecimal("4.0").floor() } shouldBe BigDecimal("4") }
        test("ceil positive") { with(ops) { BigDecimal("3.2").ceil() } shouldBe BigDecimal("4") }
        test("ceil negative") { with(ops) { BigDecimal("-1.5").ceil() } shouldBe BigDecimal("-1") }
        test("ceil integer") { with(ops) { BigDecimal("4.0").ceil() } shouldBe BigDecimal("4") }
    }

    // ── FloatingPointScald ────────────────────────────────────────────────────

    context("FloatingPointScald.bigDecimal") {
        val ops = FloatingPointScald.bigDecimal

        test("scald by 1 multiplies by 10") {
            with(ops) { BigDecimal("3.14").scald(1) } shouldBe BigDecimal("31.4")
        }
        test("scald by -1 divides by 10") {
            with(ops) { BigDecimal("31.4").scald(-1) } shouldBe BigDecimal("3.14")
        }
        test("scald by 0 is identity") {
            with(ops) { BigDecimal("2.5").scald(0) } shouldBe BigDecimal("2.5")
        }
        // scaleByPowerOfTen adjusts the scale, so BigDecimal("1").scald(3) produces 1E+3
        // (scale -3), not 1000 (scale 0). Use compareTo for numeric equality across scales.
        test("scald by 3") {
            with(ops) { BigDecimal("1").scald(3) }.compareTo(BigDecimal("1000")) shouldBe 0
        }
    }

    // ── IntegerPower ──────────────────────────────────────────────────────────

    context("IntegerPower.bigDecimal") {
        val ops = IntegerPower.bigDecimal

        test("pow 0 returns one") { with(ops) { BigDecimal("5").pow(0) } shouldBe BigDecimal("1") }
        test("pow 1 returns self") { with(ops) { BigDecimal("5").pow(1) } shouldBe BigDecimal("5") }
        test("pow 2") { with(ops) { BigDecimal("3").pow(2) } shouldBe BigDecimal("9") }
        test("pow 10") { with(ops) { BigDecimal("2").pow(10) } shouldBe BigDecimal("1024") }

        // BigDecimal.pow(int) is a Java member that shadows the trait extension at concrete
        // call sites, so the trait's require(n >= 0) guard is unreachable there. Java's pow
        // throws ArithmeticException ("Invalid Operation") for negative exponents. The require
        // guard fires only in generic dispatch contexts (tested via powWith).
        test("negative exponent at concrete call site throws ArithmeticException") {
            shouldThrow<ArithmeticException> {
                with(ops) { BigDecimal("2").pow(-1) }
            }
        }
        test("negative exponent in generic dispatch throws IllegalArgumentException") {
            shouldThrow<IllegalArgumentException> {
                powWith(ops, BigDecimal("2"), -1)
            }
        }
    }
})
