package com.kelvsyc.kotlin.groovy

import groovy.lang.GroovyObject
import groovy.lang.GroovyObjectSupport
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlin.properties.ReadWriteProperty

private val GroovyObject.name: String by groovyProperty()
private val GroovyObject.personName: String by groovyProperty("name")
private var GroovyObject.mutableName: String by groovyProperty("name")
private val GroovyObject.maybeName: String? by groovyProperty("name")
private val GroovyObject.nameAsInt: Int by groovyProperty("name")

private class MapObject : GroovyObjectSupport(), Map<String, Any?> by mapOf("name" to "Ada")

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

    test("a GroovyObject that is also a Map delegates through Groovy property access") {
        val row = MapObject()
        val name: String by row
        val missing: String? by row
        name shouldBe "Ada"
        missing.shouldBeNull()
    }

    test("groovyProperty is a ReadWriteProperty") {
        val delegate: ReadWriteProperty<GroovyObject, String> = groovyProperty("name")
        delegate.getValue(person(), Pair<String, String>::first) shouldBe "Ada"
    }

    test("a nullable extension property reads null") {
        person(null).maybeName.shouldBeNull()
    }

    test("an extension property of the wrong type fails with ClassCastException") {
        shouldThrow<ClassCastException> { person().nameAsInt }
    }

    test("an extension property reading null into a non-null type fails") {
        shouldThrow<NullPointerException> { person(null).personName }
    }
})
