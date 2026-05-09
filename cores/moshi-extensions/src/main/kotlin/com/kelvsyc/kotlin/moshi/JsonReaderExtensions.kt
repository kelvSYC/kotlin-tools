package com.kelvsyc.kotlin.moshi

import com.squareup.moshi.JsonReader

/**
 * Reads a JSON object, invoking [body] for each key-value pair.
 *
 * The receiver inside [body] is the [JsonReader], positioned after the key name.
 * The key is passed as the parameter.
 */
inline fun JsonReader.readObject(body: JsonReader.(name: String) -> Unit) {
    beginObject()
    while (hasNext()) {
        val name = nextName()
        body(name)
    }
    endObject()
}

/**
 * Reads a JSON array, invoking [body] for each element.
 *
 * The receiver inside [body] is the [JsonReader], positioned at the start of each element.
 */
inline fun JsonReader.readArray(body: JsonReader.() -> Unit) {
    beginArray()
    while (hasNext()) {
        body()
    }
    endArray()
}

/**
 * Reads all elements of a JSON array into a list using the given [transform].
 */
inline fun <T> JsonReader.readArrayToList(transform: JsonReader.() -> T): List<T> {
    val result = mutableListOf<T>()
    readArray {
        result.add(transform())
    }
    return result
}

/**
 * Reads a JSON object into a map using the given [valueTransform] for each entry.
 */
inline fun <V> JsonReader.readObjectToMap(valueTransform: JsonReader.(name: String) -> V): Map<String, V> {
    val result = mutableMapOf<String, V>()
    readObject { name ->
        result[name] = valueTransform(name)
    }
    return result
}
