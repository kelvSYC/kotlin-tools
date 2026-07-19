package com.kelvsyc.kotlin.core.traits.integral

import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Returns a read-only property delegate that reads bit [bit] of the value returned by [getter],
 * yielding `true` if that bit is set and `false` if it is clear.
 *
 * The mask for position [bit] is computed as `lsb.leftRotate(bit)`, avoiding any dependence on
 * [BitShift]. A bit is considered set when `value AND mask != allClear`.
 *
 * [bit] must be in `[0, sizeBits)`. Passing a value outside that range throws
 * [IllegalArgumentException] at construction time. This prevents silent wrap-around: for example,
 * `leftRotate(sizeBits)` in most implementations reduces the count modulo `sizeBits`, so without
 * this guard bit `sizeBits` would silently alias bit 0.
 *
 * The delegate re-evaluates [getter] on every access, so it reflects updates to a mutable backing
 * field.
 *
 * @see mutableBitFlag
 * @see bitRange
 */
fun <T> BitCollection<T>.bitFlag(getter: () -> T, bit: Int): ReadOnlyProperty<Any?, Boolean> {
    require(bit in 0 until sizeBits) { "bit $bit is out of range [0, $sizeBits)" }
    val mask = lsb.leftRotate(bit)
    return ReadOnlyProperty { _, _ ->
        getter().bitwiseAnd(mask) != allClear
    }
}

/**
 * Returns a read-only property delegate that tests bit [bit] of the captured [value].
 *
 * This is a snapshot overload: [value] is captured at construction time and never re-read. Use the
 * [getter]-based overload when the backing value can change.
 *
 * @see bitFlag
 */
fun <T> BitCollection<T>.bitFlag(value: T, bit: Int): ReadOnlyProperty<Any?, Boolean> =
    bitFlag({ value }, bit)

/**
 * Returns a read-write property delegate that reads and writes bit [bit] of the value managed by
 * [getter]/[setter].
 *
 * The mask for position [bit] is computed once at construction as `lsb.leftRotate(bit)`.
 * - **get**: returns `true` if `getter() AND mask != allClear`.
 * - **set `true`**: calls `setter(getter() OR mask)`.
 * - **set `false`**: calls `setter(getter() AND mask.invert())`.
 *
 * [bit] must be in `[0, sizeBits)`. Passing a value outside that range throws
 * [IllegalArgumentException] at construction time (see [bitFlag] for why this matters).
 *
 * @see bitFlag
 * @see mutableBitRange
 */
fun <T> BitCollection<T>.mutableBitFlag(
    getter: () -> T,
    setter: (T) -> Unit,
    bit: Int,
): ReadWriteProperty<Any?, Boolean> {
    require(bit in 0 until sizeBits) { "bit $bit is out of range [0, $sizeBits)" }
    val mask = lsb.leftRotate(bit)
    return object : ReadWriteProperty<Any?, Boolean> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): Boolean =
            getter().bitwiseAnd(mask) != allClear

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
            val current = getter()
            setter(if (value) current.bitwiseOr(mask) else current.bitwiseAnd(mask.invert()))
        }
    }
}

/**
 * Returns a read-only property delegate that extracts the contiguous range of [count] bits
 * starting at position [start] from the value returned by [getter], yielding them as a `T` value
 * with those bits placed at the LSB positions `[0, count)`.
 *
 * **How it works** — only [BitCollection] operations are needed; [BitShift] is not required:
 * 1. A *low mask* of [count] consecutive 1-bits at positions `[0, count)` is built by OR-ing
 *    [count] single-bit masks: `lsb OR lsb.leftRotate(1) OR … OR lsb.leftRotate(count-1)`.
 * 2. A *high mask* positions that window at `[start, start+count)` via `loMask.leftRotate(start)`.
 * 3. To extract: `value AND hiMask` zeroes every bit outside the window; `rightRotate(start)` then
 *    moves the window bits down to `[0, count)`. The zero bits that rotate in from the high end are
 *    harmless because the AND already cleared them.
 *
 * [start] must be ≥ 0, [count] must be > 0, and `start + count` must be ≤ `sizeBits` (otherwise
 * `leftRotate` would wrap the mask across the word boundary).
 *
 * The delegate re-evaluates [getter] on every access.
 *
 * @see mutableBitRange
 * @see bitFlag
 */
