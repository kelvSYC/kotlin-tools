# commons-numbers-extensions

Kotlin extensions for [Apache Commons Numbers](https://commons.apache.org/proper/commons-numbers/).
This module provides idiomatic wrappers and extension functions for Commons Numbers types that are
worth using alongside Kotlin; it does not re-export Commons Numbers itself, so the relevant
`commons-numbers-*` dependency is also required in your build.

Not every area of Commons Numbers is addressed here. Some parts are better served by pure Kotlin
(including other modules in this project); others add no meaningful value over the Commons API
as-is. This document maps each area of the library to one of three categories.

---

## Covered by this module

### `org.apache.commons.numbers.core` — algebraic interfaces

Generic operator overloads for the three algebraic interfaces in `commons-numbers-core`, so that
any type implementing them gets Kotlin operator syntax for free.

**`Addition<T>`** (`AdditionExtensions.kt`):
- `unaryPlus()` — identity (`+x`).
- `plus(rhs: T)` — delegates to `add`.
- `unaryMinus()` — delegates to `negate`.
- `minus(rhs: T)` — implemented as `add(-rhs)` (negate then add).

**`Multiplication<T>`** (`MultiplicationExtensions.kt`):
- `times(rhs: T)` — delegates to `multiply`.
- `div(rhs: T)` — implemented as `multiply(rhs.reciprocal())`.

**`NativeOperators<T>`** (`NativeOperatorsExtensions.kt`):
Overloads for types that expose `subtract`/`divide` directly, taking precedence over the more
general `Addition`/`Multiplication` overloads when both are in scope:
- `minus(rhs: T)` — delegates to `subtract`.
- `times(rhs: Int)` — delegates to `multiply(Int)`.
- `div(rhs: T)` — delegates to `divide`.

### `org.apache.commons.numbers.core.DD`

**`DD` operators** (`DdExtensions.kt`):
- `unaryPlus()`, `unaryMinus()`.
- `plus(Double)`, `plus(DD)`.
- `minus(Double)`, `minus(DD)`.
- `times(Int)`, `times(Double)`, `times(DD)`.
- `div(Double)`, `div(DD)`.

**Primitive conversions to `DD`** (`DoubleExtensions.kt`, `IntExtensions.kt`, `LongExtensions.kt`):
- `Double.toDD()`, `Int.toDD()`, `Long.toDD()`.

### `org.apache.commons.numbers.complex.Complex`

**`Complex` operators** (`ComplexExtensions.kt`):
- `component1()`, `component2()` — destructuring into real and imaginary parts.
- `unaryPlus()`, `unaryMinus()`.
- `plus(Double)`, `plus(Complex)`.
- `minus(Double)`, `minus(Complex)`.
- `times(Double)`, `times(Complex)`.
- `div(Double)`, `div(Complex)`.

**Primitive extensions relating to `Complex`** (`DoubleExtensions.kt`):
- `Double.i` — creates `Complex.ofCartesian(0.0, this)`, enabling the `3.0 + 5.0.i` idiom.
- `operator Double.minus(rhs: Complex)` — enables `1.0 - complex` (delegates to
  `Complex.subtractFrom`).

### `org.apache.commons.numbers.fraction` — `Fraction` and `BigFraction`

**`Fraction` operators** (`FractionExtensions.kt`):
- `component1()`, `component2()` — destructuring into numerator and denominator.
- `unaryPlus()`, `unaryMinus()`.
- `plus(Int)`, `plus(Fraction)`.
- `minus(Int)`, `minus(Fraction)`.
- `times(Int)`, `times(Fraction)`.
- `div(Int)`, `div(Fraction)`.
- `toBigFraction()` — converts to `BigFraction`.

**`BigFraction` operators** (`BigFractionExtensions.kt`):
- `component1()`, `component2()` — destructuring into numerator and denominator.
- `unaryPlus()`, `unaryMinus()`.
- `plus(Int)`, `plus(Long)`, `plus(BigInteger)`, `plus(BigFraction)`.
- `minus(Int)`, `minus(Long)`, `minus(BigInteger)`, `minus(BigFraction)`.
- `times(Int)`, `times(Long)`, `times(BigInteger)`, `times(BigFraction)`.
- `div(Int)`, `div(Long)`, `div(BigInteger)`, `div(BigFraction)`.

**Primitive conversions to `Fraction`/`BigFraction`**:
- `Double.toFraction()`, `Double.toBigFraction()` — via `Fraction.from` / `BigFraction.from`.
- `Int.toFraction()`, `Int.toBigFraction()`.
- `Long.toBigFraction()`.
- `BigInteger.toBigFraction()`.

**String parsing** (`StringExtensions.kt`):
- `String.toFraction()` — throws `NumberFormatException` on invalid input.
- `String.toFractionOrNull()` — returns `null` on invalid input.
- `String.toBigFraction()` — throws `NumberFormatException` on invalid input.
- `String.toBigFractionOrNull()` — returns `null` on invalid input.

---

## Better served by pure Kotlin

*(None identified.)*

---

## Not currently covered

| Area | Reason not covered |
|---|---|
| `commons-numbers-angle` | `Angle` is a thin wrapper with no arithmetic operators; the API is already concise. |
| `commons-numbers-arrays` | Utility methods for sorting and statistics; no meaningful wrapping to add. |
| `commons-numbers-combinatorics` | `BinomialCoefficient`, `Factorial`, `Stirling` — functional APIs with no natural operator mapping. |
| `commons-numbers-gamma` | Gamma and related special functions; no idiomatic Kotlin gap to fill. |
| `commons-numbers-primes` | `Primes` utility class; self-contained with a clean API. |
| `commons-numbers-quaternion` | `Quaternion` — candidate for future coverage (operators, destructuring). |
| `commons-numbers-rootfinder` | `UnivariateSolver` — functional API; no Kotlin wrapper warranted. |
