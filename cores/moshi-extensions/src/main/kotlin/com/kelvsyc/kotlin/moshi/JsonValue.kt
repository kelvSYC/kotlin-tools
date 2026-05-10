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
                is JsonObject -> current.members[segment]
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
 * A JSON object, containing named members.
 *
 * Implements [Map] by delegation, so standard collection operations like [forEach], [filter],
 * [map], and destructuring work directly.
 *
 * @property members The key-value mappings in this object.
 */
data class JsonObject(val members: Map<String, JsonValue>) : JsonValue(), Map<String, JsonValue> by members {
    override fun asObject(): JsonObject = this
}

/**
 * A JSON array, containing ordered elements.
 *
 * Implements [List] by delegation, so standard collection operations like [forEach], [filter],
 * [map], [first], and indexed access work directly.
 *
 * @property elements The values in this array.
 */
data class JsonArray(val elements: List<JsonValue>) : JsonValue(), List<JsonValue> by elements {
    override fun asArray(): JsonArray = this
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
