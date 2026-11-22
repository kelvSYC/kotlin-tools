// Settings plugin to be applied to all builds of kotlin-tools. The shared settings plugin provides the
// repositories, the `libs` catalog (with the shared platform), semver versioning and the foojay toolchain resolver.

// Applied imperatively rather than in a `plugins {}` block: generating a precompiled settings plugin's accessors applies
// the `plugins {}` block to a stand-in Settings after settings evaluation, where Gradle rejects the repositories the
// shared plugin declares.
apply(plugin = "com.kelvsyc.internal.settings")
