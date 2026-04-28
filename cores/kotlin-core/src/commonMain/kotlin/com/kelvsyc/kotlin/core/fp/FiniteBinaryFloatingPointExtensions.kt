package com.kelvsyc.kotlin.core.fp

import com.kelvsyc.kotlin.core.BFloat16
import com.kelvsyc.kotlin.core.Float16

/**
 * Converts this value to a [FiniteBinaryFloatingPoint], preserving the full significand.
 *
 * For normal values, the implicit leading 1 bit is made explicit in the significand. For subnormal values, the
 * significand is the raw mantissa field with no leading bit added. Special values (infinity, NaN) are not supported
 * and will throw [IllegalArgumentException].
 */
fun BFloat16.toRegularBinaryFloatingPoint(): FiniteBinaryFloatingPoint<UShort> {
    require(isFinite()) { "Cannot convert non-finite BFloat16 (bits=$bits) to FiniteBinaryFloatingPoint" }
    val sign = bits < 0
    val fraction = mantissa.toUShort()
    return if (biasedExponent == 0) {
        // Zero or subnormal: value = fraction × 2^(-133)
        FiniteBinaryFloatingPoint(sign, -133, fraction)
    } else {
        // Normal: value = (2^7 | fraction) × 2^(biasedExponent - 134)
        FiniteBinaryFloatingPoint(sign, biasedExponent - 134, ((1 shl 7) or mantissa).toUShort())
    }
}

/**
 * Converts this floating point representation to a [BFloat16].
 *
 * Due to the need to pack the sign and exponent, not all values can be represented. A value that is too large or too
 * small will be converted into their respective infinity or zero values.
 */
fun FiniteBinaryFloatingPoint<UShort>.toBFloat16(): BFloat16 {
    if (significand == 0u.toUShort()) return if (sign) BFloat16(0x8000.toShort()) else BFloat16(0)

    val signBit = if (sign) 0x8000 else 0
    val lead = 31 - significand.toInt().countLeadingZeroBits()
    val biasedExp = exponent.toLong() + lead + 127L

    return when {
        biasedExp >= 255L -> BFloat16((signBit or 0x7F80).toShort())
        biasedExp > 0L -> {
            val fraction: Int = if (lead <= 7) {
                (significand.toInt() and ((1 shl lead) - 1)) shl (7 - lead)
            } else {
                val shift = lead - 7
                val roundBit = (significand.toInt() ushr (shift - 1)) and 1
                val sticky = (significand.toInt() and ((1 shl (shift - 1)) - 1)) != 0
                val frac = (significand.toInt() ushr shift) and 0x7F
                if (roundBit == 1 && (sticky || frac and 1 != 0)) frac + 1 else frac
            }
            BFloat16((signBit + (biasedExp.toInt() shl 7) + fraction).toShort())
        }
        else -> {
            // Subnormal or underflow. A subnormal BFloat16 stores m in 7 bits where value = m × 2^(-133).
            // Here m = significand × 2^(exponent + 133).
            val subShift = exponent + 133
            val m: Int = when {
                subShift > 0 -> (significand.toInt() shl subShift) and 0x7F
                subShift == 0 -> significand.toInt() and 0x7F
                subShift > -16 -> (significand.toInt() ushr (-subShift)) and 0x7F
                else -> 0
            }
            BFloat16((signBit or m).toShort())
        }
    }
}

/**
 * Converts this value to a [FiniteBinaryFloatingPoint], preserving the full significand.
 *
 * For normal values, the implicit leading 1 bit is made explicit in the significand. For subnormal values, the
 * significand is the raw mantissa field with no leading bit added. Special values (infinity, NaN) are not supported
 * and will throw [IllegalArgumentException].
 */
fun Float16.toRegularBinaryFloatingPoint(): FiniteBinaryFloatingPoint<UShort> {
    require(isFinite()) { "Cannot convert non-finite Float16 (bits=${bits.toInt() and 0xFFFF}) to RegularBinaryFloatingPoint" }
    val sign = bits < 0
    val fraction = mantissa.toUShort()
    return if (biasedExponent == 0) {
        // Zero or subnormal: value = fraction × 2^(-24)
        FiniteBinaryFloatingPoint(sign, -24, fraction)
    } else {
        // Normal: value = (2^10 | fraction) × 2^(biasedExponent - 25)
        FiniteBinaryFloatingPoint(sign, biasedExponent - 25, ((1 shl 10) or mantissa).toUShort())
    }
}

