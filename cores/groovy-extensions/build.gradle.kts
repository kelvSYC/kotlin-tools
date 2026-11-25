plugins {
    id("com.kelvsyc.internal.dokka")
    id("com.kelvsyc.internal.github-publishing")
    id("com.kelvsyc.internal.kotlin-jvm-library")
    id("com.kelvsyc.internal.kover")
    `jvm-test-suite`
}

group = "com.kelvsyc.kotlin"

val groovy3 = libs.groovy3
// `libs.groovy` is a group accessor (the catalog also has groovy-sql entries); asProvider() is the entry itself.
val latestGroovy = libs.groovy.asProvider()

fun majorOf(groovy: Provider<MinimalExternalModuleDependency>) =
    checkNotNull(groovy.get().version) { "Groovy catalog entries must declare a version" }.substringBefore('.')

// Consumers bring their own Groovy (3.x as org.codehaus.groovy, 4+ as org.apache.groovy), so it is never published as a
// dependency: compile against the oldest supported line, and test against it and the newest.
// Real builders for the builder DSL's smoke tests, per Groovy line.
val groovy3BuilderModules = listOf(libs.groovy.xml3, libs.groovy.json3, libs.groovy.ant3)
val latestGroovyBuilderModules = listOf(libs.groovy.xml, libs.groovy.json, libs.groovy.ant)

dependencies {
    compileOnly(groovy3)
    testImplementation(groovy3)
    groovy3BuilderModules.forEach { testImplementation(it) }
}

val kotestModules = listOf(
    "io.kotest:kotest-assertions-core",
    "io.kotest:kotest-assertions-shared",
    "io.kotest:kotest-framework-engine",
    "io.kotest:kotest-runner-junit5",
)

testing {
    suites {
        named<JvmTestSuite>("test") {
            targets.configureEach {
                testTask.configure { systemProperty("groovy.expected.major", majorOf(groovy3)) }
            }
        }
        register<JvmTestSuite>("latestGroovyTest") {
            dependencies {
                implementation(project())
                implementation(latestGroovy)
                latestGroovyBuilderModules.forEach { implementation(it) }
                implementation(platform(libs.conventions.platform))
                kotestModules.forEach { implementation(it) }
            }
            targets.configureEach {
                testTask.configure { systemProperty("groovy.expected.major", majorOf(latestGroovy)) }
            }
        }
    }
}

// Both suites run the same specs.
kotlin.sourceSets.named("latestGroovyTest") {
    kotlin.srcDir("src/test/kotlin")
}

tasks.check {
    dependsOn(testing.suites.named("latestGroovyTest"))
}

// Link Groovy types (Closure, GroovyObject, GString, ...) to Groovy's API docs. These types exist in every supported
// Groovy line, so the latest docs serve, though they may list members newer than Groovy 3.
dokka {
    dokkaSourceSets.configureEach {
        externalDocumentationLinks.register("groovy") {
            url("https://docs.groovy-lang.org/latest/html/api/")
            packageListUrl("https://docs.groovy-lang.org/latest/html/api/element-list")
        }
    }
}
