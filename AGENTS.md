# Agent Guidance

Guidance for coding agents (and humans) working in this Gradle composite build: requirements, build commands, conventions, architecture and gotchas. This file is written to be model-neutral; `CLAUDE.md` only imports it (plus any Claude-specific setup). Keep tool- or vendor-specific instructions out of it.

## Requirements

- **Tests must pass.** All code changes must pass `./gradlew :check` before completing a task.
- **Tests must exist.** New public API surface requires corresponding tests in `cores/*/src/jvmTest/kotlin/` (JVM-only) or `cores/*/src/commonTest/kotlin/` (multiplatform). Follow the Kotest `FunSpec` style used in existing tests.
- **README must stay current.** Any change that adds or significantly modifies a public type, interface, factory function, or trait instance must include a corresponding update to the component's `README.md`.
- **Root README must stay current.** When adding a new core, add it to the Components table in the root `README.md`.
- **Be concise.** Avoid unnecessary explanation or narrative.
- **No unsolicited explanations.** Only explain what you're doing if explicitly asked.

## Build Commands

**Always invoke Gradle from the repository root.** Never `cd` into a component directory (`cores/*`, `gradle/*`) to run Gradle — each component is an included build and does not have its own wrapper. Running Gradle from a component directory will fail or produce incorrect results.

```bash
./gradlew :build          # Build all cores
./gradlew :check          # Run tests across all cores
./gradlew :publish        # Publish to GitHub Packages (requires `gpr.user`/`gpr.key` Gradle properties or `GITHUB_ACTOR`/`GITHUB_TOKEN`)
./gradlew dokkaGenerate   # Generate HTML API docs for all cores
```

Single core (included build form, from repository root):
```bash
./gradlew :kotlin-core:build
./gradlew :kotlin-core:allTests
```

## Kotlin Style Guidelines

No detekt is configured yet, so these are not enforced by the build. Follow them as conventions:

- No wildcard imports (only `java.util.*` is allowed).
- Do not catch `Exception`, `RuntimeException`, `Error`, `Throwable`, `NullPointerException`, `IndexOutOfBoundsException`, or `IllegalMonitorStateException`.
- Do not throw `Exception`, `RuntimeException`, `Error`, or `Throwable`.
- No `TODO:`, `FIXME:`, or `STOPSHIP:` markers.
- No unexplained numeric literals in non-test, non-`.kts` source; extract to named constants.
- Remove unused private declarations.

## Architecture

This is a **composite build** of Kotlin Multiplatform libraries, published to GitHub Packages (`maven.pkg.github.com/kelvSYC/kotlin-tools`).

### Build Hierarchy

The root `settings.gradle.kts` composes:

1. **`gradle/`** — Internal build infrastructure (not published):
   - `gradle/libs.versions.toml` — Pins the shared build conventions version (see Shared Build Conventions)
   - `gradle/settings` — Thin local settings plugin (`com.kelvsyc.internal.kotlin-tools.settings`): applies the shared settings plugin and includes `gradle/plugins` into every core build
   - `gradle/plugins/dokka-convention` — Convention plugin: `dokka` (Dokka HTML docs, GitHub source links resolved from git HEAD)
   - `gradle/plugins/kotlin-convention` — Convention plugins: `kotlin-multiplatform-base`, `kotlin-multiplatform-library`, `kotlin-multiplatform-jvm`, `kotlin-multiplatform-js`

2. **`cores/`** — Published Kotlin Multiplatform libraries (group `com.kelvsyc.kotlin`). Each is an independent included build.

### Shared Build Conventions

