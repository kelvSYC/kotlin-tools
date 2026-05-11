package com.kelvsyc.kotlin.snakeyaml

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

class YamlValueExtensionsTest : FunSpec({

    val yaml = YamlMapping(
        mapOf(
            "name" to YamlScalar("Alice"),
            "age" to YamlScalar("30"),
            "active" to YamlScalar("true"),
            "score" to YamlScalar("95.5"),
            "address" to YamlNull,
            "tags" to YamlSequence(listOf(YamlScalar("admin"), YamlScalar("user"))),
        )
    )

    context("typed path accessors") {
        test("stringAt returns string value") {
            yaml.stringAt("name") shouldBe "Alice"
        }

        test("stringAt returns raw value for numeric scalar") {
            yaml.stringAt("age") shouldBe "30"
        }

        test("intAt returns integer value") {
            yaml.intAt("age") shouldBe 30
        }

        test("intAt returns null for non-integer") {
            yaml.intAt("name").shouldBeNull()
        }

        test("longAt returns long value") {
            yaml.longAt("age") shouldBe 30L
        }

        test("doubleAt returns double value") {
            yaml.doubleAt("score") shouldBe 95.5
        }

        test("booleanAt returns boolean value") {
            yaml.booleanAt("active") shouldBe true
        }

        test("booleanAt returns null for non-boolean") {
            yaml.booleanAt("name").shouldBeNull()
        }

        test("mappingAt returns null for non-mapping") {
            yaml.mappingAt("name").shouldBeNull()
        }

        test("sequenceAt returns sequence") {
            yaml.sequenceAt("tags")?.size shouldBe 2
        }

        test("isNullAt returns true for null value") {
            yaml.isNullAt("address") shouldBe true
        }

        test("isNullAt returns true for missing path") {
            yaml.isNullAt("missing") shouldBe true
        }

        test("isNullAt returns false for present value") {
            yaml.isNullAt("name") shouldBe false
        }
    }

    context("sequence extraction helpers") {
        test("strings extracts scalar string values") {
            val seq = YamlSequence(listOf(YamlScalar("a"), YamlNull, YamlScalar("b")))
            seq.strings() shouldBe listOf("a", "b")
        }

        test("strings returns empty list for empty sequence") {
            YamlSequence(emptyList()).strings() shouldBe emptyList()
        }
    }
})
