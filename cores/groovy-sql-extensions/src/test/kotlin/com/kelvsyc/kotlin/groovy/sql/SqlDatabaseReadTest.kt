package com.kelvsyc.kotlin.groovy.sql

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

private fun withBooks(block: (SqlDatabase) -> Unit) = withTestDatabase { ds, _ -> block(SqlDatabase.of(ds)) }

class SqlDatabaseReadTest : FunSpec({
    test("rows with a param template") {
        withBooks { db ->
            db.rows { "select title from books where author = ${param("Austen")} order by id" }
                .map { it["title"] } shouldBe listOf("Emma", "Persuasion")
        }
    }

    test("rows with sql placeholders") {
        withBooks { db ->
            db.rows(sql("select title from books where author = ? order by id", "Austen"))
                .map { it["title"] } shouldBe listOf("Emma", "Persuasion")
        }
    }

    test("injection attempts are bound, not pasted") {
        withBooks { db ->
            val evil = "x' OR '1'='1"
            db.rows { "select * from books where author = ${param(evil)}" } shouldBe emptyList()
            db.rows(sql("select * from books where author = ?", evil)) shouldBe emptyList()
        }
    }

    test("in-lists") {
        withBooks { db ->
            db.rows { "select title from books where id in (${param(listOf(1, 3))}) order by id" }
                .map { it["title"] } shouldBe listOf("Dune", "Persuasion")
        }
    }

    test("trustedSql") {
        withBooks { db -> db.rows(trustedSql("select count(*) c from books")).single()["c"] shouldBe 3 }
    }

    test("mapped rows") {
        withBooks { db ->
            db.rows(sql("select title, year from books order by id")) { "${it["title"]}:${it["year"]}" } shouldBe
                listOf("Dune:1965", "Emma:1815", "Persuasion:1817")
        }
    }

    test("firstRow with no rows is null") {
        withBooks { db -> db.firstRow { "select * from books where id = ${param(99)}" }.shouldBeNull() }
    }

    test("firstRow with several rows is the first") {
        withBooks { db ->
            val row = db.firstRow(trustedSql("select title from books order by id"))
            row.shouldNotBeNull()
            row["title"] shouldBe "Dune"
        }
    }

    test("forEachRow visits every row in order") {
        withBooks { db ->
            val titles = mutableListOf<Any?>()
            db.forEachRow(trustedSql("select title from books order by id")) { titles += it.getObject("title") }
            titles shouldBe listOf("Dune", "Emma", "Persuasion")
        }
    }

    test("an exception in forEachRow's action propagates and leaves no cursor open") {
        withTestDatabase { ds, _ ->
            ds.connection.use { conn ->
                val db = SqlDatabase.borrowing(conn)
                shouldThrow<IllegalStateException> {
                    db.forEachRow(trustedSql("select title from books order by id")) { error("stop") }
                }
                // A cursor left open on conn would hold a read lock, and this write from another connection would
                // fail with SQLITE_BUSY.
                SqlDatabase.of(ds).insertBook("After") shouldBe 1
            }
        }
    }

    test("rows takes a template block and a transform") {
        withTestDatabase { ds, _ ->
            SqlDatabase.of(ds).rows({ "select title from books where author = ${param("Austen")} order by id" }) {
                it["title"]
            } shouldBe listOf("Emma", "Persuasion")
        }
    }

    test("param(null) binds NULL, matching nothing, on both query forms") {
        withTestDatabase { ds, _ ->
            val db = SqlDatabase.of(ds)
            db.executeUpdate(trustedSql("insert into books (title, author, year) values ('Anonymous', null, 1900)"))
            db.rows { "select * from books where author = ${param(null)}" } shouldBe emptyList()
            db.rows(sql("select * from books where author = ?", null)) shouldBe emptyList()
            db.executeUpdate { "delete from books where author = ${param(null)}" } shouldBe 0
            ds.bookCount() shouldBe 4
        }
    }

    test("useRows filters and maps lazily") {
        withBooks { db ->
            var pulled = 0
            val titles = db.useRows(trustedSql("select title, author from books order by id")) { rows ->
                rows.onEach { pulled++ }.filter { it["author"] == "Austen" }.map { it["title"] }.toList()
            }
            titles shouldBe listOf("Emma", "Persuasion")
            pulled shouldBe 3
        }
    }

    test("useRows stops early without leaving a cursor open") {
        withTestDatabase { ds, _ ->
            ds.connection.use { conn ->
                val db = SqlDatabase.borrowing(conn)
                var pulled = 0
                db.useRows(trustedSql("select title from books order by id")) { rows ->
                    rows.onEach { pulled++ }.first()["title"]
                } shouldBe "Dune"
                pulled shouldBe 1
                // A cursor left open on conn would block this write from another connection.
                SqlDatabase.of(ds).insertBook("After") shouldBe 1
            }
        }
    }

    test("useRows rows stay readable after iteration") {
        withBooks { db ->
            val kept = db.useRows(trustedSql("select id, title as name from books order by id")) { it.toList() }
            kept.map { it["name"] } shouldBe listOf("Dune", "Emma", "Persuasion")
            kept.first()["id"] shouldBe 1
        }
    }

    test("useRows' sequence can't be used after the block") {
        withBooks { db ->
            val escaped = db.useRows(trustedSql("select title from books")) { it }
            shouldThrow<IllegalStateException> { escaped.toList() }
        }
    }

    test("useRows' sequence can be iterated only once") {
        withBooks { db ->
            db.useRows(trustedSql("select title from books")) { rows ->
                rows.count() shouldBe 3
                shouldThrow<IllegalStateException> { rows.count() }
            }
        }
    }

    test("useRows takes a template block") {
        withBooks { db ->
            db.useRows({ "select title from books where author = ${param("Austen")} order by id" }) { rows ->
                rows.map { it["title"] }.toList()
            } shouldBe listOf("Emma", "Persuasion")
        }
    }

    test("useRows inside a transaction sees its uncommitted rows") {
        withTestDatabase { ds, _ ->
            SqlDatabase.of(ds).transaction { tx ->
                tx.insertBook("Pending")
                tx.useRows(trustedSql("select count(*) c from books")) { it.single()["c"] } shouldBe 4
                ds.bookCount() shouldBe 3
            }
        }
    }

    test("an exception in useRows' block propagates and leaves no cursor open") {
        withTestDatabase { ds, _ ->
            ds.connection.use { conn ->
                val db = SqlDatabase.borrowing(conn)
                shouldThrow<IllegalArgumentException> {
                    db.useRows(trustedSql("select title from books")) { rows ->
                        rows.first()
                        throw IllegalArgumentException("stop")
                    }
                }
                SqlDatabase.of(ds).insertBook("After") shouldBe 1
            }
        }
    }
})
