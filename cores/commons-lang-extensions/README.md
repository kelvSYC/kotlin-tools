# commons-lang-extensions

Kotlin extensions for [Apache Commons Lang 3](https://commons.apache.org/proper/commons-lang/).
This module provides idiomatic wrappers and bridge functions for Commons Lang types that are worth
using alongside Kotlin; it does not re-export Commons Lang itself, so a `commons-lang3` dependency
is also required in your build.

Not every area of Commons Lang 3 is addressed here. Some parts are better served by pure Kotlin
(including other modules in this project); others add no meaningful value over the Commons API
as-is. This document maps each area of the library to one of three categories.

---

## Covered by this module

### `org.apache.commons.lang3.math.Fraction`

**`Fraction` operators** (`FractionExtensions.kt`):
- `component1()`, `component2()` — destructuring into numerator and denominator.
- `unaryPlus()`, `unaryMinus()`.
- `plus(Int)`, `plus(Fraction)`.
- `minus(Int)`, `minus(Fraction)`.
- `times(Int)`, `times(Fraction)`.
- `div(Int)`, `div(Fraction)`.

Comparison operators (`<`, `>`, `<=`, `>=`) are available natively because `Fraction` already
implements `Comparable<Fraction>`.

**kotlin-core trait instance** (`FractionRationalArithmetic.kt`):
- `RationalArithmetic.Companion.fraction: RationalArithmetic<Fraction, Int>` — full arithmetic for
  `Fraction`. All defaults (`add`, `subtract`, `multiply`, `divide`, `compareTo`, `floor`, `ceil`,
  `integerPart`, `fractionalPart`) are inherited from `RationalArithmetic<Fraction, Int>`. The `of`
  implementation delegates to `Fraction.getReducedFraction()` for GCD reduction and handles sign
  normalization for negative denominators. Intermediate cross-multiplications may silently overflow.

**kotlin-core bridge** (`FractionBridge.kt`):
- `Fraction.toRational(): Rational<Int>` — converts to a normalised `Rational<Int>` (positive
  denominator, fully reduced).
- `Rational<Int>.toFraction(): Fraction` — creates a `Fraction` from a canonical `Rational<Int>`.
- `Rational.fractionConverter: Converter<Fraction, Rational<Int>>` — bidirectional converter.

### `org.apache.commons.lang3.Range`

Two bridge functions between Kotlin's `ClosedRange<T>` and Commons `Range<T>`:

- `ClosedRange<T>.toCommonsRange()` — produces a `Range<T>` with the same inclusive bounds.
  If `start > endInclusive` by natural ordering, `Range.of` normalises the interval by swapping
  the bounds.
- `Range<T>.toClosedRange()` — produces a Kotlin `ClosedRange<T>`.

### `org.apache.commons.lang3.tuple`

Conversion and destructuring for `ImmutablePair`, `MutablePair`, `ImmutableTriple`, and
`MutableTriple`. These are useful when interoperating with Java code that returns Commons tuple
types.

**Pair conversions:**
- `Pair<A, B>.toImmutablePair(): ImmutablePair<A, B>`
- `Pair<A, B>.toMutablePair(): MutablePair<A, B>`
- `Pair<L, R>.toKotlinPair(): Pair<L, R>` (on `CommonsPair`)

**Triple conversions:**
- `Triple<A, B, C>.toImmutableTriple(): ImmutableTriple<A, B, C>`
- `Triple<A, B, C>.toMutableTriple(): MutableTriple<A, B, C>`
- `CommonsTriple<L, M, R>.toKotlinTriple(): Triple<L, M, R>`

**Destructuring operators** (`component1`, `component2`, `component3`) on `CommonsPair` and
`CommonsTriple`, covering all subclasses (both immutable and mutable).

### `org.apache.commons.lang3.reflect.TypeLiteral`

`typeLiteralOf<T>(): TypeLiteral<T>` — inline reified factory eliminating the anonymous subclass
boilerplate required to capture a generic type token at runtime.

### `org.apache.commons.lang3.reflect.TypeUtils`

- `buildWildcardType { ... }: WildcardType` — DSL wrapper over `TypeUtils.WildcardTypeBuilder`.
- `parameterizedTypeOf(KClass<*>, vararg Type): ParameterizedType` — `KClass`-accepting wrapper
  for `TypeUtils.parameterize`.
- `parameterizedTypeOf<T>(vararg Type): ParameterizedType` — inline reified overload.

---

## Better served by pure Kotlin

These areas of Commons Lang 3 are not covered by this module because the functionality has been
reimplemented in **kotlin-core** without any Commons dependency, available on all Kotlin targets.

### `org.apache.commons.lang3.EnumUtils` → `kotlin-core`

`isValidEnum`, `getEnum`, and `getEnumIgnoreCase` (the three highest-value operations, with
optional case-insensitive matching) are available in `kotlin-core` as `isValidEnum<E>()`,
`enumValueOfOrNull<E>()`, and `enumValueOfOrDefault<E>()`. All are inline reified functions
backed by `enumValues<E>()` with no JVM dependency and no reflection overhead.

### `org.apache.commons.lang3.Conversion` → `kotlin-core`

The bit-packing and unpacking operations are covered by `ConversionExtensions` and
`UnsignedConversionExtensions` in `kotlin-core`. The reimplementation:

- Renames functions to follow Kotlin's `toXxx()` convention (`BooleanArray.toByte()` rather than
  `binaryToByte()`, `Int.toByteArray()` rather than `intToByteArray()`, and so on).
