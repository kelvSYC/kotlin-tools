package com.kelvsyc.kotlin.moshi

/**
 * A type-safe representation of a JSON value, providing an untyped tree API for navigating
 * parsed JSON without requiring data classes.
 *
 * This serves as a Kotlin-idiomatic replacement for Groovy's `JsonSlurper`, offering typed
 * accessors and path-based navigation over Moshi-parsed JSON structures.
 */
sealed class JsonValue {
    /**
     * Returns this value as a [JsonObject], or `null` if it is not an object.
     */
    open fun asObject(): JsonObject? = null

    /**
     * Returns this value as a [JsonArray], or `null` if it is not an array.
     */
    open fun asArray(): JsonArray? = null

    /**
     * Returns this value as a [String], or `null` if it is not a string.
     */
    open fun asString(): String? = null

    /**
     * Returns this value as a [Number], or `null` if it is not a number.
     */
    open fun asNumber(): Number? = null

    /**
     * Returns this value as a [Boolean], or `null` if it is not a boolean.
     */
    open fun asBoolean(): Boolean? = null

    /**
     * Returns `true` if this value is [JsonNull].
     */
    open fun isNull(): Boolean = false

    /**
     * Navigates to a nested value by key path segments.
     *
     * Each segment is interpreted as an object key or array index (when parseable as an integer).
     * Returns `null` if any segment fails to resolve.
     */
    fun at(vararg segments: String): JsonValue? {
        var current: JsonValue? = this
        for (segment in segments) {
            current = when (current) {
                is JsonObject -> current.entries[segment]
                is JsonArray -> segment.toIntOrNull()?.let { index ->
                    current.elements.getOrNull(index)
                }
                else -> null
            }
        }
        return current
    }
}

/**
 * A JSON object, containing named entries.
 *
 * @property entries The key-value mappings in this object.
 */
data class JsonObject(val entries: Map<String, JsonValue>) : JsonValue() {
    override fun asObject(): JsonObject = this

    /**
     * Returns the value associated with [key], or `null` if not present.
     */
    operator fun get(key: String): JsonValue? = entries[key]

    /**
     * Returns the keys in this object.
     */
    val keys: Set<String> get() = entries.keys
}

/**
 * A JSON array, containing ordered elements.
 *
 * @property elements The values in this array.
 */
data class JsonArray(val elements: List<JsonValue>) : JsonValue() {
    override fun asArray(): JsonArray = this

    /**
     * Returns the element at the given [index].
     */
    operator fun get(index: Int): JsonValue = elements[index]

    /**
     * Returns the number of elements in this array.
     */
    val size: Int get() = elements.size
}

/**
 * A JSON string value.
 *
 * @property value The string content.
 */
data class JsonString(val value: String) : JsonValue() {
    override fun asString(): String = value
}

/**
 * A JSON numeric value.
 *
 * @property value The numeric content.
 */
data class JsonNumber(val value: Number) : JsonValue() {
    override fun asNumber(): Number = value
}

/**
 * A JSON boolean value.
 *
 * @property value The boolean content.
 */
data class JsonBoolean(val value: Boolean) : JsonValue() {
    override fun asBoolean(): Boolean = value
}

/**
 * The JSON null value.
 */
data object JsonNull : JsonValue() {
    override fun isNull(): Boolean = true
}
