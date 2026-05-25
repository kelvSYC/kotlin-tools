package com.kelvsyc.kotlin.core.traits.fp

/**
 * `FloatingPointSinCos` is a trait providing a combined sine and cosine operation for a
 * floating-point type [T].
 *
 * [sincos] computes both `sin(x)` and `cos(x)` in a single operation, returning the pair
 * `(sin(x), cos(x))`. On platforms where the underlying math library provides a native joint
 * computation (e.g. POSIX `sincos`/`sincosf` on macOS), this avoids the redundant argument
 * reduction that separate [FloatingPointTrigonometry.sin] and [FloatingPointTrigonometry.cos]
 * calls would perform.
 *
 * Instances are intentionally absent on platforms where no native joint computation exists.
 * Emulating [sincos] by calling [FloatingPointTrigonometry.sin] and [FloatingPointTrigonometry.cos]
 * separately would provide no benefit and falsely imply platform-level support. Callers on those
 * platforms should use the individual functions from [FloatingPointTrigonometry] instead.
 *
 * Platform availability (via a custom cinterop targeting `<math.h>`):
 * - **macOS arm64**: [FloatingPointSinCos.float] and [FloatingPointSinCos.double] via the Apple
 *   BSD extension `sincosf`/`sincos`.
 * - **Linux x64**: same, via the GNU glibc extension (requires `-D_GNU_SOURCE`).
 * - **Windows x64**: no instances — `sincos` is absent from MSVCRT.
 * - **JVM / JS**: no instances — `java.lang.Math` and JS `Math` have no `sincos`.
 *
 * Building the cinterop on macOS requires the full Xcode app (not just command line tools), as
 * Kotlin/Native's cinterop header parser calls `xcrun xcodebuild` to locate the SDK. On Linux
 * and on CI macOS runners (which ship with Xcode), this constraint does not apply.
 */
interface FloatingPointSinCos<T> {
    companion object

    /**
     * Returns `SinCosResult(sin(this), cos(this))` computed atomically, matching the convention
     * of [kotlin.math.sin]/[kotlin.math.cos].
     */
    fun T.sincos(): SinCosResult<T>
}
