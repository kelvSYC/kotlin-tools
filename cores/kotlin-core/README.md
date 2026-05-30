# kotlin-core

Core numeric types, collection extensions, and trait-based abstractions for Kotlin Multiplatform
(JVM, JS, and Native targets).

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
- **EnumSet** (`EnumSet`, `MutableEnumSet`) — `BooleanArray`-backed `Set` of enum constants, iterating in ordinal order; all-platforms alternative to `java.util.EnumSet`
- **EnumListMultimap** (`EnumListMultimap`, `MutableEnumListMultimap`) — array-backed `ListMultimap` keyed by enum constants, iterating in ordinal order; `asMap` returns an `EnumMap<K, List<V>>` live view
- **EnumSetMultimap** (`EnumSetMultimap`, `MutableEnumSetMultimap`) — array-backed `SetMultimap` keyed by enum constants, iterating in ordinal order; `asMap` returns an `EnumMap<K, Set<V>>` live view
- **EnumSetMultiset** (`EnumSetMultiset`, `MutableEnumSetMultiset`) — `IntArray`-backed `SetMultiset` of enum constants, iterating in ordinal order; `asMap` returns an `EnumMap<K, Int>` live view
- **PriorityQueue** (`PriorityQueue<T>`) — binary min-heap implementing `MutableCollection<T>`, ordered by a caller-supplied `Comparator`; supports `peek`/`poll`, composite `addOrPoll`/`pollOrAdd`, `drainSorted`/`toSortedList`, bulk `removeAll`/`retainAll` with O(n) re-heapification, and O(n) bottom-up construction via `heapify`; companion top-level extensions include `nSmallest`/`nLargest` (O(n log k) top-K selection) and `mergeSorted` (lazy k-way merge of pre-sorted iterables); all-platforms alternative to `java.util.PriorityQueue`
- **SortedSet / SortedMap** (`SortedSet<E>`, `MutableSortedSet<E>`, `SortedMap<K,V>`, `MutableSortedMap<K,V>`) — comparator-ordered set and map backed by a red-black tree (`TreeSet`, `TreeMap`); `SortedMap.keys` is typed as `SortedSet<K>`, fixing Java's `keySet()` type hole; combines Java's split `SortedMap`/`NavigableMap` into one interface with inclusive/exclusive bound range views (`headSet`/`tailSet`/`subSet`, `headMap`/`tailMap`/`subMap`) that return snapshots rather than live views; floor/ceiling/lower/higher key navigation; all-platforms alternative to `java.util.TreeMap`/`java.util.TreeSet`
- **SortedMultiset** (`SortedMultiset<E>`, `MutableSortedMultiset<E>`) — comparator-ordered multiset backed by a red-black tree; iterator yields elements in comparator order with each distinct element repeated its count times; `elements` is typed as `SortedSet<E>` and `asMap` as `SortedMap<E, Int>`; supports floor/ceiling/lower/higher navigation and inclusive/exclusive bound range views (`headMultiset`/`tailMultiset`/`subMultiset`) that return snapshots; `descendingMultiset()` returns a snapshot in reversed order; factory functions in `SortedMultisets.kt` parallel `SortedSets.kt`
- **SortedListMultimap** (`SortedListMultimap<K,V>`, `MutableSortedListMultimap<K,V>`) — comparator-ordered `ListMultimap` backed by a red-black tree; keys are in comparator order, values per key remain in insertion order; `asMap` is typed as `SortedMap<K, List<V>>`; supports floor/ceiling/lower/higher key navigation and inclusive/exclusive bound range views (`headMultimap`/`tailMultimap`/`subMultimap`) that return snapshots; `descendingMultimap()` returns a snapshot with keys in reversed order; factory functions in `SortedListMultimaps.kt`
- **SortedSetMultimap** (`SortedSetMultimap<K,V>`, `MutableSortedSetMultimap<K,V>`) — comparator-ordered `SetMultimap` backed by a red-black tree; keys are in comparator order, values per key are an unordered `Set`; `asMap` is typed as `SortedMap<K, Set<V>>`; same key navigation and range view API as `SortedListMultimap`; factory functions in `SortedSetMultimaps.kt`
- **BiSortedSetMultimap** (`BiSortedSetMultimap<K,V>`, `MutableBiSortedSetMultimap<K,V>`) — extends `SortedSetMultimap` with a second `valueComparator: Comparator<in V>`; values per key are a `SortedSet<V>` ordered by `valueComparator`; `asMap` is typed as `SortedMap<K, SortedSet<V>>`; per-key value navigation is available through the `SortedSet<V>` values in `asMap` directly; snapshots from range views preserve both comparators; factory functions in `BiSortedSetMultimaps.kt`
- **SortedFlatMultimap** (`SortedFlatMultimap<K,V>`, `MutableSortedFlatMultimap<K,V>`) — a sorted variant of `FlatMultimap` where the sort axis is a **single `Comparator<Pair<K,V>>`** over whole pairs, not just keys; this makes it fundamentally different from the key-sorted multimap types above: `keys` is `Set<K>` (not `SortedSet`), `asMap` is `Map<K, List<V>>` (not `SortedMap`), and the comparator can sort value-first or by any pair criterion — something impossible with `SortedListMultimap` or `BiSortedSetMultimap`; `entries` yields pairs in comparator order; navigation methods (`firstEntry`, `lastEntry`, `floorEntry`, `ceilingEntry`, `lowerEntry`, `higherEntry`) operate on pairs; range views (`headMultimap`/`tailMultimap`/`subMultimap`/`descendingMultimap`) use pair bounds and return snapshots; duplicate pairs are always preserved even when the comparator returns 0, because pair contents may be mutable; backed by a sorted `ArrayList` plus a `HashMap` key-index for O(1) key-based lookup; factory functions in `SortedFlatMultimaps.kt`
- **BiMap** (`BiMap<K,V>`, `MutableBiMap<K,V>`) — a `Map` that enforces a bijection: each value maps to exactly one key; exposes a live `inverse: BiMap<V,K>` view backed by the same storage; `MutableBiMap.put` throws `IllegalArgumentException` on value collision while `forcePut` removes the conflicting key first; all-platforms alternative to Guava's `BiMap` and Apache Commons' `BidiMap`; factory functions in `BiMaps.kt`

