package com.kelvsyc.kotlin.guava

import com.google.common.primitives.UnsignedInteger
import com.google.common.primitives.UnsignedLong
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class DiscreteDomainTest : FunSpec({

    context("ShortDiscreteDomain") {
        test("next returns incremented value") {
            ShortDiscreteDomain.next(0) shouldBe 1
        }
        test("next returns null at max") {
            ShortDiscreteDomain.next(Short.MAX_VALUE) shouldBe null
        }
        test("previous returns decremented value") {
            ShortDiscreteDomain.previous(0) shouldBe (-1).toShort()
        }
        test("previous returns null at min") {
            ShortDiscreteDomain.previous(Short.MIN_VALUE) shouldBe null
        }
        test("distance is positive when end > start") {
            ShortDiscreteDomain.distance(0, 10) shouldBe 10L
        }
        test("distance is negative when end < start") {
            ShortDiscreteDomain.distance(10, 0) shouldBe -10L
        }
        test("distance spans full range") {
            ShortDiscreteDomain.distance(Short.MIN_VALUE, Short.MAX_VALUE) shouldBe 65535L
        }
        test("minValue and maxValue") {
            ShortDiscreteDomain.minValue() shouldBe Short.MIN_VALUE
            ShortDiscreteDomain.maxValue() shouldBe Short.MAX_VALUE
        }
    }

    context("UByteDiscreteDomain") {
        test("next returns incremented value") {
            UByteDiscreteDomain.next(0u.toUByte()) shouldBe 1u.toUByte()
        }
        test("next returns null at max") {
            UByteDiscreteDomain.next(UByte.MAX_VALUE) shouldBe null
        }
        test("previous returns decremented value") {
            UByteDiscreteDomain.previous(1u.toUByte()) shouldBe 0u.toUByte()
        }
        test("previous returns null at min") {
            UByteDiscreteDomain.previous(UByte.MIN_VALUE) shouldBe null
        }
        test("distance spans full range") {
            UByteDiscreteDomain.distance(UByte.MIN_VALUE, UByte.MAX_VALUE) shouldBe 255L
        }
        test("minValue and maxValue") {
            UByteDiscreteDomain.minValue() shouldBe UByte.MIN_VALUE
            UByteDiscreteDomain.maxValue() shouldBe UByte.MAX_VALUE
        }
    }

    context("UShortDiscreteDomain") {
        test("next returns incremented value") {
            UShortDiscreteDomain.next(0u.toUShort()) shouldBe 1u.toUShort()
        }
        test("next returns null at max") {
            UShortDiscreteDomain.next(UShort.MAX_VALUE) shouldBe null
        }
        test("previous returns decremented value") {
            UShortDiscreteDomain.previous(1u.toUShort()) shouldBe 0u.toUShort()
        }
        test("previous returns null at min") {
            UShortDiscreteDomain.previous(UShort.MIN_VALUE) shouldBe null
        }
        test("distance spans full range") {
            UShortDiscreteDomain.distance(UShort.MIN_VALUE, UShort.MAX_VALUE) shouldBe 65535L
        }
        test("minValue and maxValue") {
            UShortDiscreteDomain.minValue() shouldBe UShort.MIN_VALUE
            UShortDiscreteDomain.maxValue() shouldBe UShort.MAX_VALUE
        }
    }

    context("UIntDiscreteDomain") {
        test("next returns incremented value") {
            UIntDiscreteDomain.next(0u) shouldBe 1u
        }
        test("next returns null at max") {
            UIntDiscreteDomain.next(UInt.MAX_VALUE) shouldBe null
        }
        test("previous returns decremented value") {
            UIntDiscreteDomain.previous(1u) shouldBe 0u
        }
        test("previous returns null at min") {
            UIntDiscreteDomain.previous(UInt.MIN_VALUE) shouldBe null
        }
        test("distance is positive when end > start") {
            UIntDiscreteDomain.distance(0u, 1000u) shouldBe 1000L
        }
        test("distance is negative when end < start") {
            UIntDiscreteDomain.distance(1000u, 0u) shouldBe -1000L
        }
        test("distance spans full range") {
            UIntDiscreteDomain.distance(UInt.MIN_VALUE, UInt.MAX_VALUE) shouldBe 4294967295L
        }
        test("minValue and maxValue") {
            UIntDiscreteDomain.minValue() shouldBe UInt.MIN_VALUE
            UIntDiscreteDomain.maxValue() shouldBe UInt.MAX_VALUE
        }
    }

    context("ULongDiscreteDomain") {
        test("next returns incremented value") {
            ULongDiscreteDomain.next(0uL) shouldBe 1uL
        }
        test("next returns null at max") {
            ULongDiscreteDomain.next(ULong.MAX_VALUE) shouldBe null
        }
        test("previous returns decremented value") {
            ULongDiscreteDomain.previous(1uL) shouldBe 0uL
        }
        test("previous returns null at min") {
            ULongDiscreteDomain.previous(ULong.MIN_VALUE) shouldBe null
        }
        test("distance is positive for small forward step") {
            ULongDiscreteDomain.distance(0uL, 1000uL) shouldBe 1000L
        }
        test("distance is negative for small backward step") {
            ULongDiscreteDomain.distance(1000uL, 0uL) shouldBe -1000L
        }
        test("distance clamps to Long.MAX_VALUE when diff exceeds Long range") {
            ULongDiscreteDomain.distance(ULong.MIN_VALUE, ULong.MAX_VALUE) shouldBe Long.MAX_VALUE
        }
        test("minValue and maxValue") {
            ULongDiscreteDomain.minValue() shouldBe ULong.MIN_VALUE
            ULongDiscreteDomain.maxValue() shouldBe ULong.MAX_VALUE
        }
    }

    context("UnsignedIntegerDiscreteDomain") {
        test("next returns incremented value") {
            UnsignedIntegerDiscreteDomain.next(UnsignedInteger.ZERO) shouldBe UnsignedInteger.ONE
        }
        test("next returns null at max") {
            UnsignedIntegerDiscreteDomain.next(UnsignedInteger.MAX_VALUE) shouldBe null
        }
        test("previous returns decremented value") {
            UnsignedIntegerDiscreteDomain.previous(UnsignedInteger.ONE) shouldBe UnsignedInteger.ZERO
        }
        test("previous returns null at min") {
            UnsignedIntegerDiscreteDomain.previous(UnsignedInteger.ZERO) shouldBe null
        }
        test("distance spans full range") {
            UnsignedIntegerDiscreteDomain.distance(UnsignedInteger.ZERO, UnsignedInteger.MAX_VALUE) shouldBe 4294967295L
        }
        test("minValue and maxValue") {
            UnsignedIntegerDiscreteDomain.minValue() shouldBe UnsignedInteger.ZERO
            UnsignedIntegerDiscreteDomain.maxValue() shouldBe UnsignedInteger.MAX_VALUE
        }
    }

    context("UnsignedLongDiscreteDomain") {
        test("next returns incremented value") {
            UnsignedLongDiscreteDomain.next(UnsignedLong.ZERO) shouldBe UnsignedLong.ONE
        }
        test("next returns null at max") {
            UnsignedLongDiscreteDomain.next(UnsignedLong.MAX_VALUE) shouldBe null
        }
        test("previous returns decremented value") {
            UnsignedLongDiscreteDomain.previous(UnsignedLong.ONE) shouldBe UnsignedLong.ZERO
        }
        test("previous returns null at min") {
            UnsignedLongDiscreteDomain.previous(UnsignedLong.ZERO) shouldBe null
        }
        test("distance is positive for small forward step") {
            UnsignedLongDiscreteDomain.distance(UnsignedLong.ZERO, UnsignedLong.valueOf(1000L)) shouldBe 1000L
        }
        test("distance is negative for small backward step") {
            UnsignedLongDiscreteDomain.distance(UnsignedLong.valueOf(1000L), UnsignedLong.ZERO) shouldBe -1000L
        }
        test("distance clamps to Long.MAX_VALUE when diff exceeds Long range") {
            UnsignedLongDiscreteDomain.distance(UnsignedLong.ZERO, UnsignedLong.MAX_VALUE) shouldBe Long.MAX_VALUE
        }
        test("minValue and maxValue") {
            UnsignedLongDiscreteDomain.minValue() shouldBe UnsignedLong.ZERO
            UnsignedLongDiscreteDomain.maxValue() shouldBe UnsignedLong.MAX_VALUE
        }
    }
})
