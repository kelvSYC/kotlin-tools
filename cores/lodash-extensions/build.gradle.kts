plugins {
    id("com.kelvsyc.internal.dokka")
    id("com.kelvsyc.internal.github-publishing")
    id("com.kelvsyc.internal.kotlin-multiplatform-library")
    id("com.kelvsyc.internal.kotlin-multiplatform-js")
}

group = "com.kelvsyc.kotlin"

kotlin {
    sourceSets {
        jsMain {
            dependencies {
                implementation(npm("lodash", "4.17.21"))
            }
        }
    }
}
