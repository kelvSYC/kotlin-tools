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
