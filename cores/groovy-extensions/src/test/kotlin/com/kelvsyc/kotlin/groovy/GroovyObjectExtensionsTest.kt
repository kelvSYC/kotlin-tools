package com.kelvsyc.kotlin.groovy

import groovy.lang.GroovyObject
import groovy.lang.MissingPropertyException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class GroovyObjectExtensionsTest : FunSpec({
    fun person() = evalGroovy(
        """
        class Person {
            String name
            String greet() { "hi " + name }
            String greet(String g) { g + " " + name }
            String describe(Object o) { o == null ? "null" : o.toString() }
            def apply(Closure c) { c(name) }
        }
        new Person(name: 'Ada')
        """.trimIndent()
    ) as GroovyObject

    test("get reads a property") {
        person()["name"] shouldBe "Ada"
    }

    test("set writes a property") {
        val p = person()
        p["name"] = "Grace"
        p["name"] shouldBe "Grace"
    }

    test("callMethod with no arguments") {
        person().callMethod("greet") shouldBe "hi Ada"
    }

    test("callMethod with one argument") {
        person().callMethod("greet", "hello") shouldBe "hello Ada"
    }

    test("callMethod with a single null argument") {
        person().callMethod("describe", null) shouldBe "null"
    }

    test("callMethod passing a closure") {
        person().callMethod("apply", closureOf { n: String -> n.length }) shouldBe 3
    }

    test("unknown property fails with MissingPropertyException") {
        shouldThrow<MissingPropertyException> { person()["nope"] }
    }
})
