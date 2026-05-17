plugins {
    id("com.kelvsyc.internal.kotlin-tools.dokka")
    id("com.kelvsyc.internal.kotlin-tools.github-publishing")
    id("com.kelvsyc.internal.kotlin-tools.kotlin-multiplatform-js-library")
}

group = "com.kelvsyc.kotlin"

kotlin {
    sourceSets {
        jsMain {
            dependencies {
                api("com.kelvsyc.kotlin:kotlin-core")
                api(libs.kotlinx.datetime)
                api(npm("luxon", "3.4.4"))
            }
        }
    }
}
