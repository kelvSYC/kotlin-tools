package com.kelvsyc.kotlin.moshi

import com.squareup.moshi.JsonWriter

/**
 * Writes a JSON object, invoking [body] to populate its entries.
 *
 * The receiver inside [body] is the [JsonWriter].
 */
inline fun JsonWriter.writeObject(body: JsonWriter.() -> Unit) {
    beginObject()
    body()
    endObject()
}

/**
 * Writes a JSON array, invoking [body] to populate its elements.
 *
 * The receiver inside [body] is the [JsonWriter].
 */
inline fun JsonWriter.writeArray(body: JsonWriter.() -> Unit) {
    beginArray()
    body()
    endArray()
}

/**
 * Writes a named field with a string value. Null values are always serialized.
 */
fun JsonWriter.field(name: String, value: String?) {
    name(name)
    if (value == null) {
        val wasSerializeNulls = serializeNulls
        serializeNulls = true
        nullValue()
        serializeNulls = wasSerializeNulls
    } else {
        value(value)
    }
}

/**
 * Writes a named field with a numeric value. Null values are always serialized.
 */
fun JsonWriter.field(name: String, value: Number?) {
    name(name)
    if (value == null) {
        val wasSerializeNulls = serializeNulls
        serializeNulls = true
        nullValue()
        serializeNulls = wasSerializeNulls
    } else {
        value(value)
    }
}

/**
 * Writes a named field with a boolean value. Null values are always serialized.
 */
fun JsonWriter.field(name: String, value: Boolean?) {
    name(name)
    if (value == null) {
        val wasSerializeNulls = serializeNulls
        serializeNulls = true
        nullValue()
        serializeNulls = wasSerializeNulls
    } else {
        value(value)
    }
}

/**
 * Writes a named field containing a JSON object, invoking [body] to populate it.
 */
inline fun JsonWriter.objectField(name: String, body: JsonWriter.() -> Unit) {
    name(name)
    writeObject(body)
}

/**
 * Writes a named field containing a JSON array, invoking [body] to populate it.
 */
inline fun JsonWriter.arrayField(name: String, body: JsonWriter.() -> Unit) {
    name(name)
    writeArray(body)
}
