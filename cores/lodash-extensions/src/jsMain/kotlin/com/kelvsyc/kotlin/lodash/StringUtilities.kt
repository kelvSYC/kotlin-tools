package com.kelvsyc.kotlin.lodash

/** Converts this string to camel case. For example, `"foo-bar"` → `"fooBar"`. */
fun String.toCamelCase(): String = Lodash.camelCase(this)

/** Converts this string to kebab case. For example, `"fooBar"` → `"foo-bar"`. */
fun String.toKebabCase(): String = Lodash.kebabCase(this)

/** Converts this string to snake case. For example, `"fooBar"` → `"foo_bar"`. */
fun String.toSnakeCase(): String = Lodash.snakeCase(this)

/**
 * Truncates this string to [maxLength] characters. If truncation occurs, appends [omission]
 * (default `"…"`) to the result. The returned string, including the omission, is at most
 * [maxLength] characters long.
 */
fun String.truncated(maxLength: Int, omission: String = "…"): String {
    val options: dynamic = js("{}")
    options.length = maxLength
    options.omission = omission
    return Lodash.truncate(this, options)
}

/** Converts HTML special characters (`&`, `<`, `>`, `"`, `'`) to their HTML entity equivalents. */
fun String.htmlEscaped(): String = Lodash.escape(this)

/** Converts HTML entities (`&amp;`, `&lt;`, `&gt;`, `&quot;`, `&#39;`) back to their characters. */
fun String.htmlUnescaped(): String = Lodash.unescape(this)
