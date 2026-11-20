// Settings plugin to be applied to all builds of kotlin-tools. The shared settings plugin provides the
// repositories, the `libs` catalog (with the shared platform), semver versioning and the foojay toolchain resolver.

// Applied imperatively rather than in a `plugins {}` block: generating this script's accessors applies the
// `plugins {}` block against the compile classpath, which lacks the foojay resolver the shared plugin applies
// (com.kelvsyc.internal:settings-plugin declares it as a runtime-only dependency). The fix belongs upstream: the shared
// settings plugin should expose the foojay resolver as an `api` dependency, after which this can become `plugins {}`.
apply(plugin = "com.kelvsyc.internal.settings")

// kotlin-tools-only: the convention plugins in gradle/plugins at the repository root. Found by walking up from the
// settings directory, so that builds at any depth (cores/*, aggregation, metadata) can apply this plugin; the
// gradle/plugins build itself applies it too, and must not include itself.
val buildDir: File = layout.settingsDirectory.asFile.canonicalFile
val conventionPlugins: File = generateSequence(buildDir) { it.parentFile }
    .map { it.resolve("gradle/plugins") }
    .firstOrNull { it.resolve("settings.gradle.kts").isFile }
    ?: error("No gradle/plugins build found above $buildDir")

if (conventionPlugins != buildDir) {
    pluginManagement.includeBuild(conventionPlugins.path)
}
