plugins {
    `kotlin-dsl`
}

group = "com.kelvsyc.internal.kotlin-tools"

kotlin {
    jvmToolchain(25)
}

dependencies {
    implementation(platform(libs.conventions.platform))
    implementation(libs.conventions.kotlin.plugin)
    implementation(libs.kotest.plugin)
    implementation(libs.kotlin.plugin)
}
