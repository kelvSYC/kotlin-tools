package com.kelvsyc.kotlin.groovy.sql

import com.kelvsyc.kotlin.groovy.closureOf
import groovy.sql.GroovyResultSet
import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import java.sql.Connection
import java.util.Collections
import java.util.WeakHashMap
import javax.sql.DataSource

/**
 * Safe, Kotlin-idiomatic access to a database through `groovy.sql.Sql`.
 *
 * How a `SqlDatabase` is obtained decides who owns its connection, and only [open] returns one that can be closed:
 * [of] (a pooled [DataSource]; recommended), [connect] (owned, closed when its block ends), [open] (owned, an
 * [OwnedSqlDatabase] to `use`), [borrowing] (the caller's connection, never closed here) and [wrapping] (an existing
 * [Sql], which the caller keeps owning).
 *
 * Every operation takes a [SqlQuery], whose parameters are always bound, never pasted into the SQL text.
 *
 * A database over a [DataSource] may be shared between threads: each [transaction] runs on its own connection, and
 * other threads' operations stay outside it. A database over a single connection ([connect], [open], [borrowing], or
 * [wrapping] a connection-based [Sql]) shares that connection, and so any running transaction, between threads.
 */
sealed interface SqlDatabase {
    /**
     * The underlying [Sql], for anything this database doesn't offer; inside a [transaction], the transaction's. For a
     * database made by [borrowing], closing it leaves the borrowed connection open.
     *
     * It has none of this database's protections. A Kotlin string template passed to it is plain text with its values
     * pasted in, and `Sql` doesn't bind every value of a GString: it pastes quoted values (`'${x}'`) into the text
     * before Groovy 6 and rewrites `= null` to `is null`. Use its placeholder methods, such as `rows(String, List)`,
     * with constant text.
     */
    val groovy: Sql

    /**
     * Runs [query] and returns all its rows.
     */
    fun rows(query: SqlQuery): List<GroovyRowResult>

    /**
     * Runs the query built by [block] (see [query]) and returns all its rows.
     */
    fun rows(block: SqlQueryScope.() -> String): List<GroovyRowResult> = rows(query(block))

    /**
     * Runs [query] and returns its rows, each mapped by [transform].
     */
    fun <T> rows(query: SqlQuery, transform: (GroovyRowResult) -> T): List<T> = rows(query).map(transform)

    /**
     * Runs the query built by [block] (see [query]) and returns its rows, each mapped by [transform].
     */
    fun <T> rows(block: SqlQueryScope.() -> String, transform: (GroovyRowResult) -> T): List<T> =
        rows(query(block), transform)

    /**
     * Runs [query] and returns its first row, or `null` when there is none.
     */
    fun firstRow(query: SqlQuery): GroovyRowResult?

    /**
     * Runs the query built by [block] (see [query]) and returns its first row, or `null` when there is none.
     */
    fun firstRow(block: SqlQueryScope.() -> String): GroovyRowResult? = firstRow(query(block))

    /**
     * Runs [query] and passes each row to [action] while the result set is open, without loading all rows first. An
     * exception from [action] stops the iteration and propagates.
     */
    fun forEachRow(query: SqlQuery, action: (GroovyResultSet) -> Unit)

    /**
     * Runs [query], an `insert`, `update` or `delete`, and returns the number of rows it changed.
     */
    fun executeUpdate(query: SqlQuery): Int

    /**
     * Runs the statement built by [block] (see [query]); see the [SqlQuery] overload.
     */
    fun executeUpdate(block: SqlQueryScope.() -> String): Int = executeUpdate(query(block))

    /**
     * Runs [query], any statement; returns `true` when its first result is a result set, as `Sql.execute` does.
     */
    fun execute(query: SqlQuery): Boolean

    /**
     * Runs the statement built by [block] (see [query]); see the [SqlQuery] overload.
     */
    fun execute(block: SqlQueryScope.() -> String): Boolean = execute(query(block))

    /**
     * Runs [query], an `insert`, and returns the keys it generated: one list of key columns per inserted row.
     */
    fun executeInsert(query: SqlQuery): List<List<Any?>>

    /**
     * Runs the insert built by [block] (see [query]); see the [SqlQuery] overload.
     */
    fun executeInsert(block: SqlQueryScope.() -> String): List<List<Any?>> = executeInsert(query(block))

    /**
     * Runs [block] in a transaction: committed when it returns, rolled back when it throws (the exception propagates
     * unchanged). This database's operations on the calling thread run in the transaction, whether made through the
     * block's argument or this database.
     *
     * A `transaction` inside another on the same thread joins it: it neither commits nor rolls back by itself, and
     * only the outermost block does. If the outer block catches an inner block's exception and returns normally,
     * everything done so far, including the inner block's work, is committed. Databases wrapping the same [Sql] share
     * their transactions, and so does a database wrapping a transaction's own [groovy]; databases made separately over
     * the same [DataSource] don't, and run theirs on separate connections.
     *
     * On a single connection already in the caller's transaction (auto-commit off, as a borrowed connection may be),
     * `transaction` joins it and leaves committing and rolling back to its owner, who must commit. Over a [DataSource],
     * each transaction runs on a connection of its own, so a `transaction` inside the underlying [Sql]'s own
     * `withTransaction` or `cacheConnection` is a separate transaction: use this database's `transaction` instead of
     * `Sql.withTransaction`.
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
         * A database over an existing [sql], which the caller keeps owning. Every database wrapping the same [sql]
         * shares its transactions.
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

/**
 * The running transaction's Sql on each thread, per base Sql, so that every database wrapping one Sql shares its
 * transactions. Weak keys: an entry goes away with its Sql.
 */
private val transactionSqls: MutableMap<Sql, ThreadLocal<Sql>> = Collections.synchronizedMap(WeakHashMap())

/** The transaction Sqls this library has running, so that a database wrapping one joins its transaction. */
private val activeTransactionSqls: MutableSet<Sql> =
    Collections.synchronizedSet(Collections.newSetFromMap(WeakHashMap()))

/*
 * Queries are always run through Sql's String overloads, never its GString ones: given a GString, Sql pastes values
 * that are surrounded by quotes into the SQL text and rewrites `= null` to `is null`, so a bound parameter would not
 * stay bound.
 */

internal open class DefaultSqlDatabase(private val base: Sql) : SqlDatabase {
    private val transactionSql: ThreadLocal<Sql> = transactionSqls.computeIfAbsent(base) { ThreadLocal() }

