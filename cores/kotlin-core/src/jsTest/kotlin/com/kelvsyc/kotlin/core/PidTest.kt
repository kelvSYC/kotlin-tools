package com.kelvsyc.kotlin.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan

class PidTest : FunSpec({
    test("currentPid returns a positive PID") {
        currentPid().value shouldBeGreaterThan 0L
    }
})
