package com.kelvsyc.kotlin.decimal

@JsModule("decimal.js")
@JsNonModule
external class Decimal {
    constructor(value: String)
    constructor(value: Double)
    constructor(value: Int)
    constructor(value: Decimal)

    val d: Array<Int>   // internal digits (base 10^7)
    val e: Int          // exponent
    val s: Int          // sign: -1, 0, or 1

    // Arithmetic
    fun plus(n: Decimal): Decimal
    fun minus(n: Decimal): Decimal
    fun times(n: Decimal): Decimal
    fun dividedBy(n: Decimal): Decimal
    fun modulo(n: Decimal): Decimal
    fun toPower(n: Int): Decimal

    // Rounding
    fun ceil(): Decimal
    fun floor(): Decimal
    fun round(): Decimal
    fun truncated(): Decimal
    fun toDecimalPlaces(d: Int, rounding: Int = definedExternally): Decimal
    fun toSignificantDigits(d: Int, rounding: Int = definedExternally): Decimal

    // Sign / absolute value
    fun abs(): Decimal
    fun negated(): Decimal
    fun isNegative(): Boolean
    fun isPositive(): Boolean
    fun isZero(): Boolean

    // Classification
    fun isNaN(): Boolean
    fun isFinite(): Boolean
    fun isInteger(): Boolean

    // Comparison — returns Double because JS returns NaN when either operand is NaN
    fun comparedTo(n: Decimal): Double
    fun equals(n: Decimal): Boolean

    // Math
    fun squareRoot(): Decimal

    // Output
    fun toNumber(): Double
    fun toFixed(d: Int = definedExternally): String
    fun toPrecision(d: Int = definedExternally): String

    companion object {
        // Returns the Decimal constructor itself (dynamic) for chaining; use clone() for isolated contexts.
        fun set(config: dynamic): dynamic

        // Returns a new independent Decimal constructor with its own configuration.
        fun clone(config: dynamic = definedExternally): dynamic
    }
}
