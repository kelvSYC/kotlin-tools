# decimal-extensions

Kotlin/JS extensions for [Decimal.js](https://mikemcl.github.io/decimal.js/), providing
arbitrary-precision decimal arithmetic with idiomatic Kotlin operators, `Converter<A,B>` bridges
to Kotlin primitives, and `kotlin-core` trait instances that make `Decimal` participate in the
same trait ecosystem as JVM `BigDecimal`.

**Platform:** JS only.

**Dependencies consumers need:** `decimal.js` (npm) and `com.kelvsyc.kotlin:kotlin-core` are
both `api` dependencies and must be on the consumer's classpath.

---

## The `Decimal` type

`Decimal` is a Kotlin `external class` backed by Decimal.js's own `Decimal` constructor.
Instances are immutable — every arithmetic method returns a new `Decimal`.

Supported constructors:
```kotlin
Decimal("1.23456789012345678901234567890")  // string — lossless
Decimal(1.5)                                // Double — loses precision beyond ~17 digits
Decimal(42)                                 // Int
Decimal(existing)                           // copy from another Decimal
```

For `Long` values, use `Long.toDecimal()` (converts via string, preserving all digits).

---

## Arithmetic operators

```kotlin
val a = Decimal("1.5")
val b = Decimal("2.5")

a + b   // Decimal("4")
a - b   // Decimal("-1")
a * b   // Decimal("3.75")
a / b   // Decimal("0.6")
a % b   // Decimal("1.5")
-a      // Decimal("-1.5")
```

---

## Conversions

Each pair provides a `Converter<A, Decimal>` val plus convenience extension functions.

| Kotlin type | Function | Converter | Notes |
|---|---|---|---|
| `Double` | `toKotlinDouble()` / `Double.toDecimal()` | `decimalToDouble` | Lossy beyond ~17 significant digits |
| `String` | `toKotlinString()` / `String.toDecimal()` | `decimalToString` | Lossless round-trip |
| `Long` | `toKotlinLong()` / `Long.toDecimal()` | `decimalToLong` | Safe only when `isInteger()` holds |

---

## Rounding

`DecimalRounding` is a Kotlin enum mirroring Decimal.js's 10 rounding modes:

| Enum value | Code | Description |
|---|---|---|
| `UP` | 0 | Away from zero |
| `DOWN` | 1 | Toward zero |
| `CEIL` | 2 | Toward +∞ |
| `FLOOR` | 3 | Toward −∞ |
| `HALF_UP` | 4 | Half away from zero (default) |
| `HALF_DOWN` | 5 | Half toward zero |
| `HALF_EVEN` | 6 | Half to even (banker's rounding) |
| `HALF_CEIL` | 7 | Half toward +∞ |
| `HALF_FLOOR` | 8 | Half toward −∞ |
| `EUCLID` | 9 | As per Euclidean division |

Kotlin-friendly overloads accept the enum directly:

```kotlin
Decimal("1.555").toDecimalPlaces(2, DecimalRounding.HALF_UP)  // Decimal("1.56")
Decimal("1.2345").toSignificantDigits(3, DecimalRounding.HALF_EVEN)  // Decimal("1.23")
```

Shorthand methods for common cases: `ceil()`, `floor()`, `round()`, `truncated()`.

---

## kotlin-core trait instances

`Decimal` participates in the `kotlin-core` trait ecosystem, mirroring JVM `BigDecimal`.

### Arithmetic traits

```kotlin
Addition.decimal         // Addition<Decimal>     zero = Decimal("0")
Multiplication.decimal   // Multiplication<Decimal>  one = Decimal("1")
Division.decimal         // Division<Decimal>     uses global Decimal.js precision
Division.decimal(precision: Int, rounding: DecimalRounding)  // independent precision context
Signed.decimal           // Signed<Decimal>       isNegative, negate, abs
```

`Division.decimal(precision, rounding)` uses `Decimal.clone()` to create an independent
constructor with its own precision context — division with this instance never disturbs
global `Decimal.set()` settings.

### Equality

Decimal.js has IEEE-like equality semantics: NaN ≠ NaN, and there is no negative zero.
Decimal.js normalises internally so `Decimal("1.0")` and `Decimal("1.00")` are equal
(no cohort distinction, unlike BID/DPD fixed-width formats).

```kotlin
ValueEquality.decimalNumerical    // NaN ≠ NaN — delegates to Decimal.equals()
ValueEquality.decimalEquivalence  // NaN == NaN — reflexive, suitable for data structures
```

### Comparison

Decimal.js has **partial comparability**: `comparedTo()` returns NaN when either operand is
NaN. Accordingly, `Decimal` provides a `PartialComparator<Decimal>` rather than implementing
`Comparable<Decimal>`:

```kotlin
Decimal.partialComparator.compare(Decimal("1"), Decimal("2"))    // -1
Decimal.partialComparator.compare(Decimal("NaN"), Decimal("1"))  // null

// Total-order Comparator with explicit NaN placement (positive = NaN last):
val cmp: Comparator<Decimal> = Decimal.partialComparator.asComparator(1)
```

---

## Precision and configuration

Decimal.js uses a global precision (default: 20 significant digits). Adjust via:

```kotlin
val config: dynamic = js("{}")
config.precision = 50
config.rounding = DecimalRounding.HALF_EVEN.code
Decimal.set(config)
```

For arithmetic that should not affect global state, use `Division.decimal(precision, rounding)`.
