package com.kelvsyc.kotlin.groovy.sql

import com.kelvsyc.kotlin.groovy.closureOf
import groovy.sql.Sql
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.maps.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import org.sqlite.SQLiteDataSource
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Proxy
import java.sql.Connection
import java.sql.PreparedStatement
import javax.sql.DataSource

private fun SQLiteDataSource.bookCount(): Int = connection.use { conn ->
    conn.createStatement().use { it.executeQuery("select count(*) from books").use { rs -> rs.next(); rs.getInt(1) } }
}

private fun SqlDatabase.insertBook(title: String) =
    executeUpdate { "insert into books (title, author, year) values (${param(title)}, 'Anon', 2000)" }

/** A data source whose connections start with auto-commit off, as some pools are configured. */
private fun SQLiteDataSource.withAutoCommitOff(): DataSource {
    val delegate = this
    return Proxy.newProxyInstance(DataSource::class.java.classLoader, arrayOf(DataSource::class.java)) { _, method, args ->
        try {
            val result = method.invoke(delegate, *(args ?: emptyArray()))
            if (result is Connection) result.autoCommit = false
            result
        } catch (e: InvocationTargetException) {
            throw e.targetException
        }
    } as DataSource
}

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
                tracker.closed.shouldNotBeEmpty()
                tracker.closed.values.toList() shouldBe List(tracker.closed.size) { true }
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
