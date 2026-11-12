import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    id("com.kelvsyc.internal.kotlin-tools.dokka")
    id("com.kelvsyc.internal.kotlin-tools.github-publishing")
    id("com.kelvsyc.internal.kotlin-tools.kotlin-multiplatform-js-library")
}

group = "com.kelvsyc.kotlin"

kotlin {
    // kotlin.time.Instant, used throughout the public API, is experimental before Kotlin 2.3.
    compilerOptions {
        apiVersion.set(KotlinVersion.KOTLIN_2_3)
        languageVersion.set(KotlinVersion.KOTLIN_2_3)
    }

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
