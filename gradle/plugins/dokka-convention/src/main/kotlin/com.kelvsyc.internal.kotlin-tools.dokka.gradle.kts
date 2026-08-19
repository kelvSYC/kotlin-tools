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

val jacksonVersion: Provider<String> = libs
    .findLibrary("jackson-bom")
    .get()
    .map { it.versionConstraint.requiredVersion }

val jsoupVersion: Provider<String> = libs
    .findLibrary("jsoup")
    .get()
    .map { it.versionConstraint.requiredVersion }

// dokka-core resolves its own generator/plugin classpaths (the "dokka*" configurations
// created per publication format) independently of this project's dependency graph, so
// the platform's Jackson BOM override (gradle/platform/build.gradle.kts) never reaches
// the vulnerable Jackson and jsoup versions it bundles transitively. These configurations
// are resolver-role-locked (dependencies can't be declared against them directly), so the
// versions must be forced via resolution strategy instead of importing a BOM.
configurations.matching { it.name.startsWith("dokka") }.configureEach {
    resolutionStrategy.eachDependency {
        // jackson-annotations dropped its patch version segment in the 2.20 line, so it
        // never matches the rest of the family's version string; leave it to resolve
        // against the other forced modules' own (correct) dependency metadata instead.
        if (requested.group.startsWith("com.fasterxml.jackson") && requested.name != "jackson-annotations") {
            useVersion(jacksonVersion.get())
        }
        if (requested.group == "org.jsoup" && requested.name == "jsoup") {
            useVersion(jsoupVersion.get())
        }
    }
}

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

// dokka-javadoc does not support KMP (https://github.com/Kotlin/dokka/issues/1753).
// KMP's JVM target applies the java plugin internally, so we must exclude it here;
// the KMP JVM convention plugin handles javadoc JARs separately.
pluginManager.withPlugin("java") {
    if (!pluginManager.hasPlugin("org.jetbrains.kotlin.multiplatform")) {
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
}

tasks.named("assemble") {
    dependsOn(tasks.named("dokkaGeneratePublicationHtml"))
}