- **EnumBiMap** (`EnumBiMap<K:Enum,V>`, `MutableEnumBiMap<K:Enum,V>`) — ordinal-array-backed BiMap keyed by enum constants, iterating in ordinal order; `inverse` is a plain `BiMap<V,K>`; factory functions in `EnumBiMaps.kt`
- **SortedBiMap** (`SortedBiMap<K,V>`, `MutableSortedBiMap<K,V>`) — key side comparator-ordered, backed by a red-black tree; `inverse` is a plain `BiMap<V,K>`; factory functions in `SortedBiMaps.kt`
- **BiSortedBiMap** (`BiSortedBiMap<K,V>`, `MutableBiSortedBiMap<K,V>`) — both directions comparator-ordered, each backed by a red-black tree; `inverse` is a `SortedBiMap<V,K>`; factory functions in `BiSortedBiMaps.kt`
- **BiEnumBiMap** (`BiEnumBiMap<K:Enum,V:Enum>`, `MutableBiEnumBiMap<K:Enum,V:Enum>`) — both directions ordinal-array-backed; iterates keys in ordinal order; `inverse` is an `EnumBiMap<V,K>`; factory functions in `BiEnumBiMaps.kt`
- **EnumSortedBiMap** (`EnumSortedBiMap<K:Enum,V>`, `MutableEnumSortedBiMap<K:Enum,V>`) — enum-backed keys (ordinal order) and comparator-sorted values; `inverse` is a `SortedEnumBiMap<V,K>`; factory functions in `EnumSortedBiMaps.kt`
- **SortedEnumBiMap** (`SortedEnumBiMap<K,V:Enum>`, `MutableSortedEnumBiMap<K,V:Enum>`) — comparator-sorted keys and enum-backed values; `inverse` is an `EnumSortedBiMap<V,K>` (mutual inverse pair with `EnumSortedBiMap`); factory functions in `EnumSortedBiMaps.kt`

### Structures

Algorithm-support data structures whose primary purpose is computation rather than general data
storage. Unlike collections, these are typically built up over the course of an algorithm and
queried at the end, rather than being general-purpose containers.

- **DisjointSet** (`DisjointSet<E>`, `MutableDisjointSet<E>`) — a Union-Find structure
  maintaining a partition of elements into mutually exclusive equivalence classes. Supports
  near-O(1) connectivity queries (`connected`, `find`) and O(α(n)) amortized merges (`union`)
  via union-by-rank and path halving. **Growth-only**: classes can be merged but never split;
  this is intentional, as the semantics of splitting a merged class are undefined without full
  merge history. Elements not passed to `union` are treated as implicit singletons — `find`
  returns the element itself and `getPartition` returns a single-element set. Factory functions:
  - `mutableDisjointSetOf()` — lazy/unknown universe, `HashMap`-backed
  - `mutableDisjointSetOf(universe)` — pre-registers a known universe as singletons (soft
    constraint: `union` with out-of-universe elements still registers them lazily)
  - `mutableEnumDisjointSetOf<E>()` — enum universe, `IntArray`-backed via ordinal indexing
  - `buildDisjointSet { }` / `buildEnumDisjointSet<E> { }` — builder returning a read-only view

- **IndexedPriorityQueue** (`IndexedPriorityQueue<T, P>`) — a min-heap priority queue that supports
  O(log n) priority updates via an inverse-position map from elements to their current heap
  positions. Unlike `PriorityQueue<T>`, where priority is implicitly derived from the element via a
  comparator, an `IndexedPriorityQueue` stores priority `P` as explicit, mutable, caller-supplied
  state independent of `T` — there is no `(T) -> P` transformer. The `Comparator<in P>` operates
  purely on `P` values. Supports `add`, `pollMin`, `peekMin`, `contains`, `getPriority`, `remove`,
  `decreaseKey`, `increaseKey`, and `updatePriority`. The known-universe and enum variants pre-allocate
  fixed arrays and are more memory-efficient when the element set is bounded. Factory functions:
  - `indexedPriorityQueueOf(comparator)` — dynamic universe, `HashMap`-backed
  - `indexedPriorityQueueOf(comparator, universe)` — known universe, pre-allocated arrays; `add`
    for an element outside `universe` throws `IllegalArgumentException`
  - `enumIndexedPriorityQueueOf<E, P>(comparator)` — enum universe, ordinal-indexed arrays with no
    T→Int map
  - `minIndexedPriorityQueueOf()` / `minIndexedPriorityQueueOf(universe)` /
    `minEnumIndexedPriorityQueueOf<E, P>()` — natural-order convenience variants for `P : Comparable<P>`

