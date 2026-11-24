plugins {
    id("com.kelvsyc.internal.dokka")
    id("com.kelvsyc.internal.github-publishing")
    id("com.kelvsyc.internal.kotlin-jvm-library")
    id("com.kelvsyc.internal.kover")
}

group = "com.kelvsyc.kotlin"

dependencies {
    api("com.kelvsyc.kotlin:kotlin-core:$version")
    api(libs.commons.lang)
    implementation(kotlin("reflect", kotlin.coreLibrariesVersion))
}

dokka {
    dokkaSourceSets.configureEach {
        externalDocumentationLinks.register("commons-lang") {
            url("https://commons.apache.org/proper/commons-lang/apidocs/")
            packageListUrl("https://commons.apache.org/proper/commons-lang/apidocs/element-list")
        }
    }
}
