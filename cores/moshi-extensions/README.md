# moshi-extensions

Kotlin extensions for [Moshi](https://github.com/square/moshi).
This module provides a type-safe untyped JSON tree API, streaming DSL extensions, and convenience
parsing functions that make Moshi ergonomic for dynamic JSON navigation — filling the role that
Groovy's `JsonSlurper` and `JsonBuilder` play for Groovy users.

---

## JSON Tree API

### `JsonValue` sealed hierarchy

A sealed class hierarchy representing JSON values without requiring data classes or adapters:

- `JsonObject` — a JSON object; implements `Map<String, JsonValue>` by delegation
- `JsonArray` — an ordered JSON array; implements `List<JsonValue>` by delegation
- `JsonString` — a string value
- `JsonNumber` — a numeric value
- `JsonBoolean` — a boolean value
- `JsonNull` — the null value

Each subtype provides typed accessors (`asString()`, `asNumber()`, `asBoolean()`, `asObject()`,
`asArray()`, `isNull()`) that return `null` when the value is not of the expected type.

### Collection interop

`JsonObject` implements `Map<String, JsonValue>` and `JsonArray` implements `List<JsonValue>`,
so standard Kotlin collection operations work directly:

```kotlin
val json = """{"users": [{"name": "Alice", "active": true}, {"name": "Bob", "active": false}]}""".parseJson()

// Map operations on JsonObject
val obj = json as JsonObject
obj.keys                          // Set<String>
obj.forEach { (key, value) -> }
obj.filter { (_, v) -> v is JsonString }

// List operations on JsonArray
val users = json.arrayAt("users")!!
users.filter { it.booleanAt("active") == true }
users.map { it.stringAt("name") }
users.first()
users.any { it.stringAt("name") == "Alice" }
```

### Path-based navigation

`JsonValue.at(vararg segments)` navigates nested structures by key or array index:

```kotlin
val json = """{"user": {"scores": [10, 20, 30]}}""".parseJson()
json.at("user", "scores", "1")  // JsonNumber(20.0)
```

Typed path accessors combine navigation with type coercion:

- `stringAt(vararg segments)` — returns `String?`
- `numberAt(vararg segments)` — returns `Number?`
- `intAt(vararg segments)` / `longAt(vararg segments)` / `doubleAt(vararg segments)`
- `booleanAt(vararg segments)` — returns `Boolean?`
- `objectAt(vararg segments)` / `arrayAt(vararg segments)`
- `isNullAt(vararg segments)` — returns `true` if null or absent

### JsonPath queries (partial implementation)

`JsonPath` provides a **partial** implementation of JSONPath-style queries over the `JsonValue`
tree. This is **not** a complete or conformant implementation of
[RFC 9535](https://www.rfc-editor.org/rfc/rfc9535.html) — it supports only a minimal subset of the
query language:

| Syntax | Description | Supported |
|--------|-------------|-----------|
| `$` | Root element | Yes |
| `.key` / `['key']` | Child access by name | Yes |
| `[0]` | Child access by array index | Yes |
| `.*` / `[*]` | Wildcard (all children) | Yes |
| `..key` | Recursive descent | Yes |
| `[start:end:step]` | Array slices | **No** |
| `[?expression]` | Filter expressions | **No** |
| Functions | Function extensions | **No** |

```kotlin
val json = """{"store": {"book": [{"author": "A"}, {"author": "B"}]}}""".parseJson()

json.query("$.store.book[*].author")   // [JsonString("A"), JsonString("B")]
json.queryOne("$.store.book[0].author") // JsonString("A")
json.query("$..author")                // [JsonString("A"), JsonString("B")]
```

- `JsonValue.query(path)` — returns all matching nodes as a `List<JsonValue>`
- `JsonValue.queryOne(path)` — returns the single match, or `null` if zero or multiple matches

The `JsonPath` class can also be pre-parsed for repeated use:

```kotlin
val path = JsonPath.parse("$..author")
path.query(root)
```

### Array extraction

- `JsonArray.strings()` — extracts string elements, filtering out non-strings
- `JsonArray.numbers()` — extracts numeric elements, filtering out non-numbers

### Parsing

Extension functions for quick parsing into `JsonValue`:

- `String.parseJson()` — parse a JSON string
- `BufferedSource.parseJson()` — parse from an Okio source
- `InputStream.parseJson()` — parse from a Java input stream

### Moshi integration

`Moshi.Builder.addJsonValueAdapter()` registers the `JsonValueAdapter` so that `JsonValue` can be
used directly with Moshi's typed adapter system.

---

## Streaming DSL Extensions

### `JsonReader` extensions

Kotlin-idiomatic wrappers around Moshi's streaming reader:

- `readObject { name -> ... }` — iterate over object entries
- `readArray { ... }` — iterate over array elements
- `readArrayToList { ... }` — collect array elements into a typed list
- `readObjectToMap { name -> ... }` — collect object entries into a typed map

### `JsonWriter` extensions

Kotlin-idiomatic wrappers around Moshi's streaming writer:

- `writeObject { ... }` — write a JSON object
- `writeArray { ... }` — write a JSON array
- `field(name, value)` — write a named field (overloaded for `String?`, `Number?`, `Boolean?`)
- `objectField(name) { ... }` — write a named field containing a nested object
- `arrayField(name) { ... }` — write a named field containing a nested array

Null values passed to `field()` are always serialized (producing JSON `null`), regardless of the
writer's `serializeNulls` setting.
