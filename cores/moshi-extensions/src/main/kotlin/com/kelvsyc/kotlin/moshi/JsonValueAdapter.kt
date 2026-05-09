package com.kelvsyc.kotlin.moshi

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter

/**
 * A [JsonAdapter] that reads and writes [JsonValue] instances, bridging Moshi's streaming API
 * to the untyped tree representation.
 */
class JsonValueAdapter : JsonAdapter<JsonValue>() {
    override fun fromJson(reader: JsonReader): JsonValue {
        return when (reader.peek()) {
            JsonReader.Token.BEGIN_OBJECT -> readObject(reader)
            JsonReader.Token.BEGIN_ARRAY -> readArray(reader)
            JsonReader.Token.STRING -> JsonString(reader.nextString())
            JsonReader.Token.NUMBER -> JsonNumber(reader.nextDouble())
            JsonReader.Token.BOOLEAN -> JsonBoolean(reader.nextBoolean())
            JsonReader.Token.NULL -> {
                reader.nextNull<Unit>()
                JsonNull
            }
            else -> {
                reader.skipValue()
                JsonNull
            }
        }
    }

    private fun readObject(reader: JsonReader): JsonObject {
        val entries = mutableMapOf<String, JsonValue>()
        reader.beginObject()
        while (reader.hasNext()) {
            val name = reader.nextName()
            entries[name] = fromJson(reader)
        }
        reader.endObject()
        return JsonObject(entries)
    }

    private fun readArray(reader: JsonReader): JsonArray {
        val elements = mutableListOf<JsonValue>()
        reader.beginArray()
        while (reader.hasNext()) {
            elements.add(fromJson(reader))
        }
        reader.endArray()
        return JsonArray(elements)
    }

    override fun toJson(writer: JsonWriter, value: JsonValue?) {
        when (value) {
            is JsonObject -> writeObject(writer, value)
            is JsonArray -> writeArray(writer, value)
            is JsonString -> writer.value(value.value)
            is JsonNumber -> writer.value(value.value)
            is JsonBoolean -> writer.value(value.value)
            is JsonNull, null -> writer.nullValue()
        }
    }

    private fun writeObject(writer: JsonWriter, obj: JsonObject) {
        writer.beginObject()
        val wasSerializeNulls = writer.serializeNulls
        writer.serializeNulls = true
        for ((key, value) in obj.entries) {
            writer.name(key)
            toJson(writer, value)
        }
        writer.serializeNulls = wasSerializeNulls
        writer.endObject()
    }

    private fun writeArray(writer: JsonWriter, array: JsonArray) {
        writer.beginArray()
        for (element in array.elements) {
            toJson(writer, element)
        }
        writer.endArray()
    }
}
