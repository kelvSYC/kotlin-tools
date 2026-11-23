plugins {
    `java-platform`
    id("com.kelvsyc.internal.github-publishing")
}

group = "com.kelvsyc.kotlin"

val componentNames = file("../../cores")
    .listFiles { f -> f.isDirectory && f.resolve("settings.gradle.kts").exists() }
    ?.toList()
    .orEmpty()
    .map { it.name }

dependencies {
    constraints {
        componentNames.forEach {
            api("$group:$it:$version")
        }
    }
}

publishing {
    publications.register<MavenPublication>("maven") {
        from(components["javaPlatform"])
    }
}

tasks.withType<PublishToMavenRepository>().configureEach {
    // Read at configuration time and held in a local: a script-level val would capture the script object in the action.
    val publishedVersion = version.toString()
    doFirst {
        require(publishedVersion != "unspecified") {
            "Cannot publish: no git tag found. Tag the repo with a 'v' prefix before publishing."
        }
    }
}
