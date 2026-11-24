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
