package com.kelvsyc.kotlin.groovy.sql

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
})
