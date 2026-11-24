package com.kelvsyc.kotlin.groovy.sql

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.sqlite.SQLiteDataSource

private fun SQLiteDataSource.countBooks(): Int = connection.use { conn ->
    conn.createStatement().use { it.executeQuery("select count(*) from books").use { rs -> rs.next(); rs.getInt(1) } }
}

private fun SqlDatabase.insert(title: String) =
    executeUpdate { "insert into books (title, author, year) values (${param(title)}, 'Anon', 2000)" }

class SqlDatabaseWriteTest : FunSpec({
    test("executeUpdate counts rows") {
        withTestDatabase { ds, _ ->
            SqlDatabase.of(ds).executeUpdate {
                "update books set year = year + 1 where author = ${param("Austen")}"
            } shouldBe 2
        }
    }

    test("executeInsert returns generated keys") {
        withTestDatabase { ds, _ ->
            val keys = SqlDatabase.of(ds).executeInsert(
                sql("insert into books (title, author, year) values (?, ?, ?)", "Ulysses", "Joyce", 1922)
            )
            keys.single().single().toString() shouldBe "4"
        }
    }

    test("execute runs DDL") {
        withTestDatabase { ds, _ ->
            val db = SqlDatabase.of(ds)
            db.execute(trustedSql("create table t (x integer)")) shouldBe false
            db.rows(trustedSql("select count(*) c from t")).single()["c"] shouldBe 0
        }
    }

    test("transaction commits on normal exit") {
        withTestDatabase { ds, _ ->
            SqlDatabase.of(ds).transaction { it.insert("A") } shouldBe 1
            ds.countBooks() shouldBe 4
        }
    }

    test("transaction rolls back on an exception") {
        withTestDatabase { ds, _ ->
            shouldThrow<IllegalStateException> {
                SqlDatabase.of(ds).transaction {
                    it.insert("A")
                    error("boom")
                }
            }
            ds.countBooks() shouldBe 3
        }
    }

    test("a nested transaction joins the outer one") {
        withTestDatabase { ds, _ ->
            shouldThrow<IllegalStateException> {
                SqlDatabase.of(ds).transaction { outer ->
                    outer.insert("A")
                    outer.transaction { inner ->
                        inner.insert("B")
                        error("boom")
                    }
                }
            }
            ds.countBooks() shouldBe 3
        }
    }

    test("an inner exception caught by the outer block commits at the outer end") {
        withTestDatabase { ds, _ ->
            SqlDatabase.of(ds).transaction { outer ->
                outer.insert("A")
                runCatching {
                    outer.transaction { inner ->
                        inner.insert("B")
                        error("boom")
                    }
                }
                ds.countBooks() shouldBe 3
            }
            ds.countBooks() shouldBe 5
        }
    }
})
