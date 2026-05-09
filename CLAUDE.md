# CLAUDE.md

Architectural reference for this Gradle composite build. See `AGENTS.md` for build commands and quick-reference gotchas.

## Requirements

- **Tests must pass** before any task is complete: `./gradlew :check`.

## Build Commands

**Always invoke Gradle from the repository root.** Never `cd` into a component directory (`cores/*`, `gradle/*`) to run Gradle — each component is an included build and does not have its own wrapper. Running Gradle from a component directory will fail or produce incorrect results.

```bash
./gradlew :build          # Build all cores
./gradlew :check          # Run tests across all cores
./gradlew :publish        # Publish to GitHub Packages (requires GITHUB_ACTOR, GITHUB_TOKEN)
./gradlew dokkaGenerate   # Generate HTML API docs for all cores
```

Single core (included build form, from repository root):
```bash
./gradlew :kotlin-core:build
./gradlew :kotlin-core:allTests
```

## Architecture

This is a **composite build** of Kotlin Multiplatform libraries, published to GitHub Packages (`maven.pkg.github.com/kelvSYC/kotlin-tools`).

### Build Hierarchy

The root `settings.gradle.kts` composes:

1. **`gradle/`** — Internal build infrastructure (not published):
   - `gradle/platform` — BOM centralizing all dependency versions (group `com.kelvsyc.internal.kotlin-tools`)
   - `gradle/settings` — Settings plugin (`com.kelvsyc.internal.kotlin-tools.settings`) wiring platform/catalog and semver into every core build
   - `gradle/plugins/dokka-convention` — Convention plugin: `dokka` (Dokka HTML docs, GitHub source links resolved from git HEAD)
   - `gradle/plugins/kotlin-convention` — Convention plugins: `kotlin-multiplatform-base`, `kotlin-multiplatform-library`, `kotlin-multiplatform-jvm`, `kotlin-multiplatform-js`
   - `gradle/plugins/publishing-convention` — Convention plugin: `github-publishing` (Maven publication to GitHub Packages)

2. **`cores/`** — Published Kotlin Multiplatform libraries (group `com.kelvsyc.kotlin`). Each is an independent included build.

### Component Settings Pattern

Every core's `settings.gradle.kts`:
```kotlin
pluginManagement { includeBuild("../../gradle/settings") }
plugins { id("com.kelvsyc.internal.kotlin-tools.settings") }
```

### Convention Plugins

- `kotlin-multiplatform-base` — Applies `kotlin("multiplatform")`, Kotlin 2.3 compiler options, platform BOM in `commonMain`, Kotest engine/assertions in `commonTest`, `commonMain` platform-import check
- `kotlin-multiplatform-library` — Applies base; enables sources JAR for publication
- `kotlin-multiplatform-jvm` — Applies base; adds JVM target, JDK 25 toolchain, `kotest-runner-junit5` in `jvmTest`, JUnit Platform test task
- `kotlin-multiplatform-js` — Applies base; adds JS IR target with Node.js
- `dokka` — Configures Dokka HTML generation with GitHub source links resolved from git HEAD; wires `assemble` → `dokkaGeneratePublicationHtml`
- `github-publishing` — Applies `maven-publish`; configures GitHub Packages repository using `GITHUB_ACTOR`/`GITHUB_TOKEN`

### Testing

Tests use [Kotest](https://kotest.io/) with JUnit Platform (JVM target only).

### Versioning

Driven by git tags via `com.javiersc.semver`, applied through the settings plugin.

### JDK Constraints

The `kotlin-multiplatform-jvm` convention plugin pins the JVM toolchain to JDK 25. The `gradle/settings` and convention plugin builds use JDK 21 for the Gradle daemon.
