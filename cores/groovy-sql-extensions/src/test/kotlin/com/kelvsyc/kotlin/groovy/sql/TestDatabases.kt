package com.kelvsyc.kotlin.groovy.sql

import org.sqlite.SQLiteDataSource
import java.nio.file.Files

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
