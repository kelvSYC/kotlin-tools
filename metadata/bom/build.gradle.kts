plugins {
    `java-platform`
    id("com.kelvsyc.internal.kotlin-tools.github-publishing")
}

group = "com.kelvsyc.kotlin"

val componentNames = file("../../cores")
    .listFiles { f -> f.isDirectory }
    ?.toList()
    .orEmpty()
    .map { it.name }

val bomVersion = providers.exec {
    commandLine("git", "describe", "--tags", "--match", "v*", "--abbrev=0")
}.standardOutput.asText.map { it.trim().removePrefix("v") }

dependencies {
    constraints {
        componentNames.forEach {
            api("$group:$it:${bomVersion.get()}")
        }
    }
}

publishing {
    publications.register<MavenPublication>("maven") {
        from(components["javaPlatform"])
    }
}
