package com.kelvsyc.kotlin.guava

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.math.RoundingMode

class BigDecimalsTest : FunSpec({

    context("BigDecimal.roundToDouble") {
        test("exact representable value") {
            BigDecimal("1.0").roundToDouble(RoundingMode.UNNECESSARY) shouldBe 1.0
        }
        test("non-representable: floor rounds down") {
            // 2^53 + 1 cannot be represented exactly; floor gives 2^53
            BigDecimal.valueOf(9007199254740993L).roundToDouble(RoundingMode.FLOOR) shouldBe 9007199254740992.0
        }
        test("non-representable: ceiling rounds up") {
            // 2^53 + 1 cannot be represented exactly; ceiling gives 2^53 + 2
            BigDecimal.valueOf(9007199254740993L).roundToDouble(RoundingMode.CEILING) shouldBe 9007199254740994.0
        }
        test("exact integer value") {
            BigDecimal("1000000").roundToDouble(RoundingMode.UNNECESSARY) shouldBe 1_000_000.0
        }
        test("non-representable: unnecessary throws") {
            // 2^53 + 1 has no exact Double representation
            shouldThrow<ArithmeticException> {
                BigDecimal.valueOf(9007199254740993L).roundToDouble(RoundingMode.UNNECESSARY)
            }
        }
    }
})
