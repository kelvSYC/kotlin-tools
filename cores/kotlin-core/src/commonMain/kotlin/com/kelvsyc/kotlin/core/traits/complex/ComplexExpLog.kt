package com.kelvsyc.kotlin.core.traits.complex

import com.kelvsyc.kotlin.core.Complex
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointArithmetic
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointExpLog
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointHypot
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointSinCos
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointTrigonometry
import com.kelvsyc.kotlin.core.traits.fp.double
import com.kelvsyc.kotlin.core.traits.fp.float

/**
 * `ComplexExpLog` is a trait providing exponential, logarithm, and power operations over complex
 * values of type [C] whose components are of type [T].
 *
 * Two factory functions are available via [Companion.from]:
 * - `from(expLog, trig, hypot, arith, ...)`: universal — uses separate [FloatingPointTrigonometry.sin]
 *   and [FloatingPointTrigonometry.cos] calls in [exp].
 * - `from(expLog, sinCos, trig, hypot, arith, ...)`: optimised — uses a single
 *   [FloatingPointSinCos.sincos] call in [exp] where the platform supports atomic joint
 *   sine/cosine computation.
 *
 * Both paths compute [ln] via [FloatingPointHypot] for the modulus, avoiding overflow for large
 * inputs. Complex-exponent power is named [powComplex] rather than `pow` to avoid a JVM erasure
 * clash with the scalar-exponent overload.
 *
 * Standard instances for [Complex]<[Float]> and [Complex]<[Double]> are available as
 * [Companion.float] and [Companion.double]; these use the universal trig-based path.
 */
interface ComplexExpLog<C, T> {
    fun C.real(): T
    fun C.imaginary(): T
    fun of(real: T, imaginary: T): C

    /** Returns `exp(a)·cos(b) + i·exp(a)·sin(b)` where `this = a + i·b`. */
    fun C.exp(): C

    /** Returns `ln(hypot(a, b)) + i·atan2(b, a)`. Uses [FloatingPointHypot] for the modulus. */
    fun C.ln(): C

    /** Returns `exp(y · ln(this))` for real scalar exponent `y`. */
    fun C.pow(y: T): C

    /** Returns `exp(w · ln(this))` for complex exponent `w`. */
    fun C.powComplex(w: C): C

    companion object
}

// ── Factories ─────────────────────────────────────────────────────────────────

/**
 * Returns a [ComplexExpLog] backed by [FloatingPointTrigonometry] — universal, works on all platforms.
 *
 * [exp] calls [FloatingPointTrigonometry.sin] and [FloatingPointTrigonometry.cos] separately.
 */
fun <C, T> ComplexExpLog.Companion.from(
    expLog: FloatingPointExpLog<T>,
    trig: FloatingPointTrigonometry<T>,
    hypot: FloatingPointHypot<T>,
    arith: FloatingPointArithmetic<T>,
    real: C.() -> T,
    imaginary: C.() -> T,
    construct: (T, T) -> C,
): ComplexExpLog<C, T> = object : ComplexExpLog<C, T> {
    override fun C.real(): T = real()
    override fun C.imaginary(): T = imaginary()
    override fun of(real: T, imaginary: T): C = construct(real, imaginary)

    override fun C.exp(): C {
        val a = real(); val b = imaginary()
        val e = expLog.run { a.exp() }
        return with(arith) {
            construct(
                e.multiply(trig.run { b.cos() }),
                e.multiply(trig.run { b.sin() }),
            )
        }
    }

    override fun C.ln(): C {
        val a = real(); val b = imaginary()
        val r = with(hypot) { a.hypot(b) }
        return construct(
            expLog.run { r.ln() },
            with(trig) { b.atan2(a) },
        )
    }

    override fun C.pow(y: T): C {
        val lnZ = ln()
        val lnR = lnZ.real(); val lnI = lnZ.imaginary()
        return with(arith) {
            construct(lnR.multiply(y), lnI.multiply(y))
        }.exp()
    }

    override fun C.powComplex(w: C): C {
        val lnZ = ln()
        val lnR = lnZ.real(); val lnI = lnZ.imaginary()
        val wR = w.real(); val wI = w.imaginary()
        return with(arith) {
            construct(
                wR.multiply(lnR).subtract(wI.multiply(lnI)),
                wR.multiply(lnI).add(wI.multiply(lnR)),
            )
        }.exp()
    }
}

/**
 * Returns a [ComplexExpLog] backed by [FloatingPointSinCos] — optimised for platforms where
 * joint sine/cosine computation is natively supported (macOS ARM64, Linux x64).
 *
 * [exp] calls [FloatingPointSinCos.sincos] once to obtain both components simultaneously.
 * [ln] still uses [FloatingPointTrigonometry] for [FloatingPointTrigonometry.atan2].
 */
fun <C, T> ComplexExpLog.Companion.from(
    expLog: FloatingPointExpLog<T>,
    sinCos: FloatingPointSinCos<T>,
    trig: FloatingPointTrigonometry<T>,
    hypot: FloatingPointHypot<T>,
    arith: FloatingPointArithmetic<T>,
    real: C.() -> T,
    imaginary: C.() -> T,
    construct: (T, T) -> C,
): ComplexExpLog<C, T> = object : ComplexExpLog<C, T> {
    override fun C.real(): T = real()
    override fun C.imaginary(): T = imaginary()
    override fun of(real: T, imaginary: T): C = construct(real, imaginary)

    override fun C.exp(): C {
        val a = real(); val b = imaginary()
        val e = expLog.run { a.exp() }
        val (s, c) = sinCos.run { b.sincos() }
        return with(arith) { construct(e.multiply(c), e.multiply(s)) }
    }

    override fun C.ln(): C {
        val a = real(); val b = imaginary()
        val r = with(hypot) { a.hypot(b) }
        return construct(
            expLog.run { r.ln() },
            with(trig) { b.atan2(a) },
        )
    }

    override fun C.pow(y: T): C {
        val lnZ = ln()
        val lnR = lnZ.real(); val lnI = lnZ.imaginary()
        return with(arith) {
            construct(lnR.multiply(y), lnI.multiply(y))
        }.exp()
    }

    override fun C.powComplex(w: C): C {
        val lnZ = ln()
        val lnR = lnZ.real(); val lnI = lnZ.imaginary()
        val wR = w.real(); val wI = w.imaginary()
        return with(arith) {
            construct(
                wR.multiply(lnR).subtract(wI.multiply(lnI)),
                wR.multiply(lnI).add(wI.multiply(lnR)),
            )
        }.exp()
    }
}

// ── Float ─────────────────────────────────────────────────────────────────────

private val floatInstance: ComplexExpLog<Complex<Float>, Float> =
    ComplexExpLog.from(
        expLog = FloatingPointExpLog.float,
        trig = FloatingPointTrigonometry.float,
        hypot = FloatingPointHypot.float,
        arith = FloatingPointArithmetic.float,
        real = { real },
        imaginary = { imaginary },
        construct = ::Complex,
    )

// ── Double ────────────────────────────────────────────────────────────────────

private val doubleInstance: ComplexExpLog<Complex<Double>, Double> =
    ComplexExpLog.from(
        expLog = FloatingPointExpLog.double,
        trig = FloatingPointTrigonometry.double,
        hypot = FloatingPointHypot.double,
        arith = FloatingPointArithmetic.double,
        real = { real },
        imaginary = { imaginary },
        construct = ::Complex,
    )

val ComplexExpLog.Companion.float: ComplexExpLog<Complex<Float>, Float> get() = floatInstance
val ComplexExpLog.Companion.double: ComplexExpLog<Complex<Double>, Double> get() = doubleInstance
