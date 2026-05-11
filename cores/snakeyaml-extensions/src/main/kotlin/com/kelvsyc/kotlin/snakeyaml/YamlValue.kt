package com.kelvsyc.kotlin.snakeyaml

/**
 * A type-safe representation of a YAML value, providing an untyped tree API for navigating
 * parsed YAML without requiring data classes.
 *
 * YAML scalars are all represented by [YamlScalar], which stores the raw string value and
 * provides typed coercion methods. Unlike JSON, YAML does not distinguish between strings,
 * numbers, and booleans at the node level — the tag and context determine interpretation.
 */
sealed class YamlValue {
    /**
     * Returns this value as a [YamlMapping], or `null` if it is not a mapping.
     */
    open fun asMapping(): YamlMapping? = null

    /**
     * Returns this value as a [YamlSequence], or `null` if it is not a sequence.
     */
    open fun asSequence(): YamlSequence? = null

    /**
     * Returns this value as a [YamlScalar], or `null` if it is not a scalar.
     */
    open fun asScalar(): YamlScalar? = null

    /**
     * Returns `true` if this value is [YamlNull].
     */
    open fun isNull(): Boolean = false

    /**
     * Navigates to a nested value by key path segments.
     *
     * Each segment is interpreted as a mapping key or sequence index (when parseable as an
     * integer). Returns `null` if any segment fails to resolve.
     */
    fun at(vararg segments: String): YamlValue? {
        var current: YamlValue? = this
        for (segment in segments) {
            current = when (current) {
                is YamlMapping -> current.members[segment]
                is YamlSequence -> segment.toIntOrNull()?.let { index ->
                    current.elements.getOrNull(index)
                }
                else -> null
            }
        }
        return current
    }
}

/**
 * A YAML mapping (dictionary), containing key-value pairs with string keys.
 *
 * Implements [Map] by delegation, so standard collection operations like [forEach], [filter],
 * [map], and destructuring work directly.
 *
 * Non-string keys in the underlying YAML are converted to their string representation.
 *
 * @property members The key-value mappings.
 */
data class YamlMapping(val members: Map<String, YamlValue>) : YamlValue(), Map<String, YamlValue> by members {
    override fun asMapping(): YamlMapping = this
}

/**
 * A YAML sequence (list), containing ordered elements.
 *
 * Implements [List] by delegation, so standard collection operations like [forEach], [filter],
 * [map], [first], and indexed access work directly.
 *
 * @property elements The values in this sequence.
 */
data class YamlSequence(val elements: List<YamlValue>) : YamlValue(), List<YamlValue> by elements {
    override fun asSequence(): YamlSequence = this
}

/**
 * A YAML scalar value, storing the raw string content with typed coercion methods.
 *
 * @property value The raw string content of the scalar.
 */
data class YamlScalar(val value: String) : YamlValue() {
    override fun asScalar(): YamlScalar = this

    /**
     * Returns the raw string value.
     */
    fun asString(): String = value

    /**
     * Returns the value as an [Int], or `null` if it cannot be parsed as an integer.
     */
    fun asInt(): Int? = value.toIntOrNull()

    /**
     * Returns the value as a [Long], or `null` if it cannot be parsed as a long.
     */
    fun asLong(): Long? = value.toLongOrNull()

    /**
     * Returns the value as a [Double], or `null` if it cannot be parsed as a double.
     */
    fun asDouble(): Double? = value.toDoubleOrNull()

    /**
     * Returns the value as a [Boolean], or `null` if the value is not a recognized
     * YAML boolean (`true`/`false`).
     */
    fun asBoolean(): Boolean? = value.toBooleanStrictOrNull()
}

/**
 * The YAML null value, representing an explicit `null`, `~`, or empty value.
 */
data object YamlNull : YamlValue() {
    override fun isNull(): Boolean = true
}
