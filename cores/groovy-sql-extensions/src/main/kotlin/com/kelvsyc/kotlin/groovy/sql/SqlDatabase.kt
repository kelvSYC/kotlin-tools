package com.kelvsyc.kotlin.groovy.sql

import com.kelvsyc.kotlin.groovy.closureOf
import groovy.sql.GroovyResultSet
import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import java.sql.Connection
import javax.sql.DataSource

/**
 * Safe, Kotlin-idiomatic access to a database through `groovy.sql.Sql`.
 *
 * How a `SqlDatabase` is obtained decides who owns its connection, and only [open] returns one that can be closed:
 * [of] (a pooled [DataSource]; recommended), [connect] (owned, closed when its block ends), [open] (owned, an
 * [OwnedSqlDatabase] to `use`), [borrowing] (the caller's connection, never closed here) and [wrapping] (an existing
 * [Sql], which the caller keeps owning).
 *
 * Like [Sql], one `SqlDatabase` isn't meant for concurrent transactions from several threads.
 */
sealed interface SqlDatabase {
    /**
     * The underlying [Sql], for anything this wrapper doesn't offer. For a database made by [borrowing], closing it
     * leaves the borrowed connection open.
     */
    val groovy: Sql

    /**
     * Runs [query] and returns all its rows.
     */
    fun rows(query: SqlQuery): List<GroovyRowResult> = when (val form = query.form) {
        is SqlQuery.Form.Template -> groovy.rows(form.gstring)
        is SqlQuery.Form.Text ->
            if (form.parameters.isEmpty()) groovy.rows(form.text) else groovy.rows(form.text, form.parameters)
    }

    /**
     * Runs the query built by [block] (see [query]) and returns all its rows.
     */
    fun rows(block: SqlQueryScope.() -> String): List<GroovyRowResult> = rows(query(block))

    /**
     * Runs [query] and returns its rows, each mapped by [transform].
     */
    fun <T> rows(query: SqlQuery, transform: (GroovyRowResult) -> T): List<T> = rows(query).map(transform)

    /**
     * Runs [query] and returns its first row, or `null` when there is none.
     */
    fun firstRow(query: SqlQuery): GroovyRowResult? = when (val form = query.form) {
        is SqlQuery.Form.Template -> groovy.firstRow(form.gstring)
        is SqlQuery.Form.Text ->
            if (form.parameters.isEmpty()) groovy.firstRow(form.text) else groovy.firstRow(form.text, form.parameters)
    }

    /**
     * Runs the query built by [block] (see [query]) and returns its first row, or `null` when there is none.
     */
    fun firstRow(block: SqlQueryScope.() -> String): GroovyRowResult? = firstRow(query(block))

    /**
     * Runs [query] and passes each row to [action] while the result set is open, without loading all rows first. An
     * exception from [action] stops the iteration and propagates.
     */
    fun forEachRow(query: SqlQuery, action: (GroovyResultSet) -> Unit) {
        val closure = closureOf { row: GroovyResultSet -> action(row) }
        when (val form = query.form) {
            is SqlQuery.Form.Template -> groovy.eachRow(form.gstring, closure)
            is SqlQuery.Form.Text ->
                if (form.parameters.isEmpty()) {
                    groovy.eachRow(form.text, closure)
                } else {
                    groovy.eachRow(form.text, form.parameters, closure)
                }
        }
    }

    /**
     * Runs [query], an `insert`, `update` or `delete`, and returns the number of rows it changed.
     */
    fun executeUpdate(query: SqlQuery): Int = when (val form = query.form) {
        is SqlQuery.Form.Template -> groovy.executeUpdate(form.gstring)
        is SqlQuery.Form.Text ->
            if (form.parameters.isEmpty()) {
                groovy.executeUpdate(form.text)
            } else {
                groovy.executeUpdate(form.text, form.parameters)
            }
    }

    /**
     * Runs the statement built by [block] (see [query]); see the [SqlQuery] overload.
     */
    fun executeUpdate(block: SqlQueryScope.() -> String): Int = executeUpdate(query(block))

    /**
     * Runs [query], any statement; returns `true` when its first result is a result set, as `Sql.execute` does.
     */
    fun execute(query: SqlQuery): Boolean = when (val form = query.form) {
        is SqlQuery.Form.Template -> groovy.execute(form.gstring)
        is SqlQuery.Form.Text ->
            if (form.parameters.isEmpty()) groovy.execute(form.text) else groovy.execute(form.text, form.parameters)
    }

