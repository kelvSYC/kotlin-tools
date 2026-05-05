# kotlin-core

Core numeric types, collection extensions, and trait-based abstractions for Kotlin Multiplatform.

## Major Areas

### Numeric Types

Extended-precision and specialized numeric types:

- **`Float16` / `BFloat16`** — 16-bit floating-point values with array and iterator support
- **`BidFloat` / `BidDouble` / `DpdFloat` / `DpdDouble`** — 32-bit and 64-bit decimal floating-point in BID and DPD encodings
- **`Complex<T>`** — Generic complex number container (real + imaginary components)
- **`Rational<T>`** — Rational numbers in canonical form (gcd = 1, positive denominator)
- **`PartialComparator`** — Comparisons where incomparability is valid (e.g., NaN)

### Collections

Extended collection types beyond the Kotlin standard library:

- **Multisets** (`Multiset`, `ListMultiset`, `SetMultiset`) — collections that track element occurrence counts; mutable variants available
- **Multimaps** (`FlatMultimap`, `ListMultimap`, `SetMultimap`) — maps associating keys with multiple values; mutable variants available
- **EnumMap** (`EnumMap`, `MutableEnumMap`) — array-backed `Map` keyed by enum constants, iterating in ordinal order; supports nullable values and live-backed views; all-platforms alternative to `java.util.EnumMap`
- **EnumListMultimap** (`EnumListMultimap`, `MutableEnumListMultimap`) — array-backed `ListMultimap` keyed by enum constants, iterating in ordinal order; `asMap` returns an `EnumMap<K, List<V>>` live view
- **EnumSetMultimap** (`EnumSetMultimap`, `MutableEnumSetMultimap`) — array-backed `SetMultimap` keyed by enum constants, iterating in ordinal order; `asMap` returns an `EnumMap<K, Set<V>>` live view
- **EnumSetMultiset** (`EnumSetMultiset`, `MutableEnumSetMultiset`) — `IntArray`-backed `SetMultiset` of enum constants, iterating in ordinal order; `asMap` returns an `EnumMap<K, Int>` live view
- **PriorityQueue** (`PriorityQueue<T>`) — binary min-heap implementing `MutableCollection<T>`, ordered by a caller-supplied `Comparator`; supports `peek`/`poll`, composite `addOrPoll`/`pollOrAdd`, `drainSorted`/`toSortedList`, bulk `removeAll`/`retainAll` with O(n) re-heapification, and O(n) bottom-up construction via `heapify`; companion top-level extensions include `nSmallest`/`nLargest` (O(n log k) top-K selection) and `mergeSorted` (lazy k-way merge of pre-sorted iterables); all-platforms alternative to `java.util.PriorityQueue`

### Floating-Point Representations (`fp`)

Structural representations for floating-point values:

- **`FiniteBinaryFloatingPoint<T>`** — raw sign/exponent/significand view of a finite IEEE binary value; intentionally carries no trait instances, as it is a structural representation rather than a numeric type (arithmetic and ordering belong to the native type it was decoded from)
- **`FiniteDecimalFloatingPoint<T>`** — raw sign/exponent/significand (cohort) view of a finite decimal floating-point value; same design intent as `FiniteBinaryFloatingPoint` — no trait instances by design
- **`DoubleBinaryFloatingPoint<T>`** — two-component high/low representation for extended precision
- **`DoubleDouble`** — extended-precision arithmetic using a pair of `Double` values

### Traits

Type-level abstractions defining operations for numeric types. All concrete types are plain data containers; operations are provided through trait instances.

#### Binary Floating-Point (`traits/fp`)
- `BinaryFloatingPoint<T>` — metadata and equality semantics (IEEE and compound formats)
- `IeeeBinaryFloatingPoint<T>` — IEEE 754-specific metadata (sign bits, exponent/significand widths)
- `FloatingPointArithmetic<T>` — standard math operations (+, −, ×, ÷, sqrt, remainder, etc.)
- `FloatingPointClassification<T>` — NaN / infinity detection
- `FloatingPointRounding<T>` — floor and ceiling (instances for `BFloat16`, `Float16`, `Float`, `Double`, `DoubleDouble`)
- `FloatingPointScalb<T>` — binary scaling × 2^n (instances for `BFloat16`, `Float16`, `Float`, `Double`, `DoubleDouble`)
- `FusedMultiplyAdd<T>` — fused multiply-add with accurate rounding
- Sub-interfaces: `Binary16<T>`, `Binary32<T>`, `Binary64<T>` — format-specific specializations (e.g. `Float16` implements `Binary16<Float16>` via its companion object)

