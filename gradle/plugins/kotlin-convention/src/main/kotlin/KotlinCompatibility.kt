import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion

/**
 * Oldest Kotlin version that consumers of the published cores are expected to use.
 *
 * The Kotlin Gradle plugin version (catalog `kotlin`) only determines the compiler used to build the cores. What
 * consumers need is governed by the language/API version and by the `kotlin-stdlib` version written into the
 * published metadata, which defaults to the plugin version unless `coreLibrariesVersion` pins it.
 *
 * Kept out of the version catalog so that dependency update tooling does not propose bumping it.
 */
object KotlinCompatibility {
    /** Language and API version the cores are compiled against. */
    val LANGUAGE_VERSION = KotlinVersion.KOTLIN_2_2

    /** `kotlin-stdlib` (and other core library) version declared in published metadata. */
    const val CORE_LIBRARIES_VERSION = "2.2.21"
}

/**
 * Configures [extension] so the compiled output and published metadata only require
 * [KotlinCompatibility.LANGUAGE_VERSION] from consumers.
 */
fun configureKotlinCompatibility(extension: KotlinProjectExtension, compilerOptions: KotlinCommonCompilerOptions) {
    extension.coreLibrariesVersion = KotlinCompatibility.CORE_LIBRARIES_VERSION
    compilerOptions.apiVersion.set(KotlinCompatibility.LANGUAGE_VERSION)
    compilerOptions.languageVersion.set(KotlinCompatibility.LANGUAGE_VERSION)
}

/**
 * Fails if the applied Kotlin Gradle plugin differs from the catalog `kotlin` version.
 *
 * Conflict resolution on the convention plugin classpath can silently raise the plugin to Gradle's embedded Kotlin
 * version (or the stdlib required by another build plugin), leaving the catalog version stale.
 */
fun Project.checkKotlinPluginVersion(libs: VersionCatalog) {
    val expected = libs.findVersion("kotlin").get().requiredVersion
    val actual = getKotlinPluginVersion()
    check(actual == expected) {
        "Kotlin Gradle plugin $actual is applied, but the version catalog declares kotlin = \"$expected\". " +
            "The plugin was likely raised by Gradle's embedded Kotlin or another build plugin's stdlib requirement; " +
            "update the catalog to match."
    }
}
