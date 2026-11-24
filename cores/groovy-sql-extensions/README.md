# groovy-sql-extensions

Safe, Kotlin-idiomatic database access through [`groovy.sql.Sql`](https://docs.groovy-lang.org/latest/html/api/groovy/sql/Sql.html).
`SqlDatabase` wraps a `Sql` and offers only safe ways to run queries: no operation takes a plain `String` query, so a
Kotlin string template can't paste values into SQL by accident. Built on
[groovy-extensions](../groovy-extensions/README.md).

---

## Requirements

Supports Groovy 3.0 and later; tested against Groovy 3.0 and the newest Groovy release (currently 6.0). Neither Groovy
nor `groovy-sql` is a dependency of this library: add `groovy-sql` yourself, under the coordinates of your Groovy line.

```kotlin
dependencies {
    implementation("com.kelvsyc.kotlin:groovy-sql-extensions:<version>")
    implementation("org.apache.groovy:groovy-sql:<4.x, 5.x or 6.x>") // or org.codehaus.groovy:groovy-sql:3.0.x
}
```

## Obtaining a database

How a `SqlDatabase` is obtained decides who owns its connection. Only `open` returns something closeable, so a database
can't close a connection it doesn't own.

```kotlin
SqlDatabase.of(dataSource)                            // pooled; nothing to close (recommended)
SqlDatabase.connect(url, user, password) { db -> }    // owned; closed when the block ends, even on an exception
SqlDatabase.open(url, user, password).use { db -> }   // owned; for lifetimes spanning a block
SqlDatabase.borrowing(connection)                     // your connection; never closed here
SqlDatabase.wrapping(sql)                             // your Sql; you keep owning it
```

Prefer `of(dataSource)` in applications; the URL forms suit scripts and tools. `user` and `password` are optional (for
example for embedded databases), and `driverClassName` loads a JDBC driver first.

Every database exposes the underlying `Sql` as `db.groovy`, for anything not offered here. For `borrowing`, closing
`db.groovy` still leaves your connection open.

## Queries

Every operation takes a `SqlQuery`, never a plain `String`: in Kotlin, `"… where id = $id"` pastes the value into the
SQL text, where Groovy would have bound it as a parameter. A query is made one of three ways:

```kotlin
// Template: param() binds each value (the usual way)
db.rows { "select * from books where author = ${param(author)} and year > ${param(year)}" }

// Placeholders and values, JDBC style
db.rows(sql("select * from books where author = ? and year > ?", author, year))

// Explicitly trusted text, for SQL you build yourself (table names, generated clauses): nothing in it is bound
db.rows(trustedSql("select * from $table"))
```

- `param(collection)` (or an array) expands to one parameter per element, for `in` lists:
  `"… where id in (${param(ids)})"`. An empty collection throws `IllegalArgumentException`, since `in ()` isn't valid
  SQL. A `ByteArray` binds as one binary value. `sql(…)` doesn't expand: its parameters bind one per `?`.
- `param(null)` binds SQL `NULL`, so `= ${param(null)}` matches nothing, as in SQL.
- Don't quote `param()`: it binds a value, so write `= ${param(x)}`, not `= '${param(x)}'`. A quoted `param()` throws
  `IllegalArgumentException`.
- Values from `Sql.expand(…)`, which `Sql` pastes into the text instead of binding, are rejected by `param` and `sql`;
  use `trustedSql` for that. Typed parameters such as `Sql.VARCHAR(x)` are bound as usual.
- `query.text` (with `?` placeholders) and `query.parameters` show what will be run, for logging.

## Writing safe queries

The point of `SqlDatabase` is that the easy way to write a query is also the safe one. What it can't prevent:

- **A forgotten `param()`.** Inside `db.rows { "… ${x}" }`, a value without `param()` is pasted into the text like any
  Kotlin template. Kotlin can't tell the two apart, so review query templates for interpolations without `param()`.
  `trustedSql` is the only form meant to contain built text, which makes deliberate cases stand out.
- **Built text in `sql(…)`.** Its text should be a constant; values belong in its parameters.
- **The escape hatch.** `db.groovy` is the plain `groovy.sql.Sql`, with none of these protections:
  - A Kotlin string template passed to it (`db.groovy.rows("… where id = $id")`) is plain text with the value pasted in.
  - A `GString` passed to it isn't fully bound either. `Sql` pastes a value written between quotes
    (`'${x}'`) into the SQL text on Groovy before 6.0 (Groovy 6 rejects it unless `groovy.sql.injection.lenient` is
    set), and rewrites `= ${x}` with a `null` value to `is null`, so it matches `NULL` rows. A Groovy GString query and
    one built with `gstring { }` from groovy-extensions behave the same way.

  If you do use `db.groovy`, use its placeholder methods (`rows(String, List)` and the like) with constant text.

`SqlDatabase` itself never hands `Sql` a `GString`: every query runs as text with `?` placeholders through `Sql`'s
placeholder methods, so every `param()` value is bound, and `= ${param(null)}` matches nothing, as in SQL. Code ported
from Groovy that relied on `= ${null}` meaning `is null` must say `is null` explicitly.

## Reading

```kotlin
db.rows(query): List<GroovyRowResult>                 // all rows
db.rows(query) { row -> Book(row.title, row.year) }   // all rows, mapped
db.firstRow(query): GroovyRowResult?                  // null when there are none
db.forEachRow(query) { row -> … }                     // streams rows while the result set is open
```

`rows` and `firstRow` also take the template block directly: `db.rows { "… ${param(x)}" }`. Read columns with
`row["title"]`, or declare typed accessors with `groovy-extensions`:
`val GroovyRowResult.title: String by groovyProperty()`. `forEachRow` doesn't load all rows first; an exception from its
action stops the iteration and propagates.

## Writing and transactions

```kotlin
db.executeUpdate { "update books set year = ${param(year)} where id = ${param(id)}" }   // rows changed
db.executeInsert(sql("insert into books (title) values (?)", title))                   // generated keys
db.execute(trustedSql("create table archive (id integer)"))                              // any statement

db.transaction { tx ->
    tx.executeUpdate { … }
    tx.executeUpdate { … }
}   // committed on return, rolled back if the block throws
```

A `transaction` inside another on the same thread joins it: only the outermost block commits or rolls back. If the outer
block catches an inner block's exception and returns normally, everything done so far, including the inner block's
work, is committed. On a connection already in a transaction managed elsewhere (auto-commit off, as a borrowed
connection may be), `transaction` joins that one and leaves committing to its owner. Exceptions propagate unchanged,
checked ones included.

A database from `of(dataSource)` can be shared between threads: each transaction runs on its own connection, and other
threads' operations stay outside it. A database over a single connection (`connect`, `open`, `borrowing`) shares that
connection, and any running transaction, between threads.

Batches, stored procedures and anything else not offered here are available through `db.groovy`.
