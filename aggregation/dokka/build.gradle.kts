import org.jetbrains.dokka.gradle.DokkaExtension

plugins {
    id("com.kelvsyc.internal.kotlin-tools.dokka")
}

group = "com.kelvsyc.kotlin"

val componentNames = gradle.includedBuilds
    .filter { it.projectDir.parentFile.name == "cores" }
    .map { it.name }

configure<DokkaExtension> {
    moduleName.set("kelvSYC Kotlin Tools")
}

dependencies {
    componentNames.forEach {
        dokka("$group:$it") // from included build $it
    }
}