#### Decimal Floating-Point (`traits/dfp`)
- `BidFloatArithmetic` / `BidDoubleArithmetic` — arithmetic for BID-encoded decimal types
- `DecimalFloatingPointEncoding`, `DecimalFloatingPointCohorts` — decimal structure and cohort classification
- `FloatingPointScald<T>` — decimal scaling × 10^n (instances for `BidFloat`, `BidDouble`, `DpdFloat`, `DpdDouble`; in `traits/fp`)

#### Integral / Bitwise (`traits/integral`)
- `Bitwise<T>` — AND, OR, XOR, NOT
- `BitShift<T>` / `ArithmeticRightShift<T>` — logical and arithmetic shifts
- `BitCollection<T>` — bit manipulation and population count
- Sub-interfaces: `Int8<T>`–`Int64<T>` / `UInt8<T>`–`UInt64<T>` — width-specific signed and unsigned specializations

#### Complex Numbers (`traits/complex`)
- `ComplexArithmetic<C, T>` — arithmetic operations on complex type `C` with component type `T`; defaults for `add`/`subtract`/`negate`/`conjugate`; `multiply`/`divide` remain abstract (naive vs strict are fundamentally different algorithms). Supports naive (textbook) and strict (FMA + Annex G) variants for `Complex<Float>` and `Complex<Double>`.
- `ComplexModulus<C, T>` — modulus operations (`squaredModulus`, `modulus`) on complex type `C`
- `ImaginaryArithmetic<T>` — arithmetic on `Imaginary<T>` values

#### Rational Numbers (`traits/rational`)
- `RationalArithmetic<R, T>` — arithmetic and normalization for rational type `R` with component type `T`; `R` may be `Rational<T>` or any compatible rational representation (e.g. `Fraction`, `BigFraction` from commons-numbers-extensions)
- `RationalNumber<R, T>` — structural queries (zero/sign/reciprocal) over a rational type `R`

#### Double-Double (`traits/dd`)
- `DoubleDoubleArithmetic` — operations on high/low pairs
- `TwoSum`, `TwoProduct`, `TwoDivision` — building blocks for extended-precision arithmetic

#### Cross-cutting (`traits/`)

These traits are not specific to any numeric domain and can be implemented independently or composed
into larger traits:

- `Addition<T>` — additive identity (`zero`) and `add`/`subtract`; extended by `IntegerArithmetic`
  and `FloatingPointArithmetic`, so all their instances automatically satisfy `Addition<T>`
- `Multiplication<T>` — multiplicative identity (`one`) and `multiply`; same
- `Division<T>` — `divide` with implementation-defined semantics (truncating for integers, IEEE 754
  for floating-point, exact for rationals and complex)
- `Signed<T>` — sign queries (`isNegative`, `isPositive`), `negate`, and `abs`; extended by
  `SignedIntegerArithmetic` and `FloatingPointSign`; JVM instances for `BigInteger` and `BigDecimal`

#### Common (`traits/common`)
- `ValueEquality<T>` — distinguishes structural equality from IEEE numerical equality

### Composable trait instances

Not every trait has an instance for every numeric type. The tables below show which
companion-object instances are provided out of the box. Entries marked **JVM** are only
available on the JVM target; entries marked **—** have no built-in instance (but may be
constructible via a factory function).

#### Binary floating-point

Types: `BFloat16`, `Float16`, `Float`, `Double`, `DoubleDouble`

| Trait | `BFloat16` | `Float16` | `Float` | `Double` | `DoubleDouble` |
|---|:---:|:---:|:---:|:---:|:---:|
| `FloatingPointArithmetic<T>` | ✓ | ✓ | ✓ | ✓ | JVM ¹ |
| `FloatingPointSquare<T>` | ✓ | ✓ | ✓ | ✓ | ✓ |
| `FloatingPointSquareRoot<T>` | ✓ | ✓ | ✓ | ✓ | — |
| `FloatingPointRounding<T>` | ✓ | ✓ | ✓ | ✓ | ✓ |
| `FloatingPointScalb<T>` | ✓ | ✓ | ✓ | ✓ | ✓ |
| `FloatingPointRemainder<T>` | ✓ ² | ✓ ² | ✓ ² | ✓ ² | — |
| `FusedMultiplyAdd<T>` | JVM ³ | JVM ³ | JVM ³ | JVM ³ | — |
| `IntegerPower<T>` | ✓ | ✓ | ✓ | ✓ | ✓ |

