package com.kelvsyc.kotlin.groovy

import groovy.lang.GroovySystem
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

class GroovyVersionTest : FunSpec({
    test("runs on the Groovy major this suite is for") {
        val expected = System.getProperty("groovy.expected.major")
        expected.shouldNotBeNull()
        GroovySystem.getVersion().substringBefore('.') shouldBe expected
    }

    test("evalGroovy evaluates with bindings") {
        evalGroovy("x + 1", "x" to 41) shouldBe 42
    }

    test("only one Groovy is on the classpath") {
        GroovySystem::class.java.classLoader.getResources("groovy/lang/GroovySystem.class").toList().size shouldBe 1
    }
})
