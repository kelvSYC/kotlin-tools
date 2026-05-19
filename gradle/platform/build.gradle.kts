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
    constraints {
        api(libs.kotlinx.datetime)
        api(libs.moshi)
        api(libs.moshi.kotlin)
        api(libs.snakeyaml.engine)
        api(libs.commons.collections4)
    }
}
