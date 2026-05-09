package com.kelvsyc.kotlin.moshi

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class JsonValueAdapterTest : FunSpec({

    context("parsing JSON strings") {
        test("parses object") {
            val result = """{"name": "Alice", "age": 30}""".parseJson()
            result.shouldBeInstanceOf<JsonObject>()
            result.stringAt("name") shouldBe "Alice"
            result.doubleAt("age") shouldBe 30.0
        }

        test("parses array") {
            val result = """[1, 2, 3]""".parseJson()
            result.shouldBeInstanceOf<JsonArray>()
            val arr = result.asArray()
            arr shouldBe JsonArray(listOf(JsonNumber(1.0), JsonNumber(2.0), JsonNumber(3.0)))
        }

        test("parses nested structure") {
            val json = """
                {
                    "user": {
                        "name": "Bob",
                        "scores": [10, 20, 30]
                    }
                }
            """.trimIndent()
            val result = json.parseJson()
            result.stringAt("user", "name") shouldBe "Bob"
            result.arrayAt("user", "scores")!!.size shouldBe 3
        }

        test("parses null values") {
            val result = """{"value": null}""".parseJson()
            result.at("value").shouldBeInstanceOf<JsonNull>()
        }

        test("parses boolean values") {
            val result = """{"flag": true}""".parseJson()
            result.booleanAt("flag") shouldBe true
        }

        test("parses string value") {
            val result = """"hello"""".parseJson()
            result.shouldBeInstanceOf<JsonString>()
            result.asString() shouldBe "hello"
        }
    }

    context("round-trip serialization") {
        test("object round-trips through adapter") {
            val original = JsonObject(
                mapOf(
                    "name" to JsonString("Alice"),
                    "active" to JsonBoolean(true),
                    "score" to JsonNumber(95.0),
                    "data" to JsonNull,
                )
            )
            val adapter = JsonValueAdapter()
            val json = adapter.toJson(original)
            val parsed = json.parseJson()
            parsed shouldBe original
        }

        test("array round-trips through adapter") {
            val original = JsonArray(
                listOf(
                    JsonString("a"),
                    JsonNumber(1.0),
                    JsonBoolean(false),
                    JsonNull,
                )
            )
            val adapter = JsonValueAdapter()
            val json = adapter.toJson(original)
            val parsed = json.parseJson()
            parsed shouldBe original
        }
    }
})
