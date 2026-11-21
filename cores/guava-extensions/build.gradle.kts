plugins {
    id("com.kelvsyc.internal.dokka")
    id("com.kelvsyc.internal.github-publishing")
    id("com.kelvsyc.internal.kotlin-jvm-library")
}

group = "com.kelvsyc.kotlin"

dependencies {
    api("com.kelvsyc.kotlin:kotlin-core:$version")
    api(libs.guava)
    implementation(kotlin("reflect", kotlin.coreLibrariesVersion))
}

val guavaVersion: Provider<String> = libs.guava.map { it.versionConstraint.requiredVersion }

dokka {
    dokkaSourceSets.configureEach {
        externalDocumentationLinks.register("guava") {
            url(guavaVersion.map { "https://guava.dev/releases/$it/api/docs/" })
            packageListUrl(guavaVersion.map { "https://guava.dev/releases/$it/api/docs/element-list" })
        }
    }
}
