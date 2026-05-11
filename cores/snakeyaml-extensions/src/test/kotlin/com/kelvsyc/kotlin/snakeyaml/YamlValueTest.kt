package com.kelvsyc.kotlin.snakeyaml

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

class YamlValueTest : FunSpec({

    context("asX accessors") {
        test("YamlScalar returns scalar value") {
            val value: YamlValue = YamlScalar("hello")
            value.asScalar()?.asString() shouldBe "hello"
            value.asMapping().shouldBeNull()
            value.asSequence().shouldBeNull()
            value.isNull() shouldBe false
        }

        test("YamlScalar coerces to int") {
            YamlScalar("42").asInt() shouldBe 42
        }

        test("YamlScalar coerces to long") {
            YamlScalar("9999999999").asLong() shouldBe 9999999999L
        }

        test("YamlScalar coerces to double") {
            YamlScalar("3.14").asDouble() shouldBe 3.14
        }

        test("YamlScalar coerces to boolean") {
            YamlScalar("true").asBoolean() shouldBe true
            YamlScalar("false").asBoolean() shouldBe false
        }

        test("YamlScalar returns null for non-parseable int") {
            YamlScalar("hello").asInt().shouldBeNull()
        }

        test("YamlScalar returns null for non-boolean") {
            YamlScalar("yes").asBoolean().shouldBeNull()
        }

        test("YamlNull reports as null") {
            val value: YamlValue = YamlNull
            value.isNull() shouldBe true
            value.asScalar().shouldBeNull()
            value.asMapping().shouldBeNull()
            value.asSequence().shouldBeNull()
        }

        test("YamlMapping returns self from asMapping") {
            val mapping = YamlMapping(mapOf("key" to YamlScalar("value")))
            mapping.asMapping() shouldBe mapping
        }

        test("YamlSequence returns self from asSequence") {
            val seq = YamlSequence(listOf(YamlScalar("a")))
            seq.asSequence() shouldBe seq
        }
    }

    context("YamlMapping as Map") {
        val mapping = YamlMapping(
            mapOf(
                "name" to YamlScalar("Alice"),
                "age" to YamlScalar("30"),
                "active" to YamlScalar("true"),
            )
        )

        test("get returns value by key") {
            mapping["name"] shouldBe YamlScalar("Alice")
        }

        test("get returns null for missing key") {
            mapping["missing"].shouldBeNull()
        }

        test("keys returns all keys") {
            mapping.keys shouldBe setOf("name", "age", "active")
        }

        test("size returns entry count") {
            mapping.size shouldBe 3
        }

        test("containsKey checks key presence") {
            mapping.containsKey("name") shouldBe true
            mapping.containsKey("missing") shouldBe false
        }

        test("isEmpty returns false for non-empty mapping") {
            mapping.isEmpty() shouldBe false
        }

        test("isEmpty returns true for empty mapping") {
            YamlMapping(emptyMap()).isEmpty() shouldBe true
        }
    }

    context("YamlSequence as List") {
        val seq = YamlSequence(listOf(YamlScalar("a"), YamlScalar("b"), YamlScalar("c")))

        test("get returns element by index") {
            seq[0] shouldBe YamlScalar("a")
            seq[2] shouldBe YamlScalar("c")
        }

        test("size returns element count") {
            seq.size shouldBe 3
        }

        test("contains checks element presence") {
            seq.contains(YamlScalar("b")) shouldBe true
            seq.contains(YamlScalar("z")) shouldBe false
        }

        test("isEmpty returns false for non-empty sequence") {
            seq.isEmpty() shouldBe false
        }

        test("isEmpty returns true for empty sequence") {
            YamlSequence(emptyList()).isEmpty() shouldBe true
        }

        test("first returns first element") {
            seq.first() shouldBe YamlScalar("a")
        }
    }

    context("path navigation with at()") {
        val yaml = YamlMapping(
            mapOf(
                "server" to YamlMapping(
                    mapOf(
                        "host" to YamlScalar("localhost"),
                        "ports" to YamlSequence(
                            listOf(YamlScalar("8080"), YamlScalar("8443"))
                        ),
                    )
                ),
            )
        )

        test("navigates nested mappings") {
            yaml.at("server", "host") shouldBe YamlScalar("localhost")
        }

        test("navigates into sequences by index") {
            yaml.at("server", "ports", "0") shouldBe YamlScalar("8080")
        }

        test("returns null for missing path") {
            yaml.at("server", "database").shouldBeNull()
        }

        test("returns null for invalid sequence index") {
            yaml.at("server", "ports", "99").shouldBeNull()
        }

        test("returns null for non-numeric sequence index") {
            yaml.at("server", "ports", "abc").shouldBeNull()
        }

        test("returns null when navigating through a scalar") {
            yaml.at("server", "host", "first").shouldBeNull()
        }
    }
})
