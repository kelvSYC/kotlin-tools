# AGENTS.md

Critical gotchas and quick reference for automated agents working in this Gradle composite build. For comprehensive architecture, see `CLAUDE.md`.

## Requirements for All Agents

- **Tests must pass.** All code changes must pass `./gradlew :check` before completing a task.
- **Tests must exist.** New public API surface requires corresponding tests in `cores/*/src/jvmTest/kotlin/` (JVM-only) or `cores/*/src/commonTest/kotlin/` (multiplatform). Follow the Kotest `FunSpec` style used in existing tests.
- **README must stay current.** Any change that adds or significantly modifies a public type, interface, factory function, or trait instance must include a corresponding update to the component's `README.md`.
- **Root README must stay current.** When adding a new core, add it to the Components table in the root `README.md`.
- **Be concise.** Avoid unnecessary explanation or narrative.
- **No unsolicited explanations.** Only explain what you're doing if explicitly asked.

## Claude Setup

Before using Claude Code in this repo, create `.claude/settings.local.json` (gitignored):
```json
{
  "permissions": {
    "allow": [
      "Skill(update-config)",
      "Skill(update-config:*)"
    ]
  }
}
```

## Build Commands

**Always invoke Gradle from the repository root.** Never `cd` into a component directory (`cores/*`, `gradle/*`) to run Gradle — each component is an included build and does not have its own wrapper. Running Gradle from a component directory will fail or produce incorrect results.

Root commands (aggregate across all cores):
```bash
./gradlew :build          # Full build
./gradlew :check          # Tests only
./gradlew :publish        # Publish to GitHub Packages
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

## Critical Gotchas

### Never `cd` Into Components ⚠️

This is a composite build. All Gradle commands must run from the repository root using the root wrapper (`./gradlew`). Do **not** `cd cores/kotlin-core && ./gradlew build` — the components do not have their own Gradle wrapper and rely on the root build for resolution of included builds, convention plugins, and the version catalog.

### Publishing Requires Env Vars ⚠️

`./gradlew :publish` requires `GITHUB_ACTOR` and `GITHUB_TOKEN`. Without them, it fails.

### Core Settings Pattern ⚠️

Every core's `settings.gradle.kts` must include:
```kotlin
pluginManagement { includeBuild("../../gradle/settings") }
plugins { id("com.kelvsyc.internal.kotlin-tools.settings") }
```

Do not modify these — they wire up the composite build correctly.

## Quick Navigation

- **Architecture & build hierarchy**: See `CLAUDE.md`
- **Version catalog**: `gradle/libs.versions.toml`
- **Convention plugins**: `gradle/plugins/*/src/main/kotlin/`
- **Source code**: `cores/*/src/commonMain/kotlin/`
- **Tests**: `cores/*/src/commonTest/kotlin/` and `cores/*/src/jvmTest/kotlin/`
