import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    id("com.kelvsyc.internal.kotlin-tools.dokka")
    id("com.kelvsyc.internal.kotlin-tools.github-publishing")
    id("com.kelvsyc.internal.kotlin-tools.kotlin-multiplatform-library")
    id("com.kelvsyc.internal.kotlin-tools.kotlin-multiplatform-jvm")
    id("com.kelvsyc.internal.kotlin-tools.kotlin-multiplatform-js")
    id("com.kelvsyc.internal.kotlin-tools.kotlin-multiplatform-native")
}

group = "com.kelvsyc.kotlin"

kotlin {
    // sincos/sincosf are BSD/GNU extensions absent from platform.posix and platform.darwin.
    // They are exposed via a cinterop targeting math.h. Not wired on Windows (mingwX64) because
    // sincos is not available in MSVCRT.
    targets.withType<KotlinNativeTarget>().configureEach {
        if (name != "mingwX64") {
            compilations.getByName("main").cinterops.create("mathext") {
                defFile(project.file("src/nativeInterop/cinterop/mathext.def"))
            }
        }
        if (name == "linuxX64") {
            compilations.getByName("main").cinterops.create("gnumath") {
                defFile(project.file("src/nativeInterop/cinterop/gnumath.def"))
            }
        }
        if (name == "macosArm64") {
            compilations.getByName("main").cinterops.create("macmath") {
                defFile(project.file("src/nativeInterop/cinterop/macmath.def"))
            }
        }
    }
}
