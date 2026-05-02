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
