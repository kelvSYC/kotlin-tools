# commons-collections-extensions

Kotlin extensions for [Apache Commons Collections 4](https://commons.apache.org/proper/commons-collections/).
This module provides idiomatic wrappers and bridge functions for Commons Collections types that are worth
using alongside Kotlin; it does not re-export Commons Collections itself, so a `commons-collections4` dependency
is also required in your build.

Not every area of Commons Collections 4 is addressed here. Some parts are better served by pure Kotlin
(including other modules in this project); others add no meaningful value over the Commons API
as-is. This document maps each area of the library to one of three categories.

---

## Covered by this module

### `org.apache.commons.collections4.Bag` and `org.apache.commons.collections4.SortedBag`

**Factory functions** (`Bags.kt`):
- `hashBagOf()` — creates an empty `Bag<E>` backed by `HashBag`.
- `hashBagOf(vararg elements: E)` — creates a `Bag<E>` populated with the given elements.
- `treeBagOf()` — creates an empty `Bag<E>` backed by `TreeBag` (ordered by natural element order).
- `treeBagOf(vararg elements: E)` — creates a `TreeBag<E>` populated with the given elements.

**DSL builders** (`Bags.kt`):
- `buildBag { }` — DSL for constructing a `HashBag<E>` with elements added via the `add` function inside the lambda.
- `buildTreeBag { }` — DSL for constructing a `TreeBag<E>`.

**Operator overloads on `Bag<E>`** (`Bags.kt`):
- `operator fun plusAssign(element: E)` — adds one occurrence of the element via `+=`.
- `operator fun plusAssign(other: Bag<E>)` — merges another bag via `+=` (adds all elements with their multiplicities).
- `operator fun minusAssign(element: E)` — removes one occurrence of the element via `-=`.
- `operator fun minusAssign(other: Bag<E>)` — removes all elements of another bag (by multiplicity) via `-=`.

### `org.apache.commons.collections4.BidiMap`

**Factory functions** (`BidiMaps.kt`):
- `dualHashBidiMapOf()` — creates an empty bidirectional map backed by `DualHashBidiMap`.
- `dualHashBidiMapOf(vararg pairs: Pair<K, V>)` — creates a `BidiMap<K, V>` from key-value pairs.
- `dualTreeBidiMapOf()` — creates an empty `BidiMap<K, V>` backed by `DualTreeBidiMap` (ordered by key).
- `dualTreeBidiMapOf(vararg pairs: Pair<K, V>)` — creates a `DualTreeBidiMap<K, V>` from pairs.

**DSL builders** (`BidiMaps.kt`):
- `buildBidiMap { }` — DSL for constructing a `DualHashBidiMap<K, V>`.
- `buildDualTreeBidiMap { }` — DSL for constructing a `DualTreeBidiMap<K, V>`.

**Property** (`BidiMaps.kt`):
- `BidiMap<K, V>.inverse: BidiMap<V, K>` — shorthand for `inverseBidiMap()`, returning the dual view with keys and values swapped.

### `org.apache.commons.collections4.MultiValuedMap`

**Factory functions** (`MultiValuedMaps.kt`):
- `arrayListValuedHashMapOf()` — creates an empty `MultiValuedMap<K, V>` backed by `ArrayListValuedHashMap`.
- `arrayListValuedHashMapOf(vararg pairs: Pair<K, V>)` — creates a `MultiValuedMap<K, V>` populated with pairs (values are stored in lists).
- `hashSetValuedHashMapOf()` — creates an empty `MultiValuedMap<K, V>` backed by `HashSetValuedHashMap` (values are stored in sets).
- `hashSetValuedHashMapOf(vararg pairs: Pair<K, V>)` — creates a `HashSetValuedHashMap<K, V>` from pairs.

**DSL builders** (`MultiValuedMaps.kt`):
- `buildListValuedMap { }` — DSL for constructing an `ArrayListValuedHashMap<K, V>`.
- `buildSetValuedMap { }` — DSL for constructing a `HashSetValuedHashMap<K, V>`.

**Operator overloads on `MultiValuedMap<K, V>`** (`MultiValuedMaps.kt`):
- `operator fun get(key: K): Collection<V>` — bracket access returning the collection of values for the key (may be empty). **Note**: This is a bridge function; `MultiValuedMap` does not directly implement `get(K)` with `Collection<V>` return type.
- `operator fun plusAssign(pair: Pair<K, V>)` — adds a single key-value pair via `+=`.

### `org.apache.commons.collections4.OrderedMap`

**Factory functions** (`OrderedMaps.kt`):
- `linkedOrderedMapOf()` — creates an empty `LinkedMap<K, V>` (insertion-ordered map backed by Commons Collections).
- `linkedOrderedMapOf(vararg pairs: Pair<K, V>)` — creates a `LinkedMap<K, V>` populated from pairs, maintaining insertion order.

**DSL builder** (`OrderedMaps.kt`):
- `buildLinkedMap { }` — DSL for constructing a `LinkedMap<K, V>`.

### `org.apache.commons.collections4.Trie`

**Factory functions** (`Tries.kt`):
- `patriciaTrie()` — creates an empty `PatriciaTrie<V>` (PATRICIA trie with string keys).
- `patriciaTrie(vararg pairs: Pair<String, V>)` — creates a `PatriciaTrie<V>` populated from key-value pairs.

**DSL builder** (`Tries.kt`):
- `buildPatriciaTrie { }` — DSL for constructing a `PatriciaTrie<V>`.

**Extension functions** (`Tries.kt`):
- `PatriciaTrie<V>.keysWithPrefix(prefix: String): Set<String>` — returns the set of all keys with the given prefix.
- `PatriciaTrie<V>.entriesWithPrefix(prefix: String): Set<Map.Entry<String, V>>` — returns the set of all entries whose keys have the given prefix.

### `org.apache.commons.collections4.queue.CircularFifoQueue`

**Factory functions** (`CircularFifoQueues.kt`):
- `circularFifoQueueOf(maxElements: Int)` — creates an empty fixed-capacity FIFO queue backed by `CircularFifoQueue`.
- `circularFifoQueueOf(maxElements: Int, vararg elements: E)` — creates a queue pre-populated with elements (if the queue is filled, older elements are overwritten).

**Operator overloads on `CircularFifoQueue<E>`** (`CircularFifoQueues.kt`):
- `operator fun plusAssign(element: E)` — adds an element via `+=`; if the queue is at capacity, the oldest element is discarded.
- `operator fun minusAssign(element: E)` — removes the first occurrence of the element via `-=`.

**Availability**: The `in` operator for membership testing is available natively via the `Collection<E>` interface.

### `Collection<T>` multiset algebra

Functions that implement **multiset semantics** (element frequency-based operations, distinct from Kotlin's
`Set.union`/`intersect`/`subtract` which are set-identity-based). Delegates to `org.apache.commons.collections4.CollectionUtils`.

**Multiset operations** (`CollectionExtensions.kt`):
- `Collection<T>.multisetUnion(other: Collection<T>): Collection<T>` — returns a collection with the maximum frequency of each element from both inputs.
- `Collection<T>.multisetIntersection(other: Collection<T>): Collection<T>` — returns a collection with the minimum frequency of each element from both inputs.
- `Collection<T>.multisetSubtract(other: Collection<T>): Collection<T>` — returns a collection where counts from the second input are subtracted from the first (frequencies remain non-negative).
- `Collection<T>.multisetDisjunction(other: Collection<T>): Collection<T>` — returns a collection representing the symmetric difference by frequency (elements in either input but weighted by frequency difference).

**Comparison functions** (`CollectionExtensions.kt`):
- `Collection<T>.isSubCollectionOf(superset: Collection<T>): Boolean` — checks if this collection is a multiset subset (all elements with their frequencies are contained in the superset).
- `Collection<T>.isEqualCollection(other: Collection<T>): Boolean` — checks if this collection has the same element frequencies as another (order-insensitive).

**Analysis functions** (`CollectionExtensions.kt`):
- `Collection<T>.cardinalityMap(): Map<T, Int>` — returns a map of element → frequency (cardinality).
- `Collection<T>.permutations(): Collection<List<T>>` — returns a collection of all permutations of this collection (lexicographic order).

### `List<T>` extensions

Delegates to `org.apache.commons.collections4.ListUtils`.

**Longest Common Subsequence** (`ListExtensions.kt`):
- `List<T>.longestCommonSubsequence(other: List<T>): List<T>` — returns the longest common subsequence using default element equality.
- `List<T>.longestCommonSubsequence(other: List<T>, equator: (T, T) -> Boolean): List<T>` — returns the longest common subsequence using a custom element equality function.

**List-level set operations** (`ListExtensions.kt`):
- `List<T>.listUnion(other: List<T>): List<T>` — returns a list-level union (order-preserving, elements in either list).
- `List<T>.listIntersection(other: List<T>): List<T>` — returns a list-level intersection (elements in both lists).
- `List<T>.listSubtract(other: List<T>): List<T>` — returns a list-level subtraction (elements in the first list but not in the second).

### `Map<K, V>` extensions

Delegates to `org.apache.commons.collections4.MapUtils`.

**Inversion** (`MapExtensions.kt`):
- `Map<K, V>.invertedMap(): Map<V, K>` — swaps keys and values. If duplicate values exist, one key survives (which key is determined by the underlying implementation).

**Multi-valued conversion** (`MapExtensions.kt`):
- `Map<K, Collection<V>>.toMultiValuedMap(): MultiValuedMap<K, V>` — converts a map of collections (e.g., `Map<K, List<V>>`) into a `MultiValuedMap<K, V>` backed by `ArrayListValuedHashMap`.

### `Set<T>` extensions

Delegates to `org.apache.commons.collections4.SetUtils`.

**Symmetric difference** (`SetExtensions.kt`):
- `Set<T>.disjunction(other: Set<T>): Set<T>` — returns the symmetric difference: elements in either set but not both.

---

## Better served by pure Kotlin

These areas of Commons Collections 4 are not covered by this module because the functionality has been
reimplemented in Kotlin stdlib or is available through idiomatic Kotlin features.

### Collections and sequences

Kotlin's `Sequence<T>` and the stdlib extension functions (`filter`, `map`, `fold`, `reduce`, `any`, `all`,
`count`, `chunked`, `zipWithNext`, `groupBy`, etc.) supersede the need for `FluentIterable<E>` and
transforming iterators. These are more composable and work across all Kotlin targets.

