package com.kelvsyc.kotlin.groovy.sql

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.sqlite.SQLiteDataSource
import java.io.IOException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

private fun SQLiteDataSource.count(): Int = connection.use { conn ->
    conn.createStatement().use { it.executeQuery("select count(*) from books").use { rs -> rs.next(); rs.getInt(1) } }
}

class ReviewFixesTest : FunSpec({
    test("a quoted param marker is rejected instead of pasted") {
        withTestDatabase { ds, _ ->
            val db = SqlDatabase.of(ds)
            val e = shouldThrow<IllegalArgumentException> {
                db.rows { "select title from books where author = '${param("x' OR '1'='1")}'" }
            }
            e.message shouldContain "quote"
        }
    }

    test("param(null) binds NULL, matching nothing, on both query forms") {
        withTestDatabase { ds, _ ->
            val db = SqlDatabase.of(ds)
            db.executeUpdate(trustedSql("insert into books (title, author, year) values ('Anonymous', null, 1900)"))
            db.rows { "select * from books where author = ${param(null)}" } shouldBe emptyList()
            db.rows(sql("select * from books where author = ?", null)) shouldBe emptyList()
            db.executeUpdate { "delete from books where author = ${param(null)}" } shouldBe 0
            ds.count() shouldBe 4
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
            ds.count() shouldBe 3
        }
    }

    test("a checked exception from a transaction propagates unwrapped") {
        withTestDatabase { ds, _ ->
            shouldThrow<IOException> { SqlDatabase.of(ds).transaction<Unit> { throw IOException("io") } }
            ds.count() shouldBe 3
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
                ds.count() shouldBe 3
                shouldThrow<IllegalStateException> { db.transaction { error("boom") } }
                conn.rollback()
            }
            ds.count() shouldBe 3
        }
    }
})