- Unpack functions return new arrays instead of requiring a pre-allocated destination buffer.
- Extends coverage to unsigned types (`UByte`, `UShort`, `UInt`, `ULong`).
- Fixes a defect in the original Kotlin wrapper where `ByteArray.toLong()` defaulted to
  `Int.SIZE_BYTES` (4) instead of `Long.SIZE_BYTES` (8).

### `org.apache.commons.lang3.BitField` → `kotlin-core`

Property delegation for bit fields and boolean flags (`ByteBitFieldDelegate`, `IntFlagDelegate`,
etc.) is superseded by `BitCollectionDelegates` in `kotlin-core`. The `kotlin-core` approach is
strictly more capable: it is generic over any type implementing `BitCollection<T>` (including
`Long`, `UInt`, `ULong`, and custom types), requires no Commons dependency, accepts plain getter
and setter lambdas rather than property references, supports an explicit snapshot-vs-live
distinction, and provides `bitRange`/`mutableBitRange` for extracting multi-bit sub-fields.

### `org.apache.commons.lang3.mutable.*`

`MutableObject<T>`, `MutableInt`, `MutableByte`, and the rest of the mutable wrapper types as
property delegates are superseded by `var`. The only remaining use case — passing these types as
in-out parameters in Java-interop calls — does not benefit from a property-delegate wrapper.

### `org.apache.commons.lang3.concurrent` (initializers)

`AtomicInitializer`, `AtomicSafeInitializer`, and `ConstantInitializer` as property delegates
are superseded by Kotlin's `lazy`. The threading modes `LazyThreadSafetyMode.PUBLICATION`
(publish-one-winner, equivalent to `AtomicInitializer`) and `LazyThreadSafetyMode.SYNCHRONIZED`
(exactly-once, equivalent to `AtomicSafeInitializer`) cover the same ground without a Commons
dependency.

---

## Not currently covered

These areas of Commons Lang 3 are not addressed by this module. In some cases a clear idiomatic
Kotlin alternative exists; in others the functionality is too niche to justify extensions.

| Area | Reason not covered |
|---|---|
| `Validate` | Superseded by Kotlin's `require`, `check`, and `error`. |
| `StringUtils` | Kotlin's `String`/`CharSequence` extensions cover the majority of the API. The remainder (abbreviation, wrapping, Levenshtein distance) is niche enough that no extensions are warranted. |
| `builder.*` (`EqualsBuilder`, `HashCodeBuilder`, `ToStringBuilder`, `CompareToBuilder`) | Superseded by Kotlin `data class` for value types; standard `Objects.hash` and manual implementations for other cases. |
| `NumberUtils` safe-parse overloads | Superseded by `String.toIntOrNull() ?: default` and its equivalents. |
| `BooleanUtils` | Superseded by Kotlin null-safety and standard boolean operations. |
| `CharUtils` | Covered by Kotlin's `Char` extensions. |
| `ObjectUtils` | Superseded by Kotlin null-safety operators and extension functions. |
| `reflect.FieldUtils` / `MethodUtils` / `ConstructorUtils` | Reflection delegates (`ReflectedFieldDelegate`) were evaluated and excluded. The use case — accessing private fields of external frameworks — is too specific to warrant library support. Kotlin's own `KClass` reflection API covers the same ground. |
| `tuple.Pair` / `Triple` as primary types | Commons `MutablePair`/`MutableTriple` are not encouraged as alternatives to Kotlin's built-in `Pair`/`Triple`; mutable structured data is better expressed as a data class. The extensions in this module exist for interop with code that already returns these types. |
| `concurrent.ConcurrentMap` extensions | `createIfAbsent` is only useful alongside `ConcurrentInitializer`, which is superseded by `lazy`; `putIfAbsentAtomic` is a rename of `ConcurrentMap.putIfAbsent` with no substantive difference. |
| `text.*` | Largely deprecated upstream. |
| `time.*` | Superseded by `java.time`. |
| `math.IEEE754rUtils` | Covered by Kotlin's floating-point semantics and `kotlin-core`. |
| `exception.*`, `event.*`, `SystemUtils`, `StopWatch`, `RandomUtils`, `RandomStringUtils`, `SerializationUtils` | Niche utilities; no additional value currently provided. |
