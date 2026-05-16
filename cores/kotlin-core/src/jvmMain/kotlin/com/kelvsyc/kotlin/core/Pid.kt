package com.kelvsyc.kotlin.core

/**
 * A type-safe wrapper around the `long` process IDs produced by [ProcessHandle] and [Process].
 *
 * Using a raw [Long] for process IDs is error-prone: any arbitrary number can be passed
 * where a PID is expected. This value class provides compile-time safety at zero runtime
 * cost.
 */
@JvmInline
actual value class Pid @PublishedApi internal constructor(actual val value: Long) {
    /**
     * Returns the [ProcessHandle] for this PID, or `null` if the process is not alive
     * or otherwise inaccessible.
     */
    fun toProcessHandle(): ProcessHandle? = ProcessHandle.of(value).orElse(null)
}

/** Returns the [Pid] of this process handle. */
val ProcessHandle.pidKt: Pid get() = Pid(pid())

/** Returns the [Pid] of this process. */
val Process.pidKt: Pid get() = Pid(pid())

/** Returns the [Pid] of the current process. */
actual fun currentPid(): Pid = ProcessHandle.current().pidKt
