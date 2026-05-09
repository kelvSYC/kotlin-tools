package com.kelvsyc.kotlin.moshi

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

class JsonValueExtensionsTest : FunSpec({

    val json = JsonObject(
        mapOf(
            "name" to JsonString("Alice"),
            "age" to JsonNumber(30.0),
            "active" to JsonBoolean(true),
            "address" to JsonNull,
            "tags" to JsonArray(listOf(JsonString("admin"), JsonString("user"))),
            "scores" to JsonArray(listOf(JsonNumber(95.0), JsonNumber(87.0))),
        )
    )

    context("typed path accessors") {
        test("stringAt returns string value") {
            json.stringAt("name") shouldBe "Alice"
        }

        test("stringAt returns null for non-string") {
            json.stringAt("age").shouldBeNull()
        }

        test("numberAt returns numeric value") {
            json.numberAt("age") shouldBe 30.0
        }

        test("intAt returns integer value") {
            json.intAt("age") shouldBe 30
        }

        test("longAt returns long value") {
            json.longAt("age") shouldBe 30L
        }

        test("doubleAt returns double value") {
            json.doubleAt("age") shouldBe 30.0
        }

        test("booleanAt returns boolean value") {
            json.booleanAt("active") shouldBe true
        }

        test("objectAt returns null for non-object") {
            json.objectAt("name").shouldBeNull()
        }

        test("arrayAt returns array") {
            json.arrayAt("tags")?.size shouldBe 2
        }

        test("isNullAt returns true for null value") {
            json.isNullAt("address") shouldBe true
        }

        test("isNullAt returns true for missing path") {
            json.isNullAt("missing") shouldBe true
        }

        test("isNullAt returns false for present value") {
            json.isNullAt("name") shouldBe false
        }
    }

    context("array extraction helpers") {
        test("strings filters to string elements") {
            val arr = JsonArray(listOf(JsonString("a"), JsonNumber(1.0), JsonString("b")))
            arr.strings() shouldBe listOf("a", "b")
        }

        test("numbers filters to numeric elements") {
            val arr = JsonArray(listOf(JsonNumber(1.0), JsonString("x"), JsonNumber(2.0)))
            arr.numbers() shouldBe listOf(1.0, 2.0)
        }
    }
})
