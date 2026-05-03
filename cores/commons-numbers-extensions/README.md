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

**kotlin-core bridge** (`DdBridge.kt`):
- `DD.toDoubleDouble(): DoubleDouble` — transfers the (hi, lo) pair directly; NaN maps to
  `DoubleDouble.NaN` (avoiding the `|hi| ≥ |lo|` precondition check in `DoubleDouble.create`).
- `DoubleDouble.toDD(): DD` — uses `DD.ofSum(high, low)` (TwoSum); preserves the mathematical
  value. Structural (hi, lo) identity is guaranteed only when `|low| < ulp(high) / 2` strictly.
- `DoubleDouble.Companion.ddConverter: Converter<DD, DoubleDouble>` — bidirectional converter.

**kotlin-core trait instances** (`DdFloatingPointArithmetic.kt`):
- `FloatingPointArithmetic.Companion.dd: FloatingPointArithmetic<DD>` — delegates `add`,
  `subtract`, `multiply`, `divide`, `negate`, `abs`, `isFinite`, and `isZero` to `DD`'s Java
  methods. `isNaN` and `isInfinite` are derived from `hi()`. `isNegative` uses `hi()`'s sign bit.
  `isInteger` performs a bit-pattern check on both components (same logic as `DoubleDouble`).
  `compareTo` uses a NaN-last, hi-then-lo total ordering. Only defined for finite inputs; DD's
  two-sum algorithm produces NaN when given non-finite intermediate values.
- `FloatingPointSquareRoot.Companion.dd: FloatingPointSquareRoot<DD>` — delegates to `DD.sqrt()`.
- `FloatingPointRounding.Companion.dd: FloatingPointRounding<DD>` — delegates `floor` and `ceil`
  to `DD.floor()` and `DD.ceil()`.
- `FloatingPointScalb.Companion.dd: FloatingPointScalb<DD>` — delegates to `DD.scalb(Int)`;
  computes `x × 2^n` by scaling both the high and low components independently.

**kotlin-core trait coverage for `DD`:**

Not all kotlin-core composable traits have `DD` instances. The table below maps each trait to its
implementation status.

| kotlin-core trait | `DD` instance | Notes |
|---|:---:|---|
| `FloatingPointArithmetic<T>` | ✓ | `FloatingPointArithmetic.dd` |
| `FloatingPointSquare<T>` | ✓ | `FloatingPointSquare.dd` — delegates to `DD.multiply(this)` |
| `FloatingPointSquareRoot<T>` | ✓ | `FloatingPointSquareRoot.dd` — delegates to `DD.sqrt()` |
| `FloatingPointRounding<T>` | ✓ | `FloatingPointRounding.dd` — delegates to `DD.floor()` / `DD.ceil()` |
| `FloatingPointScalb<T>` | ✓ | `FloatingPointScalb.dd` — delegates to `DD.scalb(Int)` |
| `IntegerPower<T>` | ✓ * | `IntegerPower.dd` — binary exponentiation via `DD.multiply()` |
| `FloatingPointRemainder<T>` | — | No `DD` remainder operation is exposed by Commons Numbers |
| `FusedMultiplyAdd<T>` | — | `DD` has no hardware FMA path; software emulation would be circular |

\* `DD` has a Java member `pow(int n)` that handles negative exponents by returning the reciprocal,
rather than throwing. At concrete call sites `DD.pow(-1)` calls the Java member and returns
`this.reciprocal()`, not `IllegalArgumentException`. The trait's `require(n >= 0)` guard only
fires in generic dispatch contexts where the extension cannot be shadowed.

### `org.apache.commons.numbers.complex.Complex`

**`Complex` operators** (`ComplexExtensions.kt`):
- `component1()`, `component2()` — destructuring into real and imaginary parts.
- `unaryPlus()`, `unaryMinus()`.
- `plus(Double)`, `plus(Complex)`.
- `minus(Double)`, `minus(Complex)`.
- `times(Double)`, `times(Complex)`.
- `div(Double)`, `div(Complex)`.

Trait instances (`FloatingPointArithmetic`, `ComplexArithmetic`, etc.) are not provided directly
for Commons `Complex`; convert via `toKotlinComplex()` to access kotlin-core's instances instead.

**kotlin-core bridge** (`ComplexBridge.kt`, `ComplexValueEquality.kt`):
- `Complex.toKotlinComplex(): kotlin-core Complex<Double>` — lossless conversion.
- `Complex<Double>.toCommonsComplex(): Complex` — lossless conversion.
- `commonsComplexConverter: Converter<Complex, Complex<Double>>` — top-level bidirectional
  converter (no companion object on `Complex<T>`).
- `commonsComplexNumericalEquality: ValueEquality<Complex>` — IEEE 754 component equality:
  NaN ≠ NaN, +0 = −0.
- `commonsComplexEquivalenceEquality: ValueEquality<Complex>` — delegates to `Complex.equals()`,
  which uses `Double.doubleToLongBits`: NaN = NaN, +0 ≠ −0; consistent with `hashCode`.

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

`RationalArithmetic` instances are not provided directly for `Fraction` or `BigFraction`; convert
via `toRational()` to access kotlin-core's instances instead.

**kotlin-core bridges** (`FractionBridge.kt`, `BigFractionBridge.kt`):
- `Fraction.toRational(): Rational<Int>` — converts to a normalised `Rational<Int>` (positive
  denominator, fully reduced). Note that `Fraction.of` does not guarantee a positive denominator,
  so the `Rational` may have a negated numerator relative to the original `Fraction`.
- `Rational<Int>.toFraction(): Fraction` — creates a `Fraction` from a canonical `Rational<Int>`.
- `Rational.fractionConverter: Converter<Fraction, Rational<Int>>` — bidirectional converter.
- `BigFraction.toRational(): Rational<BigInteger>` — same semantics as the `Fraction` variant.
- `Rational<BigInteger>.toBigFraction(): BigFraction`.
- `Rational.bigFractionConverter: Converter<BigFraction, Rational<BigInteger>>`.

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
