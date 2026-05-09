package com.kelvsyc.kotlin.moshi

import com.squareup.moshi.JsonReader
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer

class JsonReaderExtensionsTest : FunSpec({

    context("readObject") {
        test("iterates over object entries") {
            val json = """{"a": 1, "b": 2}"""
            val reader = JsonReader.of(Buffer().writeUtf8(json))
            val entries = mutableMapOf<String, Double>()
            reader.readObject { name ->
                entries[name] = nextDouble()
            }
            entries shouldBe mapOf("a" to 1.0, "b" to 2.0)
        }
    }

    context("readArray") {
        test("iterates over array elements") {
            val json = """["x", "y", "z"]"""
            val reader = JsonReader.of(Buffer().writeUtf8(json))
            val elements = mutableListOf<String>()
            reader.readArray {
                elements.add(nextString())
            }
            elements shouldBe listOf("x", "y", "z")
        }
    }

    context("readArrayToList") {
        test("collects array into typed list") {
            val json = """[10, 20, 30]"""
            val reader = JsonReader.of(Buffer().writeUtf8(json))
            val result = reader.readArrayToList { nextInt() }
            result shouldBe listOf(10, 20, 30)
        }
    }

    context("readObjectToMap") {
        test("collects object into typed map") {
            val json = """{"x": "hello", "y": "world"}"""
            val reader = JsonReader.of(Buffer().writeUtf8(json))
            val result = reader.readObjectToMap { nextString() }
            result shouldBe mapOf("x" to "hello", "y" to "world")
        }
    }
})
