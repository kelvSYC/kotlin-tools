# lodash-extensions

Kotlin extensions for [Lodash](https://lodash.com/), targeting the JS platform.

This module wraps the subset of Lodash that Kotlin's standard library does not cover: function
decorators and string transformations with no direct stdlib equivalent. Collection and math
utilities are omitted because the Kotlin stdlib already covers them idiomatically.

A `lodash` npm dependency is also required in your build:

```kotlin
implementation(npm("lodash", "4.17.21"))
```

---

## Covered by this module

### Function utilities (`FunctionUtilities.kt`)

Decorators that wrap a function and return a new function of the same type.

**`debounce`** — delays invocation until calls stop arriving:

```kotlin
val onInput = debounce(::handleInput, waitMs = 300)
val onScroll = debounce(::handleScroll, waitMs = 200, leading = true)  // fires immediately on first call
```

Parameters:
- `leading` — invoke on the leading edge (immediately on first call). Default: `false`.
- `trailing` — invoke on the trailing edge (after the wait). Default: `true`.
- `maxWaitMs` — maximum delay before the function is forced to invoke. Default: `null` (no maximum).

**`throttle`** — ensures at most one invocation per interval:

```kotlin
val onResize = throttle(::recalculateLayout, waitMs = 100)
```

Parameters:
- `leading` — invoke on the leading edge. Default: `true`.
- `trailing` — invoke on the trailing edge. Default: `true`.

**`memoize`** — caches results by first argument:

```kotlin
val cached = memoize(::expensiveCompute)
cached(42)  // computed
cached(42)  // returned from cache
cached(7)   // computed
```

**`once`** — invokes the function at most once; subsequent calls return the first result:

```kotlin
val init = once(::setupApplication)
init()  // runs
init()  // no-op, returns the original result
```

> **Note on async behaviour:** `debounce` and `throttle` use JavaScript timers internally.
> Tests for trailing-edge invocation require async test support (e.g., `kotlinx-coroutines-test`
> with `delay()`). Leading-edge invocation fires synchronously and can be tested without timers.

---

### String utilities (`StringUtilities.kt`)

Extension functions on `String` for transformations not in the Kotlin stdlib.

**Case conversion:**

```kotlin
"foo-bar".toCamelCase()   // "fooBar"
"fooBar".toKebabCase()    // "foo-bar"
"fooBar".toSnakeCase()    // "foo_bar"
"Foo Bar".toCamelCase()   // "fooBar"
```

**Truncation:**

```kotlin
"Hello, World!".truncated(maxLength = 8)              // "Hello, …"
"Hello, World!".truncated(maxLength = 9, omission = "...") // "Hello,..."
"Hi".truncated(maxLength = 10)                        // "Hi"  (unchanged)
```

The returned string, including the omission, is at most `maxLength` characters long.

**HTML escaping:**

```kotlin
"<b>Hello & 'world'</b>".htmlEscaped()
// "&lt;b&gt;Hello &amp; &#39;world&#39;&lt;/b&gt;"

"&lt;b&gt;Hello&lt;/b&gt;".htmlUnescaped()
// "<b>Hello</b>"
```

Covers `&`, `<`, `>`, `"`, and `'`.

---

## Not covered

| Area | Reason |
|---|---|
| Collection utilities (`chunk`, `flatten`, `zip`, `uniq`, `groupBy`, …) | Covered by Kotlin stdlib (`chunked`, `flatten`, `zip`, `distinct`, `groupBy`, …). |
| Math utilities (`clamp`, `sum`, `min`, `max`) | Covered by `coerceIn`, `sum()`, `min()`, `max()` in stdlib. |
| Object utilities (`merge`, `pick`, `omit`, `cloneDeep`) | Operate on `dynamic` JS objects; no idiomatic Kotlin type to extend. |
| String padding/trimming | Covered by `padStart`, `padEnd`, `trim`, `trimStart`, `trimEnd` in stdlib. |
