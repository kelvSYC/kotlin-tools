package com.kelvsyc.kotlin.groovy.sql

import groovy.sql.Sql
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.maps.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import java.sql.Connection

private fun SqlDatabase.count() = groovy.firstRow("select count(*) c from books")["c"]

class SqlDatabaseOwnershipTest : FunSpec({
    test("of uses the data source") {
        withTestDatabase { ds, _ -> SqlDatabase.of(ds).count() shouldBe 3 }
    }

    test("borrowing never closes the connection") {
        withTestDatabase { ds, _ ->
            ds.connection.use { conn ->
                val db = SqlDatabase.borrowing(conn)
                db.count() shouldBe 3
                db.groovy.close()
                conn.isClosed shouldBe false
            }
        }
    }

    test("wrapping returns the same Sql") {
        withTestDatabase { ds, _ ->
            val sql = Sql(ds)
            SqlDatabase.wrapping(sql).groovy shouldBeSameInstanceAs sql
        }
    }

    test("connect closes its connection and returns the block's value") {
        withTestDatabase { _, url ->
            lateinit var conn: Connection
            val result = SqlDatabase.connect(url) { db ->
                conn = db.groovy.connection
                db.count()
            }
            result shouldBe 3
            conn.isClosed shouldBe true
        }
    }

    test("connect closes when the block throws") {
        withTestDatabase { _, url ->
            lateinit var conn: Connection
            shouldThrow<IllegalStateException> {
                SqlDatabase.connect(url) { db ->
                    conn = db.groovy.connection
                    error("boom")
                }
            }
            conn.isClosed shouldBe true
        }
    }

    test("open is closed by use") {
        withTestDatabase { _, url ->
            lateinit var conn: Connection
            SqlDatabase.open(url).use { db ->
                conn = db.groovy.connection
                db.count() shouldBe 3
            }
            conn.isClosed shouldBe true
        }
    }

    test("credentials and driver class are accepted") {
        withTestDatabase { _, url ->
            val count = SqlDatabase.connect(url, user = "", password = "", driverClassName = "org.sqlite.JDBC") {
                it.count()
            }
            count shouldBe 3
            SqlDatabase.open(url, driverClassName = "org.sqlite.JDBC").use { it.count() shouldBe 3 }
        }
    }

    test("non-owning databases aren't closeable") {
        withTestDatabase { ds, _ ->
            (SqlDatabase.of(ds) is AutoCloseable) shouldBe false
            ds.connection.use { (SqlDatabase.borrowing(it) is AutoCloseable) shouldBe false }
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
})
