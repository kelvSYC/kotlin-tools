pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.pkg.github.com/kelvSYC/gradle-build-conventions") {
            name = "GitHubPackagesBuildConventions"
            credentials {
                username = providers.gradleProperty("gpr.user").orNull ?: System.getenv("GITHUB_ACTOR")
                password = providers.gradleProperty("gpr.key").orNull ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }

    versionCatalogs.register("libs") {
        from(files("../libs.versions.toml"))
    }
}

plugins {
    // Provisions this build's JDK toolchain. A literal version: a settings `plugins {}` block is evaluated before any
    // version catalog exists. Keep it in step with the shared catalog's `foojay-resolver-plugin`.
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
