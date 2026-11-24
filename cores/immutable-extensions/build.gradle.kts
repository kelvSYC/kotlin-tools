plugins {
    id("com.kelvsyc.internal.dokka")
    id("com.kelvsyc.internal.github-publishing")
    id("com.kelvsyc.internal.kotlin-multiplatform-library")
    id("com.kelvsyc.internal.kotlin-multiplatform-js")
    id("com.kelvsyc.internal.kover")
}

group = "com.kelvsyc.kotlin"

kotlin {
    sourceSets {
        jsMain {
            dependencies {
                api("com.kelvsyc.kotlin:kotlin-core:$version")
                api(npm("immutable", "4.3.7"))
            }
        }
    }
}
