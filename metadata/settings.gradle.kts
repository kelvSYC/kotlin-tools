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
    // Resolve .git worktree pointer files: jgit's FileRepositoryBuilder.setGitDir() (used by
    // com.javiersc.semver 0.9.0) does not follow `gitdir:` pointers, so in a git worktree the
    // raw `.git` file must be replaced with the real worktree git directory.
    gitDir.set(layout.settingsDirectory.dir(resolveGitDir("../.git").absolutePath))
    tagPrefix.set("v")
}

fun resolveGitDir(relativePath: String): java.io.File {
    val marker = layout.settingsDirectory.asFile.resolve(relativePath)
    if (!marker.isFile) return marker
    val pointer = marker.readLines()
        .firstOrNull { it.startsWith("gitdir:") }
        ?.substringAfter("gitdir:")
        ?.trim()
        ?: return marker
    val pointed = java.io.File(pointer)
    val resolved = if (pointed.isAbsolute) pointed else marker.parentFile.resolve(pointed)
    return resolved.canonicalFile
}

include("bom")
include("catalog")
