package com.kelvsyc.kotlin.commons.numbers

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class LongExtensionsTest : FunSpec() {
    init {
        test("toDD") { 3L.toDD().hi() shouldBe 3.0 }
        test("toBigFraction") { 3L.toBigFraction().numeratorAsLong shouldBe 3L }
    }
}
