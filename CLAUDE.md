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

### Dependencies Between Cores

All cores share one version, derived from the same repository git tag, so a core's own `version` is always the version of every sibling core it depends on. Declare sibling dependencies with it explicitly — `api("com.kelvsyc.kotlin:kotlin-core:$version")` — and the root composite build substitutes the included build regardless of the version. A versionless declaration would be published versionless (Kotlin Multiplatform target publications ignore `versionMapping`); the `github-publishing` convention fails module metadata generation if any published dependency lacks a version (`RequireDependencyVersions`).

### Component Settings Pattern

Every core's `settings.gradle.kts`:
```kotlin
pluginManagement { includeBuild("../../gradle/settings") }
plugins { id("com.kelvsyc.internal.kotlin-tools.settings") }
```

### Convention Plugins

- `kotlin-multiplatform-base` — Applies `kotlin("multiplatform")`, Kotlin consumer-compatibility settings (see below), Kotlin Gradle plugin version check, platform BOM in `commonMain`, Kotest engine/assertions in `commonTest`, `commonMain` platform-import check
- `kotlin-multiplatform-library` — Applies base; enables sources JAR for publication
- `kotlin-multiplatform-jvm` — Applies base; adds JVM target, JDK 25 toolchain with a Java 17 release (`JvmCompatibility`), `kotest-runner-junit5` in `jvmTest`, JUnit Platform test task
- `kotlin-multiplatform-js` — Applies base; adds JS IR target with Node.js; raises the JS stdlib back to the compiler version (Kotlin/JS rejects an older stdlib klib ABI)
- `dokka` — Configures Dokka HTML generation with GitHub source links resolved from git HEAD; wires `assemble` → `dokkaGeneratePublicationHtml`
- `github-publishing` — Applies `maven-publish`; configures GitHub Packages repository using `GITHUB_ACTOR`/`GITHUB_TOKEN`; publishes resolved dependency versions (`versionMapping { fromResolutionResult() }`) so versionless declarations don't publish versionless (not honoured by Kotlin Multiplatform target publications), and fails module metadata generation if a published dependency still lacks a version; strips the internal platform BOM (`com.kelvsyc.internal.kotlin-tools:platform`) from published POM and Gradle module metadata so that consumers don't see an unresolvable dependency. The convention plugins still declare `implementation(platform(...))` for local resolution — the stripping happens at publication time via `pom.withXml` and a `GenerateModuleMetadata` post-processing action (`StripInternalPlatform`).

### Testing

Tests use [Kotest](https://kotest.io/) with JUnit Platform (JVM target only).

### Kotlin Consumer Compatibility

The Kotlin Gradle plugin version (catalog `kotlin`) only selects the compiler. What consumers need is set by `KotlinCompatibility` in `gradle/plugins/kotlin-convention` (deliberately outside the version catalog so Dependabot leaves it alone):

- `apiVersion`/`languageVersion` = 2.2, and `coreLibrariesVersion` = 2.2.x, so the `kotlin-stdlib` declared in published metadata is 2.2.x. JVM consumers can use Kotlin 2.1+ (compilers read metadata one version ahead).
- Klibs (JS, native) carry the compiler's ABI version, so JS/native consumers need a compiler at least as new as the Kotlin Gradle plugin, whatever the language version is.
- Cores needing newer APIs override per core (e.g. `luxon-extensions` uses 2.3 for stable `kotlin.time.Instant`).

The applied Kotlin Gradle plugin can be silently raised above the catalog version by Gradle's embedded Kotlin (via `kotlin-dsl`) or another build plugin's stdlib requirement. The convention plugins fail the build when the applied plugin version differs from the catalog `kotlin` version, so a Gradle bump that raises embedded Kotlin requires bumping the catalog in the same change. They also fail when any other `org.jetbrains.kotlin` artifact on the build script classpath differs from the plugin version (`checkKotlinToolchainAlignment`): a build plugin depending on a newer `kotlin-bom` can raise e.g. `kotlin-daemon-client` alone, which surfaces as "Could not connect to Kotlin compile daemon" followed by `OutOfMemoryError: Metaspace`.

### Versioning

Driven by git tags via `com.javiersc.semver`, applied through the settings plugin.

### JDK Constraints

Published JVM artifacts require Java 17, and the cores are compiled with JDK 25. `JvmCompatibility` in `gradle/plugins/kotlin-convention` sets both. The `kotlin-jvm-library` and `kotlin-multiplatform-jvm` convention plugins apply it, and it does three things:

- Compiles with `jvmTarget = 17` and `-Xjdk-release=17`, so only the Java 17 API is available. Lowering `jvmTarget` alone is not enough: against a JDK 21+ class library, `MutableList.removeFirst()` compiles to `java.util.List.removeFirst`, which fails with `NoSuchMethodError` on Java 17.
- Sets `options.release = 17` on Java compilation, which makes the published `org.gradle.jvm.version` 17.
- Runs tests on JDK 17, which Gradle provisions automatically.

Use LTS JDKs only. Raise the release only if a library needs it. The convention plugins themselves compile for JDK 25, so the build must run on JDK 25.
