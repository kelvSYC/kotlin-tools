plugins {
    `java-platform`
    id("com.kelvsyc.internal.kotlin-tools.github-publishing")
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
    doFirst {
        require(version != "unspecified") {
            "Cannot publish: no git tag found. Tag the repo with a 'v' prefix before publishing."
        }
    }
}
