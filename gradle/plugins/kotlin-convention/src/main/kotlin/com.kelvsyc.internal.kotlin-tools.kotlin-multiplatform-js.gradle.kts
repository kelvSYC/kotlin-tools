plugins {
    id("com.kelvsyc.internal.kotlin-tools.kotlin-multiplatform-base")
}

kotlin {
    js(IR) {
        nodejs()
        binaries.library()
    }
}
