package com.kelvsyc.kotlin.core

/**
 * A type-safe wrapper around a process ID.
 *
 * Using a raw numeric value for process IDs is error-prone: any arbitrary number can be passed
 * where a PID is expected. This value class provides compile-time safety at zero runtime cost.
 *
 * ## Platform notes
 *
 * - **JVM**: backed by the `long` returned by [ProcessHandle.pid()][ProcessHandle.pid].
 *   Extension properties [ProcessHandle.pidKt] and [Process.pidKt] and the method
 *   [toProcessHandle] are available.
 * - **JS (Node.js)**: backed by `process.pid`, widened to `Long`.
 */
expect value class Pid @PublishedApi internal constructor(val value: Long)

/**
 * Returns the [Pid] of the current process.
 */
expect fun currentPid(): Pid
