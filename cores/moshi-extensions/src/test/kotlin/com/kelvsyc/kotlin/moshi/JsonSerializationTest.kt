package com.kelvsyc.kotlin.moshi

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beTheSameInstanceAs
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

private fun <T : Serializable> roundTrip(value: T): T {
    val bytes = ByteArrayOutputStream().also { ObjectOutputStream(it).use { oos -> oos.writeObject(value) } }.toByteArray()
    @Suppress("UNCHECKED_CAST")
    return ObjectInputStream(ByteArrayInputStream(bytes)).use { it.readObject() as T }
}

class JsonSerializationTest : FunSpec({

    context("JsonValue round-trips") {
        test("JsonString") {
            val value = JsonString("hello")
            roundTrip(value) shouldBe value
        }

        test("JsonNumber") {
            val value = JsonNumber(42)
            roundTrip(value) shouldBe value
        }

        test("JsonBoolean") {
            val value = JsonBoolean(true)
            roundTrip(value) shouldBe value
        }

        test("JsonNull preserves singleton") {
            val result = roundTrip(JsonNull)
            result shouldBe JsonNull
            result should beTheSameInstanceAs(JsonNull)
        }

        test("JsonArray") {
            val value = JsonArray(listOf(JsonString("a"), JsonNull))
            roundTrip(value) shouldBe value
        }

        test("JsonObject") {
            val value = JsonObject(mapOf("key" to JsonString("value"), "count" to JsonNumber(1)))
            roundTrip(value) shouldBe value
        }
    }

    context("JsonPathSegment round-trips") {
        test("Key") {
            val value = JsonPathSegment.Key("foo")
            roundTrip(value) shouldBe value
        }

        test("Index") {
            val value = JsonPathSegment.Index(2)
            roundTrip(value) shouldBe value
        }

        test("Wildcard preserves singleton") {
            val result = roundTrip(JsonPathSegment.Wildcard)
            result shouldBe JsonPathSegment.Wildcard
            result should beTheSameInstanceAs(JsonPathSegment.Wildcard)
        }

        test("RecursiveDescent preserves singleton") {
            val result = roundTrip(JsonPathSegment.RecursiveDescent)
            result shouldBe JsonPathSegment.RecursiveDescent
            result should beTheSameInstanceAs(JsonPathSegment.RecursiveDescent)
        }
    }

    context("JsonPath round-trips") {
        test("simple path") {
            val value = JsonPath.parse("$.foo.bar")
            roundTrip(value) shouldBe value
        }

        test("path with array index") {
            val value = JsonPath.parse("$.items[0]")
            roundTrip(value) shouldBe value
        }
    }
})
