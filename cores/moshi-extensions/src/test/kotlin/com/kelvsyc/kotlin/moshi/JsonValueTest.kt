package com.kelvsyc.kotlin.moshi

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class JsonValueTest : FunSpec({

    context("asX accessors") {
        test("JsonString returns string value") {
            val value: JsonValue = JsonString("hello")
            value.asString() shouldBe "hello"
            value.asNumber().shouldBeNull()
            value.asBoolean().shouldBeNull()
            value.asObject().shouldBeNull()
            value.asArray().shouldBeNull()
            value.isNull() shouldBe false
        }

        test("JsonNumber returns numeric value") {
            val value: JsonValue = JsonNumber(42.0)
            value.asNumber() shouldBe 42.0
            value.asString().shouldBeNull()
        }

        test("JsonBoolean returns boolean value") {
            val value: JsonValue = JsonBoolean(true)
            value.asBoolean() shouldBe true
            value.asString().shouldBeNull()
        }

        test("JsonNull reports as null") {
            val value: JsonValue = JsonNull
            value.isNull() shouldBe true
            value.asString().shouldBeNull()
            value.asNumber().shouldBeNull()
            value.asBoolean().shouldBeNull()
        }

        test("JsonObject returns self from asObject") {
            val obj = JsonObject(mapOf("key" to JsonString("value")))
            obj.asObject() shouldBe obj
        }

        test("JsonArray returns self from asArray") {
            val arr = JsonArray(listOf(JsonNumber(1.0)))
            arr.asArray() shouldBe arr
        }
    }

    context("JsonObject access") {
        val obj = JsonObject(
            mapOf(
                "name" to JsonString("Alice"),
                "age" to JsonNumber(30.0),
            )
        )

        test("get returns value by key") {
            obj["name"] shouldBe JsonString("Alice")
        }

        test("get returns null for missing key") {
            obj["missing"].shouldBeNull()
        }

        test("keys returns all keys") {
            obj.keys shouldBe setOf("name", "age")
        }
    }

    context("JsonArray access") {
        val arr = JsonArray(listOf(JsonString("a"), JsonString("b"), JsonString("c")))

        test("get returns element by index") {
            arr[0] shouldBe JsonString("a")
            arr[2] shouldBe JsonString("c")
        }

        test("size returns element count") {
            arr.size shouldBe 3
        }
    }

    context("path navigation with at()") {
        val json = JsonObject(
            mapOf(
                "user" to JsonObject(
                    mapOf(
                        "name" to JsonString("Bob"),
                        "scores" to JsonArray(
                            listOf(JsonNumber(10.0), JsonNumber(20.0), JsonNumber(30.0))
                        ),
                    )
                ),
            )
        )

        test("navigates nested objects") {
            json.at("user", "name") shouldBe JsonString("Bob")
        }

        test("navigates into arrays by index") {
            json.at("user", "scores", "1") shouldBe JsonNumber(20.0)
        }

        test("returns null for missing path") {
            json.at("user", "email").shouldBeNull()
        }

        test("returns null for invalid array index") {
            json.at("user", "scores", "99").shouldBeNull()
        }

        test("returns null for non-numeric array index") {
            json.at("user", "scores", "abc").shouldBeNull()
        }

        test("returns null when navigating through a scalar") {
            json.at("user", "name", "first").shouldBeNull()
        }
    }
})
