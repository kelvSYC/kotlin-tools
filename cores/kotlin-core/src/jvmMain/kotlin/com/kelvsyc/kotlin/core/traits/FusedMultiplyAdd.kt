package com.kelvsyc.kotlin.core.traits

private val floatInstance: FusedMultiplyAdd<Float> = object : FusedMultiplyAdd<Float> {
    override fun fma(a: Float, b: Float, c: Float): Float = Math.fma(a, b, c)
}

private val doubleInstance: FusedMultiplyAdd<Double> = object : FusedMultiplyAdd<Double> {
    override fun fma(a: Double, b: Double, c: Double): Double = Math.fma(a, b, c)
}

val FusedMultiplyAdd.Companion.float: FusedMultiplyAdd<Float>
    get() = floatInstance

val FusedMultiplyAdd.Companion.double: FusedMultiplyAdd<Double>
    get() = doubleInstance
