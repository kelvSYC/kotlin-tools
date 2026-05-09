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
    tagPrefix.set("v")
}

include("bom")
include("catalog")
