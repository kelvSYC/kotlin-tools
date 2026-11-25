# groovy-extensions

Kotlin adapters for Groovy APIs that take `groovy.lang.Closure`, and for Groovy's dynamic objects, usable without Gradle.
Gradle's Kotlin DSL has similar adapters (`closureOf`, `delegateClosureOf`, `KotlinClosure0`–`KotlinClosure3`), but only
inside Gradle's API; Gradle plugin authors should keep using those, since Gradle bundles its own Groovy.

---

## Requirements

Supports Groovy 3.0 and later. It is tested against Groovy 3.0, the oldest supported line, and against the newest Groovy
release (currently 6.0); the lines in between are expected to work but are not tested separately. Groovy is **not** a
dependency of this library: add it yourself, under the coordinates of your Groovy line.

```kotlin
dependencies {
    implementation("com.kelvsyc.kotlin:groovy-extensions:<version>")
    implementation("org.apache.groovy:groovy:<4.x, 5.x or 6.x>") // or org.codehaus.groovy:groovy:3.0.x
}
```

## Kotlin lambdas as closures

`closureOf` turns a Kotlin lambda of up to three parameters into a `Closure`. Groovy APIs that inspect a closure's
parameter count see the lambda's: `Map.each` passes an entry to a one-parameter closure, but key and value to a
two-parameter one.

```kotlin
val answer = closureOf { -> 42 }                       // no parameters: write the arrow
val double = closureOf { x: Int -> x * 2 }             // name and type each parameter
val entries = closureOf { key: String, value: Int -> println("$key=$value") }
```

Declare parameters explicitly: `closureOf { 42 }` and `closureOf { it }` are ambiguous between the overloads. A lambda
returning `Unit` returns `null` to Groovy, so `[1, 2].collect(closureOf { x: Int -> println(x) })` gives `[null, null]`.

## Delegate closures

`delegateClosureOf<T>` turns a Kotlin receiver lambda into a closure for configuration-style Groovy APIs: it resolves
against its delegate first and runs the lambda with the delegate, which must be a `T`, as receiver. It works whether the
API calls it with no arguments or passes the target, as Groovy's `with` does. If the API passes the target without setting
a delegate (`[server].each(configure)`), the target is the receiver.

```kotlin
class Server { var port = 0 }

val configure = delegateClosureOf<Server> { port = 8080 }
// Groovy: server.with(configure)
```

A non-reified overload, `delegateClosureOf(Server::class.java) { … }`, takes the type explicitly.

## Calling closures from Kotlin

A `Closure` handed back by Groovy can be called like a Kotlin function, or converted to a Kotlin function type of up to
three parameters.

```kotlin
val sum: Closure<Int> = …
sum(1, 2)                                        // closure.call(1, 2)
val f: (Int, Int) -> Int = sum.toFunction2()
```

## Dynamic Groovy objects

Properties and methods of a `GroovyObject` (any Groovy class, and dynamic types such as `groovy.sql.GroovyResultSet`)
can be reached by name:

```kotlin
person["name"]                       // obj.name
person["name"] = "Grace"             // obj.name = "Grace"
person.callMethod("greet", "hello")  // obj.greet("hello")
```

The method call is `callMethod`, not `invoke`, because a `Closure` is itself a `GroovyObject` and `invoke` calls the
closure. Types that also implement `Map`, such as `groovy.sql.GroovyRowResult`, resolve `obj["key"]` to their own
`Map.get`, not to this extension.

### Delegated properties

A Kotlin property can be delegated to a Groovy object's property of the same name:

```kotlin
val name: String by person           // person.getProperty("name")
var age: Int by person               // writes go to person.setProperty("age", …)
```

For extension properties, which can't refer to their receiver in the delegate expression, `groovyProperty()` reads and
writes the receiver's property instead, optionally under another name. This gives typed facades over dynamic objects:

```kotlin
val GroovyRowResult.firstName: String by groovyProperty("first_name")
val GroovyRowResult.age: Int by groovyProperty()
```

`groovyProperty()` returns a standard `ReadWriteProperty<GroovyObject, V>`, with `V` inferred from the property's type, so
the delegate can also be stored, passed around or wrapped.

Values are cast to the property's type when read: a value of the wrong type fails with `ClassCastException`, and `null`
read into a non-null property fails with `NullPointerException`. For types that also implement `Map`, such as
`GroovyRowResult`, `val x by row` resolves to this delegate rather than the standard library's `Map` delegate whenever
this library's `getValue` is imported (imports take precedence over Kotlin's default imports). Groovy reads a map's
properties as its keys, so the result is the same, except that a missing key reads as `null` instead of throwing
`NoSuchElementException`.

