package com.kelvsyc.kotlin.groovy.sql

import org.sqlite.SQLiteDataSource
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Proxy
import java.nio.file.Files
import java.sql.Connection
import java.sql.PreparedStatement
import javax.sql.DataSource

/**
 * Runs [block] against a fresh SQLite database in a temporary file (an in-memory SQLite database is private to one
 * connection), holding `books(id, title, author, year)` with three rows. The file is deleted afterwards.
 */
fun withTestDatabase(block: (dataSource: SQLiteDataSource, url: String) -> Unit) {
    val file = Files.createTempFile("groovy-sql-extensions", ".db")
    try {
        val url = "jdbc:sqlite:$file"
        val dataSource = SQLiteDataSource().apply { setUrl(url) }
        dataSource.connection.use { connection ->
            connection.createStatement().use { statement ->
                statement.executeUpdate(
                    "create table books (id integer primary key, title text, author text, year integer)"
                )
                statement.executeUpdate(
                    "insert into books values (1, 'Dune', 'Herbert', 1965), (2, 'Emma', 'Austen', 1815), " +
                        "(3, 'Persuasion', 'Austen', 1817)"
                )
            }
        }
        block(dataSource, url)
    } finally {
        Files.deleteIfExists(file)
    }
}

/** The number of rows in `books`, read through a fresh connection. */
fun SQLiteDataSource.bookCount(): Int = connection.use { conn ->
    conn.createStatement().use { it.executeQuery("select count(*) from books").use { rs -> rs.next(); rs.getInt(1) } }
}

fun SqlDatabase.insertBook(title: String) =
    executeUpdate { "insert into books (title, author, year) values (${param(title)}, 'Anon', 2000)" }

/** A data source whose connections start with auto-commit off, as some pools are configured. */
fun SQLiteDataSource.withAutoCommitOff(): DataSource {
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
class StatementTracker(connection: Connection) {
    val closed = mutableMapOf<PreparedStatement, Boolean>()
    val connection: Connection = proxy(connection) { method, target, args ->
        val result = method.invoke(target, *args)
        if (result is PreparedStatement) track(result) else result
    }

    fun track(statement: PreparedStatement): PreparedStatement {
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
