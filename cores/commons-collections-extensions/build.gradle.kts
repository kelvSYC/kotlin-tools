plugins {
    id("com.kelvsyc.internal.kotlin-tools.dokka")
    id("com.kelvsyc.internal.kotlin-tools.github-publishing")
    id("com.kelvsyc.internal.kotlin-tools.kotlin-jvm-library")
}

group = "com.kelvsyc.kotlin"

dependencies {
    api("com.kelvsyc.kotlin:kotlin-core")
    api(libs.commons.collections4)
}
