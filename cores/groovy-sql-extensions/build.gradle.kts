plugins {
    id("com.kelvsyc.internal.dokka")
    id("com.kelvsyc.internal.github-publishing")
    id("com.kelvsyc.internal.kotlin-jvm-library")
    id("com.kelvsyc.internal.kover")
    `jvm-test-suite`
}

group = "com.kelvsyc.kotlin"

// Prototype: moves to the shared catalog (groovy3, groovy-sql3, groovy, groovy-sql) once it has the groovy-sql entries.
val groovy3 = "org.codehaus.groovy:groovy:3.0.25"
val groovySql3 = "org.codehaus.groovy:groovy-sql:3.0.25"
val latestGroovy = "org.apache.groovy:groovy:6.0.0"
val latestGroovySql = "org.apache.groovy:groovy-sql:6.0.0"

fun majorOf(coordinates: String) = coordinates.substringAfterLast(':').substringBefore('.')

// Consumers bring their own Groovy and groovy-sql (3.x as org.codehaus.groovy, 4+ as org.apache.groovy), so neither is
// published as a dependency: compile against the oldest supported line, and test against it and the newest.
dependencies {
    api("com.kelvsyc.kotlin:groovy-extensions:$version")
    compileOnly(groovy3)
    compileOnly(groovySql3)
    testImplementation(groovy3)
    testImplementation(groovySql3)
    testImplementation(libs.sqlite.jdbc)
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
                implementation(latestGroovySql)
                implementation(libs.sqlite.jdbc)
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