/**
 * Converts this floating point representation to a [Float16].
 *
 * Due to the need to pack the sign and exponent, not all values can be represented. A value that is too large or too
 * small will be converted into their respective infinity or zero values.
 */
fun FiniteBinaryFloatingPoint<UShort>.toFloat16(): Float16 {
    if (significand == 0u.toUShort()) return if (sign) Float16(0x8000.toShort()) else Float16(0)

    val signBit = if (sign) 0x8000 else 0
    val lead = 31 - significand.toInt().countLeadingZeroBits()
    val biasedExp = exponent.toLong() + lead + 15L

    return when {
        biasedExp >= 31L -> Float16((signBit or 0x7C00).toShort())
        biasedExp > 0L -> {
            val fraction: Int = if (lead <= 10) {
                (significand.toInt() and ((1 shl lead) - 1)) shl (10 - lead)
            } else {
                val shift = lead - 10
                val roundBit = (significand.toInt() ushr (shift - 1)) and 1
                val sticky = (significand.toInt() and ((1 shl (shift - 1)) - 1)) != 0
                val frac = (significand.toInt() ushr shift) and 0x3FF
                if (roundBit == 1 && (sticky || frac and 1 != 0)) frac + 1 else frac
            }
            Float16((signBit + (biasedExp.toInt() shl 10) + fraction).toShort())
        }
        else -> {
            // Subnormal or underflow. A subnormal Float16 stores m in 10 bits where value = m × 2^(-24).
            // Here m = significand × 2^(exponent + 24).
            val subShift = exponent + 24
            val m: Int = when {
                subShift > 0 -> (significand.toInt() shl subShift) and 0x3FF
                subShift == 0 -> significand.toInt() and 0x3FF
                subShift > -16 -> (significand.toInt() ushr (-subShift)) and 0x3FF
                else -> 0
            }
            Float16((signBit or m).toShort())
        }
    }
}

/**
 * Converts this value to a [FiniteBinaryFloatingPoint], preserving the full significand.
 *
 * For normal values, the implicit leading 1 bit is made explicit in the significand. For subnormal values, the
 * significand is the raw mantissa field with no leading bit added. Special values (infinity, NaN) are not supported
 * and will throw [IllegalArgumentException].
 */
fun Float.toRegularBinaryFloatingPoint(): FiniteBinaryFloatingPoint<UInt> {
    require(isFinite()) { "Cannot convert non-finite value $this to RegularBinaryFloatingPoint" }
    val bits = toRawBits()
    val sign = bits < 0
    val biasedExp = (bits ushr 23) and 0xFF
    val fraction = (bits and 0x7FFFFF).toUInt()
    return if (biasedExp == 0) {
        // Zero or subnormal: value = fraction × 2^(-149)
        FiniteBinaryFloatingPoint(sign, -149, fraction)
    } else {
        // Normal: value = (2^23 | fraction) × 2^(biasedExp-150)
        FiniteBinaryFloatingPoint(sign, biasedExp - 150, (1u shl 23) or fraction)
    }
}

/**
 * Converts this value to a [FiniteBinaryFloatingPoint], preserving the full significand.
 *
 * For normal values, the implicit leading 1 bit is made explicit in the significand. For subnormal values, the
 * significand is the raw mantissa field with no leading bit added. Special values (infinity, NaN) are not supported
 * and will throw [IllegalArgumentException].
 */
fun Double.toRegularBinaryFloatingPoint(): FiniteBinaryFloatingPoint<ULong> {
    require(isFinite()) { "Cannot convert non-finite value $this to RegularBinaryFloatingPoint" }
    val bits = toRawBits()
    val sign = bits < 0
    val biasedExp = ((bits ushr 52) and 0x7FFL).toInt()
    val fraction = (bits and 0x000FFFFFFFFFFFFFL).toULong()
    return if (biasedExp == 0) {
        // Zero or subnormal: value = fraction × 2^(-1074)
        FiniteBinaryFloatingPoint(sign, -1074, fraction)
    } else {
        // Normal: value = (2^52 | fraction) × 2^(biasedExp-1075)
        FiniteBinaryFloatingPoint(sign, biasedExp - 1075, (1uL shl 52) or fraction)
    }
}