¹ `DoubleDouble` is exposed as `DoubleBinaryFloatingPointArithmetic<DoubleDouble, Double>` — a
sub-interface of `FloatingPointArithmetic<DoubleDouble>` — via
`DoubleBinaryFloatingPointArithmetic.Companion.doubleDouble`. Internally it uses FMA for
`TwoProduct`; no non-JVM instance is currently provided.

² Two variants exist per type: an IEEE 754 nearest-integer remainder (e.g.
`FloatingPointRemainder.floatIeee754`) and a truncating remainder matching Kotlin's `%` operator
(e.g. `FloatingPointRemainder.floatTruncating`).

³ The built-in `expect`/`actual` instances delegate to `java.lang.Math.fma` on JVM. A software
emulation that satisfies the same contract — the Boldo-Melquiond algorithm — is available on all
platforms via `FusedMultiplyAdd.Companion.from(arith, twoProduct, twoSum)`, though it cannot
recover a finite result when `a × b` overflows to infinity.

#### Decimal floating-point

Types: `BidFloat` (Decimal32 BID), `BidDouble` (Decimal64 BID), `DpdFloat` (Decimal32 DPD),
`DpdDouble` (Decimal64 DPD)

| Trait | `BidFloat` | `BidDouble` | `DpdFloat` | `DpdDouble` |
|---|:---:|:---:|:---:|:---:|
| `FloatingPointArithmetic<T>` | ✓ | ✓ | ✓ ⁴ | ✓ ⁴ |
| `FloatingPointSquare<T>` | ✓ | ✓ | ✓ ⁴ | ✓ ⁴ |
| `FloatingPointSquareRoot<T>` | ✓ | ✓ | ✓ ⁴ | ✓ ⁴ |
| `FloatingPointRemainder<T>` | ✓ ² | ✓ ² | ✓ ²⁴ | ✓ ²⁴ |
| `FusedMultiplyAdd<T>` | ✓ | ✓ | ✓ ⁴ | ✓ ⁴ |
| `FloatingPointScald<T>` | ✓ | ✓ | ✓ ⁴ | ✓ ⁴ |
| `IntegerPower<T>` | ✓ | ✓ | ✓ ⁴ | ✓ ⁴ |
| `FloatingPointRounding<T>` | — | — | — | — |
| `FloatingPointScalb<T>` | — | — | — | — |

⁴ DPD instances convert to the corresponding BID encoding, apply the BID operation, and convert
back. They depend on the BID instance for the same operation.

#### Double-double building blocks

These traits are composable primitives for building extended-precision arithmetic. They only make
sense for floating-point types with an exact error representation.

| Trait | `Float` | `Double` | Dependencies |
|---|:---:|:---:|---|
| `TwoSum<T>` | ✓ | ✓ | `FloatingPointArithmetic<T>` |
| `TwoProduct<T>` | ✓ ⁵ | ✓ ⁵ | `FloatingPointArithmetic<T>` + `IeeeBinaryFloatingPoint<T>` |
| `TwoDiv<T>` | JVM | JVM | `FloatingPointArithmetic<T>` + `FusedMultiplyAdd<T>` |
| `DoubleBinaryFloatingPointArithmetic<F, T>` | — | JVM ⁶ | `TwoProduct<T>`, `TwoSum<T>`, optionally `TwoDiv<T>` |

⁵ The companion instances (`TwoProduct.float`, `TwoProduct.double`) use Veltkamp-Dekker splitting,
which requires only ordinary floating-point arithmetic and works on all platforms. An FMA-backed
alternative — one multiply plus one FMA instead of seventeen operations — is available via
`TwoProduct.Companion.from(arith, fma)`.

⁶ `DoubleBinaryFloatingPointArithmetic.Companion.doubleDouble` is JVM-only and uses an FMA-backed
`TwoProduct` and `TwoDiv` internally. The factory `DoubleBinaryFloatingPointArithmetic.Companion.from`
is available on all platforms and accepts any compatible `TwoProduct` implementation, including the
Veltkamp-Dekker one.

#### Integer

Signed: `Byte`, `Short`, `Int`, `Long`, `BigInteger` (JVM only)
Unsigned: `UByte`, `UShort`, `UInt`, `ULong`