fun <T> BitCollection<T>.bitRange(getter: () -> T, start: Int, count: Int): ReadOnlyProperty<Any?, T> {
    require(start >= 0) { "start must be non-negative, was $start" }
    require(count > 0) { "count must be positive, was $count" }
    require(start + count <= sizeBits) { "bit range [$start, ${start + count}) exceeds sizeBits=$sizeBits" }
    val loMask = (0 until count).fold(allClear) { acc, i -> acc.bitwiseOr(lsb.leftRotate(i)) }
    val hiMask = loMask.leftRotate(start)
    return ReadOnlyProperty { _, _ ->
        getter().bitwiseAnd(hiMask).rightRotate(start)
    }
}

/**
 * Returns a read-write property delegate that reads and writes the contiguous range of [count]
 * bits starting at position [start] of the value managed by [getter]/[setter].
 *
 * The masks are computed once at construction (see [bitRange] for a detailed explanation of the
 * mask-and-rotate technique).
 * - **get**: extracts bits `[start, start+count)` to LSB positions, identical to [bitRange].
 * - **set**: the supplied value is first masked to [count] bits (`value AND loMask`), rotated up to
 *   position [start], then merged into the current backing value by clearing the destination range
 *   and OR-ing in the new bits. Any bits in the supplied value above position `count-1` are ignored.
 *
 * [start] must be ≥ 0, [count] must be > 0, and `start + count` must be ≤ `sizeBits`.
 *
 * @see bitRange
 * @see mutableBitFlag
 */
fun <T> BitCollection<T>.mutableBitRange(
    getter: () -> T,
    setter: (T) -> Unit,
    start: Int,
    count: Int,
): ReadWriteProperty<Any?, T> {
    require(start >= 0) { "start must be non-negative, was $start" }
    require(count > 0) { "count must be positive, was $count" }
    require(start + count <= sizeBits) { "bit range [$start, ${start + count}) exceeds sizeBits=$sizeBits" }
    val loMask = (0 until count).fold(allClear) { acc, i -> acc.bitwiseOr(lsb.leftRotate(i)) }
    val hiMask = loMask.leftRotate(start)
    return object : ReadWriteProperty<Any?, T> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): T =
            getter().bitwiseAnd(hiMask).rightRotate(start)

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            val current = getter()
            val newBits = value.bitwiseAnd(loMask).leftRotate(start)
            setter(current.bitwiseAnd(hiMask.invert()).bitwiseOr(newBits))
        }
    }
}

/**
 * Returns a read-only property delegate for an **extension property** whose receiver [R] varies
 * from one call site to the next — e.g. `val SomeType.flagName: Boolean by
 * ops.extensionBitFlag({ someField }, 0)`.
 *
 * [bitFlag]'s `getter: () -> T` closure is captured once, when the delegate expression itself is
 * evaluated. For a top-level extension property that expression runs exactly once (like a
 * top-level `val`'s initializer), with no receiver bound yet — so a getter referencing a member of
 * [R] fails to resolve. [extractor] sidesteps this: it is an `R.() -> T` function, invoked with the
 * actual receiver instance on every access via the delegate's `thisRef` parameter, so [T] is read
 * fresh from whichever instance the property is being accessed on. [extractor] may be a bare lambda
 * (`{ someField }`) or a bound member reference (`SomeType::someField`); both resolve against the
 * receiver rather than the enclosing lexical scope.
 *
 * [bit] must be in `[0, sizeBits)`; see [bitFlag] for why out-of-range values throw eagerly.
 *
 * @see bitFlag
 * @see mutableExtensionBitFlag
 */
fun <R, T> BitCollection<T>.extensionBitFlag(extractor: R.() -> T, bit: Int): ReadOnlyProperty<R, Boolean> {
    require(bit in 0 until sizeBits) { "bit $bit is out of range [0, $sizeBits)" }
    val mask = lsb.leftRotate(bit)
    return ReadOnlyProperty { thisRef: R, _ -> thisRef.extractor().bitwiseAnd(mask) != allClear }
}

