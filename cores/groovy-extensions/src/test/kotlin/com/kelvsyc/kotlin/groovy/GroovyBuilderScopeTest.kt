package com.kelvsyc.kotlin.groovy

import groovy.lang.GroovyObject
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs

private fun recorder() = evalGroovy(
    """
    class Recorder {
        List calls = []
        String title
        def methodMissing(String name, args) { calls << [name, args as List]; "ret:" + name }
    }
    new Recorder()
    """.trimIndent()
) as GroovyObject

@Suppress("UNCHECKED_CAST")
private fun GroovyObject.calls() = this["calls"] as List<List<Any?>>

class GroovyBuilderScopeTest : FunSpec({
    test("positional arguments pass through") {
        val rec = recorder()
        rec.withGroovyBuilder { "m"("a", 1) }
        rec.calls() shouldBe listOf(listOf("m", listOf("a", 1)))
    }

    test("named arguments go first as one map") {
        val rec = recorder()
        rec.withGroovyBuilder { "m"("a", "k" to 1, "b", "j" to 2) }
        val args = rec.calls().single()[1] as List<*>
        args shouldBe listOf(mapOf("k" to 1, "j" to 2), "a", "b")
        (args[0] as Map<*, *>).keys.toList() shouldBe listOf("k", "j")
    }

    test("no named arguments means no map") {
        val rec = recorder()
        rec.withGroovyBuilder { "m"() }
        rec.calls() shouldBe listOf(listOf("m", emptyList<Any?>()))
    }

    test("a call returns the builder's result") {
        recorder().withGroovyBuilder { "m"() } shouldBe "ret:m"
    }

    test("property access reads and writes the delegate") {
        val rec = recorder()
        rec.withGroovyBuilder {
            this["title"] = "x"
            this["title"] shouldBe "x"
        }
        rec["title"] shouldBe "x"
    }

    test("withGroovyBuilder returns the block's result") {
        recorder().withGroovyBuilder { 42 } shouldBe 42
    }

    test("of exposes the delegate") {
        val rec = recorder()
        GroovyBuilderScope.of(rec).delegate shouldBeSameInstanceAs rec
    }
})
