package com.kelvsyc.kotlin.core.traits.integral

import com.kelvsyc.kotlin.core.isJvm
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class AdditionalIntegerArithmeticTest : FunSpec({

    // ── IntegerArithmetic.Companion.byte ──────────────────────────────────────

    context("IntegerArithmetic.Companion.byte") {
        val ops = IntegerArithmetic.byte

        test("zero is 0") { ops.zero shouldBe 0.toByte() }
        test("one is 1") { ops.one shouldBe 1.toByte() }
        test("add") { with(ops) { 2.toByte().add(3.toByte()) } shouldBe 5.toByte() }
        test("wraps at Byte.MAX_VALUE") { with(ops) { Byte.MAX_VALUE.add(1.toByte()) } shouldBe Byte.MIN_VALUE }
        test("subtract") { with(ops) { 5.toByte().subtract(3.toByte()) } shouldBe 2.toByte() }
        test("wraps at Byte.MIN_VALUE") { with(ops) { Byte.MIN_VALUE.subtract(1.toByte()) } shouldBe Byte.MAX_VALUE }
        test("multiply") { with(ops) { 6.toByte().multiply(3.toByte()) } shouldBe 18.toByte() }
        test("divide truncates toward zero: -7 / 2 = -3") { with(ops) { (-7).toByte().divide(2.toByte()) } shouldBe (-3).toByte() }
        test("divide by zero throws").config(enabledIf = { isJvm }) { shouldThrow<ArithmeticException> { with(ops) { 1.toByte().divide(0.toByte()) } } }
        test("rem has sign of dividend: -7 % 2 = -1") { with(ops) { (-7).toByte().rem(2.toByte()) } shouldBe (-1).toByte() }
        test("rem by zero throws").config(enabledIf = { isJvm }) { shouldThrow<ArithmeticException> { with(ops) { 1.toByte().rem(0.toByte()) } } }
        test("compareTo: 1 < 2") { (with(ops) { 1.toByte().compareTo(2.toByte()) } < 0) shouldBe true }
    }

    // ── IntegerArithmetic.Companion.short ─────────────────────────────────────

    context("IntegerArithmetic.Companion.short") {
        val ops = IntegerArithmetic.short

        test("zero is 0") { ops.zero shouldBe 0.toShort() }
        test("one is 1") { ops.one shouldBe 1.toShort() }
        test("wraps at Short.MAX_VALUE") { with(ops) { Short.MAX_VALUE.add(1.toShort()) } shouldBe Short.MIN_VALUE }
        test("divide truncates toward zero: -7 / 2 = -3") { with(ops) { (-7).toShort().divide(2.toShort()) } shouldBe (-3).toShort() }
        test("divide by zero throws").config(enabledIf = { isJvm }) { shouldThrow<ArithmeticException> { with(ops) { 1.toShort().divide(0.toShort()) } } }
        test("rem has sign of dividend: -7 % 2 = -1") { with(ops) { (-7).toShort().rem(2.toShort()) } shouldBe (-1).toShort() }
    }

    // ── SignedIntegerArithmetic.Companion.byte ────────────────────────────────

    context("SignedIntegerArithmetic.Companion.byte") {
        val ops = SignedIntegerArithmetic.byte

        test("zero is 0") { ops.zero shouldBe 0.toByte() }
        test("one is 1") { ops.one shouldBe 1.toByte() }
        test("negate positive") { with(ops) { 5.toByte().negate() } shouldBe (-5).toByte() }
        test("negate negative") { with(ops) { (-5).toByte().negate() } shouldBe 5.toByte() }
        test("MIN_VALUE negate wraps to itself") { with(ops) { Byte.MIN_VALUE.negate() } shouldBe Byte.MIN_VALUE }
        test("abs positive") { with(ops) { 5.toByte().abs() } shouldBe 5.toByte() }
        test("abs negative") { with(ops) { (-5).toByte().abs() } shouldBe 5.toByte() }
        test("MIN_VALUE abs wraps to itself") { with(ops) { Byte.MIN_VALUE.abs() } shouldBe Byte.MIN_VALUE }
        test("floorDiv rounds toward -inf: -7 floorDiv 2 = -4") { with(ops) { (-7).toByte().floorDiv(2.toByte()) } shouldBe (-4).toByte() }
        test("mod result has sign of divisor: -7 mod 2 = 1") { with(ops) { (-7).toByte().mod(2.toByte()) } shouldBe 1.toByte() }
        test("ceilDiv rounds toward +inf: 7 ceilDiv 2 = 4") { with(ops) { 7.toByte().ceilDiv(2.toByte()) } shouldBe 4.toByte() }
        test("ceilDiv rounds toward +inf: -7 ceilDiv 2 = -3") { with(ops) { (-7).toByte().ceilDiv(2.toByte()) } shouldBe (-3).toByte() }
    }

    // ── SignedIntegerArithmetic.Companion.short ───────────────────────────────

    context("SignedIntegerArithmetic.Companion.short") {
        val ops = SignedIntegerArithmetic.short

        test("zero is 0") { ops.zero shouldBe 0.toShort() }
        test("one is 1") { ops.one shouldBe 1.toShort() }
        test("negate positive") { with(ops) { 5.toShort().negate() } shouldBe (-5).toShort() }
        test("MIN_VALUE negate wraps to itself") { with(ops) { Short.MIN_VALUE.negate() } shouldBe Short.MIN_VALUE }
        test("abs negative") { with(ops) { (-5).toShort().abs() } shouldBe 5.toShort() }
        test("floorDiv rounds toward -inf: -7 floorDiv 2 = -4") { with(ops) { (-7).toShort().floorDiv(2.toShort()) } shouldBe (-4).toShort() }
        test("ceilDiv rounds toward +inf: 7 ceilDiv 2 = 4") { with(ops) { 7.toShort().ceilDiv(2.toShort()) } shouldBe 4.toShort() }
    }

    // ── IntegerArithmetic.Companion.ubyte ─────────────────────────────────────

    context("IntegerArithmetic.Companion.ubyte") {
        val ops = IntegerArithmetic.ubyte

        test("zero is 0u") { ops.zero shouldBe 0u.toUByte() }
        test("one is 1u") { ops.one shouldBe 1u.toUByte() }
        test("add") { with(ops) { 2u.toUByte().add(3u.toUByte()) } shouldBe 5u.toUByte() }
        test("wraps at UByte.MAX_VALUE") { with(ops) { UByte.MAX_VALUE.add(1u.toUByte()) } shouldBe 0u.toUByte() }
        test("subtract wraps below zero: 0 - 1 = MAX_VALUE") { with(ops) { 0u.toUByte().subtract(1u.toUByte()) } shouldBe UByte.MAX_VALUE }
        test("divide") { with(ops) { 6u.toUByte().divide(2u.toUByte()) } shouldBe 3u.toUByte() }
        test("divide by zero throws").config(enabledIf = { isJvm }) { shouldThrow<ArithmeticException> { with(ops) { 1u.toUByte().divide(0u.toUByte()) } } }
        test("rem") {
            // UByte.rem(UByte): UInt is a member function of the inline class and shadows our
            // member extension at concrete call sites. Exercise via generic dispatch instead.
            fun <T> IntegerArithmetic<T>.invokeRem(a: T, b: T): T = with(this) { a.rem(b) }
            ops.invokeRem(7u.toUByte(), 3u.toUByte()) shouldBe 1u.toUByte()
        }
        test("compareTo: 1u < 2u") { (with(ops) { 1u.toUByte().compareTo(2u.toUByte()) } < 0) shouldBe true }
        test("unsigned compareTo: MAX_VALUE > 1u") { (with(ops) { UByte.MAX_VALUE.compareTo(1u.toUByte()) } > 0) shouldBe true }
    }

    // ── IntegerArithmetic.Companion.ushort ────────────────────────────────────

    context("IntegerArithmetic.Companion.ushort") {
        val ops = IntegerArithmetic.ushort

        test("zero is 0u") { ops.zero shouldBe 0u.toUShort() }
        test("one is 1u") { ops.one shouldBe 1u.toUShort() }
        test("wraps at UShort.MAX_VALUE") { with(ops) { UShort.MAX_VALUE.add(1u.toUShort()) } shouldBe 0u.toUShort() }
        test("divide by zero throws").config(enabledIf = { isJvm }) { shouldThrow<ArithmeticException> { with(ops) { 1u.toUShort().divide(0u.toUShort()) } } }
        test("unsigned compareTo: MAX_VALUE > 1u") { (with(ops) { UShort.MAX_VALUE.compareTo(1u.toUShort()) } > 0) shouldBe true }
    }

    // ── IntegerArithmetic.Companion.uint ──────────────────────────────────────

    context("IntegerArithmetic.Companion.uint") {
        val ops = IntegerArithmetic.uint

        test("zero is 0u") { ops.zero shouldBe 0u }
        test("one is 1u") { ops.one shouldBe 1u }
        test("add") { with(ops) { 2u.add(3u) } shouldBe 5u }
        test("wraps at UInt.MAX_VALUE") { with(ops) { UInt.MAX_VALUE.add(1u) } shouldBe 0u }
        test("subtract wraps: 0u - 1u = MAX_VALUE") { with(ops) { 0u.subtract(1u) } shouldBe UInt.MAX_VALUE }
        test("divide: 6u / 2u = 3u") { with(ops) { 6u.divide(2u) } shouldBe 3u }
        test("divide by zero throws").config(enabledIf = { isJvm }) { shouldThrow<ArithmeticException> { with(ops) { 1u.divide(0u) } } }
        test("rem: 7u % 3u = 1u") { with(ops) { 7u.rem(3u) } shouldBe 1u }
        test("unsigned compareTo: MAX_VALUE > 1u") { (with(ops) { UInt.MAX_VALUE.compareTo(1u) } > 0) shouldBe true }
    }

    // ── IntegerArithmetic.Companion.ulong ─────────────────────────────────────

    context("IntegerArithmetic.Companion.ulong") {
        val ops = IntegerArithmetic.ulong

        test("zero is 0uL") { ops.zero shouldBe 0uL }
        test("one is 1uL") { ops.one shouldBe 1uL }
        test("add") { with(ops) { 2uL.add(3uL) } shouldBe 5uL }
        test("wraps at ULong.MAX_VALUE") { with(ops) { ULong.MAX_VALUE.add(1uL) } shouldBe 0uL }
        test("divide by zero throws").config(enabledIf = { isJvm }) { shouldThrow<ArithmeticException> { with(ops) { 1uL.divide(0uL) } } }
        test("unsigned compareTo: MAX_VALUE > 1uL") { (with(ops) { ULong.MAX_VALUE.compareTo(1uL) } > 0) shouldBe true }
    }

    // ── Singleton identity ────────────────────────────────────────────────────

    context("singleton identity") {
        test("IntegerArithmetic.byte is stable") { IntegerArithmetic.byte shouldBe IntegerArithmetic.byte }
        test("IntegerArithmetic.short is stable") { IntegerArithmetic.short shouldBe IntegerArithmetic.short }
        test("IntegerArithmetic.ubyte is stable") { IntegerArithmetic.ubyte shouldBe IntegerArithmetic.ubyte }
        test("IntegerArithmetic.ushort is stable") { IntegerArithmetic.ushort shouldBe IntegerArithmetic.ushort }
        test("IntegerArithmetic.uint is stable") { IntegerArithmetic.uint shouldBe IntegerArithmetic.uint }
        test("IntegerArithmetic.ulong is stable") { IntegerArithmetic.ulong shouldBe IntegerArithmetic.ulong }
        test("SignedIntegerArithmetic.byte is stable") { SignedIntegerArithmetic.byte shouldBe SignedIntegerArithmetic.byte }
        test("SignedIntegerArithmetic.short is stable") { SignedIntegerArithmetic.short shouldBe SignedIntegerArithmetic.short }
        test("IntegerArithmetic.byte and SignedIntegerArithmetic.byte are distinct") {
            IntegerArithmetic.byte shouldNotBe SignedIntegerArithmetic.byte
        }
        test("IntegerArithmetic.short and SignedIntegerArithmetic.short are distinct") {
            IntegerArithmetic.short shouldNotBe SignedIntegerArithmetic.short
        }
    }
})
