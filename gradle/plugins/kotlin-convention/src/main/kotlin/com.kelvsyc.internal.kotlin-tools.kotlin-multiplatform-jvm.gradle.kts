import kotlin.jvm.optionals.getOrNull
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication

plugins {
    id("com.kelvsyc.internal.kotlin-tools.kotlin-multiplatform-base")
}

val libs = versionCatalogs.named("libs")

kotlin {
    jvm()

    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }

    sourceSets.jvmTest.dependencies {
        libs.findLibrary("kotest-runner").getOrNull()?.let { implementation(it) }
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

// dokka-javadoc does not support KMP, so we package KDoc HTML as the javadoc JAR instead.
pluginManager.withPlugin("org.jetbrains.dokka") {
    val javadocJar by tasks.registering(Jar::class) {
        archiveClassifier.set("javadoc")
        from(tasks.named("dokkaGeneratePublicationHtml"))
    }

    pluginManager.withPlugin("maven-publish") {
        extensions.configure<PublishingExtension> {
            publications.withType<MavenPublication>().matching { it.name == "jvm" }.configureEach {
                artifact(javadocJar)
            }
        }
    }
}
