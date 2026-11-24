package com.kelvsyc.kotlin.groovy

import groovy.lang.GroovyObject

/**
 * Reads the property [name] of this object, as Groovy's `obj.name` does.
 */
operator fun GroovyObject.get(name: String): Any? = getProperty(name)

/**
 * Writes the property [name] of this object, as Groovy's `obj.name = value` does.
 */
operator fun GroovyObject.set(name: String, value: Any?) = setProperty(name, value)

/**
 * Calls the method [method] of this object with [args], dispatched dynamically as Groovy's `obj.method(args)` is.
 *
 * Not named `invoke`: a `Closure` is itself a [GroovyObject], and on a closure `invoke` is the closure call.
 */
fun GroovyObject.callMethod(method: String, vararg args: Any?): Any? = invokeMethod(method, args)
