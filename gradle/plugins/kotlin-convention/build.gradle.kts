plugins {
    `kotlin-dsl`
}

group = "com.kelvsyc.internal.kotlin-tools"

kotlin {
    jvmToolchain(25)
}

dependencies {
    implementation(platform("com.kelvsyc.internal.kotlin-tools:platform"))
    implementation(libs.kotlin.plugin)
}
