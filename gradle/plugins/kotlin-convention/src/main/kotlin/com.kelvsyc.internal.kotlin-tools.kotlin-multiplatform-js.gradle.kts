plugins {
    id("com.google.devtools.ksp")
    id("io.kotest")
    id("com.kelvsyc.internal.kotlin-tools.kotlin-multiplatform-base")
}

kotlin {
    js(IR) {
        nodejs()
        binaries.library()
    }
}

// kotlinNodeJsSetup manages a shared Node.js installation in ~/.gradle/nodejs/ that Gradle
// cannot reliably fingerprint across CI runners (the tarball contains OS-specific headers that
// may not extract fully, causing MD5 hash failures on missing files). Opting out of state
// tracking means Gradle always runs the task but never tries to snapshot its outputs.
tasks.matching { it.name == "kotlinNodeJsSetup" }.configureEach {
    doNotTrackState("Node.js installation directory contains platform-specific files that may not fully extract on all CI runners")
}
