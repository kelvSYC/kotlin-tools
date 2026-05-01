package com.kelvsyc.kotlin.core.traits

/**
 * `Gcd` is a composable trait providing greatest common divisor and least common multiple operations
 * on values of type [T].
 *
 * For signed types, [gcd] returns a non-negative result by operating on the absolute values of both
 * inputs. This inherits the overflow behaviour of [SignedIntegerArithmetic.abs]: passing [Int.MIN_VALUE]
 * or [Long.MIN_VALUE] as either argument wraps, returning a negative result. Callers that depend on a
 * non-negative return must avoid those values.
 *
 * For unsigned types, [gcd] operates on the raw values directly.
 *
 * By convention, `gcd(0, 0) = 0`. `lcm(a, 0) = lcm(0, a) = 0`.
 *
 * [lcm] overflow is implementation-defined (wrapping), matching the behaviour of [IntegerArithmetic.multiply].
 *
 * Instances for all eight primitive integral types are available via the companion:
 * - Signed: [Companion.byte], [Companion.short], [Companion.int], [Companion.long]
 * - Unsigned: [Companion.ubyte], [Companion.ushort], [Companion.uint], [Companion.ulong]
 *
 * An instance can also be derived from an [IntegerArithmetic] or [SignedIntegerArithmetic] via
 * [Companion.from].
 */
interface Gcd<T> {
    companion object

    fun T.gcd(other: T): T
    fun T.lcm(other: T): T
}

/**
 * Returns a [Gcd] instance derived from [arithmetic] for an unsigned integral type.
 *
 * [Gcd.gcd] uses the Euclidean algorithm on the raw values. [Gcd.lcm] is computed as
 * `(a / gcd(a, b)) * b`, dividing before multiplying to reduce intermediate overflow.
 */
fun <T> Gcd.Companion.from(arithmetic: IntegerArithmetic<T>): Gcd<T> = object : Gcd<T> {
    override fun T.gcd(other: T): T {
        var x = this
        var y = other
        while (with(arithmetic) { y.compareTo(zero) } != 0) {
            val next = with(arithmetic) { x.rem(y) }
            x = y
            y = next
        }
        return x
    }

    override fun T.lcm(other: T): T {
        val self = this
        if (with(arithmetic) { self.compareTo(zero) } == 0 || with(arithmetic) { other.compareTo(zero) } == 0) {
            return arithmetic.zero
        }
        val g = self.gcd(other)
        return with(arithmetic) { self.divide(g).multiply(other) }
    }
}

/**
 * Returns a [Gcd] instance derived from [arithmetic] for a signed integral type.
 *
 * [Gcd.gcd] applies [SignedIntegerArithmetic.abs] to both inputs before running the Euclidean
 * algorithm, ensuring a non-negative result. [Gcd.lcm] is computed as `(|a| / gcd(a, b)) * |b|`
 * and is therefore also non-negative. Both operations inherit the overflow behaviour of [abs] for
 * [Int.MIN_VALUE] / [Long.MIN_VALUE].
 */
fun <T> Gcd.Companion.from(arithmetic: SignedIntegerArithmetic<T>): Gcd<T> = object : Gcd<T> {
    override fun T.gcd(other: T): T {
        val self = this
        var x = with(arithmetic) { self.abs() }
        var y = with(arithmetic) { other.abs() }
        while (with(arithmetic) { y.compareTo(zero) } != 0) {
            val next = with(arithmetic) { x.rem(y) }
            x = y
            y = next
        }
        return x
    }

    override fun T.lcm(other: T): T {
        val self = this
        val absA = with(arithmetic) { self.abs() }
        val absB = with(arithmetic) { other.abs() }
        if (with(arithmetic) { absA.compareTo(zero) } == 0 || with(arithmetic) { absB.compareTo(zero) } == 0) {
            return arithmetic.zero
        }
        val g = self.gcd(other)
        return with(arithmetic) { absA.divide(g).multiply(absB) }
    }
}

private val byteInstance: Gcd<Byte> by lazy { Gcd.from(SignedIntegerArithmetic.byte) }
private val shortInstance: Gcd<Short> by lazy { Gcd.from(SignedIntegerArithmetic.short) }
private val intInstance: Gcd<Int> by lazy { Gcd.from(SignedIntegerArithmetic.int) }
private val longInstance: Gcd<Long> by lazy { Gcd.from(SignedIntegerArithmetic.long) }
private val ubyteInstance: Gcd<UByte> by lazy { Gcd.from(IntegerArithmetic.ubyte) }
private val ushortInstance: Gcd<UShort> by lazy { Gcd.from(IntegerArithmetic.ushort) }
private val uintInstance: Gcd<UInt> by lazy { Gcd.from(IntegerArithmetic.uint) }
private val ulongInstance: Gcd<ULong> by lazy { Gcd.from(IntegerArithmetic.ulong) }

val Gcd.Companion.byte: Gcd<Byte> get() = byteInstance
val Gcd.Companion.short: Gcd<Short> get() = shortInstance
val Gcd.Companion.int: Gcd<Int> get() = intInstance
val Gcd.Companion.long: Gcd<Long> get() = longInstance
val Gcd.Companion.ubyte: Gcd<UByte> get() = ubyteInstance
val Gcd.Companion.ushort: Gcd<UShort> get() = ushortInstance
val Gcd.Companion.uint: Gcd<UInt> get() = uintInstance
val Gcd.Companion.ulong: Gcd<ULong> get() = ulongInstance
