plugins {
    `version-catalog`
    id("com.kelvsyc.internal.kotlin-tools.github-publishing")
}

group = "com.kelvsyc.kotlin"

val projectVersionAlias = "kotlin-tools-version"

val componentNames = file("../../cores")
    .listFiles { f -> f.isDirectory && f.resolve("settings.gradle.kts").exists() }
    ?.toList()
    .orEmpty()
    .map { it.name }

catalog {
    versionCatalog {
        version(projectVersionAlias, version.toString())

        componentNames.forEach {
            library(it, group.toString(), it).versionRef(projectVersionAlias)
        }
    }
}

publishing {
    publications.register<MavenPublication>("maven") {
        from(components["versionCatalog"])
    }
}

tasks.withType<PublishToMavenRepository>().configureEach {
    doFirst {
        require(version != "unspecified") {
            "Cannot publish: no git tag found. Tag the repo with a 'v' prefix before publishing."
        }
    }
}
