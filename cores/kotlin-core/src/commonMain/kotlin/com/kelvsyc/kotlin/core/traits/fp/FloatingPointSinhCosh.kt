package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.BFloat16
import com.kelvsyc.kotlin.core.Float16

/**
 * `FloatingPointSinhCosh` is a trait providing a combined hyperbolic sine and cosine operation
 * for a floating-point type [T].
 *
 * [sinhcosh] computes both `sinh(x)` and `cosh(x)` from a single `exp(x)` evaluation:
 * `sinh(x) = (eˣ − e⁻ˣ) / 2` and `cosh(x) = (eˣ + e⁻ˣ) / 2`, reusing `e⁻ˣ = 1 / eˣ`.
 * This halves the number of exponential evaluations compared to calling
 * [FloatingPointTrigonometry.sinh] and [FloatingPointTrigonometry.cosh] separately.
 *
 * Unlike [FloatingPointSinCos], which requires a platform-specific native joint function
 * (`sincos`/`sincosf`) and is intentionally absent where that function is unavailable, this
 * trait is implemented entirely in `commonMain` because no equivalent `sinhcosh` POSIX function
 * exists on any platform; the `exp`-based derivation is the optimal implementation everywhere.
 *
 * Standard implementations for [Float16], [BFloat16], [Float], and [Double] are available as
 * [Companion.float16], [Companion.bfloat16], [Companion.float], and [Companion.double] respectively.
 */
interface FloatingPointSinhCosh<T> {
    companion object

    /**
     * Returns `SinhCoshResult(sinh(this), cosh(this))` computed from a single `exp` call.
     */
    fun T.sinhcosh(): SinhCoshResult<T>
}

private val bfloat16Instance: FloatingPointSinhCosh<BFloat16> = object : FloatingPointSinhCosh<BFloat16> {
    override fun BFloat16.sinhcosh(): SinhCoshResult<BFloat16> {
        val ex = kotlin.math.exp(this.toFloat().toDouble())
        val exInv = 1.0 / ex
        return SinhCoshResult(BFloat16(((ex - exInv) / 2.0).toFloat()), BFloat16(((ex + exInv) / 2.0).toFloat()))
    }
}

private val float16Instance: FloatingPointSinhCosh<Float16> = object : FloatingPointSinhCosh<Float16> {
    override fun Float16.sinhcosh(): SinhCoshResult<Float16> {
        val ex = kotlin.math.exp(this.toFloat().toDouble())
        val exInv = 1.0 / ex
        return SinhCoshResult(Float16(((ex - exInv) / 2.0).toFloat()), Float16(((ex + exInv) / 2.0).toFloat()))
    }
}

private val floatInstance: FloatingPointSinhCosh<Float> = object : FloatingPointSinhCosh<Float> {
    override fun Float.sinhcosh(): SinhCoshResult<Float> {
        val ex = kotlin.math.exp(this.toDouble())
        val exInv = 1.0 / ex
        return SinhCoshResult(((ex - exInv) / 2.0).toFloat(), ((ex + exInv) / 2.0).toFloat())
    }
}

private val doubleInstance: FloatingPointSinhCosh<Double> = object : FloatingPointSinhCosh<Double> {
    override fun Double.sinhcosh(): SinhCoshResult<Double> {
        val ex = kotlin.math.exp(this)
        val exNeg = kotlin.math.exp(-this)
        return SinhCoshResult((ex - exNeg) / 2.0, (ex + exNeg) / 2.0)
    }
}

val FloatingPointSinhCosh.Companion.bfloat16: FloatingPointSinhCosh<BFloat16> get() = bfloat16Instance
val FloatingPointSinhCosh.Companion.float16: FloatingPointSinhCosh<Float16> get() = float16Instance
val FloatingPointSinhCosh.Companion.float: FloatingPointSinhCosh<Float> get() = floatInstance
val FloatingPointSinhCosh.Companion.double: FloatingPointSinhCosh<Double> get() = doubleInstance
