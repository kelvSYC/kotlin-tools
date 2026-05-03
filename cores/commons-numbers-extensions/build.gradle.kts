plugins {
    id("com.kelvsyc.internal.kotlin-tools.dokka")
    id("com.kelvsyc.internal.kotlin-tools.github-publishing")
    id("com.kelvsyc.internal.kotlin-tools.kotlin-jvm-library")
}

group = "com.kelvsyc.kotlin"

dependencies {
    api("com.kelvsyc.kotlin:kotlin-core")
    api(libs.commons.numbers.complex)
    api(libs.commons.numbers.core)
    api(libs.commons.numbers.fraction)

    testImplementation(libs.kotest.property)
}
