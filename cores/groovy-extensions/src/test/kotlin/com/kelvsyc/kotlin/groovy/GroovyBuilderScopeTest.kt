package com.kelvsyc.kotlin.groovy

import groovy.lang.GroovyObject
import groovy.util.Node
import groovy.util.NodeBuilder
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.string.shouldContain
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

@GroovyBuilderDsl
private class TreeScope(scope: GroovyBuilderScope) : GroovyBuilderScope by scope {
    fun branch(name: String, block: TreeScope.() -> Unit = {}) = "branch"("name" to name) { TreeScope(this).block() }
}

private fun firstCallOrSkip(builder: GroovyObject, skip: Boolean): Any? {
    builder.withGroovyBuilder {
        if (skip) return "skipped"
        return "m"()
    }
}

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

    test("NodeBuilder builds a tree") {
        val root = NodeBuilder().withGroovyBuilder {
            "root"("id" to 1) {
                "child"("text")
                "a"("href" to "x", "link")
            }
        } as Node
        root.name() shouldBe "root"
        root.attributes() shouldBe mapOf("id" to 1)
        val (child, a) = root.children().map { it as Node }
        child.name() shouldBe "child"
        child.value() shouldBe "text"
        a.attributes() shouldBe mapOf("href" to "x")
        a.value() shouldBe "link"
    }

    test("ObjectGraphBuilder builds objects") {
        val builder = evalGroovy(
            """
            class Company { String name; List employees = [] }
            class Employee { String name }
            def b = new groovy.util.ObjectGraphBuilder()
            b.classLoader = this.class.classLoader
            b.classNameResolver = { it.capitalize() }
            b
            """.trimIndent()
        ) as GroovyObject
        val company = builder.withGroovyBuilder {
            "company"("name" to "ACME") { "employee"("name" to "Duke") }
        } as GroovyObject
        company["name"] shouldBe "ACME"
        val employees = company["employees"] as List<*>
        employees.size shouldBe 1
        (employees.single() as GroovyObject)["name"] shouldBe "Duke"
    }

    test("blocks follow the delegate the builder sets") {
        val outer = evalGroovy(
            """
            class Inner { List log; def methodMissing(String n, args) { log << "inner:" + n; null } }
            class Outer {
                List log = []
                def methodMissing(String n, args) {
                    log << "outer:" + n
                    def c = args.find { it instanceof Closure }
                    if (c) { c.delegate = new Inner(log: log); c.resolveStrategy = Closure.DELEGATE_FIRST; c() }
                    null
                }
            }
            new Outer()
            """.trimIndent()
        ) as GroovyObject
        outer.withGroovyBuilder { "a" { "b"() } }
        outer["log"] shouldBe listOf("outer:a", "inner:b")
    }

    test("blocks with an argument and no delegate use the argument") {
        val builder = evalGroovy(
            """
            class Child { List log; def methodMissing(String n, args) { log << "child:" + n; null } }
            class ArgBuilder {
                List log = []
                def methodMissing(String n, args) {
                    log << n
                    def c = args.find { it instanceof Closure }
                    if (c) c.call(new Child(log: log))
                    null
                }
            }
            new ArgBuilder()
            """.trimIndent()
        ) as GroovyObject
        builder.withGroovyBuilder { "a" { "b"() } }
        builder["log"] shouldBe listOf("a", "child:b")
    }

    test("a non-GroovyObject delegate fails clearly") {
        val builder = evalGroovy(
            """
            class Plain {
                def methodMissing(String n, args) {
                    def c = args.find { it instanceof Closure }
                    c.delegate = "plain"
                    c()
                }
            }
            new Plain()
            """.trimIndent()
        ) as GroovyObject
        val e = shouldThrow<IllegalStateException> { builder.withGroovyBuilder { "a" { "b"() } } }
        e.message shouldContain "GroovyObject"
    }

    test("block results reach the builder") {
        val builder = evalGroovy(
            """
            class Returning {
                def methodMissing(String n, args) { def c = args.find { it instanceof Closure }; c ? c() : null }
            }
            new Returning()
            """.trimIndent()
        ) as GroovyObject
        builder.withGroovyBuilder { "m" { 42 } } shouldBe 42
        builder.withGroovyBuilder { "m" { } }.shouldBeNull()
    }

    test("a typed scope can be layered on by delegation") {
        val root = NodeBuilder().withGroovyBuilder {
            "root" { TreeScope(this).run { branch("a") { branch("b") } } }
        } as Node
        val a = root.children().single() as Node
        a.attributes() shouldBe mapOf("name" to "a")
        (a.children().single() as Node).attributes() shouldBe mapOf("name" to "b")
    }

    test("blocks with neither delegate nor argument run against the enclosing scope") {
        val html = evalGroovy(
            """
            class Html {
                StringBuilder out = new StringBuilder()
                def body(Closure c) { out << "<body>"; c(); out << "</body>"; null }
                def p(String s) { out << "<p>" + s + "</p>"; null }
            }
            new Html()
            """.trimIndent()
        ) as GroovyObject
        html.withGroovyBuilder { "body" { "p"("x") } }
        html["out"].toString() shouldBe "<body><p>x</p></body>"
    }

    test("a non-GroovyObject argument without a delegate falls back to the enclosing scope") {
        val builder = evalGroovy(
            """
            class Items {
                List log = []
                def each(Closure c) { ["a", "b"].each { c.call(it) }; null }
                def note(String s) { log << s; null }
            }
            new Items()
            """.trimIndent()
        ) as GroovyObject
        builder.withGroovyBuilder { "each" { "note"("seen") } }
        builder["log"] shouldBe listOf("seen", "seen")
    }

    test("the outermost block can return from the enclosing function") {
        firstCallOrSkip(recorder(), skip = true) shouldBe "skipped"
        firstCallOrSkip(recorder(), skip = false) shouldBe "ret:m"
    }
})
