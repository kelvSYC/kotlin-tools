package com.kelvsyc.kotlin.core

// JS BigInt is a primitive with no corresponding object; external interface gives structural typing.
external interface BigInt {
    companion object
}

@JsName("BigInt")
external fun bigIntOf(value: String): BigInt

@JsName("BigInt")
external fun bigIntOf(value: Int): BigInt

// Internal bridge used by BigIntConverters to serialize a BigInt to its decimal string.
// `val self = this` is required: js() substitutes local variable names reliably, but `this`
// refers to the JS execution context (not the Kotlin receiver) outside member-extension scopes.
@Suppress("NOTHING_TO_INLINE")
internal inline fun BigInt.toDecimalString(): String {
    val self = this
    return js("self.toString(10)")
}

// Public inline operators — `val self = this` ensures js() sees the Kotlin receiver as a
// substitutable local variable rather than relying on JS `this`.
@Suppress("NOTHING_TO_INLINE")
inline operator fun BigInt.plus(other: BigInt): BigInt {
    val self = this; return js("self + other")
}
@Suppress("NOTHING_TO_INLINE")
inline operator fun BigInt.minus(other: BigInt): BigInt {
    val self = this; return js("self - other")
}
@Suppress("NOTHING_TO_INLINE")
inline operator fun BigInt.times(other: BigInt): BigInt {
    val self = this; return js("self * other")
}
@Suppress("NOTHING_TO_INLINE")
inline operator fun BigInt.div(other: BigInt): BigInt {
    val self = this; return js("self / other")
}
@Suppress("NOTHING_TO_INLINE")
inline operator fun BigInt.rem(other: BigInt): BigInt {
    val self = this; return js("self % other")
}
@Suppress("NOTHING_TO_INLINE")
inline operator fun BigInt.unaryMinus(): BigInt {
    val self = this; return js("-self")
}

// compareTo as an operator extension gives < / > / <= / >= sugar without asserting Comparable.
@Suppress("NOTHING_TO_INLINE")
inline operator fun BigInt.compareTo(other: BigInt): Int {
    val self = this; return js("self < other ? -1 : self > other ? 1 : 0")
}

val BigInt.Companion.comparator: Comparator<BigInt>
    get() = Comparator { a, b -> a.compareTo(b) }
