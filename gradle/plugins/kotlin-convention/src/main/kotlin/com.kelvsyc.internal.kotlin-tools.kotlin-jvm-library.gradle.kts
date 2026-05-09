import kotlin.jvm.optionals.getOrNull
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    kotlin("jvm")
    id("com.autonomousapps.dependency-analysis")
}

val libs = versionCatalogs.named("libs")

kotlin {
    compilerOptions {
        apiVersion.set(KotlinVersion.KOTLIN_2_3)
        languageVersion.set(KotlinVersion.KOTLIN_2_3)
    }

    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
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

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
