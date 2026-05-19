import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsEnvSpec
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsPlugin

plugins {
    id("com.google.devtools.ksp")
    id("io.kotest")
    id("com.kelvsyc.internal.kotlin-tools.kotlin-multiplatform-base")
}

kotlin {
    js(IR) {
        nodejs()
        binaries.library()
    }
}

// Pin Node.js to 22.x LTS. Node.js 24.x bundles OpenSSL 3.3/3.4 headers that include
// architecture-specific fipskey.h symlinks which don't resolve on Linux, causing
// calculateDirHash to crash with FileNotFoundException in kotlinNodeJsSetup.
// OpenSSL 3.2.x (used by Node.js 22.x) does not have this issue.
rootProject.plugins.withType<NodeJsPlugin> {
    rootProject.extensions.configure<NodeJsEnvSpec> {
        version.set("22.12.0")
    }
}
