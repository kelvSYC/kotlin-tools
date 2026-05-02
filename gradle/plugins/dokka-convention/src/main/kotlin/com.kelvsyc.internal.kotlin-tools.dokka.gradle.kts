import java.net.URI
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.jetbrains.dokka.gradle.DokkaExtension

plugins {
    id("org.jetbrains.dokka")
}

val gitCommitHash: Provider<String> = providers.exec {
    commandLine("git", "rev-parse", "HEAD")
}.standardOutput.asText.map { it.trim() }

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

val guavaVersion: Provider<String> = libs
    .findLibrary("guava")
    .get()
    .map { it.versionConstraint.requiredVersion }

configure<DokkaExtension> {
    val rootGradle = generateSequence(gradle, Gradle::getParent).last()
    val relativePath = layout.projectDirectory.asFile
        .toRelativeString(rootGradle.rootProject.layout.projectDirectory.asFile)

    dokkaSourceSets.configureEach {
        enableJdkDocumentationLink.set(true)
        enableKotlinStdLibDocumentationLink.set(true)

        sourceLink {
            remoteUrl.set(gitCommitHash.map { URI("https://github.com/kelvSYC/kotlin-tools/blob/$it/$relativePath") })
        }

        externalDocumentationLinks.register("commons-lang") {
            url("https://commons.apache.org/proper/commons-lang/apidocs/")
            packageListUrl("https://commons.apache.org/proper/commons-lang/apidocs/element-list")
        }

        externalDocumentationLinks.register("commons-numbers") {
            url("https://commons.apache.org/proper/commons-numbers/apidocs/")
            packageListUrl("https://commons.apache.org/proper/commons-numbers/apidocs/element-list")
        }

        externalDocumentationLinks.register("guava") {
            url(guavaVersion.map { "https://guava.dev/releases/$it/api/docs/" })
            packageListUrl(guavaVersion.map { "https://guava.dev/releases/$it/api/docs/element-list" })
        }
    }
}

pluginManager.withPlugin("java") {
    apply(plugin = "org.jetbrains.dokka-javadoc")
    configure<DokkaExtension> {
        dokkaSourceSets.configureEach {
            jdkVersion.convention(
                project.the<JavaPluginExtension>().toolchain.languageVersion.map { it.asInt() }.orElse(25)
            )
        }
    }
    configure<JavaPluginExtension> {
        withJavadocJar()
    }
    tasks.named<Jar>("javadocJar") {
        from(tasks.named("dokkaGeneratePublicationJavadoc"))
    }
}

tasks.named("assemble") {
    dependsOn(tasks.named("dokkaGeneratePublicationHtml"))
}
