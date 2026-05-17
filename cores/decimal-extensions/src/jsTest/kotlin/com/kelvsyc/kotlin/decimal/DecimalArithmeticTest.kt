package com.kelvsyc.kotlin.decimal

import com.kelvsyc.kotlin.core.traits.Addition
import com.kelvsyc.kotlin.core.traits.Division
import com.kelvsyc.kotlin.core.traits.Multiplication
import com.kelvsyc.kotlin.core.traits.Signed
import com.kelvsyc.kotlin.core.traits.ValueEquality
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

class DecimalArithmeticTest : FunSpec({
    test("Addition.decimal zero is 0") {
        with(Addition.decimal) { zero.toKotlinString() shouldBe "0" }
    }

    test("Addition.decimal add") {
        with(Addition.decimal) { Decimal("1.5").add(Decimal("2.5")).toKotlinString() shouldBe "4" }
    }

    test("Addition.decimal subtract") {
        with(Addition.decimal) { Decimal("5").subtract(Decimal("1.5")).toKotlinString() shouldBe "3.5" }
    }

    test("Multiplication.decimal one is 1") {
        with(Multiplication.decimal) { one.toKotlinString() shouldBe "1" }
    }

    test("Multiplication.decimal multiply") {
        with(Multiplication.decimal) { Decimal("3").multiply(Decimal("4")).toKotlinString() shouldBe "12" }
    }

    test("Division.decimal divides using global precision") {
        with(Division.decimal) { Decimal("1").divide(Decimal("3")).isFinite().shouldBeTrue() }
    }

    test("Division.decimal with precision uses independent context") {
        val div = Division.decimal(5, DecimalRounding.HALF_UP)
        with(div) { Decimal("1").divide(Decimal("3")).toKotlinString() shouldBe "0.33333" }
    }

    test("Signed.decimal isNegative") {
        with(Signed.decimal) {
            Decimal("-1").isNegative().shouldBeTrue()
            Decimal("1").isNegative().shouldBeFalse()
        }
    }

    test("Signed.decimal negate") {
        with(Signed.decimal) { Decimal("5").negate().toKotlinString() shouldBe "-5" }
    }

    test("Signed.decimal abs") {
        with(Signed.decimal) { Decimal("-3").abs().toKotlinString() shouldBe "3" }
    }

    test("ValueEquality.decimalNumerical: NaN is not equal to NaN") {
        with(ValueEquality.decimalNumerical) { Decimal("NaN").isEqualTo(Decimal("NaN")).shouldBeFalse() }
    }

    test("ValueEquality.decimalNumerical: equal values") {
        with(ValueEquality.decimalNumerical) { Decimal("1.5").isEqualTo(Decimal("1.5")).shouldBeTrue() }
    }

    test("ValueEquality.decimalEquivalence: NaN equals NaN") {
        with(ValueEquality.decimalEquivalence) { Decimal("NaN").isEqualTo(Decimal("NaN")).shouldBeTrue() }
    }

    test("ValueEquality.decimalEquivalence: equal values") {
        with(ValueEquality.decimalEquivalence) { Decimal("2").isEqualTo(Decimal("2")).shouldBeTrue() }
    }
})
