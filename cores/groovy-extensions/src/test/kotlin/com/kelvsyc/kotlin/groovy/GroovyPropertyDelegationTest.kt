package com.kelvsyc.kotlin.groovy

import groovy.lang.GroovyObject
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

private val GroovyObject.name: String by groovyProperty()
private val GroovyObject.personName: String by groovyProperty("name")
private var GroovyObject.mutableName: String by groovyProperty("name")

class GroovyPropertyDelegationTest : FunSpec({
    fun person(name: String? = "Ada") = evalGroovy(
        "class Person { String name }; new Person(name: n)",
        "n" to name,
    ) as GroovyObject

    test("a local property delegated to a GroovyObject reads its property") {
        val name: String by person()
        name shouldBe "Ada"
    }

    test("a local var delegated to a GroovyObject writes its property") {
        val p = person()
        var name: String by p
        name = "Grace"
        p["name"] shouldBe "Grace"
    }

    test("a nullable delegated property reads null") {
        val name: String? by person(null)
        name.shouldBeNull()
    }

    test("reading a property of the wrong type fails with ClassCastException") {
        val name: Int by person()
        shouldThrow<ClassCastException> { name + 1 }
    }

    test("reading null into a non-null property fails") {
        val name: String by person(null)
        shouldThrow<NullPointerException> { name.length }
    }

    test("an extension property reads the receiver's property of the same name") {
        person().name shouldBe "Ada"
    }

    test("an extension property can name the Groovy property explicitly") {
        person().personName shouldBe "Ada"
    }

    test("an extension var writes the receiver's property") {
        val p = person()
        p.mutableName = "Grace"
        p["name"] shouldBe "Grace"
    }
})
