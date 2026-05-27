package com.kelvsyc.kotlin.commons.numbers.complex

import com.kelvsyc.kotlin.core.traits.complex.ComplexCubeRoot
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointCubeRoot
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointHypot
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointTrigPi
import com.kelvsyc.kotlin.core.traits.fp.double
import org.apache.commons.numbers.complex.Complex as CommonsComplex

// CommonsComplex has no cbrt() method, so the principal cube root is computed via polar form:
//   r       = hypot(a, b)
//   thetaPi = atan2Pi(b, a)    // argument in units of π, range (−1, 1]
//   cbrtR   = cbrt(r)
//   re      = cbrtR · cosPi(thetaPi / 3)
//   im      = cbrtR · sinPi(thetaPi / 3)
// FloatingPointTrigPi is used for accuracy: cosPi(1/3) = 0.5 exactly, avoiding the rounding
// error that cos(π/3) would introduce.

private val commonsComplexCubeRootInstance: ComplexCubeRoot<CommonsComplex, Double> =
    object : ComplexCubeRoot<CommonsComplex, Double> {
        private val cbrtOps = FloatingPointCubeRoot.double
        private val hypotOps = FloatingPointHypot.double
        private val trigPiOps = FloatingPointTrigPi.double

        override fun CommonsComplex.real(): Double = real
        override fun CommonsComplex.imaginary(): Double = imaginary
        override fun of(real: Double, imaginary: Double): CommonsComplex = CommonsComplex.ofCartesian(real, imaginary)

        override fun CommonsComplex.cbrt(): CommonsComplex {
            val a = real; val b = imaginary
            val r = with(hypotOps) { a.hypot(b) }
            val thetaPi = with(trigPiOps) { b.atan2Pi(a) }
            val cbrtR = with(cbrtOps) { r.cbrt() }
            val phi = thetaPi / 3.0
            return CommonsComplex.ofCartesian(
                cbrtR * with(trigPiOps) { phi.cosPi() },
                cbrtR * with(trigPiOps) { phi.sinPi() },
            )
        }
    }

/**
 * A [ComplexCubeRoot] instance for Commons Numbers [CommonsComplex] with component type [Double].
 *
 * The principal cube root is computed via the polar form using [FloatingPointTrigPi] for accuracy
 * at rational multiples of π. Commons Numbers [CommonsComplex] does not provide a native `cbrt`
 * operation.
 */
val ComplexCubeRoot.Companion.commonsComplex: ComplexCubeRoot<CommonsComplex, Double>
    get() = commonsComplexCubeRootInstance
