pluginManagement {
    includeBuild("../gradle/plugins")
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("com.kelvsyc.internal.semver")
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }

    includeBuild("../gradle/platform")

    versionCatalogs.register("libs") {
        from(files("../gradle/libs.versions.toml"))
    }
}

include("dokka")

// Builds to be aggregated
file("../cores").list { dir, _ -> dir.isDirectory }?.forEach {
    includeBuild("../cores/$it")
}
