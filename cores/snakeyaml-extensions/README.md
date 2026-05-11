# snakeyaml-extensions

Kotlin extensions for [snakeyaml-engine](https://bitbucket.org/snakeyaml/snakeyaml-engine/).
This module provides a type-safe YAML tree API and convenience parsing functions that wrap
snakeyaml-engine's low-level `Node` API with a Kotlin-idiomatic sealed class hierarchy,
typed coercion methods, and path-based navigation.

---

## YAML Tree API

### `YamlValue` sealed hierarchy

A sealed class hierarchy representing YAML values without requiring data classes:

- `YamlMapping` — a YAML mapping (dictionary); implements `Map<String, YamlValue>` by delegation
- `YamlSequence` — an ordered YAML sequence; implements `List<YamlValue>` by delegation
- `YamlScalar` — a scalar value with typed coercion methods
- `YamlNull` — the null value (`null`, `~`, or empty)

Unlike JSON, YAML does not distinguish between strings, numbers, and booleans at the node level —
all scalar content is stored as a raw string with typed coercion methods on `YamlScalar`.

### Collection interop

`YamlMapping` implements `Map<String, YamlValue>` and `YamlSequence` implements `List<YamlValue>`,
so standard Kotlin collection operations work directly:

```kotlin
val yaml = """
    users:
      - name: Alice
        active: true
      - name: Bob
        active: false
""".trimIndent().parseYaml() as YamlMapping

// Map operations on YamlMapping
yaml.keys                            // Set<String>
yaml.forEach { (key, value) -> }

// List operations on YamlSequence
val users = yaml.sequenceAt("users")!!
users.filter { it.booleanAt("active") == true }
users.map { it.stringAt("name") }
users.first()
```

### Scalar coercion

`YamlScalar` stores the raw string value and provides typed coercion methods that return `null`
when the value cannot be parsed as the requested type:

```kotlin
val scalar = YamlScalar("42")
scalar.asString()    // "42"
scalar.asInt()       // 42
scalar.asLong()      // 42L
scalar.asDouble()    // 42.0
scalar.asBoolean()   // null (not a boolean)

YamlScalar("true").asBoolean()   // true
YamlScalar("hello").asInt()      // null
```

Boolean coercion uses Kotlin's strict parsing — only `true` and `false` (case-sensitive) are
recognized. YAML 1.1 legacy boolean forms (`yes`, `no`, `on`, `off`) are not treated as booleans.

### Path-based navigation

`YamlValue.at(vararg segments)` navigates nested structures by key or sequence index:

```kotlin
val yaml = """
    server:
      host: localhost
      ports:
        - 8080
        - 8443
""".trimIndent().parseYaml()!!

yaml.at("server", "host")         // YamlScalar("localhost")
yaml.at("server", "ports", "0")   // YamlScalar("8080")
yaml.at("server", "missing")      // null
```

Typed path accessors combine navigation with type coercion:

- `stringAt(vararg segments)` — returns `String?`
- `intAt(vararg segments)` / `longAt(vararg segments)` / `doubleAt(vararg segments)`
- `booleanAt(vararg segments)` — returns `Boolean?`
- `mappingAt(vararg segments)` / `sequenceAt(vararg segments)`
- `isNullAt(vararg segments)` — returns `true` if null or absent

### Sequence extraction

- `YamlSequence.strings()` — extracts scalar string values, filtering out non-scalar elements

---

## Parsing

Extension functions for parsing YAML into a `YamlValue` tree:

- `String.parseYaml()` — parse a YAML string
- `Reader.parseYaml()` — parse from a `java.io.Reader`
- `InputStream.parseYaml()` — parse from a `java.io.InputStream`

All three return `YamlValue?` — `null` indicates an empty document.

The parser uses snakeyaml-engine's default `LoadSettings`, which implements YAML 1.2 and resolves
core schema tags (nulls, booleans, integers, floats). Non-string mapping keys are converted to
their string representation. Mapping entry order is preserved.
