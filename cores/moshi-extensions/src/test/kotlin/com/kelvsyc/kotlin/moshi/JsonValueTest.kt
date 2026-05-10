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

    context("JsonObject as Map") {
        val obj = JsonObject(
            mapOf(
                "name" to JsonString("Alice"),
                "age" to JsonNumber(30.0),
                "active" to JsonBoolean(true),
            )
        )

        test("get returns value by key") {
            obj["name"] shouldBe JsonString("Alice")
        }

        test("get returns null for missing key") {
            obj["missing"].shouldBeNull()
        }

        test("keys returns all keys") {
            obj.keys shouldBe setOf("name", "age", "active")
        }

        test("values returns all values") {
            obj.values.toSet() shouldBe setOf(JsonString("Alice"), JsonNumber(30.0), JsonBoolean(true))
        }

        test("size returns entry count") {
            obj.size shouldBe 3
        }

        test("containsKey checks key presence") {
            obj.containsKey("name") shouldBe true
            obj.containsKey("missing") shouldBe false
        }

        test("entries returns Map.Entry set") {
            obj.entries.map { it.key }.toSet() shouldBe setOf("name", "age", "active")
        }

        test("filter works as Map filter") {
            val strings = obj.filter { (_, v) -> v is JsonString }
            strings.keys shouldBe setOf("name")
        }

        test("mapValues transforms values") {
            val stringified = obj.mapValues { (_, v) -> v.asString() ?: v.asNumber()?.toString() }
            stringified["name"] shouldBe "Alice"
            stringified["age"] shouldBe "30.0"
        }

        test("forEach iterates over entries") {
            val collected = mutableListOf<String>()
            obj.forEach { (key, _) -> collected.add(key) }
            collected shouldBe listOf("name", "age", "active")
        }

        test("isEmpty returns false for non-empty object") {
            obj.isEmpty() shouldBe false
        }

        test("isEmpty returns true for empty object") {
            JsonObject(emptyMap()).isEmpty() shouldBe true
        }
    }

    context("JsonArray as List") {
        val arr = JsonArray(listOf(JsonString("a"), JsonString("b"), JsonString("c")))

        test("get returns element by index") {
            arr[0] shouldBe JsonString("a")
            arr[2] shouldBe JsonString("c")
        }

        test("size returns element count") {
            arr.size shouldBe 3
        }

        test("contains checks element presence") {
            arr.contains(JsonString("b")) shouldBe true
            arr.contains(JsonString("z")) shouldBe false
        }

        test("indexOf returns element position") {
            arr.indexOf(JsonString("b")) shouldBe 1
        }

        test("filter works as List filter") {
            val filtered = arr.filter { it.asString() != "b" }
            filtered shouldBe listOf(JsonString("a"), JsonString("c"))
        }

        test("map transforms elements") {
            val upper = arr.map { it.asString()!!.uppercase() }
            upper shouldBe listOf("A", "B", "C")
        }

        test("first returns first element") {
            arr.first() shouldBe JsonString("a")
        }

        test("any checks predicate") {
            arr.any { it.asString() == "b" } shouldBe true
            arr.any { it.asString() == "z" } shouldBe false
        }

        test("isEmpty returns false for non-empty array") {
            arr.isEmpty() shouldBe false
        }

        test("isEmpty returns true for empty array") {
            JsonArray(emptyList()).isEmpty() shouldBe true
        }

        test("iterator provides all elements in order") {
            val collected = arr.iterator().asSequence().toList()
            collected shouldBe listOf(JsonString("a"), JsonString("b"), JsonString("c"))
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
