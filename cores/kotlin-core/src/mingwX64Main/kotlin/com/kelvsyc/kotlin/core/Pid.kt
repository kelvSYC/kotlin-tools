package com.kelvsyc.kotlin.core

import platform.windows.GetCurrentProcessId

actual value class Pid @PublishedApi internal constructor(actual val value: Long)

actual fun currentPid(): Pid = Pid(GetCurrentProcessId().toLong())
