package com.kelvsyc.kotlin.core

import platform.posix.getpid

actual value class Pid @PublishedApi internal constructor(actual val value: Long)

actual fun currentPid(): Pid = Pid(getpid().toLong())
