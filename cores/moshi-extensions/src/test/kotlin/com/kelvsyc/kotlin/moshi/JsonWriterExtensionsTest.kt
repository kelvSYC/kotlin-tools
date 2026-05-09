package com.kelvsyc.kotlin.moshi

import com.squareup.moshi.JsonWriter
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer

class JsonWriterExtensionsTest : FunSpec({

    context("writeObject") {
        test("produces JSON object") {
            val buffer = Buffer()
            JsonWriter.of(buffer).use { writer ->
                writer.writeObject {
                    field("name", "Alice")
                    field("age", 30)
                }
            }
            buffer.readUtf8() shouldBe """{"name":"Alice","age":30}"""
        }
    }

    context("writeArray") {
        test("produces JSON array") {
            val buffer = Buffer()
            JsonWriter.of(buffer).use { writer ->
                writer.writeArray {
                    value(1)
                    value(2)
                    value(3)
                }
            }
            buffer.readUtf8() shouldBe "[1,2,3]"
        }
    }

    context("nested structures") {
        test("objectField writes nested object") {
            val buffer = Buffer()
            JsonWriter.of(buffer).use { writer ->
                writer.writeObject {
                    objectField("address") {
                        field("city", "Springfield")
                    }
                }
            }
            buffer.readUtf8() shouldBe """{"address":{"city":"Springfield"}}"""
        }

        test("arrayField writes nested array") {
            val buffer = Buffer()
            JsonWriter.of(buffer).use { writer ->
                writer.writeObject {
                    arrayField("tags") {
                        value("a")
                        value("b")
                    }
                }
            }
            buffer.readUtf8() shouldBe """{"tags":["a","b"]}"""
        }
    }

    context("null values") {
        test("field with null string writes JSON null") {
            val buffer = Buffer()
            JsonWriter.of(buffer).use { writer ->
                writer.writeObject {
                    field("value", null as String?)
                }
            }
            buffer.readUtf8() shouldBe """{"value":null}"""
        }
    }
})