    /**
     * Runs the statement built by [block] (see [query]); see the [SqlQuery] overload.
     */
    fun execute(block: SqlQueryScope.() -> String): Boolean = execute(query(block))

    /**
     * Runs [query], an `insert`, and returns the keys it generated: one list of key columns per inserted row.
     */
    fun executeInsert(query: SqlQuery): List<List<Any?>> = when (val form = query.form) {
        is SqlQuery.Form.Template -> groovy.executeInsert(form.gstring)
        is SqlQuery.Form.Text ->
            if (form.parameters.isEmpty()) {
                groovy.executeInsert(form.text)
            } else {
                groovy.executeInsert(form.text, form.parameters)
            }
    }

    /**
     * Runs the insert built by [block] (see [query]); see the [SqlQuery] overload.
     */
    fun executeInsert(block: SqlQueryScope.() -> String): List<List<Any?>> = executeInsert(query(block))

    /**
     * Runs [block] in a transaction: committed when it returns, rolled back when it throws (the exception propagates).
     * A `transaction` inside another joins it: it neither commits nor rolls back by itself, and only the outermost
     * block does. If the outer block catches an inner block's exception and returns normally, everything done so far,
     * including the inner block's work, is committed.
     */
    fun <T> transaction(block: (SqlDatabase) -> T): T

    companion object {
        /**
         * A database over a (typically pooled) [DataSource]: each operation borrows a connection and returns it, so
         * there is nothing to close. The recommended way in.
         */
        fun of(dataSource: DataSource): SqlDatabase = DefaultSqlDatabase(Sql(dataSource))

        /**
         * A database over the caller's [connection], which it never closes; the caller keeps owning it.
         */
        fun borrowing(connection: Connection): SqlDatabase = DefaultSqlDatabase(NonClosingSql(connection))

        /**
         * A database over an existing [sql], which the caller keeps owning.
         */
        fun wrapping(sql: Sql): SqlDatabase = DefaultSqlDatabase(sql)

        /**
         * Opens a connection to [url], owned by the returned database: close it, typically with `use`. [user] and
         * [password] are given together or not at all; [driverClassName] loads a JDBC driver first.
         */
        fun open(
            url: String,
            user: String? = null,
            password: String? = null,
            driverClassName: String? = null,
        ): OwnedSqlDatabase {
            require((user == null) == (password == null)) { "user and password must be given together" }
            val sql = when {
                user == null && driverClassName == null -> Sql.newInstance(url)
                user == null -> Sql.newInstance(url, driverClassName)
                driverClassName == null -> Sql.newInstance(url, user, password)
                else -> Sql.newInstance(url, user, password, driverClassName)
            }
            return DefaultOwnedSqlDatabase(sql)
        }

        /**
         * Opens a connection to [url] (see [open]), runs [block] with it and closes it, also when [block] throws.
         * Returns the block's result.
         */
        fun <T> connect(
            url: String,
            user: String? = null,
            password: String? = null,
            driverClassName: String? = null,
            block: (SqlDatabase) -> T,
        ): T = open(url, user, password, driverClassName).use(block)
    }
}

/**
 * A [SqlDatabase] that owns its connection: close it when done.
 */
sealed interface OwnedSqlDatabase : SqlDatabase, AutoCloseable

internal open class DefaultSqlDatabase(override val groovy: Sql) : SqlDatabase {
    private var inTransaction = false

    override fun <T> transaction(block: (SqlDatabase) -> T): T {
        if (inTransaction) return block(this)
        inTransaction = true
        try {
            var result: Any? = null
            groovy.withTransaction(closureOf { -> result = block(this) })
            @Suppress("UNCHECKED_CAST")
            return result as T
        } finally {
            inTransaction = false
        }
    }
}

internal class DefaultOwnedSqlDatabase(groovy: Sql) : DefaultSqlDatabase(groovy), OwnedSqlDatabase {
    override fun close() = groovy.close()
}

/**
 * A [Sql] over a borrowed connection: `Sql(Connection).close()` would close the caller's connection, so this one
 * doesn't.
 */
private class NonClosingSql(connection: Connection) : Sql(connection) {
    override fun close() = Unit
}
