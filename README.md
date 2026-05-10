# kotlin-tools

A composite build of Kotlin Multiplatform libraries, published to GitHub Packages.

## Components

| Library | Targets | Description |
|---|---|---|
| `kotlin-core` | JVM, JS | Core utilities: property composition and converters |
| `guava-extensions` | JVM | Google Guava extensions |
| `moshi-extensions` | JVM | Moshi JSON serialization extensions |
| `xml-extensions` | JVM | XML tree API and XPath query engine using StAX |
| `commons-lang-extensions` | JVM | Apache Commons Lang 3 extensions |
| `commons-numbers-extensions` | JVM | Apache Commons Numbers extensions |

All libraries are published under the group `com.kelvsyc.kotlin`.

A BOM (`kotlin-tools-bom`) and a Gradle version catalog are also published.

## Consuming these libraries

### Authentication

GitHub Packages requires authentication even for public repositories. You need a GitHub
Personal Access Token (PAT) with the `read:packages` scope.

**In CI (GitHub Actions):** If your repository is under the same GitHub owner, the built-in
`GITHUB_TOKEN` works automatically. Otherwise, store a PAT as a repository secret.

**For local development:** Add credentials to `~/.gradle/gradle.properties`:

```properties
gpr.user=YOUR_GITHUB_USERNAME
gpr.key=ghp_YOUR_PERSONAL_ACCESS_TOKEN
```

### Gradle setup

Add the GitHub Packages repository and version catalog to your `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://maven.pkg.github.com/kelvSYC/kotlin-tools") {
            credentials {
                username = providers.gradleProperty("gpr.user").orNull
                    ?: System.getenv("GITHUB_ACTOR")
                password = providers.gradleProperty("gpr.key").orNull
                    ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }

    versionCatalogs {
        create("kotlinTools") {
            from("com.kelvsyc.kotlin:catalog:VERSION")
        }
    }
}
```

Replace `VERSION` with the desired release version from the
[Releases](https://github.com/kelvSYC/kotlin-tools/releases) page.

Then use the catalog aliases in your build:

```kotlin
dependencies {
    implementation(kotlinTools.kotlin.core)
    implementation(kotlinTools.guava.extensions)
}
```

### CI setup (GitHub Actions)

```yaml
- name: Build
  run: ./gradlew build
  env:
    GITHUB_ACTOR: ${{ github.actor }}
    GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
```
