package com.kelvsyc.kotlin.groovy

import groovy.json.JsonBuilder
import groovy.xml.MarkupBuilder
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.io.StringWriter
import java.nio.file.Files

class GroovyBuildersTest : FunSpec({
    test("MarkupBuilder builds nested markup") {
        val writer = StringWriter()
        val builder = MarkupBuilder(writer).apply { setDoubleQuotes(true) }
        builder.withGroovyBuilder {
            "html" {
                "body"("class" to "main") {
                    listOf("Ada", "Grace").forEach { "p"(it) }
                    "a"("href" to "https://example.com", "link")
                }
            }
        }
        writer.toString().replace(Regex(">\\s+<"), "><").trim() shouldBe
            """<html><body class="main"><p>Ada</p><p>Grace</p><a href="https://example.com">link</a></body></html>"""
    }

    test("JsonBuilder builds a nested object with a mapped array") {
        val json = JsonBuilder()
        json.withGroovyBuilder {
            "library" {
                "name"("City")
                val authors = listOf("Ada" to 1815, "Grace" to 1906)
                "authors"(authors.map { (name, born) -> mapOf("name" to name, "born" to born) })
            }
        }
        json.toString() shouldBe
            """{"library":{"name":"City","authors":[{"name":"Ada","born":1815},{"name":"Grace","born":1906}]}}"""
    }

    test("AntBuilder runs tasks with nested elements") {
        val dir = Files.createTempDirectory("groovy-builders")
        try {
            val src = dir.resolve("src")
            val out = dir.resolve("out")
            val ant = groovy.ant.AntBuilder()
            ant.withGroovyBuilder { "mkdir"("dir" to src.toString()) }
            Files.writeString(src.resolve("a.txt"), "hello")
            ant.withGroovyBuilder {
                "copy"("todir" to out.toString()) { "fileset"("dir" to src.toString()) }
            }
            Files.readString(out.resolve("a.txt")) shouldBe "hello"
        } finally {
            dir.toFile().deleteRecursively()
        }
    }
})
