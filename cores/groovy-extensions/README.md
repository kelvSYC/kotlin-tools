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
