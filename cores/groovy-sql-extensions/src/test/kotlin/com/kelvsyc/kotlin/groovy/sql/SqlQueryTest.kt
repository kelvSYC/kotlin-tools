package com.kelvsyc.kotlin.groovy.sql

import groovy.sql.InParameter
import groovy.sql.Sql
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf

class SqlQueryTest : FunSpec({
    test("param becomes a bound value") {
        val q = query { "select * from books where author = ${param("Austen")}" }
        q.text shouldBe "select * from books where author = ?"
        q.parameters shouldBe listOf("Austen")
    }

    test("collections expand to one value each") {
        val q = query { "select * from books where id in (${param(listOf(1, 2, 3))})" }
        q.text shouldBe "select * from books where id in (?, ?, ?)"
        q.parameters shouldBe listOf(1, 2, 3)
        query { "in (${param(arrayOf(1, 2))})" }.parameters shouldBe listOf(1, 2)
        query { "in (${param(intArrayOf(4, 5))})" }.parameters shouldBe listOf(4, 5)
    }

    test("byte arrays bind as one value") {
        val bytes = byteArrayOf(1, 2)
        query { "x = ${param(bytes)}" }.parameters.single() shouldBe bytes
    }

    test("empty collection fails") {
        shouldThrow<IllegalArgumentException> { query { "in (${param(emptyList<Int>())})" } }
    }

    test("expanded values are rejected") {
        shouldThrow<IllegalArgumentException> { query { "x = ${param(Sql.expand("x"))}" } }.message shouldContain "trustedSql"
        shouldThrow<IllegalArgumentException> { query { "in (${param(listOf(Sql.expand("x")))})" } }
        shouldThrow<IllegalArgumentException> { sql("select ?", Sql.expand("x")) }.message shouldContain "trustedSql"
    }

    test("typed parameters pass through") {
        query { "x = ${param(Sql.VARCHAR(null))}" }.parameters.single().shouldBeInstanceOf<InParameter>()
    }

    test("sql keeps text and parameters as given") {
        val q = sql("select * from books where author = ? and year > ?", "Austen", 1800)
        q.text shouldBe "select * from books where author = ? and year > ?"
        q.parameters shouldBe listOf("Austen", 1800)
        sql("x in (?)", listOf(1, 2)).parameters shouldBe listOf(listOf(1, 2))
    }

    test("trustedSql has no parameters") {
        val q = trustedSql("select count(*) from books")
        q.text shouldBe "select count(*) from books"
        q.parameters shouldBe emptyList()
    }

    test("a marker from another query fails") {
        var leaked = ""
        query {
            leaked = param(1)
            leaked
        }
        shouldThrow<IllegalStateException> { query { "x $leaked" } }
    }
})
