# groovy-extensions

Kotlin adapters for Groovy APIs that take `groovy.lang.Closure`, and for Groovy's dynamic objects, usable without Gradle.
Gradle's Kotlin DSL has similar adapters (`closureOf`, `delegateClosureOf`, `KotlinClosure0`–`KotlinClosure3`), but only
inside Gradle's API; Gradle plugin authors should keep using those, since Gradle bundles its own Groovy.

---

## Requirements

Supports Groovy 3.0 through 6.0. Groovy is **not** a dependency of this library: add it yourself, under the coordinates
of your Groovy line.

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
API calls it with no arguments or passes the target, as Groovy's `with` does.

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
