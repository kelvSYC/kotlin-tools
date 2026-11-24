package com.kelvsyc.kotlin.groovy.sql

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
 */
interface SqlDatabase {
    /**
     * The underlying [Sql], for anything this wrapper doesn't offer. For a database made by [borrowing], closing it
     * leaves the borrowed connection open.
     */
    val groovy: Sql

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
interface OwnedSqlDatabase : SqlDatabase, AutoCloseable

internal open class DefaultSqlDatabase(override val groovy: Sql) : SqlDatabase

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
