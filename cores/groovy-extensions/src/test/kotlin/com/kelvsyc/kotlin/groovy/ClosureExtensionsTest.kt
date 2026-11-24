package com.kelvsyc.kotlin.groovy

import groovy.lang.Closure
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ClosureExtensionsTest : FunSpec({
    @Suppress("UNCHECKED_CAST")
    fun groovyClosure(source: String) = evalGroovy(source) as Closure<Any?>

    test("invoke with no arguments") {
        groovyClosure("{ -> 7 }")() shouldBe 7
    }

    test("invoke with several arguments") {
        groovyClosure("{ a, b -> a + b }")(2, 3) shouldBe 5
    }

    test("invoke with a single null argument") {
        groovyClosure("{ x -> x == null }")(null) shouldBe true
    }

    test("toFunction1 and toFunction2 call the closure") {
        groovyClosure("{ x -> x * 2 }").toFunction1<Int, Any?>()(21) shouldBe 42
        groovyClosure("{ a, b -> a - b }").toFunction2<Int, Int, Any?>()(5, 3) shouldBe 2
    }

    test("toFunction0 and toFunction3 call the closure") {
        groovyClosure("{ -> 'x' }").toFunction0()() shouldBe "x"
        groovyClosure("{ a, b, c -> a + b + c }").toFunction3<Int, Int, Int, Any?>()(1, 2, 3) shouldBe 6
    }
})
