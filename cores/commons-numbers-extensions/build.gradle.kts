plugins {
    id("com.kelvsyc.internal.dokka")
    id("com.kelvsyc.internal.github-publishing")
    id("com.kelvsyc.internal.kotlin-jvm-library")
    id("com.kelvsyc.internal.kover")
}

group = "com.kelvsyc.kotlin"

dependencies {
    api("com.kelvsyc.kotlin:kotlin-core:$version")
    api(libs.commons.numbers.complex)
    api(libs.commons.numbers.core)
    api(libs.commons.numbers.fraction)

    testImplementation(libs.kotest.property)
}

dokka {
    dokkaSourceSets.configureEach {
        externalDocumentationLinks.register("commons-numbers") {
            url("https://commons.apache.org/proper/commons-numbers/apidocs/")
            packageListUrl("https://commons.apache.org/proper/commons-numbers/apidocs/element-list")
        }
    }
}
