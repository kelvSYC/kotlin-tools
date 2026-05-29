# IndexedPriorityQueue Design Spec

Date: 2026-05-28

## Summary

Add `IndexedPriorityQueue<T, P>` to `kotlin-core`'s `structures` package, complementing the
existing `DisjointSet`. An IPQ is a priority queue that supports O(log n) priority updates
(`decreaseKey`/`increaseKey`) via an inverse-position map from elements to their current heap
positions — the operation that makes algorithms like Dijkstra and Prim efficient.

## Core Design Invariant

**P has no structural relationship to T.** Unlike `PriorityQueue<T>` (where priority is implicitly
derived from T via a `Comparator<in T>`), the IPQ stores P as caller-supplied, explicitly mutable
state, independent of T. There is no `(T) -> P` transformer; the `Comparator<in P>` operates
purely on P values. The caller maintains whatever external state is needed to compute priorities;
the IPQ only stores and orders them.

This mirrors how `DisjointSet` tracks *that* elements are equivalent without tracking *how* they
became equivalent.

Because priority is explicitly mutable, `decreaseKey`/`increaseKey`/`updatePriority` are the
defining operations. A "read-only" interface has no meaningful semantics here, so
`IndexedPriorityQueue<T, P>` is the sole public type with no `Mutable` prefix — matching
`PriorityQueue<T>`.

## Known-Universe Trade-off

Mirrors `DisjointSet`'s dynamic vs. known-universe vs. enum variants:

- **Dynamic** (`indexedPriorityQueueOf(comparator)`): elements registered lazily on `add()`;
  `HashMap<T, Int>` inverse; `ArrayDeque<Int>` free list for slot recycling after removal.
- **Known universe** (`indexedPriorityQueueOf(comparator, universe)`): slots pre-allocated to
  `universe.size`; `HashMap<T, Int>` built once at construction and immutable thereafter; `IntArray`
  for heap and position-inverse; `add()` for an element outside the universe throws.
- **Enum** (`enumIndexedPriorityQueueOf<E, P>(comparator)`): ordinal IS the slot index; no T→Int
  map; pure `IntArray` backing.

## Interface

```kotlin
interface IndexedPriorityQueue<T, P> {
    val size: Int
    fun isEmpty(): Boolean
    fun isNotEmpty(): Boolean = !isEmpty()

    // Inspection — O(1)
    fun peekMin(): T?
    fun contains(element: T): Boolean
    fun getPriority(element: T): P?

    // Mutation
    fun add(element: T, priority: P)        // O(log n); throws IAE if already present
    fun pollMin(): T?                        // O(log n); null if empty
    fun remove(element: T): Boolean          // O(log n); false if not present

    // Priority update — all throw NoSuchElementException if element not present
    fun decreaseKey(element: T, newPriority: P)     // throws IAE if comparator.compare(new, current) >= 0
    fun increaseKey(element: T, newPriority: P)     // throws IAE if comparator.compare(new, current) <= 0
    fun updatePriority(element: T, newPriority: P)  // both directions; no-op if equal
}
```

## Factory Functions (`IndexedPriorityQueues.kt`)

```kotlin
fun <T, P> indexedPriorityQueueOf(comparator: Comparator<in P>): IndexedPriorityQueue<T, P>

fun <T, P> indexedPriorityQueueOf(
    comparator: Comparator<in P>,
    universe: Iterable<T>,
): IndexedPriorityQueue<T, P>

inline fun <reified E : Enum<E>, P> enumIndexedPriorityQueueOf(
    comparator: Comparator<in P>,
): IndexedPriorityQueue<E, P>

// Natural-order convenience variants
fun <T, P : Comparable<P>> minIndexedPriorityQueueOf(): IndexedPriorityQueue<T, P>
fun <T, P : Comparable<P>> minIndexedPriorityQueueOf(universe: Iterable<T>): IndexedPriorityQueue<T, P>
inline fun <reified E : Enum<E>, P : Comparable<P>> minEnumIndexedPriorityQueueOf(): IndexedPriorityQueue<E, P>
```

