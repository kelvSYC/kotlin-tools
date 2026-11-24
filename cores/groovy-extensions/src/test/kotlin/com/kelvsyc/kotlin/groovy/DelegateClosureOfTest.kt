package com.kelvsyc.kotlin.groovy

import groovy.lang.Closure
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class DelegateClosureOfTest : FunSpec({
    class Target {
        var name: String? = null
    }

    test("resolve strategy is DELEGATE_FIRST") {
        delegateClosureOf<Target> { }.resolveStrategy shouldBe Closure.DELEGATE_FIRST
    }

    test("runs the action on the delegate when called with no arguments") {
        val target = Target()
        val c = delegateClosureOf<Target> { name = "set" }
        c.delegate = target
        c.call()
        target.name shouldBe "set"
    }

    test("runs the action on the delegate when called with the target as argument") {
        val target = Target()
        val c = delegateClosureOf<Target> { name = "set" }
        c.delegate = target
        c.call(target)
        target.name shouldBe "set"
    }

    test("Groovy's with sets the delegate itself") {
        val target = Target()
        evalGroovy("t.with(c)", "t" to target, "c" to delegateClosureOf<Target> { name = "with" })
        target.name shouldBe "with"
    }

    test("a delegate of the wrong type fails with ClassCastException") {
        val c = delegateClosureOf<Target> { }
        c.delegate = "not a target"
        shouldThrow<ClassCastException> { c.call() }
    }
})
