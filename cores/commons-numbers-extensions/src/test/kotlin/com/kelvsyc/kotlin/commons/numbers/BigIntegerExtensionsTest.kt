package com.kelvsyc.kotlin.commons.numbers

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.math.BigInteger

class BigIntegerExtensionsTest : FunSpec() {
    init {
        test("toBigFraction") {
            val n = BigInteger.valueOf(7)
            n.toBigFraction().numerator shouldBe n
        }
    }
}