| Trait | Signed ⁷ | Unsigned | `BigInteger` |
|---|:---:|:---:|:---:|
| `SignedIntegerArithmetic<T>` | ✓ | — | JVM |
| `UnsignedIntegerArithmetic<T>` | — | ✓ | — |
| `OverflowCheckedArithmetic<T>` | `Int`, `Long` (JVM) | — | — |
| `Gcd<T>` | ✓ | ✓ | — |
| `Bitwise<T>` | `Int`, `Long` | `UShort`, `UInt`, `ULong` | — |
| `BitShift<T>` | ✓ | ✓ | — |
| `ArithmeticRightShift<T>` | ✓ | — | JVM |
| `RoundingRightShift<T>` | ✓ | ✓ | — |
| `StickyRightShift<T>` | ✓ | ✓ | — |
| `PowerOfTwo<T>` | ✓ | ✓ | — |
| `Sqrt<T>` (integer) | ✓ | ✓ | — |
| `Log2<T>` | ✓ | ✓ | — |
| `Log10<T>` | ✓ | ✓ | — |
| `Primality<T>` | `Int`, `Long` | `UInt`, `ULong` (JVM) | JVM |
| `IntegerPower<T>` | `Int`, `Long` | — | JVM |

⁷ All four signed primitive types (`Byte`, `Short`, `Int`, `Long`) unless noted otherwise.

#### BigDecimal (JVM only)

`BigDecimal` is not an integer type and does not fit IEEE 754 floating-point semantics (no NaN, no
infinity), so it implements a subset of traits drawn from both domains. All instances below are
JVM-only.

| Trait | `BigDecimal` | Notes |
|---|:---:|---|
| `Addition<T>` | ✓ | `Addition.bigDecimal` |
| `Multiplication<T>` | ✓ | `Multiplication.bigDecimal` |
| `Division<T>` | ✓ ⁸ | `Division.bigDecimal` (exact); `Division.bigDecimal(MathContext)` (factory) |
| `Signed<T>` | ✓ | `Signed.bigDecimal` |
| `FloatingPointRounding<T>` | ✓ | `FloatingPointRounding.bigDecimal` — result has scale 0 |
| `FloatingPointScald<T>` | ✓ | `FloatingPointScald.bigDecimal` — delegates to `scaleByPowerOfTen` |
| `IntegerPower<T>` | ✓ | `IntegerPower.bigDecimal` — delegates to `BigDecimal.pow(int)` |
| `ValueEquality<T>` | ✓ | `ValueEquality.bigDecimalNumerical` (ignores scale); `ValueEquality.bigDecimalEquivalence` (scale-sensitive) |
| `FloatingPointArithmetic<T>` | — | No NaN or infinity; does not satisfy the FP contract |
| `IntegerArithmetic<T>` | — | Not an integer type |
| `FloatingPointScalb<T>` | — | Binary scaling (×2ⁿ) is not natural for a decimal type; use `FloatingPointScald` instead |

⁸ The exact `Division.bigDecimal` instance delegates to `BigDecimal.divide(BigDecimal)`, which
throws `ArithmeticException` for non-terminating decimal expansions (e.g. `1 / 3`). Use
`Division.bigDecimal(MathContext)` to supply an explicit precision and rounding mode.

#### Rational (kotlin-core types)

`Rational<T>` instances are available for four component types. Overflow behaviour depends on the
arithmetic instance: wrapping for primitives, overflow-checked or overflow-free for JVM types.

| Instance | Type | Notes |
|---|---|---|
| `RationalArithmetic.int` | `RationalArithmetic<Rational<Int>, Int>` | Wrapping arithmetic; cross-multiplications may silently overflow |
| `RationalArithmetic.long` | `RationalArithmetic<Rational<Long>, Long>` | Same; use `checkedLong` on JVM to detect overflow |
| `RationalArithmetic.checkedInt` | `RationalArithmetic<Rational<Int>, Int>` | JVM only; throws `ArithmeticException` on overflow |
| `RationalArithmetic.checkedLong` | `RationalArithmetic<Rational<Long>, Long>` | JVM only |
| `RationalArithmetic.bigInteger` | `RationalArithmetic<Rational<BigInteger>, BigInteger>` | JVM only; overflow-free |

The `from(arithmetic, gcd)` factory constructs a `RationalArithmetic<Rational<T>, T>` for any
`SignedIntegerArithmetic<T>` and matching `Gcd<T>`. For other rational types (e.g. `Fraction`,
`BigFraction`), see the commons-numbers-extensions module.

#### Cross-cutting granular traits

`Addition<T>`, `Multiplication<T>`, and `Division<T>` are satisfied by every type that has an
`IntegerArithmetic`, `SignedIntegerArithmetic`, `UnsignedIntegerArithmetic`, or
`FloatingPointArithmetic` instance — no separate companion property is needed. `Signed<T>` is
satisfied by every type that has a `SignedIntegerArithmetic` instance, and additionally by
`BigInteger` and `BigDecimal` via dedicated JVM-only instances (`Signed.bigInteger`,
`Signed.bigDecimal`).
