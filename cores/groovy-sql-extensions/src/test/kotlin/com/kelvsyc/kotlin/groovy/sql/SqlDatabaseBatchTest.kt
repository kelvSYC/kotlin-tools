package com.kelvsyc.kotlin.groovy.sql

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

class SqlDatabaseBatchTest : FunSpec({
    test("batch inserts each added row and returns the update counts") {
        withTestDatabase { ds, _ ->
            val counts = SqlDatabase.of(ds).batch {
                listOf("A", "B", "C").forEach { title ->
                    add { "insert into books (title, author, year) values (${param(title)}, 'Anon', 2000)" }
                }
            }
            counts shouldBe listOf(1, 1, 1)
            ds.bookCount() shouldBe 6
        }
    }

    test("batchSize sends rows in chunks and counts every row") {
        withTestDatabase { ds, _ ->
            val counts = SqlDatabase.of(ds).batch(batchSize = 2) {
                (1..5).forEach { n ->
                    add { "insert into books (title, author, year) values (${param("T$n")}, 'Anon', 2000)" }
                }
            }
            counts.size shouldBe 5
            ds.bookCount() shouldBe 8
        }
    }

    test("an added statement with different text fails before anything runs") {
        withTestDatabase { ds, _ ->
            val e = shouldThrow<IllegalArgumentException> {
                SqlDatabase.of(ds).batch {
                    add { "insert into books (title, author, year) values (${param("A")}, 'Anon', 2000)" }
                    add { "delete from books where title = ${param("A")}" }
                }
            }
            e.message shouldContain "same statement"
            ds.bookCount() shouldBe 3
        }
    }

    test("values are bound, not pasted") {
        withTestDatabase { ds, _ ->
            val evil = "x'); delete from books; --"
            SqlDatabase.of(ds).batch {
                add { "insert into books (title, author, year) values (${param(evil)}, ${param(null)}, 2000)" }
            }
            ds.bookCount() shouldBe 4
            SqlDatabase.of(ds).firstRow { "select title from books where id = ${param(4)}" }!!["title"] shouldBe evil
        }
    }

    test("an empty batch runs nothing and returns no counts") {
        withTestDatabase { ds, _ -> SqlDatabase.of(ds).batch { } shouldBe emptyList() }
    }

    test("add takes sql queries with placeholders too") {
        withTestDatabase { ds, _ ->
            SqlDatabase.of(ds).batch {
                add(sql("insert into books (title, author, year) values (?, ?, ?)", "A", "Anon", 2000))
                add(sql("insert into books (title, author, year) values (?, ?, ?)", "B", "Anon", 2001))
            } shouldBe listOf(1, 1)
        }
    }

    test("a batch inside a transaction rolls back with it") {
        withTestDatabase { ds, _ ->
            shouldThrow<IllegalStateException> {
                SqlDatabase.of(ds).transaction { tx ->
                    tx.batch { add { "insert into books (title, author, year) values (${param("A")}, 'Anon', 2000)" } }
                    error("roll back")
                }
            }
            ds.bookCount() shouldBe 3
        }
    }
})