## Internal Implementations

All in `com.kelvsyc.internal.kotlin.core.structures`.

### Shared Heap Mechanics

All three variants operate on integer slot indices:

- `heap[heapPos]` = slot index at heap position `heapPos`
- `positionOf[slot]` = current heap position; `-1` = not in queue
- Comparisons: `comparator.compare(priorityAt(heap[a]), priorityAt(heap[b]))`
- Every swap updates both `heap[pos]` and `positionOf[slot]` for both participants
- Arbitrary `remove`: swap slot with last heap entry, shrink heap, siftDown; if no movement,
  siftUp (matching `PriorityQueue.removeAt` pattern in the existing codebase)

### `HashIndexedPriorityQueue<T, P>`

| Field | Type | Purpose |
|---|---|---|
| `elementToSlot` | `HashMap<T, Int>` | Canonical "in queue" check; present iff in queue |
| `slotToElement` | `ArrayList<Any?>` | Slot → T (Any? for nullable T; cast on access) |
| `slotPriorities` | `ArrayList<Any?>` | Slot → P (Any? for nullable P; cast on access) |
| `heap` | `ArrayList<Int>` | Heap positions → slot index |
| `positionOf` | `ArrayList<Int>` | Slot index → heap position |
| `freeSlots` | `ArrayDeque<Int>` | Recycled slot indices |
| `nextSlot` | `Int` | Next fresh slot when `freeSlots` is empty |

### `ArrayIndexedPriorityQueue<T, P>`

`elementToSlot: HashMap<T, Int>` built once from universe at construction; never mutated.
`positionOf[slot] == -1` is the canonical "not in queue" sentinel; no separate boolean array needed.

| Field | Type | Init |
|---|---|---|
| `elementToSlot` | `HashMap<T, Int>` | Populated at construction, immutable |
| `heap` | `IntArray(n)` | — |
| `positionOf` | `IntArray(n)` | All `-1` |
| `slotPriorities` | `arrayOfNulls<Any>(n)` | All `null` |

### `ArrayEnumIndexedPriorityQueue<E : Enum<E>, P>`

Slot = `element.ordinal`. No `elementToSlot` map. Universe size from `enumEntries<E>().size`.
Same `IntArray` / `arrayOfNulls` layout as the known-universe variant.

## Invariants

- Each element appears at most once; `add` on a duplicate throws `IllegalArgumentException`
- `decreaseKey` requires `comparator.compare(new, current) < 0`; otherwise `IllegalArgumentException`
- `increaseKey` requires `comparator.compare(new, current) > 0`; otherwise `IllegalArgumentException`
- `updatePriority` with comparator returning 0 is a no-op
- `decreaseKey`/`increaseKey`/`updatePriority` on non-present element: `NoSuchElementException`
- `remove` on non-present element: returns `false`, no throw
- Known-universe `add` for element outside universe: `IllegalArgumentException`
- T must have stable `hashCode`/`equals` while in queue for HashMap-backed variants;
  enum variant has no such constraint

## Files

### New
- `cores/kotlin-core/src/commonMain/kotlin/com/kelvsyc/kotlin/core/structures/IndexedPriorityQueue.kt`
- `cores/kotlin-core/src/commonMain/kotlin/com/kelvsyc/kotlin/core/structures/IndexedPriorityQueues.kt`
- `cores/kotlin-core/src/commonMain/kotlin/com/kelvsyc/internal/kotlin/core/structures/HashIndexedPriorityQueue.kt`
- `cores/kotlin-core/src/commonMain/kotlin/com/kelvsyc/internal/kotlin/core/structures/ArrayIndexedPriorityQueue.kt`
- `cores/kotlin-core/src/commonMain/kotlin/com/kelvsyc/internal/kotlin/core/structures/ArrayEnumIndexedPriorityQueue.kt`
- `cores/kotlin-core/src/commonTest/kotlin/com/kelvsyc/kotlin/core/structures/IndexedPriorityQueueTest.kt`

### Modified
- `cores/kotlin-core/README.md` — add IPQ to Structures section
