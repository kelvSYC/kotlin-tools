# guava-extensions

Kotlin extensions for [Google Guava](https://github.com/google/guava).
This module provides idiomatic wrappers, factory functions, and bridge types for Guava APIs that
are worth using alongside Kotlin; it does not re-export Guava itself, so a `guava` dependency is
also required in your build.

Not every area of Guava is addressed here. Some parts are better served by pure Kotlin (including
other modules in this project); others add no meaningful value over the Guava API as-is. This
document maps each area of the library to one of three categories.

---

## Covered by this module

### `com.google.common.math`

Extensions on numeric types wrapping Guava's `IntMath`, `LongMath`, `DoubleMath`, and
`BigIntegerMath` with rounding-mode control. All functions accept a `RoundingMode` argument and
delegate to the corresponding Guava checked variant.

**`Int` and `Long` extensions** (`IntegerMath.kt`):
- `Int.log2(mode)`, `Int.log10(mode)`, `Int.sqrt(mode)` — base-2/base-10 logarithms and integer
  square root.
- `Long.log2(mode)`, `Long.log10(mode)`, `Long.sqrt(mode)`, `Long.roundToDouble(mode)`.

**`Double` extensions** (`DoubleMath.kt`):
- `Double.log2(mode)` — base-2 logarithm.
- `Double.roundToInt(mode)`, `Double.roundToLong(mode)`, `Double.roundToBigInteger(mode)`.

**`BigInteger` extensions** (`BigIntegers.kt`):
- `isPowerOfTwo: Boolean`, `floorPowerOfTwo: BigInteger`, `ceilingPowerOfTwo: BigInteger`.
- `BigInteger.log2(mode)`, `BigInteger.log10(mode)`, `BigInteger.sqrt(mode)`.
- `BigInteger.roundToDouble(mode)`.

**`BigDecimal` extensions** (`BigDecimals.kt`):
- `BigDecimal.roundToDouble(mode)`.

### `com.google.common.collect.Range`

Bridge functions and converters between Kotlin range types and Guava's `Range<T>`.

**Generic `Range<T>` bridges** (`Ranges.kt`):
- `ClosedRange<T>.toGuavaRange(): Range<T>` — produces a closed Guava range.
- `OpenEndRange<T>.toGuavaRange(): Range<T>` — produces a half-open (closed-open) Guava range.
- `Range<T>.toClosedRange(): ClosedRange<T>` — requires the range to have inclusive bounds on both
  ends; throws if either bound is absent or exclusive.
- `Range<T>.toOpenEndRange(): OpenEndRange<T>` — requires a closed lower bound and an open upper
  bound.
- `closedRangeToGuavaRange<T>(): Converter<ClosedRange<T>, Range<T>>` and
  `openEndRangeToGuavaRange<T>(): Converter<OpenEndRange<T>, Range<T>>` — reusable converters.

Nullable accessors for ranges of arbitrary boundedness:
- `lowerBoundTypeOrNull(): BoundType?`, `lowerEndpointOrNull(): T?`
- `upperBoundTypeOrNull(): BoundType?`, `upperEndpointOrNull(): T?`

**Primitive range bridges** (`IntRanges.kt`, `LongRanges.kt`, `CharRanges.kt`):
Specialised conversions avoiding boxing where possible, plus a reusable `Converter` for each pair:
- `IntRange.toGuavaRange()` / `Range<Int>.toIntRange()` / `intRangeToGuavaRange`
- `LongRange.toGuavaRange()` / `Range<Long>.toLongRange()` / `longRangeToGuavaRange`
- `CharRange.toGuavaRange()` / `Range<Char>.toCharRange()` / `charRangeToGuavaRange`

### `com.google.common.collect.DiscreteDomain`

`DiscreteDomain` implementations for types not covered by Guava's built-in domains.

**Primitive and unsigned integer domains** (`DiscreteDomains.kt`):
- `ShortDiscreteDomain` — for `Short`.
- `UByteDiscreteDomain`, `UShortDiscreteDomain`, `UIntDiscreteDomain`, `ULongDiscreteDomain` —
  for Kotlin unsigned integer types.
- `UnsignedIntegerDiscreteDomain`, `UnsignedLongDiscreteDomain` — for Guava's own
  `UnsignedInteger` and `UnsignedLong`.

**Enum domains** (`EnumDiscreteDomain.kt`):
- `EnumDiscreteDomain<E>` — a `DiscreteDomain` over any enum type, ordered by declaration order.
- `enumDiscreteDomain<E>(): EnumDiscreteDomain<E>` — inline reified factory.

**Enum-subset domain** (`collect/EnumSubsetDomain.kt`):
- `EnumSubsetDomain<E>` — a `DiscreteDomain` whose elements are `EnumSubset<E>` values
  (all 2ⁿ subsets of an enum), enumerated in binary-counting order.
- `EnumSubsetDomain.of<E>(): EnumSubsetDomain<E>` — inline reified factory.

### `com.google.common.collect` — collection types

Factory functions following Kotlin's `listOf`/`mapOf` idiom, plus DSL builders for immutable
variants.

**`BiMap`** (`collect/BiMaps.kt`):
- `buildBiMap { ... }: BiMap<K, V>` — builder DSL over `ImmutableBiMap.Builder`.
- `emptyBiMap()`, `biMapOf(...)` (0–5 pairs + vararg), `Map<K,V>.toImmutableBiMap()`.
- `enumBiMapOf<K,V>(...)`, `enumHashBiMapOf<K,V>(...)`, `hashBiMapOf(...)` — mutable variants.

**`Multimap`** (`collect/Multimaps.kt`):
- `buildMultimap { ... }`, `buildListMultimap { ... }`, `buildSetMultimap { ... }` — builder DSLs.
- `emptyMultimap()`, `multimapOf(...)`, `Multimap.toImmutableMultimap()`.
- `emptyListMultimap()`, `listMultimapOf(...)`.
- `emptySetMultimap()`, `setMultimapOf(...)`.

**`Multiset`** (`collect/Multisets.kt`, `collect/SortedMultisets.kt`):
- `buildMultiset { ... }` — builder DSL over `ImmutableMultiset.Builder`.
- `emptyMultiset()`, `multisetOf(...)` (0–5 elements + vararg), `Iterable.toImmutableMultiset()`.
- `enumMultisetOf<E>(...)`, `hashMultisetOf(...)`, `linkedMultisetOf(...)`,
  `treeMultisetOf(...)` — mutable variants with natural or custom ordering.
- `buildSortedMultiset { ... }` (natural and comparator overloads), `emptySortedMultiset()`,
  `sortedMultisetOf(...)`.

**`RangeMap`** (`collect/RangeMaps.kt`):
- `buildRangeMap { ... }` — builder DSL.
- `emptyRangeMap()`, `rangeMapOf(...)` (0–1 element + vararg),
  `operator fun RangeMap.contains(key: K): Boolean`.
- `treeRangeMapOf(...)` — mutable variant.

**`RangeSet`** (`collect/RangeSets.kt`):
- `buildRangeSet { ... }` — builder DSL.
- `emptyRangeSet()`, `rangeSetOf(...)` (0–1 element + vararg),
  `Iterable<Range<C>>.toImmutableRangeSet()`.
- `treeRangeSetOf(...)` — mutable variant.

**`ClassToInstanceMap` / `TypeToInstanceMap`** (`collect/ClassToInstanceMaps.kt`):
- `ClassToInstanceMap<*>.getTyped<T>()` and `TypeToInstanceMap<*>.getTyped<T>()` — inline reified
  `get` that avoids passing a `Class` or `TypeToken` literal at the call site.
- `ImmutableClassToInstanceMap.Builder.put(value: T)` and
  `ImmutableTypeToInstanceMap.Builder.put(value: T)` — reified `put` avoiding explicit type
  arguments.
- `MutableClassToInstanceMap.put(value: T)` and `MutableTypeToInstanceMap.put(value: T)` —
  same for the mutable variants.
- `buildClassToInstanceMap { ... }`, `buildTypeToInstanceMap { ... }` — builder DSLs.

### `com.google.common.escape`

- `@Beta buildEscaper { ... }: Escaper` — DSL wrapper over `Escapers.Builder` for constructing
  custom escapers without chaining `.builder()` and `.build()` manually.

### `com.google.common.io`

Source/sink adapters for `java.io.File` and `java.nio.file.Path`.

**`File`** (`Files.kt`):
- `@Beta File.asByteSource(): ByteSource`
- `@Beta File.asCharSource(charset): CharSource`
- `@Beta File.asByteSink(vararg modes): ByteSink`
- `@Beta File.asCharSink(charset, vararg modes): CharSink`

**`Path`** (`Paths.kt`):
- `@Beta Path.asByteSource(vararg options): ByteSource`
- `@Beta Path.asCharSource(charset, vararg options): CharSource`
- `@Beta Path.asByteSink(vararg options): ByteSink`
- `@Beta Path.asCharSink(charset, vararg options): CharSink`

### `com.google.common.hash`

- `HashFunction.hash { ... }: HashCode` — DSL that opens a `Hasher`, runs the block, and calls
  `hash()`, eliminating the intermediate `newHasher()`/`hash()` calls.
- `HashFunction.hash(expectedInputSize) { ... }: HashCode` — overload accepting an
  `expectedInputSize` hint.

### `com.google.common.primitives`

**Unsigned integer interop** (`UnsignedIntegers.kt`, `UnsignedLongs.kt`):
- `operator fun UnsignedInteger.div(other)`, `operator fun UnsignedInteger.rem(other)` — Kotlin
  operator syntax for Guava's `dividedBy`/`mod`.
- `UInt.toUnsignedInteger()`, `UnsignedInteger.toUInt()`, `uIntToUnsignedInteger` converter.
- `operator fun UnsignedLong.div(other)`, `operator fun UnsignedLong.rem(other)`.
- `ULong.toUnsignedLong()`, `UnsignedLong.toULong()`, `uLongToUnsignedLong` converter.

**kotlin-core trait instances** for `UnsignedInteger` and `UnsignedLong`:

Not all kotlin-core integer traits have instances for Guava's unsigned types. The table below
shows what is available and what each instance depends on.

| Trait | `UnsignedInteger` | `UnsignedLong` | Dependencies |
|---|:---:|:---:|---|
| `UInt32<T>` ¹ | ✓ | — | direct implementation |
| `UInt64<T>` ¹ | — | ✓ | direct implementation |
| `IntegerArithmetic<T>` | ✓ | ✓ | direct implementation |
| `CeilDiv<T>` | ✓ | ✓ | `IntegerArithmetic<T>` |
| `Gcd<T>` | ✓ | ✓ | `IntegerArithmetic<T>` |
| `PowerOfTwo<T>` | ✓ | ✓ | `UInt32<T>` / `UInt64<T>` |
| `RoundingRightShift<T>` | ✓ | ✓ | `UInt32<T>` / `UInt64<T>` + `IntegerArithmetic<T>` |
| `StickyRightShift<T>` | ✓ | ✓ | `UInt32<T>` / `UInt64<T>` |
| `UnsignedIntegerArithmetic<T>` | — | — | Guava types are not Kotlin `UInt`/`ULong` ² |
| `Log2<T>`, `Log10<T>`, `Sqrt<T>` | — | — | no instances provided |
| `Primality<T>` | — | — | no instances provided |

Companion property names follow the pattern `Trait.Companion.unsignedInteger` /
`Trait.Companion.unsignedLong` — e.g. `IntegerArithmetic.unsignedInteger`,
`UInt64.unsignedLong`.

¹ `UInt32<T>` and `UInt64<T>` are width-specific sub-interfaces of `UnsignedIntegral<T>`, which
bundles `BitCollection`, `BitShift`, and `Bitwise`. Providing either instance therefore covers all
three of those traits for the corresponding type.

² `UnsignedIntegerArithmetic<T>` in kotlin-core is designed for Kotlin's own unsigned primitives
(`UByte`, `UShort`, `UInt`, `ULong`). Guava's `UnsignedInteger` and `UnsignedLong` are distinct
boxed types and do not share that sub-interface. `IntegerArithmetic<T>` is the appropriate
arithmetic trait for them.

**Unsigned comparators** (`Comparators.kt`):
- `Comparators.unsignedByteComparator`, `Comparators.unsignedIntComparator`,
  `Comparators.unsignedLongComparator` — compare signed primitive values as if unsigned, without
  boxing.

### `com.google.common.reflect.TypeToken`

- `KType.toTypeToken(): TypeToken<*>` — converts a Kotlin `KType` to a Guava `TypeToken`,
  automatically boxing primitive types to their wrapper equivalents.
- `typeTokenOf<T>(): TypeToken<T>` — inline reified factory that eliminates the anonymous subclass
  boilerplate required to capture a generic type token at runtime.

---

## Better served by pure Kotlin

These areas of Guava are not covered by this module because idiomatic Kotlin already provides the
functionality without any Guava dependency.

### `com.google.common.base.Optional`

Superseded by Kotlin's nullable type system. `Optional.of(x)` → `x`, `Optional.absent()` → `null`,
`Optional.orNull()` → the value itself; all safe-access operators (`?.`, `?:`, `!!`) apply
directly.

### `com.google.common.base.Preconditions`

`checkArgument` → `require`, `checkState` → `check`, `checkNotNull` → `requireNotNull` or `!!`,
throwing functions → `error`. All are built into the Kotlin standard library with no overhead.

### `com.google.common.collect.ImmutableList` / `ImmutableSet` / `ImmutableMap`

Guava's immutable collection factories (`ImmutableList.of(...)`, `ImmutableMap.builder()`, etc.)
are superseded by Kotlin's `listOf`, `setOf`, `mapOf`, and `buildList`/`buildSet`/`buildMap` DSLs.
Kotlin's collections are structurally immutable at the type level and require no Guava dependency.

### `com.google.common.base.Joiner` / `Splitter`

`Joiner` is superseded by `Iterable.joinToString(separator, prefix, postfix, ...)`. `Splitter` is
superseded by `String.split(regex)`, `String.splitToSequence(...)`, and
`String.splitToSequence(delimiter, ignoreCase, limit)`.

### `com.google.common.primitives` (signed utilities)

`Ints.min`/`max`, `Longs.constrainToRange`, and similar signed-primitive utilities are covered by
Kotlin's `minOf`/`maxOf` vararg overloads and `coerceIn` — available on all Kotlin targets without
a Guava dependency.

---

## Not currently covered

These areas of Guava are not addressed by this module. In some cases a clear idiomatic Kotlin
alternative exists; in others the API is self-contained enough that Kotlin wrappers add no value.

| Area | Reason not covered |
|---|---|
| `base.Strings` | Superseded by Kotlin's `String`/`CharSequence` extension functions (`padStart`, `padEnd`, `repeat`, `isNullOrEmpty`, etc.). |
| `base.Objects` / `MoreObjects` | Superseded by Kotlin `data class` for value types; `Objects.toStringHelper` is superseded by the auto-generated `toString`. |
| `base.Stopwatch` | No idiomatic Kotlin alternative, but the API is clean and concise enough that a DSL wrapper adds no value. |
| `base.Converter` | The converter pattern is used within this module (and `kotlin-core`), but wrapping Guava's `Converter` class itself in a DSL is not currently warranted. |
| `collect.Iterables` / `Iterators` / `FluentIterable` | Superseded by Kotlin sequences (`Sequence`, `asSequence()`) and the stdlib's collection extension functions. |
| `collect.Lists` / `Sets` / `Maps` utility methods | The highest-value operations (`Lists.partition`, `Sets.cartesianProduct`, `Maps.filterKeys`, etc.) have close equivalents in the Kotlin stdlib or are niche enough to use directly via Guava. |
| `collect.Table` | Useful two-key map structure; not yet covered. |
| `collect.Queues` / `MinMaxPriorityQueue` | Niche; no additional value currently provided. |
| `cache.CacheBuilder` | The builder API is rich and works naturally in Kotlin without extension wrappers. |
| `eventbus.EventBus` | Superseded by Kotlin coroutine-based event/flow patterns for new code; interop with existing uses does not benefit from wrappers. |
| `graph.*` (`Graph`, `ValueGraph`, `Network`) | Complex domain; not yet covered. |
| `io.ByteStreams` / `CharStreams` | Low-level streaming utilities; the existing `ByteSource`/`CharSource` extensions in this module cover the common cases. |
| `net.*` | Network utility types; niche. |
| `html.HtmlEscapers` / `xml.XmlEscapers` | Use the named `Escaper` instances directly; no wrapper warranted. |