The catalog, platform BOM, semver versioning, repositories, toolchain resolver and publishing convention come from [gradle-build-conventions](https://github.com/kelvSYC/gradle-build-conventions) (group `com.kelvsyc.internal`, published to GitHub Packages). The settings plugin `com.kelvsyc.internal.settings` provides:

- the foojay toolchain resolver and the repositories (Maven Central, GitHub Packages for gradle-build-conventions and kotlin-tools, Gradle `libs-releases` for `org.gradle.experimental`)
- the `libs` version catalog (`com.kelvsyc.internal:catalog`) and the platform BOM (`libs.conventions.platform`), whose dependency versions are therefore not declared in this repository
- project versions from git tags (`com.kelvsyc.internal.semver`) and default group handling
- versionless resolution of plugin IDs in exactly the `com.kelvsyc.internal` namespace (not the local `com.kelvsyc.internal.kotlin-tools.*` IDs, which resolve from the included `gradle/plugins` build), e.g. `com.kelvsyc.internal.github-publishing` (Maven publication to GitHub Packages; strips the internal platform BOM from published POM and Gradle module metadata, and fails module metadata generation if a published dependency lacks a version)

`gradle/settings` applies it with `apply(plugin = "com.kelvsyc.internal.settings")` rather than a `plugins {}` block: generating a precompiled settings plugin's accessors applies its `plugins {}` block to a stand-in `Settings` after settings evaluation, where Gradle rejects the repositories the shared plugin declares.

The shared version is pinned once, as `gradle-build-conventions` in `gradle/libs.versions.toml`; the thin settings plugin depends on `com.kelvsyc.internal:settings-plugin` at that version, which in turn selects the catalog and platform versions. Dependabot bumps it: `.github/dependabot.yml` gives it the GitHub Packages registry of gradle-build-conventions, with the `DEPENDABOT_PACKAGES_TOKEN` Dependabot secret (a token with `read:packages`) as credentials. A manual bump is that one line.

A bump PR from Dependabot completes itself (`.github/workflows/conventions-bump.yml`): it moves the Gradle wrapper to the Gradle version the new release is built for (`.github/scripts/follow-conventions-gradle.sh`, reading the release's catalog), pushes that to the PR, and enables GitHub auto-merge, so the PR merges once the required checks pass. Other Dependabot PRs are left alone. The push and the auto-merge use the `CONVENTIONS_TOKEN` personal access token, stored as a Dependabot secret (workflows run for Dependabot see only those): a push or merge made with `GITHUB_TOKEN` triggers no workflows, so neither CI on the wrapper change nor the snapshot publish after the merge would run. The shared catalog's `gradle` and `kotlin` versions apply here: keep the wrapper and the Kotlin Gradle plugin in step with them.

Resolving the shared plugin requires GitHub Packages credentials even to configure the build (see Building Requires GitHub Packages Credentials). The Kotlin and Dokka convention plugins are local, in `gradle/plugins`.

### Dependencies Between Cores

All cores share one version, derived from the same repository git tag, so a core's own `version` is always the version of every sibling core it depends on. Declare sibling dependencies with it explicitly — `api("com.kelvsyc.kotlin:kotlin-core:$version")` — and the root composite build substitutes the included build regardless of the version. A versionless declaration would be published versionless (Kotlin Multiplatform target publications ignore `versionMapping`); the shared `github-publishing` convention fails module metadata generation if any published dependency lacks a version (`RequireDependencyVersions`).

### Component Settings Pattern

Every core's `settings.gradle.kts` (as do `metadata`, `aggregation` and `gradle/plugins`, with the matching relative path) starts with:
```kotlin
pluginManagement {
    includeBuild("../../gradle/settings")
    // Gradle resolves the included settings plugin's external dependencies with this build's plugin repositories.
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.github.com/kelvSYC/gradle-build-conventions") {
            name = "GitHubPackagesBuildConventions"
            credentials {
                username = providers.gradleProperty("gpr.user").orNull ?: System.getenv("GITHUB_ACTOR")
                password = providers.gradleProperty("gpr.key").orNull ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

plugins { id("com.kelvsyc.internal.kotlin-tools.settings") }
```
The `repositories` block is needed because the included settings plugin depends on the shared settings plugin from GitHub Packages, and Gradle resolves those dependencies with the applying build's plugin repositories. Keep the block byte-identical in every settings file.

### Convention Plugins

- `kotlin-multiplatform-base` — Applies `kotlin("multiplatform")`, Kotlin consumer-compatibility settings (see below), Kotlin Gradle plugin version check, platform BOM in `commonMain`, Kotest engine/assertions in `commonTest`, `commonMain` platform-import check
- `kotlin-multiplatform-library` — Applies base; enables sources JAR for publication
- `kotlin-multiplatform-jvm` — Applies base; adds JVM target, JDK 25 toolchain with a Java 17 release (`JvmCompatibility`), `kotest-runner-junit5` in `jvmTest`, JUnit Platform test task
- `kotlin-multiplatform-js` — Applies base; adds JS IR target with Node.js; raises the JS stdlib back to the compiler version (Kotlin/JS rejects an older stdlib klib ABI)
- `dokka` — Configures Dokka HTML generation with GitHub source links resolved from git HEAD; wires `assemble` → `dokkaGeneratePublicationHtml`
- `com.kelvsyc.internal.github-publishing` (shared) — Applies `maven-publish`; configures the GitHub Packages repository, deriving its URL from the `origin` git remote and taking credentials from the `gpr.user`/`gpr.key` Gradle properties, else `GITHUB_ACTOR`/`GITHUB_TOKEN`; strips the internal platform BOM (`com.kelvsyc.internal:platform`) from published POM and Gradle module metadata so that consumers don't see an unresolvable dependency. The convention plugins still declare `implementation(platform(...))` for local resolution.

### Testing

Tests use [Kotest](https://kotest.io/) with JUnit Platform (JVM target only).

### Kotlin Consumer Compatibility

The Kotlin Gradle plugin version (catalog `kotlin`) only selects the compiler. What consumers need is set by `KotlinCompatibility` in `gradle/plugins/kotlin-convention` (deliberately outside the version catalog so Dependabot leaves it alone):

- `apiVersion`/`languageVersion` = 2.2, and `coreLibrariesVersion` = 2.2.x, so the `kotlin-stdlib` declared in published metadata is 2.2.x. JVM consumers can use Kotlin 2.1+ (compilers read metadata one version ahead).
- Klibs (JS, native) carry the compiler's ABI version, so JS/native consumers need a compiler at least as new as the Kotlin Gradle plugin, whatever the language version is.
- Cores needing newer APIs override per core (e.g. `luxon-extensions` uses 2.3 for stable `kotlin.time.Instant`).

The applied Kotlin Gradle plugin can be silently raised above the catalog version by Gradle's embedded Kotlin (via `kotlin-dsl`) or another build plugin's stdlib requirement. The convention plugins fail the build when the applied plugin version differs from the catalog `kotlin` version, so a Gradle bump that raises embedded Kotlin requires bumping the catalog in the same change. They also fail when any other `org.jetbrains.kotlin` artifact on the build script classpath differs from the plugin version (`checkKotlinToolchainAlignment`): a build plugin depending on a newer `kotlin-bom` can raise e.g. `kotlin-daemon-client` alone, which surfaces as "Could not connect to Kotlin compile daemon" followed by `OutOfMemoryError: Metaspace`.

The compiler `kotlin` version comes from the shared catalog: bump it with a gradle-build-conventions release, then update the single `gradle-build-conventions` pin in `gradle/libs.versions.toml`.

### Versioning

Driven by git tags via the shared semver settings plugin (`com.kelvsyc.internal.semver`), applied through the shared settings plugin.

### JDK Constraints

Published JVM artifacts require Java 17, and the cores are compiled with JDK 25. `JvmCompatibility` in `gradle/plugins/kotlin-convention` sets both. The `kotlin-jvm-library` and `kotlin-multiplatform-jvm` convention plugins apply it, and it does three things:

- Compiles with `jvmTarget = 17` and `-Xjdk-release=17`, so only the Java 17 API is available. Lowering `jvmTarget` alone is not enough: against a JDK 21+ class library, `MutableList.removeFirst()` compiles to `java.util.List.removeFirst`, which fails with `NoSuchMethodError` on Java 17.
- Sets `options.release = 17` on Java compilation, which makes the published `org.gradle.jvm.version` 17.
- Runs tests on JDK 17, which Gradle provisions automatically.

The Kotlin Gradle plugin does not set `org.gradle.jvm.version` on Kotlin Multiplatform JVM targets, so `kotlin-multiplatform-jvm` sets it to 17 itself. That covers both the published variants and the JVM classpaths, so a dependency whose Gradle metadata declares a newer JVM fails resolution.

Use LTS JDKs only. Raise the release only if a library needs it. The convention plugins themselves compile for JDK 25, so the build must run on JDK 25.

## Critical Gotchas

### Never `cd` Into Components ⚠️

This is a composite build. All Gradle commands must run from the repository root using the root wrapper (`./gradlew`). Do **not** `cd cores/kotlin-core && ./gradlew build` — the components do not have their own Gradle wrapper and rely on the root build for resolution of included builds, convention plugins, and the version catalog.

### Publishing Requires Credentials ⚠️

`./gradlew :publish` requires credentials: the `gpr.user`/`gpr.key` Gradle properties, else `GITHUB_ACTOR` and `GITHUB_TOKEN`. Without them, it fails. The publish repository URL is derived from the `origin` git remote, so `origin` must point at the GitHub repository.

### Building Requires GitHub Packages Credentials ⚠️

The shared build conventions are resolved from GitHub Packages when the build is configured, so every Gradle invocation needs credentials with `read:packages`: the `gpr.user`/`gpr.key` Gradle properties (e.g. in `~/.gradle/gradle.properties`), else the `GITHUB_ACTOR`/`GITHUB_TOKEN` environment variables. CI jobs that run Gradle need `packages: read` permission and both variables set.

### Core Settings Pattern ⚠️

Do not modify the `pluginManagement`/`plugins` blocks in each core's `settings.gradle.kts` (see Component Settings Pattern under Architecture) — they wire up the composite build and must stay identical across settings files.

## Quick Navigation

- **Architecture & build hierarchy**: see Architecture above
- **Shared conventions pin**: `gradle/libs.versions.toml` (the catalog itself comes from gradle-build-conventions)
- **Convention plugins**: `gradle/plugins/*/src/main/kotlin/`
- **Source code**: `cores/*/src/commonMain/kotlin/`
- **Tests**: `cores/*/src/commonTest/kotlin/` and `cores/*/src/jvmTest/kotlin/`
