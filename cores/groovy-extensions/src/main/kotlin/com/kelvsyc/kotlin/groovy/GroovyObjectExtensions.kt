package com.kelvsyc.kotlin.groovy

import groovy.lang.GroovyObject
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
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
 * Reads and writes the property [name] (or the Kotlin property's name) of the [GroovyObject] it is accessed on, casting
 * reads to [type]. The type is captured by [groovyProperty], since a class's type parameter can't be reified.
 */
@PublishedApi
internal class GroovyPropertyDelegate<V>(
    private val name: String?,
    private val type: Class<*>,
    private val nullable: Boolean,
) : ReadWriteProperty<GroovyObject, V> {
    override fun getValue(thisRef: GroovyObject, property: KProperty<*>): V {
        val groovyName = name ?: property.name
        val value = thisRef.getProperty(groovyName)
        if (value == null && !nullable) {
            throw NullPointerException("Groovy property '$groovyName' is null, but '${property.name}' is not nullable")
        }
        @Suppress("UNCHECKED_CAST")
        return type.cast(value) as V
    }

    override fun setValue(thisRef: GroovyObject, property: KProperty<*>, value: V) =
        thisRef.setProperty(name ?: property.name, value)
}

/**
 * Returns a delegate for extension properties over Groovy objects, reading and writing the receiver's property [name], or
 * the Kotlin property's own name when [name] is `null`:
 * `val GroovyRowResult.firstName: String by groovyProperty("first_name")`. [V] is inferred from the property's type, and
 * reads are cast to it: a value of the wrong type fails with [ClassCastException], and `null` read into a non-null
 * property with [NullPointerException]. The delegate holds no per-receiver state, so one instance serves every receiver.
 */
inline fun <reified V> groovyProperty(name: String? = null): ReadWriteProperty<GroovyObject, V> =
    GroovyPropertyDelegate(name, (V::class as KClass<*>).javaObjectType, null is V)
