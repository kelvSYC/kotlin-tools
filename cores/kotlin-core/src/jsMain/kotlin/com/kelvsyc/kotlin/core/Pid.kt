package com.kelvsyc.kotlin.core

actual value class Pid @PublishedApi internal constructor(actual val value: Long)

@Suppress("NOTHING_TO_INLINE")
private inline fun nodeProcessPid(): Int = js("process.pid")

actual fun currentPid(): Pid = Pid(nodeProcessPid().toLong())
