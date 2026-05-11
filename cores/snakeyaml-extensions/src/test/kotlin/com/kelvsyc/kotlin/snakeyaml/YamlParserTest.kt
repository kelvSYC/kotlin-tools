package com.kelvsyc.kotlin.snakeyaml

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class YamlParserTest : FunSpec({

    test("parse simple mapping") {
        val yaml = """
            name: Alice
            age: 30
        """.trimIndent()
        val result = yaml.parseYaml()

        result.shouldNotBeNull()
        val mapping = result.shouldBeInstanceOf<YamlMapping>()
        mapping["name"] shouldBe YamlScalar("Alice")
        mapping["age"] shouldBe YamlScalar("30")
    }

    test("parse simple sequence") {
        val yaml = """
            - apple
            - banana
            - cherry
        """.trimIndent()
        val result = yaml.parseYaml()

        result.shouldNotBeNull()
        val seq = result.shouldBeInstanceOf<YamlSequence>()
        seq.size shouldBe 3
        seq[0] shouldBe YamlScalar("apple")
        seq[1] shouldBe YamlScalar("banana")
        seq[2] shouldBe YamlScalar("cherry")
    }

    test("parse nested mapping") {
        val yaml = """
            server:
              host: localhost
              port: 8080
        """.trimIndent()
        val result = yaml.parseYaml()

        result.shouldNotBeNull()
        val root = result.shouldBeInstanceOf<YamlMapping>()
        val server = root["server"].shouldNotBeNull().shouldBeInstanceOf<YamlMapping>()
        server["host"] shouldBe YamlScalar("localhost")
        server["port"] shouldBe YamlScalar("8080")
    }

    test("parse mapping with sequence values") {
        val yaml = """
            fruits:
              - apple
              - banana
        """.trimIndent()
        val result = yaml.parseYaml()

        result.shouldNotBeNull()
        val root = result.shouldBeInstanceOf<YamlMapping>()
        val fruits = root["fruits"].shouldNotBeNull().shouldBeInstanceOf<YamlSequence>()
        fruits.size shouldBe 2
        fruits[0] shouldBe YamlScalar("apple")
    }

    test("parse sequence of mappings") {
        val yaml = """
            - name: Alice
              role: admin
            - name: Bob
              role: user
        """.trimIndent()
        val result = yaml.parseYaml()

        result.shouldNotBeNull()
        val seq = result.shouldBeInstanceOf<YamlSequence>()
        seq.size shouldBe 2
        val first = seq[0].shouldBeInstanceOf<YamlMapping>()
        first["name"] shouldBe YamlScalar("Alice")
        first["role"] shouldBe YamlScalar("admin")
    }

    test("parse null values") {
        val yaml = """
            explicit_null: null
            tilde_null: ~
            empty_value:
        """.trimIndent()
        val result = yaml.parseYaml()

        result.shouldNotBeNull()
        val mapping = result.shouldBeInstanceOf<YamlMapping>()
        mapping["explicit_null"] shouldBe YamlNull
        mapping["tilde_null"] shouldBe YamlNull
        mapping["empty_value"] shouldBe YamlNull
    }

    test("parse boolean values as scalars") {
        val yaml = """
            enabled: true
            disabled: false
        """.trimIndent()
        val result = yaml.parseYaml()

        result.shouldNotBeNull()
        val mapping = result.shouldBeInstanceOf<YamlMapping>()
        mapping["enabled"].shouldNotBeNull().asScalar()?.asBoolean() shouldBe true
        mapping["disabled"].shouldNotBeNull().asScalar()?.asBoolean() shouldBe false
    }

    test("parse numeric values as scalars") {
        val yaml = """
            integer: 42
            floating: 3.14
            negative: -7
        """.trimIndent()
        val result = yaml.parseYaml()

        result.shouldNotBeNull()
        val mapping = result.shouldBeInstanceOf<YamlMapping>()
        mapping["integer"].shouldNotBeNull().asScalar()?.asInt() shouldBe 42
        mapping["floating"].shouldNotBeNull().asScalar()?.asDouble() shouldBe 3.14
        mapping["negative"].shouldNotBeNull().asScalar()?.asInt() shouldBe -7
    }

    test("parse quoted strings") {
        val yaml = """
            single: 'hello world'
            double: "hello world"
        """.trimIndent()
        val result = yaml.parseYaml()

        result.shouldNotBeNull()
        val mapping = result.shouldBeInstanceOf<YamlMapping>()
        mapping["single"] shouldBe YamlScalar("hello world")
        mapping["double"] shouldBe YamlScalar("hello world")
    }

    test("parse empty document returns null") {
        val result = "".parseYaml()
        result.shouldBeNull()
    }

    test("parse scalar document") {
        val result = "hello".parseYaml()
        result.shouldNotBeNull()
        result shouldBe YamlScalar("hello")
    }

    test("parse from InputStream") {
        val yaml = "key: value"
        val result = yaml.byteInputStream().parseYaml()
        result.shouldNotBeNull()
        val mapping = result.shouldBeInstanceOf<YamlMapping>()
        mapping["key"] shouldBe YamlScalar("value")
    }

    test("parse preserves mapping order") {
        val yaml = """
            zebra: 1
            alpha: 2
            middle: 3
        """.trimIndent()
        val result = yaml.parseYaml()

        result.shouldNotBeNull()
        val mapping = result.shouldBeInstanceOf<YamlMapping>()
        mapping.keys.toList() shouldBe listOf("zebra", "alpha", "middle")
    }
})
