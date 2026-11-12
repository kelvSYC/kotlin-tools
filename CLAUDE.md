# CLAUDE.md

Architectural reference for this Gradle composite build. See `AGENTS.md` for build commands and quick-reference gotchas.

## Requirements

- **Tests must pass** before any task is complete: `./gradlew :check`.
- **README must stay current** when adding or modifying public API — new types, interfaces, factory functions, or trait instances must be reflected in the component's `README.md`.
- **Root README must stay current** when adding a new core — add it to the Components table in the root `README.md`.

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

- `kotlin-multiplatform-base` — Applies `kotlin("multiplatform")`, Kotlin consumer-compatibility settings (see below), Kotlin Gradle plugin version check, platform BOM in `commonMain`, Kotest engine/assertions in `commonTest`, `commonMain` platform-import check
- `kotlin-multiplatform-library` — Applies base; enables sources JAR for publication
- `kotlin-multiplatform-jvm` — Applies base; adds JVM target, JDK 21 toolchain, `kotest-runner-junit5` in `jvmTest`, JUnit Platform test task
- `kotlin-multiplatform-js` — Applies base; adds JS IR target with Node.js; raises the JS stdlib back to the compiler version (Kotlin/JS rejects an older stdlib klib ABI)
- `dokka` — Configures Dokka HTML generation with GitHub source links resolved from git HEAD; wires `assemble` → `dokkaGeneratePublicationHtml`
- `github-publishing` — Applies `maven-publish`; configures GitHub Packages repository using `GITHUB_ACTOR`/`GITHUB_TOKEN`; publishes resolved dependency versions (`versionMapping { fromResolutionResult() }`) so versionless declarations don't publish versionless (not honoured by Kotlin Multiplatform target publications); strips the internal platform BOM (`com.kelvsyc.internal.kotlin-tools:platform`) from published POM and Gradle module metadata so that consumers don't see an unresolvable dependency. The convention plugins still declare `implementation(platform(...))` for local resolution — the stripping happens at publication time via `pom.withXml` and a `GenerateModuleMetadata` post-processing action (`StripInternalPlatform`).

### Testing

Tests use [Kotest](https://kotest.io/) with JUnit Platform (JVM target only).

### Kotlin Consumer Compatibility

The Kotlin Gradle plugin version (catalog `kotlin`) only selects the compiler. What consumers need is set by `KotlinCompatibility` in `gradle/plugins/kotlin-convention` (deliberately outside the version catalog so Dependabot leaves it alone):

- `apiVersion`/`languageVersion` = 2.2, and `coreLibrariesVersion` = 2.2.x, so the `kotlin-stdlib` declared in published metadata is 2.2.x. JVM consumers can use Kotlin 2.1+ (compilers read metadata one version ahead).
- Klibs (JS, native) carry the compiler's ABI version, so JS/native consumers need a compiler at least as new as the Kotlin Gradle plugin, whatever the language version is.
- Cores needing newer APIs override per core (e.g. `luxon-extensions` uses 2.3 for stable `kotlin.time.Instant`).

The applied Kotlin Gradle plugin can be silently raised above the catalog version by Gradle's embedded Kotlin (via `kotlin-dsl`) or another build plugin's stdlib requirement. The convention plugins fail the build when the applied plugin version differs from the catalog `kotlin` version, so a Gradle bump that raises embedded Kotlin requires bumping the catalog in the same change.

### Versioning

Driven by git tags via `com.javiersc.semver`, applied through the settings plugin.

### JDK Constraints

Published JVM artifacts target JDK 21: the `kotlin-jvm-library` and `kotlin-multiplatform-jvm` convention plugins pin the JVM toolchain to it. Use LTS JDKs only, and go newer only if a library needs it. The convention plugins themselves still compile for JDK 25, so the build must run on JDK 25.