/**
 * Returns a read-write property delegate for an **extension property** whose receiver [R] varies
 * from one call site to the next, mirroring [mutableBitFlag] but threading the receiver through
 * [extractor] and [setter] on every access instead of capturing a fixed closure — see
 * [extensionBitFlag] for why this matters for top-level extension properties.
 *
 * [bit] must be in `[0, sizeBits)`; see [bitFlag] for why out-of-range values throw eagerly.
 *
 * @see mutableBitFlag
 * @see extensionBitFlag
 */
fun <R, T> BitCollection<T>.mutableExtensionBitFlag(
    extractor: R.() -> T,
    setter: R.(T) -> Unit,
    bit: Int,
): ReadWriteProperty<R, Boolean> {
    require(bit in 0 until sizeBits) { "bit $bit is out of range [0, $sizeBits)" }
    val mask = lsb.leftRotate(bit)
    return object : ReadWriteProperty<R, Boolean> {
        override fun getValue(thisRef: R, property: KProperty<*>): Boolean =
            thisRef.extractor().bitwiseAnd(mask) != allClear

        override fun setValue(thisRef: R, property: KProperty<*>, value: Boolean) {
            val current = thisRef.extractor()
            thisRef.setter(if (value) current.bitwiseOr(mask) else current.bitwiseAnd(mask.invert()))
        }
    }
}

/**
 * Returns a read-only property delegate for an **extension property** whose receiver [R] varies
 * from one call site to the next, mirroring [bitRange] but reading [T] via [extractor] on every
 * access instead of a closure captured once — see [extensionBitFlag] for why this matters for
 * top-level extension properties.
 *
 * [start] must be ≥ 0, [count] must be > 0, and `start + count` must be ≤ `sizeBits`; see [bitRange]
 * for the mask-and-rotate mechanism and why out-of-range values throw eagerly.
 *
 * @see bitRange
 * @see mutableExtensionBitRange
 */
fun <R, T> BitCollection<T>.extensionBitRange(extractor: R.() -> T, start: Int, count: Int): ReadOnlyProperty<R, T> {
    require(start >= 0) { "start must be non-negative, was $start" }
    require(count > 0) { "count must be positive, was $count" }
    require(start + count <= sizeBits) { "bit range [$start, ${start + count}) exceeds sizeBits=$sizeBits" }
    val loMask = (0 until count).fold(allClear) { acc, i -> acc.bitwiseOr(lsb.leftRotate(i)) }
    val hiMask = loMask.leftRotate(start)
    return ReadOnlyProperty { thisRef: R, _ -> thisRef.extractor().bitwiseAnd(hiMask).rightRotate(start) }
}

/**
 * Returns a read-write property delegate for an **extension property** whose receiver [R] varies
 * from one call site to the next, mirroring [mutableBitRange] but threading the receiver through
 * [extractor] and [setter] on every access instead of capturing a fixed closure — see
 * [extensionBitFlag] for why this matters for top-level extension properties.
 *
 * [start] must be ≥ 0, [count] must be > 0, and `start + count` must be ≤ `sizeBits`.
 *
 * @see mutableBitRange
 * @see extensionBitRange
 */
fun <R, T> BitCollection<T>.mutableExtensionBitRange(
    extractor: R.() -> T,
    setter: R.(T) -> Unit,
    start: Int,
    count: Int,
): ReadWriteProperty<R, T> {
    require(start >= 0) { "start must be non-negative, was $start" }
    require(count > 0) { "count must be positive, was $count" }
    require(start + count <= sizeBits) { "bit range [$start, ${start + count}) exceeds sizeBits=$sizeBits" }
    val loMask = (0 until count).fold(allClear) { acc, i -> acc.bitwiseOr(lsb.leftRotate(i)) }
    val hiMask = loMask.leftRotate(start)
    return object : ReadWriteProperty<R, T> {
        override fun getValue(thisRef: R, property: KProperty<*>): T =
            thisRef.extractor().bitwiseAnd(hiMask).rightRotate(start)

        override fun setValue(thisRef: R, property: KProperty<*>, value: T) {
            val current = thisRef.extractor()
            val newBits = value.bitwiseAnd(loMask).leftRotate(start)
            thisRef.setter(current.bitwiseAnd(hiMask.invert()).bitwiseOr(newBits))
        }
    }
}
