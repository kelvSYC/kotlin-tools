package com.kelvsyc.kotlin.lodash

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FunctionUtilitiesTest : FunSpec({
    test("debounce with leading:true fires immediately on first call") {
        var count = 0
        val fn = debounce<() -> Unit>({ count++ }, 100, leading = true)
        fn()
        count shouldBe 1
    }

    test("debounce with leading:false does not fire immediately") {
        var count = 0
        val fn = debounce<() -> Unit>({ count++ }, 100)
        fn()
        count shouldBe 0
    }

    test("memoize caches results by first argument") {
        var callCount = 0
        val fn = memoize<(Int) -> Int> { n -> callCount++; n * 2 }

        fn(5) shouldBe 10
        fn(5) shouldBe 10
        callCount shouldBe 1

        fn(3) shouldBe 6
        callCount shouldBe 2
    }

    test("once invokes function exactly once") {
        var count = 0
        val fn = once<() -> Unit> { count++ }

        fn(); fn(); fn()
        count shouldBe 1
    }

    test("once returns result of first invocation on subsequent calls") {
        val fn = once<() -> Int> { 42 }

        fn() shouldBe 42
        fn() shouldBe 42
    }
})