/**
 * Converts this floating point representation to a [Float].
 *
 * Due to the need to pack the sign and exponent, not all values can be represented. A value that is too large or too
 * small will be converted into their respective infinity or zero values.
 */
fun FiniteBinaryFloatingPoint<UInt>.toFloat(): Float {
    if (significand == 0u) return if (sign) -0.0f else 0.0f

    val signBit = if (sign) (1 shl 31) else 0
    val lead = 31 - significand.countLeadingZeroBits()
    val biasedExp = exponent.toLong() + lead + 127L

    return when {
        biasedExp >= 255L -> Float.fromBits(signBit or 0x7F800000)
        biasedExp > 0L -> {
            val fraction: Int = if (lead <= 23) {
                ((significand and ((1u shl lead) - 1u)) shl (23 - lead)).toInt()
            } else {
                val shift = lead - 23
                val roundBit = ((significand shr (shift - 1)) and 1u).toInt()
                val sticky = (significand and ((1u shl (shift - 1)) - 1u)).toInt()
                val frac = ((significand shr shift) and 0x7FFFFFu).toInt()
                if (roundBit == 1 && (sticky != 0 || frac and 1 != 0)) frac + 1 else frac
            }
            Float.fromBits(signBit + (biasedExp.toInt() shl 23) + fraction)
        }
        else -> {
            // Subnormal or underflow. A subnormal Float stores m in 23 bits where value = m × 2^(-149).
            // Here m = significand × 2^(exponent + 149).
            val subShift = exponent + 149
            val m: UInt = when {
                subShift > 0 -> (significand shl subShift) and 0x7FFFFFu
                subShift == 0 -> significand and 0x7FFFFFu
                subShift > -32 -> (significand shr (-subShift)) and 0x7FFFFFu
                else -> 0u
            }
            Float.fromBits(signBit or m.toInt())
        }
    }
}

/**
 * Converts this floating point representation to a [Double].
 *
 * Due to the need to pack the sign and exponent, not all values can be represented. A value that is too large or too
 * small will be converted into their respective infinity or zero values.
 */
fun FiniteBinaryFloatingPoint<ULong>.toDouble(): Double {
    if (significand == 0uL) return if (sign) -0.0 else 0.0

    val signBit = if (sign) (1L shl 63) else 0L
    val lead = 63 - significand.countLeadingZeroBits()
    val biasedExp = exponent.toLong() + lead + 1023L

    return when {
        biasedExp >= 2047L -> Double.fromBits(signBit or 0x7FF0000000000000L)
        biasedExp > 0L -> {
            val fraction: Long = if (lead <= 52) {
                ((significand and ((1uL shl lead) - 1uL)) shl (52 - lead)).toLong()
            } else {
                val shift = lead - 52
                val roundBit = ((significand shr (shift - 1)) and 1uL).toInt()
                val sticky = (significand and ((1uL shl (shift - 1)) - 1uL)) != 0uL
                val frac = ((significand shr shift) and 0xFFFFFFFFFFFFFuL).toLong()
                if (roundBit == 1 && (sticky || frac and 1L != 0L)) frac + 1 else frac
            }
            Double.fromBits(signBit + (biasedExp shl 52) + fraction)
        }
        else -> {
            // Subnormal or underflow. A subnormal Double stores m in 52 bits where value = m × 2^(-1074).
            // Here m = significand × 2^(exponent + 1074).
            val subShift = exponent + 1074
            val m: ULong = when {
                subShift > 0 -> (significand shl subShift) and 0xFFFFFFFFFFFFFuL
                subShift == 0 -> significand and 0xFFFFFFFFFFFFFuL
                subShift > -64 -> (significand shr (-subShift)) and 0xFFFFFFFFFFFFFuL
                else -> 0uL
            }
            Double.fromBits(signBit or m.toLong())
        }
    }
}