- **IndexedPriorityDeque** (`IndexedPriorityDeque<T, P>`) — a double-ended indexed priority queue backed
  by a min-max heap (Atkinson et al., 1986). Supports O(1) `peekMin`/`peekMax` and O(log n)
  `pollMin`/`pollMax`, `add`, `remove`, `decreaseKey`, `increaseKey`, and `updatePriority`. Like
  `IndexedPriorityQueue`, priority `P` is independent of element `T`. **`IndexedPriorityDeque` does
  not extend `IndexedPriorityQueue`** and the two are not freely substitutable: unlike `Deque` vs.
  `Queue` (where the deque imposes zero overhead), a min-max heap sift must determine the current
  node's level (min-level vs. max-level) at every step, adding a constant-factor cost even when only
  one end is used. Prefer `IndexedPriorityQueue` for single-ended algorithms (Dijkstra, Prim, A*).
  Factory functions:
  - `indexedPriorityDequeOf(comparator)` — dynamic universe, `HashMap`-backed
  - `indexedPriorityDequeOf(comparator, universe)` — known universe, pre-allocated arrays
  - `enumIndexedPriorityDequeOf<E, P>(comparator)` — enum universe, ordinal-indexed arrays
  - `minMaxIndexedPriorityDequeOf()` / `minMaxIndexedPriorityDequeOf(universe)` /
    `minMaxEnumIndexedPriorityDequeOf<E, P>()` — natural-order convenience variants

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
- `FloatingPointSquareRoot<T>` — square root operation; correctly rounded following IEEE 754; instances for `BFloat16`, `Float16`, `Float`, `Double`, `DoubleDouble`.
- `FloatingPointRounding<T>` — directed rounding: `floor` (toward −∞), `ceil` (toward +∞), `trunc` (toward 0), `roundUp` (away from 0); instances for `BFloat16`, `Float16`, `Float`, `Double`, `DoubleDouble`. JVM-only `BigDecimal` instance via `FloatingPointRounding.bigDecimal`.
- `FloatingPointNearestRounding<T>` — nearest-integer rounding with tie-breaking: `roundHalfUp` (ties away from zero, C99 `round` / `RoundingMode.HALF_UP`), `roundHalfDown` (ties toward zero / `RoundingMode.HALF_DOWN`), `roundEven` (banker's rounding, `RoundingMode.HALF_EVEN`); instances for `BFloat16`, `Float16`, `Float`, `Double`, `DoubleDouble`. All commonMain, no platform split.
- `FloatingPointScalb<T>` — binary scaling × 2^n (instances for `BFloat16`, `Float16`, `Float`, `Double`, `DoubleDouble`)
- `FloatingPointLogb<T>` — `logb` (IEEE 754 §5.3.3, returns exponent as `T`: `logb(0)=−∞`, `logb(±∞)=+∞`, sign ignored) and `ilogb` (returns exponent as `Int`: `ilogb(0)=Int.MIN_VALUE`, `ilogb(±∞/NaN)=Int.MAX_VALUE`); instances for `BFloat16`, `Float16`, `Float`, `Double`, `DoubleDouble`. Pure commonMain bit-pattern arithmetic, no platform split.
- `FloatingPointNextValue<T>` — `nextUp` and `nextDown` (IEEE 754 §5.3.1 required operations); instances for `BFloat16`, `Float16`, `Float`, `Double`, `DoubleDouble`. `Float16`/`BFloat16` delegate to their member functions; `Float`/`Double` use bit-pattern arithmetic (`toRawBits`/`fromBits`) since `Float.nextUp()` is absent from the Kotlin/JS stdlib. The `DoubleDouble` instance uses an inline `fastTwoSum(hi, nextUp(lo))` step, which naturally carries into `hi` when the incremented `lo` exceeds the valid range `ulp(hi)/2`, including at power-of-2 exponent boundaries.
- `FloatingPointCubeRoot<T>` — cube root (`cbrt`); defined for negative inputs; instances for `BFloat16`, `Float16`, `Float`, `Double`, `DoubleDouble`. `Float` and `Float16`/`BFloat16` instances widen to `Double` for computation and narrow back (≤ 1 ULP).
- `FloatingPointHypot<T>` — hypotenuse (`sqrt(x² + y²)`) without intermediate overflow or underflow; instances for `BFloat16`, `Float16`, `Float`, `Double`, `DoubleDouble`. `Float` and `Float16`/`BFloat16` instances widen to `Double` for computation and narrow back (≤ 1 ULP).
- `FloatingPointTrigonometry<T>` — circular and hyperbolic trigonometric functions (`sin`, `cos`, `tan`, `asin`, `acos`, `atan`, `atan2`, `sinh`, `cosh`, `tanh`, `asinh`, `acosh`, `atanh`); instances for `BFloat16`, `Float16`, `Float`, `Double`. `Float16`/`BFloat16` instances widen to `Float` for computation and narrow back. No `DoubleDouble` instance.
- `FloatingPointExpLog<T>` — exponential and logarithmic functions (`exp`, `expm1`, `ln`, `ln1p`, `log2`, `log10`, `pow`); instances for `BFloat16`, `Float16`, `Float`, `Double`. `Float16`/`BFloat16` widen to `Float` for computation and narrow back. No `DoubleDouble` instance.
- `FloatingPointExp2<T>` — 2^x with Cody-Waite range reduction; instances for `BFloat16`, `Float16`, `Float`, `Double`. On macOS arm64 and Windows x64 delegates to `platform.posix.exp2`/`exp2f` (≤ 1 ULP); on JVM, JS, and Linux x64 uses Cody-Waite emulation (≤ 2 ULP). No `DoubleDouble` instance.
- `FloatingPointExp10<T>` — 10^x with accurate Cody-Waite range reduction; instances for `BFloat16`, `Float16`, `Float`, `Double`. On Linux x64 delegates to `exp10`/`exp10f` (glibc GNU extension, ≤ 1 ULP); on all other platforms uses Cody-Waite with a Dekker-split `log10(2)` constant (≤ 2 ULP). The naive `exp(x × ln 10)` accumulates O(|x|) ULP error and is intentionally excluded. No `DoubleDouble` instance.
- `FloatingPointIeee754ExpLog<T>` — IEEE 754-2019 / C23 recommended operations: `exp2m1` (2^x−1), `exp10m1` (10^x−1), `log2p1` (log₂(1+x)), `log10p1` (log₁₀(1+x)), all accurate near zero. Pure commonMain (no platform split). Instances for `BFloat16`, `Float16`, `Float`, `Double`. No `DoubleDouble` instance.
- `FloatingPointSinhCosh<T>` — combined hyperbolic sine and cosine: `sinhcosh` returns `SinhCoshResult<T>(sinh, cosh)` computed from a single `exp` evaluation (or `exp(x)` + `exp(-x)` for `Double`), saving work compared to calling `sinh` and `cosh` separately. Universal commonMain implementation (no platform split required, unlike `FloatingPointSinCos`); instances for `BFloat16`, `Float16`, `Float`, and `Double`. No `DoubleDouble` instance.
- `FloatingPointTrigPi<T>` — π-scaled trigonometric and inverse functions: `sinPi` (sin(πx)), `cosPi` (cos(πx)), `tanPi` (tan(πx)), `asinPi` (asin(x)/π), `acosPi` (acos(x)/π), `atanPi` (atan(x)/π), `atan2Pi` (atan2(y,x)/π); instances for `BFloat16`, `Float16`, `Float`, `Double`. Exact zeros at integer inputs and exact ±1 at half-integer inputs are guaranteed. On macOS arm64 delegates to native `sinpi`/`cospi`/etc. via a Darwin-C-Source cinterop (≤ 1 ULP); on all other platforms uses exact mod-2 argument reduction with a Dekker-split π (≤ 2 ULP). Naive `sin(x × Math.PI)` is intentionally excluded. No `DoubleDouble` instance.
- `FloatingPointSinCos<T>` — atomic joint sine/cosine: `sincos` returns `SinCosResult<T>(sin, cos)` without a redundant argument reduction pass. Modelled after `FusedMultiplyAdd`: instances only where the platform provides a native joint operation. Instances for `Float` and `Double` on macOS arm64 and Linux x64, via a custom cinterop targeting `<math.h>`; absent on JVM, JS, and Windows.
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
- `ComplexArg<C, T>` — argument (phase angle) of a complex value: `arg()` in radians, `argPi()` in units of π (result in (−1, 1])
- `ComplexExpLog<C, T>` — exponential (`exp`), natural logarithm (`ln`), and power (`pow` for scalar exponent, `powComplex` for complex exponent); factory functions `from(expLog, trig, hypot, arith)` and `from(expLog, sinCos, trig, hypot, arith)`
- `ComplexSquareRoot<C, T>` — principal square root (`sqrt`) with numerically stable formula via `FloatingPointHypot`
- `ComplexTrigonometry<C, T>` — forward trig (`sin`, `cos`, `tan`, `sinh`, `cosh`, `tanh`) and inverse trig (`asin`, `acos`, `atan`, `asinh`, `acosh`, `atanh`)
- `ComplexCubeRoot<C, T>` — principal cube root (`cbrt`) via polar form with π-scaled trig for accuracy at rational multiples of π
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
| `FloatingPointArithmetic<T>` | ✓ | ✓ | ✓ | ✓ | ✓ ¹ |
| `FloatingPointSquare<T>` | ✓ | ✓ | ✓ | ✓ | ✓ |
| `FloatingPointSquareRoot<T>` | ✓ | ✓ | ✓ | ✓ | ✓ |
| `FloatingPointLogb<T>` | ✓ | ✓ | ✓ | ✓ | ✓ |
| `FloatingPointNextValue<T>` | ✓ | ✓ | ✓ | ✓ | ✓ |
| `FloatingPointCubeRoot<T>` | ✓ | ✓ | ✓ | ✓ | ✓ |
| `FloatingPointHypot<T>` | ✓ | ✓ | ✓ | ✓ | ✓ |
| `FloatingPointTrigonometry<T>` | ✓ | ✓ | ✓ | ✓ | — |
| `FloatingPointExpLog<T>` | ✓ | ✓ | ✓ | ✓ | — |
| `FloatingPointExp2<T>` | ✓ ¹³ | ✓ ¹³ | ✓ ¹³ | ✓ ¹³ | — |
| `FloatingPointExp10<T>` | ✓ ¹⁴ | ✓ ¹⁴ | ✓ ¹⁴ | ✓ ¹⁴ | — |
| `FloatingPointIeee754ExpLog<T>` | ✓ | ✓ | ✓ | ✓ | — |
| `FloatingPointTrigPi<T>` | ✓ ¹⁵ | ✓ ¹⁵ | ✓ ¹⁵ | ✓ ¹⁵ | — |
| `FloatingPointSinhCosh<T>` | ✓ | ✓ | ✓ | ✓ | — |
| `FloatingPointSinCos<T>` | — | — | ✓ ¹² | ✓ ¹² | — |
| `FloatingPointRounding<T>` | ✓ | ✓ | ✓ | ✓ | ✓ |
| `FloatingPointNearestRounding<T>` | ✓ | ✓ | ✓ | ✓ | ✓ |
| `FloatingPointScalb<T>` | ✓ | ✓ | ✓ | ✓ | ✓ |
| `FloatingPointRemainder<T>` | ✓ ² | ✓ ² | ✓ ² | ✓ ² | ✓ ²¹⁶ |
| `FusedMultiplyAdd<T>` | ✓ ³ | ✓ ³ | ✓ ³ | ✓ ³ | — |
| `IntegerPower<T>` | ✓ | ✓ | ✓ | ✓ | ✓ |

¹ `DoubleDouble` is exposed as `DoubleBinaryFloatingPointArithmetic<DoubleDouble, Double>` — a
sub-interface of `FloatingPointArithmetic<DoubleDouble>` — via
`DoubleBinaryFloatingPointArithmetic.Companion.doubleDouble`.

² Two variants exist per type: an IEEE 754 nearest-integer remainder (e.g.
`FloatingPointRemainder.floatIeee754`) and a truncating remainder matching Kotlin's `%` operator
(e.g. `FloatingPointRemainder.floatTruncating`).

³ On JVM, delegates to `java.lang.Math.fma` (hardware FMA). On Native (macOS ARM64, Windows x64),
delegates to `platform.posix.fma` / `platform.posix.fmaf` (hardware FMA). On Native (Linux x64),
uses Boldo-Melquiond software emulation backed by hardware `binary32` arithmetic (`platform.posix.fma`
is excluded from the Linux platform bindings). On JS, uses the Boldo-Melquiond software emulation
backed by strict `binary32` arithmetic (see [Kotlin/JS platform notes](#kotlinjs-platform-notes)
below). The software emulation cannot recover a finite result when `a × b` overflows to infinity.

¹² Available on Native **macOS arm64** and **Linux x64** only, via a custom cinterop targeting
`<math.h>`. `sincos`/`sincosf` are BSD/GNU extensions absent from `platform.posix` and
`platform.darwin`; on Linux the cinterop passes `-D_GNU_SOURCE` to expose them. No instance on
JVM, JS, or Windows (mingwX64).

¹³ On macOS ARM64 and Windows x64, delegates to `platform.posix.exp2` / `platform.posix.exp2f`
(≤ 1 ULP). On JVM, JS, and Linux x64, uses Cody-Waite range reduction:
`exp2(x) = scalbn(exp((x − round(x)) × ln 2), round(x))` (≤ 2 ULP for all finite normal inputs).
The naive alternative `exp(x × ln 2)` would produce up to ~176 ULP error for `Float` and
~1418 ULP for `Double`. `Float16`/`BFloat16` instances widen to `Float` and narrow back.

¹⁴ On Linux x64, delegates to `exp10`/`exp10f` from glibc via a dedicated `gnumath` cinterop
(`exp10` is a GNU extension, not part of POSIX, and absent from macOS and Windows). On all
other platforms uses Cody-Waite with a Dekker-split `log10(2)` constant: split into a 26-bit
high part and a low correction so that `n × log10(2)_hi` is exact, eliminating catastrophic
cancellation (≤ 2 ULP). `Float` uses the same Cody-Waite step widened to `Double` (sufficient
for 24-bit precision). `Float16`/`BFloat16` widen to `Float` and narrow back.

¹⁶ Two variants: `FloatingPointRemainder.doubleDoubleTruncating` (`x − trunc(x/y) × y`, same sign
as `x`, matching Kotlin's `%`) and `FloatingPointRemainder.doubleDoubleIeee754` (`x − roundEven(x/y) × y`,
result in `[−|y|/2, +|y|/2]`). Precision degrades when `|x/y|` exceeds approximately 2^106 — the
representable precision of a DoubleDouble — and the fractional part of the quotient can no longer
be recovered. This is the same structural limitation as Kotlin's `%` operator on `Double` (threshold
≈ 2^53).

¹⁵ On macOS ARM64, delegates to `sinpi`/`cospi`/`tanpi`/`asinpi`/`acospi`/`atanpi`/`atan2pi`
and their `f` float variants from the Darwin `<math.h>` extension via a `macmath` cinterop
(≤ 1 ULP). On all other platforms (JVM, JS, Linux x64, Windows x64), uses exact mod-2 argument
reduction: `n = floor(|x|)`, `frac = |x| − n`, then `sin(PI_HI × frac + PI_LO × frac)` where
`(PI_HI, PI_LO)` is a Dekker-split of π (≤ 2 ULP). Integer and half-integer inputs are
hard-returned exactly. `Float16`/`BFloat16` widen to `Float` and narrow back.

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
| `TwoSum<T>` | ✓ ⁵ | ✓ | `FloatingPointArithmetic<T>` |
| `TwoProduct<T>` | ✓ ⁵⁶ | ✓ ⁶ | `FloatingPointArithmetic<T>` + `IeeeBinaryFloatingPoint<T>` |
| `TwoDiv<T>` | ✓ ⁵ | ✓ | `FloatingPointArithmetic<T>` + `FusedMultiplyAdd<T>` |
| `DoubleBinaryFloatingPointArithmetic<F, T>` | — | ✓ ⁷ | `TwoProduct<T>`, `TwoSum<T>`, optionally `TwoDiv<T>` |

⁵ The `Float` companion instances (`TwoSum.float`, `TwoProduct.float`, `TwoDiv.float`) are
`expect`/`actual` declarations. On JVM and Native they use `FloatingPointArithmetic.float` (native
`binary32` hardware). On JS they use strict `binary32` arithmetic that round-trips each result
through `Float.toRawBits()` and `Float.fromBits()` to force correct rounding. See
[Kotlin/JS platform notes](#kotlinjs-platform-notes) below.

⁶ The companion instances (`TwoProduct.float`, `TwoProduct.double`) use Veltkamp-Dekker splitting,
which requires only ordinary floating-point arithmetic and works on all platforms. An FMA-backed
alternative — one multiply plus one FMA instead of seventeen operations — is available via
`TwoProduct.Companion.from(arith, fma)`.

⁷ `DoubleBinaryFloatingPointArithmetic.Companion.doubleDouble` uses an FMA-backed `TwoProduct` and
`TwoDiv` internally, and is available on all platforms. The factory
`DoubleBinaryFloatingPointArithmetic.Companion.from` accepts any compatible `TwoProduct`
implementation, including the Veltkamp-Dekker one.

#### Integer

Signed: `Byte`, `Short`, `Int`, `Long`, `BigInteger` (JVM only), `BigInt` (JS only)
Unsigned: `UByte`, `UShort`, `UInt`, `ULong`

| Trait | Signed ⁷ | Unsigned | `BigInteger` | `BigInt` |
|---|:---:|:---:|:---:|:---:|
| `SignedIntegerArithmetic<T>` | ✓ | — | JVM | JS |
| `UnsignedIntegerArithmetic<T>` | — | ✓ | — | — |
| `OverflowCheckedArithmetic<T>` | `Int` (JVM, JS ⁹), `Long` (JVM, JS ¹⁰) | — | — | — |
| `Gcd<T>` | ✓ | ✓ | — | JS |
| `Bitwise<T>` | `Int`, `Long` | `UShort`, `UInt`, `ULong` | — | — |
| `BitShift<T>` | ✓ | ✓ | — | — |
| `ArithmeticRightShift<T>` | ✓ | — | JVM | JS |
| `RoundingRightShift<T>` | ✓ | ✓ | — | — |
| `StickyRightShift<T>` | ✓ | ✓ | — | — |
| `PowerOfTwo<T>` | ✓ | ✓ | — | — |
| `Sqrt<T>` (integer) | ✓ | ✓ | — | — |
| `Log2<T>` | ✓ | ✓ | — | — |
| `Log10<T>` | ✓ | ✓ | — | — |
| `Primality<T>` | `Int`, `Long` | `UInt`, `ULong` (JVM) | JVM | JS |
| `IntegerPower<T>` | `Int`, `Long` | — | JVM | JS |
| `DivRem<T>` | `Int`, `Long` ¹¹ | — | JVM | — |

⁷ All four signed primitive types (`Byte`, `Short`, `Int`, `Long`) unless noted otherwise.

¹¹ Native only (`Int` via C stdlib `div()`, `Long` via `lldiv()`); absent on JVM and JS for primitive
types (no single-pass primitive exists). Unsigned types and `Byte`/`Short` have no instances. The
JVM `BigInteger` instance uses `BigInteger.divideAndRemainder()`.

⁹ JS `Int` instance uses Double-promotion (exact for 32-bit values; see
[Kotlin/JS platform notes](#kotlinjs-platform-notes)).

¹⁰ JS `Long` instance uses BigInt-promotion (see [Kotlin/JS platform notes](#kotlinjs-platform-notes)).
`BigInt` is arbitrary-precision so `Long.MIN_VALUE / -1` and other overflow cases are caught by a
range check after the operation.

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
| `RationalArithmetic.bigInt` | `RationalArithmetic<Rational<BigInt>, BigInt>` | JS only; overflow-free (BigInt is arbitrary-precision) |
| `RationalArithmetic.checkedInt` | `RationalArithmetic<Rational<Int>, Int>` | JVM only; throws `ArithmeticException` on overflow |
| `RationalArithmetic.checkedLong` | `RationalArithmetic<Rational<Long>, Long>` | JVM only |
| `RationalArithmetic.bigInteger` | `RationalArithmetic<Rational<BigInteger>, BigInteger>` | JVM only; overflow-free |

The `from(arithmetic, gcd)` factory constructs a `RationalArithmetic<Rational<T>, T>` for any
`SignedIntegerArithmetic<T>` and matching `Gcd<T>`. For other rational types (e.g. `Fraction`,
`BigFraction`), see the commons-numbers-extensions module.

#### Complex (kotlin-core types)

All instances target `Complex<Float>` and `Complex<Double>` (i.e. `C = Complex<T>`, `T = Float` or `Double`).

| Instance | Type |
|---|---|
| `ComplexArithmetic.naiveFloat` | `ComplexArithmetic<Complex<Float>, Float>` — textbook multiply/divide |
| `ComplexArithmetic.strictFloat` | `ComplexArithmetic<Complex<Float>, Float>` — FMA + Annex G fixup |
| `ComplexArithmetic.naiveDouble` | `ComplexArithmetic<Complex<Double>, Double>` — textbook multiply/divide |
| `ComplexArithmetic.strictDouble` | `ComplexArithmetic<Complex<Double>, Double>` — FMA + Annex G fixup |
| `ComplexModulus.float` | `ComplexModulus<Complex<Float>, Float>` |
| `ComplexModulus.double` | `ComplexModulus<Complex<Double>, Double>` |
| `ComplexArg.float` | `ComplexArg<Complex<Float>, Float>` |
| `ComplexArg.double` | `ComplexArg<Complex<Double>, Double>` |
| `ComplexExpLog.float` | `ComplexExpLog<Complex<Float>, Float>` — universal (separate sin/cos calls) |
| `ComplexExpLog.double` | `ComplexExpLog<Complex<Double>, Double>` — universal (separate sin/cos calls) |
| `ComplexSquareRoot.float` | `ComplexSquareRoot<Complex<Float>, Float>` |
| `ComplexSquareRoot.double` | `ComplexSquareRoot<Complex<Double>, Double>` |
| `ComplexTrigonometry.float` | `ComplexTrigonometry<Complex<Float>, Float>` |
| `ComplexTrigonometry.double` | `ComplexTrigonometry<Complex<Double>, Double>` |
| `ComplexCubeRoot.float` | `ComplexCubeRoot<Complex<Float>, Float>` |
| `ComplexCubeRoot.double` | `ComplexCubeRoot<Complex<Double>, Double>` |

`ComplexExpLog.Companion.from(expLog, sinCos, trig, hypot, arith)` constructs an optimised instance
where `FloatingPointSinCos` is available (macOS ARM64, Linux x64), using a single `sincos()` call in
`exp()` instead of separate `sin()` and `cos()` calls. The `powComplex(w: C)` method is named
distinctly (not `pow`) to avoid a JVM type-erasure clash with `pow(y: T)`.

#### Cross-cutting granular traits

`Addition<T>`, `Multiplication<T>`, and `Division<T>` are satisfied by every type that has an
`IntegerArithmetic`, `SignedIntegerArithmetic`, `UnsignedIntegerArithmetic`, or
`FloatingPointArithmetic` instance — no separate companion property is needed. `Signed<T>` is
satisfied by every type that has a `SignedIntegerArithmetic` instance, and additionally by
`BigInteger` and `BigDecimal` via dedicated JVM-only instances (`Signed.bigInteger`,
`Signed.bigDecimal`).

### Platform Utilities

- **`Pid`** — type-safe wrapper around a process ID (`Long`); `currentPid()` returns the running process's PID. On JVM: backed by `ProcessHandle.pid()`; `toProcessHandle()`, `ProcessHandle.pidKt`, and `Process.pidKt` are also available. On JS (Node.js): backed by `process.pid`. On Native (Linux x64, macOS ARM64): backed by POSIX `getpid()`. On Native (Windows x64): backed by `GetCurrentProcessId()`.

### JVM Utilities

Type-safe wrappers around JVM standard library return types that use raw primitives where a distinct
type would prevent bugs:

- **`Stamp`** — wraps the `Long` stamp produced by `StampedLock`; extension functions on `StampedLock` accept and return `Stamp` instead of raw longs, preventing accidental misuse of arbitrary numbers as lock tokens
- **`EnumSubset<E>`** — wraps `EnumSet<E>` with ordering and set algebra operations

## Kotlin/JS platform notes

### `Float` is not a true `binary32` on Kotlin/JS

JavaScript has no 32-bit floating-point type. All numeric operations execute at 64-bit (`Number`,
i.e. `binary64`) precision. Kotlin/JS maps `Float` storage through a `Float32Array`, so
`Float.toRawBits()` and `Float.fromBits()` faithfully preserve the `binary32` bit pattern, but the
four arithmetic operators (`+`, `-`, `*`, `/`) execute at `binary64` precision and do **not** round
their results to the nearest `binary32` value.

This is analogous to why `BFloat16` and `Float16` do not expose native arithmetic operators on any
platform: the runtime cannot guarantee the required rounding semantics, so arithmetic is routed
through a trait that documents the precision contract explicitly.

### Strict `binary32` arithmetic

`FloatingPointArithmetic.float` uses Kotlin's built-in `Float` operators and therefore inherits the
platform behaviour described above. On Kotlin/JS, `FloatingPointArithmetic.float` carries 53 bits
of mantissa rather than 23.

Algorithms that depend on exact `binary32` rounding — error-free transformations (`TwoSum`,
`TwoProduct`, `TwoDiv`) and emulated `FusedMultiplyAdd` — require a strict `binary32` arithmetic
that forces correct rounding. On Kotlin/JS, this is provided by an internal
`strictFloatArithmetic` instance that passes each arithmetic result through JavaScript's
`Math.fround()`, quantizing the 64-bit intermediate back to 23-bit precision.

This strict arithmetic is **not** exposed as a public API. Instead, the standard companion
instances that require exact `binary32` rounding are wired to it automatically:

| Instance | JVM backing | JS backing | Native backing |
|---|---|---|---|
| `TwoSum.float` | `FloatingPointArithmetic.float` (native) | `strictFloatArithmetic` (`Math.fround`) | `FloatingPointArithmetic.float` (native) |
| `TwoProduct.float` | `FloatingPointArithmetic.float` (native) | `strictFloatArithmetic` (`Math.fround`) | `FloatingPointArithmetic.float` (native) |
| `TwoDiv.float` | `FloatingPointArithmetic.float` + hardware FMA | `strictFloatArithmetic` + emulated FMA | `FloatingPointArithmetic.float` + FMA ¹¹ |
| `FusedMultiplyAdd.float` | `java.lang.Math.fma` (hardware) | Boldo-Melquiond via `strictFloatArithmetic` | `platform.posix.fmaf` (macOS/Win) or Boldo-Melquiond (Linux) ¹¹ |

¹¹ On Linux x64, `platform.posix.fma`/`fmaf` are excluded from the platform bindings (GCC built-ins
not accessible via cinterop). The Boldo-Melquiond emulation is used instead, backed by hardware
`binary32` arithmetic. On macOS ARM64 and Windows x64, hardware FMA is available via
`platform.posix.fma`/`fmaf`.

Callers using these instances do not need to account for the platform difference. The `Math.fround`
call is a no-op on JVM (the hardware already operates in `binary32`) and necessary on JS.

### `Double` is unaffected

`Double` maps directly to JavaScript's `Number` type, which is IEEE 754 `binary64` on all
platforms. `FloatingPointArithmetic.double` and all `Double`-based trait instances behave
identically on JVM and JS.

### Integer divide-by-zero

On JVM, integer division by zero (`Int`, `Long`, etc.) throws `ArithmeticException`. On JS,
integer division compiles to JavaScript's `/` operator, which returns `Infinity` or `NaN` instead
of throwing.

### Overflow-checked arithmetic

`OverflowCheckedArithmetic.int` and `OverflowCheckedSignedArithmetic.int` are available on JS
using Double-promotion. Since JavaScript's `number` type has a 53-bit mantissa, all 32-bit integer
values are exactly representable: each arithmetic operation widens both operands to `Double`,
computes the result, checks whether it lies within `Int.MIN_VALUE..Int.MAX_VALUE`, and throws
`ArithmeticException` on overflow.

`OverflowCheckedArithmetic.long` and `OverflowCheckedSignedArithmetic.long` are also available on
JS, using BigInt-promotion. Each operation widens both `Long` operands to `BigInt`, performs the
operation in arbitrary precision, checks whether the result lies within
`Long.MIN_VALUE..Long.MAX_VALUE`, and throws `ArithmeticException` on overflow (including the
`MIN_VALUE / -1` overflow case). Division by zero throws `ArithmeticException` before reaching
BigInt. Remainder and comparison use the native Kotlin `Long` operators directly (no overflow is
possible for either operation).

### `BigInt` external declaration (JS only)

JavaScript's `BigInt` primitive provides arbitrary-precision integer arithmetic. This library
declares it as `external interface BigInt` — a structural type with no corresponding Kotlin class —
and exposes it through a set of inline operator extensions (`+`, `-`, `*`, `/`, `%`, unary `-`,
`compareTo`) that compile directly to the native BigInt operators.

`bigIntOf(value: String)` and `bigIntOf(value: Int)` are factory functions mapped via `@JsName("BigInt")`
to the global JavaScript `BigInt()` constructor. `BigInt.comparator` provides a `Comparator<BigInt>`
for use with `sortedWith`, `maxWith`, and similar APIs.

Converters between `BigInt` and Kotlin primitive types are available on the `BigInt.Companion`:

| Property | Direction | Notes |
|---|---|---|
| `BigInt.longConverter` | `Converter<BigInt, Long>` | Narrowing: throws `ArithmeticException` on out-of-range |
| `BigInt.intConverter` | `Converter<BigInt, Int>` | Narrowing: throws `ArithmeticException` on out-of-range |

Widening extension functions (no range check needed):

| Function | Result |
|---|---|
| `SignedIntegral<T>.toBigInt(value: T)` | `BigInt` via `toLong()` |
| `UnsignedIntegral<T>.toBigInt(value: T)` | `BigInt` via `toULong()` |

### `Pid` and `currentPid()`

`Pid` and `currentPid()` are available on JS (Node.js). `currentPid()` reads `process.pid` and
widens the result from the native JS integer to `Long`. The JVM-only members (`toProcessHandle()`,
`ProcessHandle.pidKt`, `Process.pidKt`) are not available on JS.

## Kotlin/Native platform notes

### `Float` is a true `binary32` on Kotlin/Native

Unlike Kotlin/JS, Kotlin/Native targets have genuine hardware 32-bit floating-point arithmetic.
`FloatingPointArithmetic.float` and the `Float`-based companion instances (`TwoSum.float`,
`TwoProduct.float`, `TwoDiv.float`) behave identically to their JVM counterparts.

### `FusedMultiplyAdd`, `FloatingPointScalb`, and `FloatingPointRemainder`

These operations call into the C99 math library (`libm`) via Kotlin/Native's C interop. Availability
depends on the target's platform bindings:

| Operation | Linux x64 | macOS ARM64 | Windows x64 |
|---|---|---|---|
| `fma` / `fmaf` | Emulated (Boldo-Melquiond) | `platform.posix.fma` / `fmaf` | `platform.posix.fma` / `fmaf` |
| `scalbn` / `scalbnf` | Emulated | `platform.posix.scalbn` / `scalbnf` | `platform.posix.scalbn` / `scalbnf` |
| `remainder` / `remainderf` | Emulated | `platform.posix.remainder` / `remainderf` | `platform.posix.remainder` / `remainderf` |
| `exp2` / `exp2f` | Emulated (Cody-Waite) | `platform.posix.exp2` / `exp2f` | `platform.posix.exp2` / `exp2f` |

On Linux x64, `platform.posix` excludes `fma`, `scalbn`, and `remainder` because GCC implements
them as built-ins or inline functions that the Kotlin/Native cinterop tool cannot bind to. The
emulated fallbacks produce correct results but without hardware FMA performance.

### `Pid` and `currentPid()`

`currentPid()` uses POSIX `getpid()` on Linux x64 and macOS ARM64, and `GetCurrentProcessId()`
on Windows x64. The JVM-only members (`toProcessHandle()`, `ProcessHandle.pidKt`, `Process.pidKt`)
are not available on Native.
