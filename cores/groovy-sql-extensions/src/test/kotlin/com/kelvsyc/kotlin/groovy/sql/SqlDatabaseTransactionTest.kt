package com.kelvsyc.kotlin.groovy.sql

import com.kelvsyc.kotlin.groovy.closureOf
import groovy.sql.Sql
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.io.IOException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class SqlDatabaseTransactionTest : FunSpec({
    test("transaction commits on normal exit") {
        withTestDatabase { ds, _ ->
            SqlDatabase.of(ds).transaction { it.insertBook("A") } shouldBe 1
            ds.bookCount() shouldBe 4
        }
    }

    test("transaction rolls back on an exception") {
        withTestDatabase { ds, _ ->
            shouldThrow<IllegalStateException> {
                SqlDatabase.of(ds).transaction {
                    it.insertBook("A")
                    error("boom")
                }
            }
            ds.bookCount() shouldBe 3
        }
    }

    test("a nested transaction joins the outer one") {
        withTestDatabase { ds, _ ->
            shouldThrow<IllegalStateException> {
                SqlDatabase.of(ds).transaction { outer ->
                    outer.insertBook("A")
                    outer.transaction { inner ->
                        inner.insertBook("B")
                        error("boom")
                    }
                }
            }
            ds.bookCount() shouldBe 3
        }
    }

    test("an inner exception caught by the outer block commits at the outer end") {
        withTestDatabase { ds, _ ->
            SqlDatabase.of(ds).transaction { outer ->
                outer.insertBook("A")
                runCatching {
                    outer.transaction { inner ->
                        inner.insertBook("B")
                        error("boom")
                    }
                }
                ds.bookCount() shouldBe 3
            }
            ds.bookCount() shouldBe 5
        }
    }

    test("another thread's operations don't join a running transaction") {
        withTestDatabase { ds, _ ->
            val db = SqlDatabase.of(ds)
            val inserted = CountDownLatch(1)
            val checked = CountDownLatch(1)
            var seenOutside = -1
            var seenInOwnTransaction = -1
            val worker = thread {
                inserted.await(5, TimeUnit.SECONDS)
                seenOutside = db.rows(trustedSql("select count(*) c from books")).single()["c"] as Int
                seenInOwnTransaction = db.transaction { tx ->
                    tx.rows(trustedSql("select count(*) c from books")).single()["c"] as Int
                }
                checked.countDown()
            }
            shouldThrow<IllegalStateException> {
                db.transaction { tx ->
                    tx.executeUpdate(trustedSql("insert into books (title, author, year) values ('X', 'Y', 2000)"))
                    inserted.countDown()
                    checked.await(5, TimeUnit.SECONDS)
                    error("roll back")
                }
            }
            worker.join()
            seenOutside shouldBe 3
            seenInOwnTransaction shouldBe 3
            ds.bookCount() shouldBe 3
        }
    }

    test("a checked exception from a transaction propagates unwrapped") {
        withTestDatabase { ds, _ ->
            shouldThrow<IOException> { SqlDatabase.of(ds).transaction<Unit> { throw IOException("io") } }
            ds.bookCount() shouldBe 3
        }
    }

    test("a transaction on a borrowed connection inside the caller's transaction joins it") {
        withTestDatabase { ds, _ ->
            ds.connection.use { conn ->
                conn.autoCommit = false
                val db = SqlDatabase.borrowing(conn)
                db.executeUpdate(trustedSql("insert into books (title, author, year) values ('Mine', 'Me', 2000)"))
                db.transaction {
                    it.executeUpdate(trustedSql("insert into books (title, author, year) values ('Tx', 'Me', 2000)"))
                }
                ds.bookCount() shouldBe 3
                shouldThrow<IllegalStateException> { db.transaction { error("boom") } }
                conn.rollback()
            }
            ds.bookCount() shouldBe 3
        }
    }

    test("two databases wrapping one Sql share their transaction") {
        withTestDatabase { ds, _ ->
            val sql = Sql(ds)
            val a = SqlDatabase.wrapping(sql)
            val b = SqlDatabase.wrapping(sql)
            a.transaction {
                a.insertBook("A")
                runCatching {
                    b.transaction {
                        b.insertBook("B")
                        error("boom")
                    }
                }
                ds.bookCount() shouldBe 3
            }
            ds.bookCount() shouldBe 5
        }
    }

    test("wrapping a transaction's Sql joins the transaction") {
        withTestDatabase { ds, _ ->
            val db = SqlDatabase.of(ds)
            db.transaction { tx ->
                SqlDatabase.wrapping(tx.groovy).transaction { it.insertBook("A") }
                ds.bookCount() shouldBe 3
            }
            ds.bookCount() shouldBe 4
        }
    }

    test("with auto-commit off by default and cached statements, a transaction still commits") {
        withTestDatabase { ds, _ ->
            // The pinned connection's read stays open (auto-commit off, nobody commits it); in SQLite's default
            // journal mode that would block every other connection's writes, so use WAL, as a server database would.
            ds.connection.use { it.createStatement().use { st -> st.execute("pragma journal_mode=wal") } }
            val sql = Sql(ds.withAutoCommitOff())
            sql.setCacheStatements(true)
            val db = SqlDatabase.wrapping(sql)
            db.rows(trustedSql("select count(*) c from books")).single()["c"] shouldBe 3
            db.transaction { it.insertBook("A") }
            sql.close()
            ds.bookCount() shouldBe 4
        }
    }

    test("with auto-commit off by default, a transaction inside cacheConnection still commits") {
        withTestDatabase { ds, _ ->
            val sql = Sql(ds.withAutoCommitOff())
            val db = SqlDatabase.wrapping(sql)
            sql.cacheConnection(closureOf { -> db.transaction { it.insertBook("A") } })
            ds.bookCount() shouldBe 4
        }
    }

    test("a transaction leaves a connection pinned by cached statements open") {
        withTestDatabase { ds, _ ->
            val sql = Sql(ds)
            sql.setCacheStatements(true)
            val db = SqlDatabase.wrapping(sql)
            db.rows(trustedSql("select count(*) c from books")).single()["c"] shouldBe 3
            db.transaction { it.insertBook("A") }
            db.rows(trustedSql("select count(*) c from books")).single()["c"] shouldBe 4
            sql.close()
        }
    }

    test("a transaction leaves the caller's cacheConnection connection open") {
        withTestDatabase { ds, _ ->
            val sql = Sql(ds)
            val db = SqlDatabase.wrapping(sql)
            var after = -1
            sql.cacheConnection(
                closureOf { ->
                    db.transaction { it.insertBook("A") }
                    after = sql.firstRow("select count(*) c from books")["c"] as Int
                }
            )
            after shouldBe 4
        }
    }
})
