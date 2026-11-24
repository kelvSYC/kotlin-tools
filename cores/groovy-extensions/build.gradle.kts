plugins {
    id("com.kelvsyc.internal.dokka")
    id("com.kelvsyc.internal.github-publishing")
    id("com.kelvsyc.internal.kotlin-jvm-library")
    id("com.kelvsyc.internal.kover")
    `jvm-test-suite`
}

group = "com.kelvsyc.kotlin"

// Prototype: moves to libs.groovy3..6 once the shared catalog has them.
val groovy3 = "org.codehaus.groovy:groovy:3.0.25"
val newerGroovy = mapOf(
    4 to "org.apache.groovy:groovy:4.0.33",
    5 to "org.apache.groovy:groovy:5.1.3",
    6 to "org.apache.groovy:groovy:6.0.0",
)

// Consumers bring their own Groovy (3.x as org.codehaus.groovy, 4+ as org.apache.groovy), so it is never published as a
// dependency: compile against the oldest supported line, and test against each line.
dependencies {
    compileOnly(groovy3)
    testImplementation(groovy3)
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
                testTask.configure { systemProperty("groovy.expected.major", "3") }
            }
        }
        newerGroovy.forEach { (major, groovy) ->
            register<JvmTestSuite>("groovy${major}Test") {
                dependencies {
                    implementation(project())
                    implementation(groovy)
                    implementation(platform(libs.conventions.platform))
                    kotestModules.forEach { implementation(it) }
                }
                targets.configureEach {
                    testTask.configure { systemProperty("groovy.expected.major", "$major") }
                }
            }
        }
    }
}

// Every suite runs the same specs.
newerGroovy.keys.forEach { major ->
    kotlin.sourceSets.named("groovy${major}Test") {
        kotlin.srcDir("src/test/kotlin")
    }
}

tasks.check {
    dependsOn(newerGroovy.keys.map { testing.suites.named("groovy${it}Test") })
}
