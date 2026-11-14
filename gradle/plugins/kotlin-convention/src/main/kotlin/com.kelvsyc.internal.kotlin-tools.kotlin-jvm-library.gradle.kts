import kotlin.jvm.optionals.getOrNull

plugins {
    `java-library`
    kotlin("jvm")
    id("com.autonomousapps.dependency-analysis")
}

val libs = versionCatalogs.named("libs")

checkKotlinPluginVersion(libs)
checkKotlinToolchainAlignment()

kotlin {
    configureKotlinCompatibility(this, compilerOptions)
    configureJvmCompatibility(compilerOptions)

    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(JvmCompatibility.TOOLCHAIN))
    }
}

java {
    withSourcesJar()
}

dependencies {
    implementation(platform("com.kelvsyc.internal.kotlin-tools:platform"))

    testImplementation(dependencies.platform("com.kelvsyc.internal.kotlin-tools:platform"))
    libs.findLibrary("kotest-assertions-core").getOrNull()?.let { testImplementation(it) }
    libs.findLibrary("kotest-assertions-shared").getOrNull()?.let { testImplementation(it) }
    libs.findLibrary("kotest-framework-engine").getOrNull()?.let { testImplementation(it) }
    libs.findLibrary("kotest-runner").getOrNull()?.let { testImplementation(it) }
}

dependencyAnalysis {
    issues {
        all {
            onUsedTransitiveDependencies {
                severity("fail")
                exclude("io.kotest:kotest-common")
            }
            onUnusedDependencies { severity("fail") }
            onIncorrectConfiguration { severity("fail") }
        }
    }
}

pluginManager.withPlugin("maven-publish") {
    extensions.configure<PublishingExtension> {
        publications.register<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
