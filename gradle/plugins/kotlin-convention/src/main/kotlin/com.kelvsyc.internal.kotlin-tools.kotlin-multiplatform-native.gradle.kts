import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    id("io.kotest")
    id("com.kelvsyc.internal.kotlin-tools.kotlin-multiplatform-base")
}

kotlin {
    val hostOs = System.getProperty("os.name")
    when {
        hostOs.startsWith("Linux")   -> linuxX64()
        hostOs == "Mac OS X"         -> macosArm64()
        hostOs.startsWith("Windows") -> mingwX64()
    }

    // All native compilations may use C interop (platform.posix.*, platform.windows.*).
    targets.withType<KotlinNativeTarget>().configureEach {
        compilations.configureEach {
            compileTaskProvider.configure {
                compilerOptions {
                    optIn.add("kotlinx.cinterop.ExperimentalForeignApi")
                }
            }
        }
    }
}
