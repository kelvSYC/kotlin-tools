package com.kelvsyc.kotlin.lodash

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class StringUtilitiesTest : FunSpec({
    test("toCamelCase converts kebab-case") {
        "foo-bar".toCamelCase() shouldBe "fooBar"
    }

    test("toCamelCase converts space-separated words") {
        "Foo Bar".toCamelCase() shouldBe "fooBar"
    }

    test("toKebabCase converts camelCase") {
        "fooBar".toKebabCase() shouldBe "foo-bar"
    }

    test("toKebabCase converts space-separated words") {
        "Foo Bar".toKebabCase() shouldBe "foo-bar"
    }

    test("toSnakeCase converts camelCase") {
        "fooBar".toSnakeCase() shouldBe "foo_bar"
    }

    test("toSnakeCase converts space-separated words") {
        "Foo Bar".toSnakeCase() shouldBe "foo_bar"
    }

    test("truncated shortens long strings") {
        "Hello, World!".truncated(8) shouldBe "Hello, …"
    }

    test("truncated leaves short strings unchanged") {
        "Hi".truncated(10) shouldBe "Hi"
    }

    test("truncated uses custom omission") {
        "Hello, World!".truncated(9, omission = "...") shouldBe "Hello,..."
    }

    test("htmlEscaped converts special characters") {
        "<b>Hello & 'world'</b>".htmlEscaped() shouldBe "&lt;b&gt;Hello &amp; &#39;world&#39;&lt;/b&gt;"
    }

    test("htmlUnescaped reverses htmlEscaped") {
        val original = "<b>Hello & 'world'</b>"
        original.htmlEscaped().htmlUnescaped() shouldBe original
    }
})