## Building GStrings

A Kotlin string template is a plain `String`: the values are pasted into the text. Some Groovy APIs treat a `GString`'s
values differently from its text. `gstring` builds a real `GString` from a Kotlin template, with `value(x)` marking each
value:

```kotlin
val g = gstring { "Hello, ${value(name)}! You have ${value(count)} new messages." }
g.strings   // ["Hello, ", "! You have ", " new messages."]
g.values    // [name, count]
g.toString() // "Hello, Ada! You have 3 new messages."
```

A recorded value whose marker is missing from the returned text, used twice, or taken from another `gstring` block
throws `IllegalStateException`.

**Not for SQL.** Don't pass a `gstring` to `groovy.sql.Sql`: `Sql` doesn't bind every value of a GString. Before
Groovy 6, a value written between quotes (`where name = '${value(name)}'`) is pasted into the SQL text, which is an
injection risk (Groovy 6 rejects it unless `groovy.sql.injection.lenient` is set), and `= ${value(null)}` is rewritten
to `is null`, so it matches `NULL` rows. For database access use
[groovy-sql-extensions](../groovy-sql-extensions/README.md), which always binds parameters.

## Groovy builders

`withGroovyBuilder` drives any Groovy builder with Groovy's calling conventions; it is tested against `NodeBuilder`,
`ObjectGraphBuilder`, `MarkupBuilder`, `JsonBuilder` and `AntBuilder`. Builder code translates line by line:

```kotlin
// Groovy: repository(url: "scp://repos.example.com/releases") { authentication(userName: "me", password: "secret") }
builder.withGroovyBuilder {
    "repository"("url" to "scp://repos.example.com/releases") {
        "authentication"("userName" to "me", "password" to "secret")
    }
    "a"("href" to "https://example.com", "link text")   // Groovy: a(href: "https://example.com", "link text")
    this["title"] = "Report"                              // property access on the builder
}
```

- **Named arguments.** Every `"key" to value` argument (a `Pair` with a `String` key), wherever it is written, is gathered
  in order into one map passed first, then the other arguments, then the nested block. Such a `Pair` therefore can't be
  passed as an ordinary value.
- **Nested blocks** run against the delegate the builder sets on them (some builders, such as `JsonBuilder`, use an
  internal object rather than themselves). When the builder sets none, they run against the argument it passes if that
  is a `GroovyObject`, else against the enclosing scope, as a Groovy closure would. Builders and their delegates must be
  `GroovyObject`s.
- A call returns what the builder returns (for example `NodeBuilder`'s nodes), and a block's result is returned to the
  builder, `Unit` as `null`.
- `withGroovyBuilder` is inline, so its outermost block can `return` from the enclosing function. Nested blocks can't:
  the builder runs them as Groovy closures.
- Write a nested block after the parentheses: `"m"("a") { … }`. A lambda inside them, `"m"("a", { … })`, is passed to
  the builder as a plain value and never run as a block.
- Builders based on Groovy's `BuilderSupport` (`NodeBuilder`, `MarkupBuilder`, `AntBuilder`, …) wrap an exception
  thrown inside a block in `groovy.lang.GroovyRuntimeException`; the original is its cause.

### Iterating

Iterate the Kotlin way. Build a collection with `map` where the builder takes one, or loop inside a block to produce
one element per item:

```kotlin
json.withGroovyBuilder {
    "authors"(authors.map { mapOf("name" to it.name, "born" to it.born) })   // a JSON array of objects
}
markup.withGroovyBuilder {
    "authors" { authors.forEach { "author"("name" to it.name) } }            // one element per author
}
```

A builder's own collection-plus-block form, Groovy's `authors(list) { a -> … }`, isn't available: the builder passes
each item to the block as its argument, which the block doesn't receive.

### Typed scopes for specific builders

`GroovyBuilderScope` is an interface, so a builder-specific layer can add typed functions by delegation:

```kotlin
class AntScope(scope: GroovyBuilderScope) : GroovyBuilderScope by scope {
    fun copy(todir: String, block: AntScope.() -> Unit = {}) = "copy"("todir" to todir) { AntScope(this).block() }
}
```

`GroovyBuilderScope`'s members are kept stable for such implementations: this library adds new capabilities as
extension functions, not members. Such scopes inherit the `@GroovyBuilderDsl` marker from `GroovyBuilderScope`, which stops a nested block from implicitly
calling an outer scope's functions (that would send them to the wrong delegate). Annotate any other receiver type used
inside builder blocks with it.
