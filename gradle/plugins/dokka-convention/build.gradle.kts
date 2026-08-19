plugins {
    `kotlin-dsl`
}

group = "com.kelvsyc.internal.kotlin-tools"

kotlin {
    jvmToolchain(25)
}

dependencies {
    implementation(platform("com.kelvsyc.internal.kotlin-tools:platform"))
    implementation(libs.dokka.plugin)
    implementation(libs.kotlin.plugin)
}

// kotlin-dsl's precompiled script plugin accessor generation resolves its own
// classpath configuration without the platform BOM's constraints, and dependencies
// can't be declared against it directly, so dokka-plugin's transitively bundled
// (vulnerable) Jackson version must be forced via resolution strategy instead.
val jacksonVersion = libs.jackson.bom.map { it.versionConstraint.requiredVersion }
configurations.matching { it.name == "precompiledScriptPluginAccessorsGenerationClasspath" }.configureEach {
    resolutionStrategy.eachDependency {
        // jackson-annotations dropped its patch version segment in the 2.20 line, so it
        // never matches the rest of the family's version string; leave it to resolve
        // against the other forced modules' own (correct) dependency metadata instead.
        if (requested.group.startsWith("com.fasterxml.jackson") && requested.name != "jackson-annotations") {
            useVersion(jacksonVersion.get())
        }
    }
}
