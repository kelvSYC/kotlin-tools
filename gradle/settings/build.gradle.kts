plugins {
    `kotlin-dsl`
}

group = "com.kelvsyc.internal.kotlin-tools"

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(platform("com.kelvsyc.internal.kotlin-tools:platform"))
    implementation(libs.foojay.resolver.plugin)
    implementation(libs.semver.plugin)
}
