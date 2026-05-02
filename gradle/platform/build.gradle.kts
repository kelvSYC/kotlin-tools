plugins {
    `java-platform`
}

group = "com.kelvsyc.internal.kotlin-tools"

javaPlatform {
    allowDependencies()
}

dependencies {
    api(platform(libs.kotest.bom))
    api(platform(libs.kotlin.gradle.plugins.bom))
    api(platform(libs.commons.numbers.bom))
}
