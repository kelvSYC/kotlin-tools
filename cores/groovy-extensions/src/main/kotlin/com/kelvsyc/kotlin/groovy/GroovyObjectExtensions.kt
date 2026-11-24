package com.kelvsyc.kotlin.groovy

import groovy.lang.GroovyObject
import kotlin.reflect.KProperty

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

/**
 * Delegates a property to the property of the same name of this object: `val name: String by groovyObject`. The value is
 * cast to the property's type when read, so a value of the wrong type fails there with [ClassCastException].
 */
inline operator fun <reified T> GroovyObject.getValue(thisRef: Any?, property: KProperty<*>): T =
    getProperty(property.name) as T

/**
 * Delegates writes of a `var` to the property of the same name of this object: `var name: String by groovyObject`.
 */
operator fun <T> GroovyObject.setValue(thisRef: Any?, property: KProperty<*>, value: T) =
    setProperty(property.name, value)

/**
 * A property delegate reading and writing a property of the [GroovyObject] it is accessed on; see [groovyProperty].
 */
class GroovyPropertyDelegate @PublishedApi internal constructor(@PublishedApi internal val name: String?) {
    /**
     * Reads the property from [thisRef], cast to the delegated property's type.
     */
    inline operator fun <reified T> getValue(thisRef: GroovyObject, property: KProperty<*>): T =
        thisRef.getProperty(name ?: property.name) as T

    /**
     * Writes the property of [thisRef].
     */
    operator fun <T> setValue(thisRef: GroovyObject, property: KProperty<*>, value: T) =
        thisRef.setProperty(name ?: property.name, value)
}

/**
 * Returns a delegate for extension properties over Groovy objects, reading and writing the receiver's property [name], or
 * the Kotlin property's own name when [name] is `null`:
 * `val GroovyRowResult.firstName: String by groovyProperty("first_name")`. The delegate holds no state, so one instance
 * serves every receiver.
 */
fun groovyProperty(name: String? = null): GroovyPropertyDelegate = GroovyPropertyDelegate(name)
