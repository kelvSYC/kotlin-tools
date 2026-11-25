package com.kelvsyc.kotlin.groovy.sql

import com.kelvsyc.kotlin.groovy.closureOf
import groovy.sql.Sql
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.sqlite.SQLiteDataSource
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Proxy
import java.sql.Connection
import java.sql.PreparedStatement

private fun SQLiteDataSource.bookCount(): Int = connection.use { conn ->
    conn.createStatement().use { it.executeQuery("select count(*) from books").use { rs -> rs.next(); rs.getInt(1) } }
}

private fun SqlDatabase.insertBook(title: String) =
    executeUpdate { "insert into books (title, author, year) values (${param(title)}, 'Anon', 2000)" }

/** Wraps [connection] so that every prepared statement it creates is recorded with whether it was closed. */
private class StatementTracker(connection: Connection) {
    val closed = mutableMapOf<PreparedStatement, Boolean>()
    val connection: Connection = proxy(connection) { method, target, args ->
        val result = method.invoke(target, *args)
        if (result is PreparedStatement) track(result) else result
    }

    private fun track(statement: PreparedStatement): PreparedStatement {
        lateinit var tracked: PreparedStatement
        tracked = proxy(statement) { method, target, args ->
            if (method.name == "close") closed[tracked] = true
            method.invoke(target, *args)
        }
        closed[tracked] = false
        return tracked
    }

    private inline fun <reified T : Any> proxy(
        target: T,
        crossinline handler: (java.lang.reflect.Method, T, Array<Any?>) -> Any?,
    ): T = Proxy.newProxyInstance(T::class.java.classLoader, arrayOf(T::class.java)) { _, method, args ->
        try {
            handler(method, target, args ?: emptyArray())
        } catch (e: InvocationTargetException) {
            throw e.targetException
        }
    } as T
}

class SmallFixesTest : FunSpec({
    test("rows takes a template block and a transform") {
        withTestDatabase { ds, _ ->
            SqlDatabase.of(ds).rows({ "select title from books where author = ${param("Austen")} order by id" }) {
                it["title"]
            } shouldBe listOf("Emma", "Persuasion")
        }
    }

    test("an exception in forEachRow's action doesn't leave a cursor open on a single connection") {
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

    test("closing a borrowed database's Sql closes its cached statements but not the connection") {
        withTestDatabase { ds, _ ->
            ds.connection.use { conn ->
                val tracker = StatementTracker(conn)
                val db = SqlDatabase.borrowing(tracker.connection)
                db.groovy.setCacheStatements(true)
                db.rows(sql("select * from books where id = ?", 1)).size shouldBe 1
                db.groovy.close()
                tracker.closed.values.toList() shouldBe List(tracker.closed.size) { true }
                (tracker.closed.size > 0) shouldBe true
                conn.isClosed shouldBe false
                db.rows(sql("select * from books where id = ?", 2)).size shouldBe 1
            }
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

    test("a transaction inside the caller's own withTransaction joins it") {
        withTestDatabase { ds, _ ->
            val sql = Sql(ds)
            val db = SqlDatabase.wrapping(sql)
            shouldThrow<IllegalStateException> {
                sql.withTransaction(
                    closureOf { ->
                        db.transaction { it.insertBook("A") }
                        ds.bookCount() shouldBe 3
                        error("boom")
                    }
                )
            }
            ds.bookCount() shouldBe 3
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
})
