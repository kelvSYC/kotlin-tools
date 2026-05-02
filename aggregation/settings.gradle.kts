pluginManagement {
    includeBuild("../gradle/plugins")
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("com.javiersc.semver") version "0.9.0"
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

semver {
    isEnabled.set(true)
    gitDir.set(layout.settingsDirectory.dir("../.git"))
}

include("dokka")

// Builds to be aggregated
file("../cores").list { dir, _ -> dir.isDirectory }?.forEach {
    includeBuild("../cores/$it")
}
