package com.kelvsyc.kotlin.groovy

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ClosureOfTest : FunSpec({
    test("parameter counts match lambda arity") {
        closureOf { -> 42 }.maximumNumberOfParameters shouldBe 0
        closureOf { a: Int -> a }.maximumNumberOfParameters shouldBe 1
        closureOf { a: Int, b: Int -> a + b }.maximumNumberOfParameters shouldBe 2
        closureOf { a: Int, b: Int, c: Int -> a + b + c }.maximumNumberOfParameters shouldBe 3
    }

    test("Groovy calls each arity") {
        evalGroovy("c()", "c" to closureOf { -> 42 }) shouldBe 42
        evalGroovy("c(5)", "c" to closureOf { a: Int -> a * 2 }) shouldBe 10
        evalGroovy("c(1, 2)", "c" to closureOf { a: Int, b: Int -> a + b }) shouldBe 3
        evalGroovy("c(1, 2, 3)", "c" to closureOf { a: Int, b: Int, c: Int -> a + b + c }) shouldBe 6
    }

    test("Map.each passes the entry to a one-parameter closure") {
        val seen = mutableListOf<Pair<String, Int>>()
        evalGroovy("[a: 1].each(c)", "c" to closureOf { e: Map.Entry<String, Int> -> seen += e.key to e.value })
        seen shouldBe listOf("a" to 1)
    }

    test("Map.each passes key and value to a two-parameter closure") {
        val seen = mutableListOf<Pair<String, Int>>()
        evalGroovy("[a: 1].each(c)", "c" to closureOf { k: String, v: Int -> seen += k to v })
        seen shouldBe listOf("a" to 1)
    }

    test("Unit-returning lambdas give Groovy null") {
        evalGroovy("[1, 2].collect(c)", "c" to closureOf { _: Int -> }) shouldBe listOf(null, null)
    }

    test("a Groovy method taking a Closure accepts one") {
        val script = "def twice(Closure f) { f(1) + f(2) }; twice(c)"
        evalGroovy(script, "c" to closureOf { x: Int -> x * 10 }) shouldBe 30
    }
})
