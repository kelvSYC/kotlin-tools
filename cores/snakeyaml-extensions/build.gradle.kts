plugins {
    id("com.kelvsyc.internal.dokka")
    id("com.kelvsyc.internal.github-publishing")
    id("com.kelvsyc.internal.kotlin-jvm-library")
}

group = "com.kelvsyc.kotlin"

dependencies {
    api(libs.snakeyaml.engine)
}
