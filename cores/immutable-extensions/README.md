# immutable-extensions

Kotlin/JS extensions for [Immutable.js](https://immutable-js.com/), providing idiomatic Kotlin
access to `ImmutableList`, `ImmutableMap`, and `ImmutableSet`: factory functions, Kotlin stdlib
integration via `asSequence()`, operator overloads, `Converter<A,B>` bridges to Kotlin stdlib
collections, and `kotlin-core` `ValueEquality` instances that surface Immutable.js's deep
structural equality through the trait ecosystem.

**Platform:** JS only.

**Dependencies consumers need:** `immutable` (npm) and `com.kelvsyc.kotlin:kotlin-core` are
both `api` dependencies and must be on the consumer's classpath.

---

## Types

| External class | JS type | Notes |
|---|---|---|
| `ImmutableList<T>` | `immutable.List` | Persistent ordered sequence |
| `ImmutableMap<K, V>` | `immutable.Map` | Persistent key-value map |
| `ImmutableSet<T>` | `immutable.Set` | Persistent unordered set |

The `Immutable` prefix is used throughout to avoid clashing with Kotlin stdlib `List`, `Map`, `Set`.

---

## Factory functions

```kotlin
immutableListOf<Int>()                        // empty
immutableListOf(1, 2, 3)                      // from elements

immutableMapOf<String, Int>()                 // empty
immutableMapOf("a" to 1, "b" to 2)           // from pairs; works for any key type

immutableSetOf<String>()                      // empty
immutableSetOf("x", "y", "z")                // from elements
```

---

## Operators

```kotlin
val list = immutableListOf(1, 2, 3)
list + 4                   // ImmutableList(1, 2, 3, 4)
list + immutableListOf(4, 5)  // ImmutableList(1, 2, 3, 4, 5)
list - 1                   // remove at index 1 → ImmutableList(1, 3)
list[0]                    // 1 (returns T?)

val map = immutableMapOf("a" to 1)
map + ("b" to 2)           // ImmutableMap(a=1, b=2)
map + immutableMapOf("b" to 2)  // same, via merge
map - "a"                  // ImmutableMap()
map["a"]                   // 1 (returns V?)
"a" in map                 // true

val set = immutableSetOf(1, 2, 3)
set + 4                    // ImmutableSet(1, 2, 3, 4)
set + immutableSetOf(4, 5) // union
set - 2                    // ImmutableSet(1, 3)
set - immutableSetOf(2, 3) // subtract
2 in set                   // true
```

---

## Conversions

```kotlin
// Kotlin stdlib → Immutable
listOf(1, 2, 3).toImmutableList()
mapOf("a" to 1).toImmutableMap()
setOf(1, 2, 3).toImmutableSet()

// Immutable → Kotlin stdlib
immutableList.toKotlinList()
immutableMap.toKotlinMap()
immutableSet.toKotlinSet()

// Converter<A, B> factory functions
val c = kotlinListToImmutableList<Int>()
c(listOf(1, 2, 3))          // → ImmutableList
c.reverse(immutableList)    // → List
```

---

## Kotlin stdlib integration (`asSequence`)

`asSequence()` bridges Immutable.js collections to the entire Kotlin stdlib:

```kotlin
immutableListOf(1, 2, 3, 4, 5)
    .asSequence()
    .filter { it % 2 == 0 }
    .map { it * 10 }
    .toList()                 // [20, 40]

immutableMapOf("a" to 1, "b" to 2)
    .asSequence()
    .filter { (_, v) -> v > 1 }
    .toMap()                  // {"b": 2}
```

Additional convenience extensions:

```kotlin
list.isNotEmpty()
list.getOrElse(index) { default }
list.firstOrNull()
list.lastOrNull()
list.forEachIndexed { index, value -> }

map.isNotEmpty()
map.getOrDefault(key, default)
map.getOrElse(key) { default }
map.keyList()     // List<K>
map.valueList()   // List<V>

set.isNotEmpty()
```

---

## Sorting

`sortedBy` extensions accept a key extractor and an optional `Comparator<R>`:

```kotlin
immutableListOf("banana", "fig", "apple")
    .sortedBy { it.length }           // ["fig", "apple", "banana"]

immutableListOf(1, 2, 3)
    .sortedBy({ it }, Comparator { a, b -> b - a })  // [3, 2, 1]

// ImmutableMap sorts by value
immutableMapOf("b" to 3, "a" to 1)
    .sortedBy { it }                  // sorted by value ascending
```

---

## kotlin-core trait instances

```kotlin
ValueEquality.immutableList    // ValueEquality<ImmutableList<*>>
ValueEquality.immutableMap     // ValueEquality<ImmutableMap<*, *>>
ValueEquality.immutableSet     // ValueEquality<ImmutableSet<*>>
```

Immutable.js has deep structural equality: two distinct instances with the same contents
compare as equal. These trait instances delegate to Immutable.js's `.equals()` method.

```kotlin
val a = immutableListOf(1, 2, 3)
val b = immutableListOf(1, 2, 3)
ValueEquality.immutableList.run { a.isEqualTo(b) }  // true
```

No `PartialComparator` is provided — Immutable.js collections have no natural total or partial order.

---

## Map key hashing

Immutable.js uses its own internal hashing for map keys. For primitive types (`String`, `Int`,
`Double`), this matches expected identity. For Kotlin objects or data classes, Immutable.js falls
back to JS reference identity rather than Kotlin's `hashCode()`. Restrict map keys to
primitive-compatible types for correct lookup behaviour.
