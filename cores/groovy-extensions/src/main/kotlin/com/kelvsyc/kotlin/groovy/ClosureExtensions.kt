package com.kelvsyc.kotlin.groovy

import groovy.lang.Closure

/**
 * Calls this closure with [args], so that a closure can be called like a Kotlin function: `closure(1, 2)`.
 */
operator fun <V> Closure<V>.invoke(vararg args: Any?): V = call(*args)

/**
 * Returns a Kotlin function that calls this closure with no arguments.
 */
fun <V> Closure<V>.toFunction0(): () -> V = { call() }

/**
 * Returns a Kotlin function that calls this closure with one argument.
 */
fun <P1, V> Closure<V>.toFunction1(): (P1) -> V = { p1 -> call(p1) }

/**
 * Returns a Kotlin function that calls this closure with two arguments.
 */
fun <P1, P2, V> Closure<V>.toFunction2(): (P1, P2) -> V = { p1, p2 -> call(p1, p2) }

/**
 * Returns a Kotlin function that calls this closure with three arguments.
 */
fun <P1, P2, P3, V> Closure<V>.toFunction3(): (P1, P2, P3) -> V = { p1, p2, p3 -> call(p1, p2, p3) }