### Utility functions

- `CollectionUtils.isNotEmpty()` → Kotlin's `isNotEmpty()` on `Collection<T>`.
- `CollectionUtils.isEmpty()` → Kotlin's `isEmpty()` on `Collection<T>`.
- `CollectionUtils.size()` → Kotlin's `size` property.
- `CollectionUtils.isFull()` — no Kotlin equivalent (niche use case for bounded collections).
- `MapUtils.isNotEmpty()`, `MapUtils.isEmpty()` → Kotlin's `isNotEmpty()` / `isEmpty()` on `Map<K, V>`.

### Predicates and transformers

Commons Collections' predicate and transformer functors (e.g., `Predicate<T>`, `Transformer<I, O>`) are
superseded by Kotlin lambdas and function types. Decorators like `ClosureUtils` and predicate combinators
are better expressed using `&&`, `||`, and higher-order functions.

---

## Not currently covered

| Area | Reason not covered |
|---|---|
| `FluentIterable<E>` / `ExtendedIterator<E>` | Superseded by Kotlin `Sequence<T>` and stdlib operators. |
| Transforming iterators / views | Sequence operations and stdlib functions cover this ground. |
| `CollectionUtils` predicates / `Predicate<T>` | Kotlin lambdas and function types are idiomatic. |
| `ClosureUtils` | Decorators for closures/side-effect operations; lambda composition is more idiomatic. |
| `Transformer<I, O>` functors | Replaced by Kotlin function types and stdlib functions like `map`. |
| `Comparator` decorators | Kotlin's comparator builder DSL and extension functions are sufficient. |
| `EqualityComparator<T>` | Use Kotlin `equals` or custom implementations; rarely warranted. |
| `IterableUtils` | Most operations are available via `Iterable<T>` and stdlib extensions. |
| `QueueUtils`, `SortedBagUtils`, `StackUtils`, `MapIterator<K, V>` utilities | Niche utilities with limited idiomatic alternatives in Kotlin. |
| `TransformedXxx` collection decorators | Sequence operations provide better composability. |
