package com.kelvsyc.kotlin.moshi

import com.squareup.moshi.JsonReader
import com.squareup.moshi.Moshi
import okio.BufferedSource
import okio.buffer
import okio.source
import java.io.InputStream

private val jsonValueAdapter = JsonValueAdapter()

/**
 * Parses this string as a [JsonValue] tree.
 */
fun String.parseJson(): JsonValue {
    val source = okio.Buffer().writeUtf8(this)
    return JsonReader.of(source).use { jsonValueAdapter.fromJson(it) }
}

/**
 * Parses JSON from this [BufferedSource] as a [JsonValue] tree.
 */
fun BufferedSource.parseJson(): JsonValue {
    return JsonReader.of(this).use { jsonValueAdapter.fromJson(it) }
}

/**
 * Parses JSON from this [InputStream] as a [JsonValue] tree.
 */
fun InputStream.parseJson(): JsonValue {
    return source().buffer().parseJson()
}

/**
 * Registers the [JsonValueAdapter] with this [Moshi.Builder], enabling direct
 * serialization and deserialization of [JsonValue] instances.
 */
fun Moshi.Builder.addJsonValueAdapter(): Moshi.Builder = add(JsonValue::class.java, JsonValueAdapter())
