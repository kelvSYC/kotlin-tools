package com.kelvsyc.kotlin.guava.collect

import com.google.common.collect.MutableClassToInstanceMap
import com.google.common.reflect.MutableTypeToInstanceMap
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

class ClassToInstanceMapsTest : FunSpec({

    context("ClassToInstanceMap.getTyped and ImmutableClassToInstanceMap.Builder.put") {
        test("put and getTyped round-trip via builder") {
            val map = buildClassToInstanceMap<Any> {
                put("hello")
                put(42)
            }
            map.getTyped<String>() shouldBe "hello"
            map.getTyped<Int>() shouldBe 42
        }
        test("getTyped returns null for absent type") {
            val map = buildClassToInstanceMap<Any> { put("hello") }
            map.getTyped<Int>().shouldBeNull()
        }
    }

    context("MutableClassToInstanceMap.put extension") {
        test("put and getTyped round-trip") {
            val map = MutableClassToInstanceMap.create<Any>()
            map.put("world")
            map.getTyped<String>() shouldBe "world"
        }
    }

    context("TypeToInstanceMap.getTyped") {
        test("getTyped round-trip via MutableTypeToInstanceMap") {
            val map = MutableTypeToInstanceMap<Any>()
            map.put("hello")
            map.put(99)
            map.getTyped<String>() shouldBe "hello"
            map.getTyped<Int>() shouldBe 99
        }
        test("getTyped returns null for absent type") {
            val map = MutableTypeToInstanceMap<Any>()
            map.put("hi")
            map.getTyped<Int>().shouldBeNull()
        }
    }

    context("buildTypeToInstanceMap") {
        test("builds immutable TypeToInstanceMap") {
            val map = buildTypeToInstanceMap<Any> {}
            map.getTyped<String>().shouldBeNull()
        }
    }
})
