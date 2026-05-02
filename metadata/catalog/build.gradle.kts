plugins {
    `version-catalog`
    id("com.kelvsyc.internal.kotlin-tools.github-publishing")
}

group = "com.kelvsyc.kotlin"

val projectVersionAlias = "kotlin-tools-version"

val componentNames = file("../../cores")
    .listFiles { f -> f.isDirectory }
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
