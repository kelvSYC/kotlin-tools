package com.kelvsyc.kotlin.lodash

/**
 * Returns a debounced version of [fn] that delays invocation until [waitMs] milliseconds have
 * elapsed since the last call.
 *
 * @param leading If `true`, invoke on the leading edge of the wait interval (immediately on first
 *   call). Defaults to `false`.
 * @param trailing If `true`, invoke on the trailing edge (after the wait interval). Defaults to
 *   `true`.
 * @param maxWaitMs Maximum time [fn] is allowed to be delayed before it is invoked, regardless of
 *   call frequency. `null` means no maximum.
 */
fun <F : Function<*>> debounce(
    fn: F,
    waitMs: Int,
    leading: Boolean = false,
    trailing: Boolean = true,
    maxWaitMs: Int? = null,
): F {
    val options: dynamic = js("{}")
    options.leading = leading
    options.trailing = trailing
    if (maxWaitMs != null) options.maxWait = maxWaitMs
    return Lodash.debounce(fn.asDynamic(), waitMs, options).unsafeCast<F>()
}

/**
 * Returns a throttled version of [fn] that invokes at most once per [waitMs] milliseconds.
 *
 * @param leading If `true`, invoke on the leading edge of the wait interval. Defaults to `true`.
 * @param trailing If `true`, invoke on the trailing edge. Defaults to `true`.
 */
fun <F : Function<*>> throttle(
    fn: F,
    waitMs: Int,
    leading: Boolean = true,
    trailing: Boolean = true,
): F {
    val options: dynamic = js("{}")
    options.leading = leading
    options.trailing = trailing
    return Lodash.throttle(fn.asDynamic(), waitMs, options).unsafeCast<F>()
}

/**
 * Returns a memoized version of [fn] whose results are cached by argument. Subsequent calls with
 * the same first argument return the cached result without re-invoking [fn].
 */
fun <F : Function<*>> memoize(fn: F): F = Lodash.memoize(fn.asDynamic()).unsafeCast<F>()

/**
 * Returns a version of [fn] that is invoked at most once. Repeated calls return the result of the
 * first invocation.
 */
fun <F : Function<*>> once(fn: F): F = Lodash.once(fn.asDynamic()).unsafeCast<F>()
