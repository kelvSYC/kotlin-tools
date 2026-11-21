import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    id("com.kelvsyc.internal.dokka")
    id("com.kelvsyc.internal.github-publishing")
    id("com.kelvsyc.internal.kotlin-multiplatform-library")
    id("com.kelvsyc.internal.kotlin-multiplatform-js")
}

group = "com.kelvsyc.kotlin"

// kotlin.time.Instant, used throughout the public API, is experimental before Kotlin 2.3.
kelvsycKotlin {
    languageVersion = KotlinVersion.KOTLIN_2_3
}

kotlin {
    sourceSets {
        jsMain {
            dependencies {
                api("com.kelvsyc.kotlin:kotlin-core:$version")
                api(libs.kotlinx.datetime)
                api(npm("luxon", "3.4.4"))
            }
        }
    }
}