    override val groovy: Sql
        get() = transactionSql.get() ?: base

    override fun rows(query: SqlQuery): List<GroovyRowResult> =
        if (query.parameters.isEmpty()) groovy.rows(query.text) else groovy.rows(query.text, query.parameters)

    override fun firstRow(query: SqlQuery): GroovyRowResult? =
        if (query.parameters.isEmpty()) groovy.firstRow(query.text) else groovy.firstRow(query.text, query.parameters)

    override fun forEachRow(query: SqlQuery, action: (GroovyResultSet) -> Unit) {
        val closure = closureOf { row: GroovyResultSet -> action(row) }
        if (query.parameters.isEmpty()) {
            groovy.eachRow(query.text, closure)
        } else {
            groovy.eachRow(query.text, query.parameters, closure)
        }
    }

    override fun executeUpdate(query: SqlQuery): Int =
        if (query.parameters.isEmpty()) {
            groovy.executeUpdate(query.text)
        } else {
            groovy.executeUpdate(query.text, query.parameters)
        }

    override fun execute(query: SqlQuery): Boolean =
        if (query.parameters.isEmpty()) groovy.execute(query.text) else groovy.execute(query.text, query.parameters)

    override fun executeInsert(query: SqlQuery): List<List<Any?>> =
        if (query.parameters.isEmpty()) {
            groovy.executeInsert(query.text)
        } else {
            groovy.executeInsert(query.text, query.parameters)
        }

    override fun <T> transaction(block: (SqlDatabase) -> T): T {
        if (transactionSql.get() != null || base in activeTransactionSqls || inCallersTransaction()) return block(this)
        // Over a DataSource, the transaction runs on a fresh Sql and connection of its own: other threads keep using
        // connections of their own, and a connection someone else pinned on the base Sql (inside cacheConnection, or
        // with statement caching on) is never committed or closed here.
        val sql = if (base.dataSource != null) freshSqlLike(base) else base
        transactionSql.set(sql)
        activeTransactionSqls += sql
        try {
            return runInTransaction(sql) { block(this) }
        } finally {
            activeTransactionSqls -= sql
            transactionSql.remove()
        }
    }

    /**
     * Whether [base] is a connection-based Sql whose connection is already in the caller's transaction (auto-commit
     * off). A DataSource-based Sql is never treated so: its pinned connection may simply come from a pool whose
     * connections start with auto-commit off.
     */
    private fun inCallersTransaction(): Boolean {
        if (base.dataSource != null) return false
        val connection = base.connection ?: return false
        return !connection.autoCommit
    }
}

/**
 * A new Sql over [base]'s DataSource with [base]'s settings, but none of its connection state.
 */
private fun freshSqlLike(base: Sql): Sql = Sql(base.dataSource).apply {
    resultSetType = base.resultSetType
    resultSetConcurrency = base.resultSetConcurrency
    resultSetHoldability = base.resultSetHoldability
    isEnableNamedQueries = base.isEnableNamedQueries
    isCacheNamedQueries = base.isCacheNamedQueries
    isCacheStatements = base.isCacheStatements
}

internal class DefaultOwnedSqlDatabase(sql: Sql) : DefaultSqlDatabase(sql), OwnedSqlDatabase {
    override fun close() = groovy.close()
}

/**
 * Runs [block] in a transaction on one connection of [sql], pinned with `cacheConnection`: committed when [block]
 * returns, rolled back when it throws, the exception propagating unchanged. (`Sql.withTransaction` would wrap checked
 * exceptions in an [java.sql.SQLException].) The connection's auto-commit setting is restored afterwards.
 */
private fun <T> runInTransaction(sql: Sql, block: () -> T): T {
    var result: Any? = null
    sql.cacheConnection(
        closureOf { connection: Connection ->
            val autoCommit = connection.autoCommit
            connection.autoCommit = false
            var committed = false
            try {
                result = block()
                connection.commit()
                committed = true
            } finally {
                if (!committed) connection.rollback()
                connection.autoCommit = autoCommit
            }
        }
    )
    @Suppress("UNCHECKED_CAST")
    return result as T
}

/**
 * A [Sql] over a borrowed connection: `Sql(Connection).close()` would close the caller's connection, so this one
 * doesn't.
 */
private class NonClosingSql(connection: Connection) : Sql(connection) {
    /**
     * Closes cached statements, as `Sql.close()` does, but leaves the borrowed connection open. (`Sql.close()` also
     * forgets its parsed named-query text, which holds no resources.)
     */
    override fun close() {
        if (isCacheStatements) {
            // Turning statement caching off closes the cached statements; turn it back on for further use.
            isCacheStatements = false
            isCacheStatements = true
        }
    }
}
