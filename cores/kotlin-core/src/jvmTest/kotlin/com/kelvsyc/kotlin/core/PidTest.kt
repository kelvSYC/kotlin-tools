package com.kelvsyc.kotlin.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

class PidTest : FunSpec({

    test("currentPid returns a positive value") {
        val pid = currentPid()
        pid.value shouldBeGreaterThan 0L
    }

    test("currentPid matches ProcessHandle.current().pid()") {
        val pid = currentPid()
        pid.value shouldBe ProcessHandle.current().pid()
    }

    test("ProcessHandle.pidKt wraps the raw pid") {
        val handle = ProcessHandle.current()
        handle.pidKt.value shouldBe handle.pid()
    }

    test("toProcessHandle round-trips for the current process") {
        val pid = currentPid()
        val handle = pid.toProcessHandle()
        handle.shouldNotBeNull()
        handle.pid() shouldBe pid.value
    }

    test("toProcessHandle returns null for an invalid pid") {
        val pid = Pid(Long.MAX_VALUE)
        pid.toProcessHandle() shouldBe null
    }

    test("Process.pidKt wraps the raw pid") {
        val process = ProcessBuilder("true").start()
        val pid = process.pidKt
        pid.value shouldBe process.pid()
        process.waitFor()
    }
})
